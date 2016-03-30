package com.yotouch.core.store.db;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.EntityRowMapper;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.entity.MetaEntityImpl;
import com.yotouch.core.entity.MetaField;
import com.yotouch.core.entity.fv.FieldValue;


@Service
public class DbStoreImpl implements DbStore {

    static final private Logger logger = LoggerFactory.getLogger(DbStoreImpl.class);

    @Autowired
    private JdbcTemplate jdbcTpl;

    private List<Map<String, String>> descTable(String name) {

        String sql = "DESCRIBE " + name;

        List<Map<String, String>> values = this.jdbcTpl.query(sql, new RowMapper<Map<String, String>>() {

            @Override
            public Map<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {

                Map<String, String> fields = new HashMap<>();

                for (String f : new String[] { "Field", "Type", "Null", "Key", "Default", "Extra" }) {
                    fields.put(f, rs.getString(f));
                }

                return fields;
            }

        });

        return values;
    }

    @Override
    public List<Map<String, Object>> fetchAll(MetaEntity me) {
        logger.info("Fetch all data from table " + me.getName());
        return this.fetchList(me, "", new Object[] {});
    }

    @Override
    public List<Map<String, Object>> fetchList(MetaEntity me, String sql, Object[] args) {
        
        MetaEntityImpl mei = (MetaEntityImpl) me;

        String fullSql = "SELECT * FROM " + mei.getTableName();

        if (!StringUtils.isEmpty(sql)) {
            fullSql += " WHERE " + sql;
        }

        List<Map<String, String>> fields = this.descTable(mei.getTableName());

        List<Map<String, Object>> rows = this.jdbcTpl.query(fullSql, args, new RowMapper<Map<String, Object>>() {

            @Override
            public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {

                Map<String, Object> row = new HashMap<>();

                for (Map<String, String> f : fields) {
                    String fieldName = f.get("Field");
                    String value = rs.getString(fieldName);

                    row.put(fieldName, value);
                }

                return row;
            }
        });

        return rows;
    }

    @Override
    public List<String> fetchAllTables() {
        String sql = "SHOW TABLES";

        List<String> rows = this.jdbcTpl.query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1);
            }
        });

        return rows;

    }

    
    
    @Override
    public void createTable(MetaEntity me) {
        MetaEntityImpl mei = (MetaEntityImpl) me;

        String sql = "CREATE TABLE " + mei.getTableName() + "(";
        
        for (MetaField<?> mf: me.getMetaFields()) {
            sql = appendFieldDDL(sql, mf, "");
        }
        
        sql += " primary key (uuid) ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        
        logger.info("Create table " + me.getName() + " sql: " + sql);
        this.jdbcTpl.execute(sql);
    }

    private String appendFieldDDL(String sql, MetaField<?> mf, String prefix) {
        String name = prefix + mf.getName();
        
        if (mf.isSingleReference()) {
            name += "Uuid";
        }
        
        if (Consts.META_FIELD_DATA_TYPE_STRING.equalsIgnoreCase(mf.getDataType())) {
            sql += name + " VARCHAR(255), "; 
        } else if (Consts.META_FIELD_DATA_TYPE_UUID.equalsIgnoreCase(mf.getDataType())) {
            sql += name + " VARCHAR(40) NOT NULL DEFAULT \"\", ";
        } else if (Consts.META_FIELD_DATA_TYPE_DATETIME.equalsIgnoreCase(mf.getDataType())) {
            sql += name + " TIMESTAMP NULL DEFAULT NULL , ";
        } else if (Consts.META_FIELD_DATA_TYPE_INT.equalsIgnoreCase(mf.getDataType())) {
            sql += name + " INT, ";
        } else if (Consts.META_FIELD_DATA_TYPE_DOUBLE.equalsIgnoreCase(mf.getDataType())) {
            sql += name + " DOUBLE, ";
        } else if (Consts.META_FIELD_DATA_TYPE_TEXT.equalsIgnoreCase(mf.getDataType())) {
            sql += name + " TEXT, ";
        } else if (Consts.META_FIELD_DATA_TYPE_BINARY.equalsIgnoreCase(mf.getDataType())) {
            sql += name + " LONGBLOB, ";
        } else if (Consts.META_FIELD_DATA_TYPE_BOOLEAN.equalsIgnoreCase(mf.getDataType())) {
            sql += name + " TINYINT, ";
        }
        return sql;
    }

    @Override
    public void alterTable(MetaEntity me) {
        MetaEntityImpl mei = (MetaEntityImpl) me;
        
        String tableName = mei.getTableName();
        List<Map<String, String>> fields = this.descTable(tableName);

        List<String> fieldNames = new ArrayList<>();
        for (Map<String, String> m: fields) {
            fieldNames.add(m.get("Field"));
        }
        
        String sql = "ALTER TABLE " + tableName + " ";
        boolean hasNew = false;
        for (MetaField<?> mf: me.getMetaFields()) {
            
            //logger.info("Scan field " + mf.getName());
            
            String newFieldName = mf.getName();
            if (mf.isSingleReference()) {
                newFieldName += "Uuid";
            }
            
            if (fieldNames.contains(newFieldName)) {
                continue;
            }
            
            if (mf.isMultiReference()) {
                continue; // TODO: yinwm - Add multi reference field
            }
            
            sql = appendFieldDDL(sql, mf, "ADD COLUMN ");
            hasNew = true;

        }
        
        logger.info("Alter table sql " + hasNew + " " + sql);
        
        if (hasNew) {
            
            int pos = sql.lastIndexOf(",");
            sql = sql.substring(0, pos);
            
            logger.info("ALTER TABLE sql " + sql);
            this.jdbcTpl.execute(sql);
        } else {
            logger.info("No new fields for " + me.getName());
        }
        
    }
    
    private void setPsValue(PreparedStatement ps, int idx, FieldValue<?> fv) throws SQLException {
        MetaField<?> mf = fv.getField();
        
        Object value = fv.getValue();
        
        //logger.info("Set ps value idx " + idx + " name " + fv.getField() + " value " + value + " value type " + (value == null ? "null" : value.getClass()));
        
        if (Consts.META_FIELD_DATA_TYPE_STRING.equals(mf.getDataType())
                || Consts.META_FIELD_DATA_TYPE_TEXT.equalsIgnoreCase(mf.getDataType())
                ) {
            ps.setString(idx, (String) fv.getValue());
        } else if (Consts.META_FIELD_DATA_TYPE_UUID.equalsIgnoreCase(mf.getDataType())) {
            
            String s = (String) fv.getValue();
            if (s == null) {
                s = "";
            }
            ps.setString(idx, s);
            
        } else if (Consts.META_FIELD_DATA_TYPE_DATETIME.equals(mf.getDataType())) {
            Calendar cal = (Calendar) fv.getValue();
            if (cal == null) {
                cal = (Calendar) fv.getField().getDefaultValue();
            }
            ps.setTimestamp(idx, new java.sql.Timestamp(cal.getTimeInMillis()));
        } else if (Consts.META_FIELD_DATA_TYPE_INT.equals(mf.getDataType())) {
            if (fv.getValue() == null) {
                ps.setObject(idx, null);
            } else {
                ps.setInt(idx, (Integer) fv.getValue());
            }
        } else if (Consts.META_FIELD_DATA_TYPE_DOUBLE.equals(mf.getDataType())) {
            Double v = (Double) fv.getValue();
            if (v == null) {
                ps.setObject(idx, null);
            } else {
                ps.setDouble(idx, v); 
            }
        } else if (Consts.META_FIELD_DATA_TYPE_BINARY.equals(mf.getDataType())) {
            ps.setBlob(idx, new ByteArrayInputStream((byte[]) fv.getValue()));
        } else if (Consts.META_FIELD_DATA_TYPE_BOOLEAN.equalsIgnoreCase(mf.getDataType())) {
            
            Boolean b = (Boolean) value;
            if (b == null) {
                ps.setObject(idx, null);
            } else {
                ps.setInt(idx, b ? 1 : 0);
            }
            
            
        }
    }


    @Override
    public String insert(MetaEntity me, List<FieldValue<?>> fvs) {
        
        MetaEntityImpl mei = (MetaEntityImpl) me;
        
        String uuid = UUID.randomUUID().toString();
        
        String sql = "INSERT INTO " + mei.getTableName() + " (uuid ";
        
        String qStr = "?";
        
        for (int i = 0; i < fvs.size(); i++) {
            FieldValue<?> fv = fvs.get(i);
            
            MetaField<?> mf = fv.getField();
            
            String fname = mf.getName();
            
            if (fname.equals("uuid") || mf.isMultiReference()) {
                continue;
            }
            
            if (mf.isSingleReference()) {
                fname = fname + "Uuid";
            }
            
            sql += " , " + fname;
            qStr += " , ?";
        }
        
        sql += ") VALUE ( "+qStr+")";
        
        logger.info("Do INSERT " + sql);
        
        this.jdbcTpl.update(sql, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1,  uuid);
                
                int pos = 2;
                for (int i = 0; i < fvs.size(); i++) {
                    FieldValue<?> fv = fvs.get(i);
                    MetaField<?> mf = fv.getField();
                    if (mf.getName().equals("uuid") || mf.isMultiReference()) {
                        continue;
                    }
                    setPsValue(ps, pos, fv);
                    pos += 1;
                }
            }
        });
        
        return uuid;        
    }

    @Override
    public void update(MetaEntity me, String uuid, List<FieldValue<?>> fvs) {
        
        MetaEntityImpl mei = (MetaEntityImpl) me;
        
        String sql = "UPDATE " + mei.getTableName() + " SET ";
        
        boolean first = true;
        List<FieldValue<?>> valueFields = new ArrayList<>();  
        for (FieldValue<?> fv: fvs) {
            if (fv.isChanged()) {
                MetaField<?> mf = fv.getField();
                if (mf.getName().equals("uuid") || mf.isMultiReference()) {
                    continue;
                }
                if (!first) {
                    sql += ",";
                }
                
                String fname = mf.getName();
                if (mf.isSingleReference()) {
                    fname += "Uuid";
                }
                
                sql += fname + " = ?";
                valueFields.add(fv);
                first = false;
            }
        }
        
        if (valueFields.isEmpty()) {
            return;
        }
        
        sql += " WHERE uuid = ?";
        
        logger.info("Yotouch Update SQL " + sql);
        
        this.jdbcTpl.update(sql, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                
                for (int i = 0; i < valueFields.size(); i++) {
                    FieldValue<?> fv = valueFields.get(i);
                    int idx = i + 1;
                    setPsValue(ps, idx, fv);
                }                
                ps.setString(valueFields.size() + 1, uuid);
            }

        });
    }

    @Override
    public List<Entity> query(MetaEntity me, String uuid, EntityRowMapper mapper) {
        MetaEntityImpl mei = (MetaEntityImpl) me;
        String sql = "SELECT * FROM " + mei.getTableName() + " WHERE uuid=?";
        Object[] args = new Object[]{uuid};
        logger.info("Yotouch SQL " + sql + " args [" + StringUtils.arrayToCommaDelimitedString(args) + "]");
        List<Entity> el = this.jdbcTpl.query(sql, args, mapper);
        return el;
    }

    @Override
    public List<Entity> querySql(MetaEntity me, String where, Object[] args, EntityRowMapper mapper) {
        MetaEntityImpl mei = (MetaEntityImpl) me;
        String sql = "SELECT * FROM " + mei.getTableName();
        if (!StringUtils.isEmpty(where)) {
            sql += " WHERE " + where;
            //logger.info("YT SQL SELECT : " + sql + " args [" + StringUtils.arrayToCommaDelimitedString(args) + "]");
            return this.jdbcTpl.query(sql, args, mapper);
        } else {
            return this.jdbcTpl.query(sql, mapper);
        }
    }

    @Override
    public void deleteRawSql(MetaEntity me, String where, Object[] args) {

        MetaEntityImpl mei = (MetaEntityImpl) me;
        
        String sql = "DELETE FROM " + mei.getTableName() + " WHERE " + where;
        logger.info("Yotouch DELETE SQL " + sql + " args [" + StringUtils.arrayToCommaDelimitedString(args) + "]");
        this.jdbcTpl.update(sql, args);
        
    }
}



