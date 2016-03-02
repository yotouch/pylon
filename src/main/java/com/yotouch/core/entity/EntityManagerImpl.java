package com.yotouch.core.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.yotouch.core.Consts;
import com.yotouch.core.config.Configure;
import com.yotouch.core.entity.mf.MultiReferenceMetaFieldImpl;
import com.yotouch.core.exception.NoSuchMetaEntityException;
import com.yotouch.core.store.db.DbStore;

@Service
public class EntityManagerImpl implements EntityManager {

    static final Logger logger = LoggerFactory.getLogger(EntityManagerImpl.class);

    @Autowired
    private DbStore dbStore;

    @Autowired
    private Configure config;

    private Map<String, MetaEntity> userEntities;
    private Map<String, MetaEntity> systemEntities;
    private Map<String, MetaEntity> mfEntities;

    private Map<String, MetaFieldImpl<?>> systemFields;

    public EntityManagerImpl() {
        this.userEntities = new HashMap<>();
        this.systemEntities = new HashMap<>();
        this.mfEntities = new HashMap<>();
    }

    @PostConstruct
    private void initMetaEntities() {
        this.userEntities = new HashMap<>();
        this.systemEntities = new HashMap<>();
        this.mfEntities = new HashMap<>();
        
        loadSystemMetaFields();
        loadFileMetaEntities("systemEntities.yaml", "");
        loadFileMetaEntities("userEntities.yaml", "usr_");
        loadDbMetaEntities();
        loadDbMetaFields();
        
        buildMultiReferenceEntities();

        rebuildDb();
    }

    

    private void buildMultiReferenceEntities() {
        
        scanMrEntities(this.systemEntities.values());
        scanMrEntities(this.userEntities.values());
        
    }

    private void scanMrEntities(Collection<MetaEntity> entities) {
        for (MetaEntity me: entities) {
            for (MetaField<?> mf: me.getMetaFields()) {
                if (mf.isMultiReference()) {
                    buildMfMapping(me, mf);
                }
            }
        }
    }

    private void buildMfMapping(MetaEntity me, MetaField<?> mf) {
        MultiReferenceMetaFieldImpl mmf = (MultiReferenceMetaFieldImpl) mf;
        
        String targetEntityName = mf.getTargetMetaEntity().getName();
        logger.info("Catch MultiReference " + me.getName() + "=>" + mf.getName() + " target " + targetEntityName);
        
        String uuid = me.getName() + "_" + mf.getName() + "_" + targetEntityName;
        
        MetaEntityImpl mei = new MetaEntityImpl(uuid, uuid, "mr_");
        mmf.setMappingMetaEntity(mei);
        
        Map<String, Object> fMap = new HashMap<>();
        fMap.put("dataType", Consts.META_FIELD_DATA_TYPE_UUID);
        fMap.put("name", me.getName() + "Uuid");
        
        MetaFieldImpl<?> mfi = MetaFieldImpl.build(this, fMap);
        mei.addField(mfi);
        mfi.setMetaEntity(mei);
        
        
        fMap = new HashMap<>();
        fMap.put("dataType", Consts.META_FIELD_DATA_TYPE_UUID);
        fMap.put("name", targetEntityName + "Uuid");
        
        mfi = MetaFieldImpl.build(this, fMap);
        mei.addField(mfi);
        mfi.setMetaEntity(mei);
        
        appendSysFields(uuid, mei);
        
        this.mfEntities.put(mei.getName(), mei);
    }

    private void addMetaEntity(MetaEntity me) {
        this.dbStore.createTable(me);
    }

    private void addMetaEntityFields(MetaEntity me) {
        this.dbStore.alterTable(me);
    }

    // CREATE OR ALTER TABLE
    private void rebuildDb() {

        List<String> tables = dbStore.fetchAllTables();

        logger.info("Tables " + tables);

        scanExistingDbTable(tables, this.systemEntities);
        scanExistingDbTable(tables, this.userEntities);
        scanExistingDbTable(tables, this.mfEntities);
    }

    private void scanExistingDbTable(List<String> tables, Map<String, MetaEntity> entities) {
        for (String metaEntityTable: entities.keySet()) {
            MetaEntityImpl mei = (MetaEntityImpl) entities.get(metaEntityTable);
            if (tables.contains(mei.getTableName())) {
                addMetaEntityFields(mei);
            } else {
                addMetaEntity(mei);
            }
        }
    }

    private void loadFileMetaEntities(String fileName, String prefix) {

        File ytEctDir = config.getEtcDir();

        File sysMetaEntities = new File(ytEctDir, fileName);

        if (sysMetaEntities.exists()) {
            Yaml yaml = new Yaml();
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> m = (Map<String, Object>) yaml.load(new FileInputStream(sysMetaEntities));

                @SuppressWarnings("unchecked")
                Map<String, Object> entities = (Map<String, Object>) m.get("entities");

                for (String en : entities.keySet()) {
                    String uuid = "uuid-sys-" + en;

                    MetaEntityImpl mei = new MetaEntityImpl(uuid, en, prefix);

                    // parse files
                    @SuppressWarnings("unchecked")
                    Map<String, Object> fields = (Map<String, Object>) entities.get(en);
                    for (String fn : fields.keySet()) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> fMap = (Map<String, Object>) fields.get(fn);

                        fMap.put("name", fn);
                        fMap.put("uuid", uuid + "-field-" + fn);

                        MetaFieldImpl<?> mfi = MetaFieldImpl.build(this, fMap);
                        mei.addField(mfi);
                        mfi.setMetaEntity(mei);
                    }

                    appendSysFields(uuid, mei);

                    this.systemEntities.put(mei.getName(), mei);
                    logger.warn("Build System metaEntity " + mei);
                    logger.warn("Build System metaEntity fiels " + mei.getMetaFields());
                }

            } catch (FileNotFoundException e) {
                logger.error("Load system field error", e);
                return;
            }
        }

    }

    private void appendSysFields(String meUuid, MetaEntityImpl mei) {
        for (String sysFn : this.systemFields.keySet()) {
            boolean hasSysField = false;
            for (MetaField<?> mf : mei.getMetaFields()) {
                if (sysFn.equalsIgnoreCase(mf.getName())) {
                    hasSysField = true;
                    break;
                }
            }

            if (hasSysField) {
                continue;
            }

            MetaFieldImpl<?> mfi = this.systemFields.get(sysFn);
            MetaFieldImpl<?> newF = mfi.copy(meUuid + "-field-" + mfi.getName());

            mei.addField(newF);
            newF.setMetaEntity(mei);
        }
    }

    private void loadSystemMetaFields() {

        this.systemFields = new HashMap<>();

        File ytEtcDir = config.getEtcDir();

        logger.warn("Load system meta fields " + ytEtcDir);

        File sysFieldFile = new File(ytEtcDir, "systemFields.yaml");

        logger.warn("Load system meta fields " + sysFieldFile + " exists " + sysFieldFile.exists());

        if (sysFieldFile.exists()) {
            Yaml yaml = new Yaml();
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> m = (Map<String, Object>) yaml.load(new FileInputStream(sysFieldFile));
                logger.info("Load system fields " + m);
                @SuppressWarnings("unchecked")
                Map<String, Object> fields = (Map<String, Object>) m.get("systemFields");

                for (String fn : fields.keySet()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> fMap = (Map<String, Object>) fields.get(fn);
                    fMap.put("name", fn);
                    fMap.put("uuid", "-");
                    this.systemFields.put(fn, MetaFieldImpl.build(this, fMap));
                }

                logger.warn("System fields " + this.systemFields);
            } catch (FileNotFoundException e) {
                logger.error("Load system field error", e);
                return;
            }
        }

    }

    private void loadDbMetaEntities() {
        logger.info("DbStore " + this.dbStore);
        MetaEntity me = this.getMetaEntity("metaEntity");
        List<Map<String, Object>> rows = dbStore.fetchAll(me);
        for (Map<String, Object> row : rows) {
            logger.info("Processing MetaEntity " + row.get("name") + " with UUID " + row.get("uuid"));

            MetaEntityImpl mei = this.buildMetaEntity(row);
            appendSysFields(mei.getUuid(), mei);
            this.userEntities.put(mei.getName(), mei);

            logger.info("Finish build user MetaEntity " + mei);
        }
    }
    
    private void loadDbMetaFields() {
        
        MetaEntity me = this.getMetaEntity("metaField");
        
        List<Map<String, Object>> rows = dbStore.fetchAll(me);
        for (Map<String, Object> row: rows) {
            logger.info("Processing MetaField " + row.get("name") + " with UUID " + row.get("uuid"));
            
            MetaFieldImpl<?> mfi = MetaFieldImpl.build(this, row);
            
            String meUuid = (String)row.get("metaEntityUuid");
            logger.info("Processing MetaField " + mfi + " for me " + meUuid);
            
            MetaEntityImpl mfMe = (MetaEntityImpl) this.getMetaEntity(meUuid);
            if (mfMe != null) {
                if (mfMe.getMetaField(mfi.getName()) == null) {
                    mfMe.addField(mfi);
                    mfi.setMetaEntity(mfMe);
                }
            }            
        }
                
    }

    private MetaEntityImpl buildMetaEntity(Map<String, Object> row) {

        String meName = (String) row.get("name");
        String meUuid = (String) row.get("uuid");

        logger.info("Try to build MetaEntity " + meName + " with UUID " + meUuid);

        MetaEntity me = this.getMetaEntity("metaField");
        List<Map<String, Object>> fieldRows = dbStore.fetchList(me, "metaEntityUuid = ?",
                new Object[] { meUuid });

        logger.info("Get field data for " + meName + " fieldData " + fieldRows);

        MetaEntityImpl mei = new MetaEntityImpl(meUuid, meName, "usr_");

        for (Map<String, Object> fr : fieldRows) {
            MetaFieldImpl<?> mf = MetaFieldImpl.build(this, fr);
            mei.addField(mf);
            mf.setMetaEntity(mei);
        }

        return mei;

    }

    @Override
    public List<MetaEntity> getMetaEntities() {
        List<MetaEntity> l = new ArrayList<>(userEntities.values());
        l.addAll(this.systemEntities.values());
        return l;
    }

    @Override
    public MetaEntity getMetaEntity(String name) {
        MetaEntity me = this.systemEntities.get(name);
        if (me == null) {
            me = this.userEntities.get(name);
        }
        
        if (me == null) {
            me = this.mfEntities.get(name);
        }
        
        for (MetaEntity mmee: this.systemEntities.values()) {
            if (mmee.getUuid().equals(name)) {
                return mmee;
            }
        }
        
        for (MetaEntity mmee: this.userEntities.values()) {
            if (mmee.getUuid().equals(name)) {
                return mmee;
            }
        }
        
        if (me == null) {
            throw new NoSuchMetaEntityException(name);
        }
        
        return me;
    }

    public void reload() {        
        initMetaEntities();
    }

}
