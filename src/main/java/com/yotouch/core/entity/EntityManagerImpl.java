package com.yotouch.core.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${mysql.lowerCaseTableNames:}")
    private String lowerCaseTableNames;

    private Map<String, MetaEntityImpl> userEntities;
    private Map<String, MetaEntityImpl> mfEntities;

    private Map<String, MetaFieldImpl<?>> systemFields;

    public EntityManagerImpl() {
        this.userEntities = new HashMap<>();
        this.mfEntities = new HashMap<>();
    }

    private boolean isLowerCase() {
        return "true".equalsIgnoreCase(this.lowerCaseTableNames)
                || "1".equalsIgnoreCase(this.lowerCaseTableNames);

    }

    @PostConstruct
    private void initMetaEntities() {
        this.userEntities = new HashMap<>();
        this.mfEntities = new HashMap<>();

        loadSystemMetaFields();
        loadSystemMetaEntities();
        loadUserEntities();

        //loadFileMetaEntities("systemEntities.yaml", "");
        //loadFileMetaEntities("userEntities.yaml", "usr_");

        // Load new addon related entities
        //loadAppMetaEntities();

        loadDbMetaEntities();
        loadDbMetaFields();
        
        buildMultiReferenceEntities();

        rebuildDb();
    }

    private void buildMultiReferenceEntities() {
        scanMrEntities(this.userEntities.values());
    }

    private void scanMrEntities(Collection<MetaEntityImpl> entities) {
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
        
        MetaEntityImpl mei = new MetaEntityImpl(uuid, uuid, uuid,"mr_", this.isLowerCase());
        mmf.setMappingMetaEntity(mei);
        
        Map<String, Object> fMap = new HashMap<>();
        fMap.put("dataType", Consts.META_FIELD_DATA_TYPE_UUID);
        fMap.put("name", "s_" + me.getName() + "Uuid");
        
        MetaFieldImpl<?> mfi = MetaFieldImpl.build(this, fMap);
        mei.addField(mfi);
        mfi.setMetaEntity(mei);
        
        
        fMap = new HashMap<>();
        fMap.put("dataType", Consts.META_FIELD_DATA_TYPE_UUID);
        fMap.put("name", "t_" + targetEntityName + "Uuid");
        
        mfi = MetaFieldImpl.build(this, fMap);
        mei.addField(mfi);
        mfi.setMetaEntity(mei);

        fMap = new HashMap<>();
        fMap.put("dataType", Consts.META_FIELD_DATA_TYPE_INT);
        fMap.put("name", "weight");

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
    public void rebuildDb() {


        List<String> tables = dbStore.fetchAllTables(this.isLowerCase());

        logger.info("Tables " + tables);

        scanExistingDbTable(tables, this.userEntities);
        scanExistingDbTable(tables, this.mfEntities);
    }

    private void scanExistingDbTable(List<String> tables, Map<String, MetaEntityImpl> entities) {
        for (String metaEntityTable: entities.keySet()) {
            MetaEntityImpl mei = (MetaEntityImpl) entities.get(metaEntityTable);
            if (tables.contains(mei.getTableName())) {
                addMetaEntityFields(mei);
            } else {
                addMetaEntity(mei);
            }
        }
    }

    private void loadUserEntities() {
        File ytHome = config.getRuntimeHome();
        
        if (ytHome == null) {

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            try {
                Resource[] resources = resolver.getResources("/etc/**.entities.yaml");
                for (Resource resource : resources) {
                    InputStream is = resource.getInputStream();
                    loadMetaEntitiesFromInputStream(is, "usr_");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        } else {

            for (File ah : ytHome.listFiles()) {
                if (ah.isDirectory()) {
                    if (ah.getName().startsWith("addon-")
                            || ah.getName().startsWith("app-")) {
                        scanEtcUserEntities(ah);
                    }
                }
            }

            if (ytHome.getName().equals("pylon")) {
                scanEtcUserEntities(ytHome);
            }
        }

    }

    private void scanEtcUserEntities(File ah) {
        File etcHome = new File(ah, "etc");

        logger.info("Checking user entities in dir " + etcHome);

        if (etcHome.exists()) {
            for (File f: etcHome.listFiles()) {
                if (f.getName().equals("userEntities.yaml")
                        || f.getName().endsWith(".entities.yaml")) {
                    loadFileMetaEntities(f, "usr_");
                }
            }
        }
    }

    private void loadSystemMetaEntities() {
        File ytHome = config.getRuntimeHome();
        
        if (ytHome == null) {
            // load file from classpath
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            try {
                Resource[] resources = resolver.getResources("/etc/systemEntities.yaml");
                for (Resource resource : resources) {
                    InputStream is = resource.getInputStream();
                    loadMetaEntitiesFromInputStream(is, "");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            File pylonHome = null;
            File appHome = null;
            for (File ah : ytHome.listFiles()) {
                if (ah.getName().equals("pylon")) {
                    pylonHome = ah;
                }

                if (ah.getName().startsWith("app-")) {
                    appHome = ah;
                }
            }

            logger.info("PYLON HOME " + pylonHome);
            logger.info("APP   HOME " + appHome);

            loadFileMetaEntities(new File(pylonHome, "etc/systemEntities.yaml"), "");
            if (appHome != null) {
                loadFileMetaEntities(new File(appHome, "etc/systemEntities.yaml"), "");
            }
        }
    }

    private void loadFileMetaEntitiesFormat2(Map<String, Object> m, String defaultPrefix) {
        @SuppressWarnings("unchecked")
        Map<String, Object> entities = (Map<String, Object>) m.get("entities");

        for (String en : entities.keySet()) {
            String uuid = "uuid-sys-" + en;

            Map<String, Object> emap = (Map<String, Object>) entities.get(en);
            String prefix = defaultPrefix;
            if (emap.containsKey("prefix")) {
                prefix = (String) emap.get("prefix");
            }
            
            String displayName = en;
            if (emap.containsKey("displayName")) {
                String dn = (String) emap.get("displayName");
                if (dn != null && !"".equals(dn)) {
                    displayName = dn;
                }
            }

            MetaEntityImpl mei = (MetaEntityImpl) this.userEntities.get(en);
            if (mei == null) {
                mei = new MetaEntityImpl(uuid, en, displayName, prefix, this.isLowerCase());
            }


            Map<String, Object> fields = (Map<String, Object>) emap.get("fields");
            buildEntityFields(uuid, mei, fields);
            appendSysFields(uuid, mei);

            this.userEntities.put(mei.getName(), mei);
        }

    }
    
    
    private void loadMetaEntitiesFromInputStream(InputStream is, String prefix) {
        Yaml yaml = new Yaml();

        @SuppressWarnings("unchecked")
        Map<String, Object> m = (Map<String, Object>) yaml.load(is);
        if (m == null) {
            return;
        }

        if (m.containsKey("format")) {
            int format = (int) m.get("format");
            if (format == 2) {
                loadFileMetaEntitiesFormat2(m, prefix);
                return ;
            }
        }


        @SuppressWarnings("unchecked")
        Map<String, Object> entities = (Map<String, Object>) m.get("entities");

        for (String en : entities.keySet()) {
            String uuid = "uuid-sys-" + en;

            MetaEntityImpl mei = (MetaEntityImpl) this.userEntities.get(en);
            if (mei == null) {
                mei = new MetaEntityImpl(uuid, en, en, prefix, this.isLowerCase());
            }

            // parse files
            @SuppressWarnings("unchecked")
            Map<String, Object> fields = (Map<String, Object>) entities.get(en);

            buildEntityFields(uuid, mei, fields);

            appendSysFields(uuid, mei);

            this.userEntities.put(mei.getName(), mei);
            logger.warn("Build System metaEntity " + mei);
            //logger.warn("Build System metaEntity fiels " + mei.getMetaFields());
        }
    }

    private void loadFileMetaEntities(File file, String prefix) {

        logger.info("Load entities " + file + " WITH defaultPrefix " + prefix);

        if (file.exists()) {
            
            try {
                InputStream is = new FileInputStream(file); 
                loadMetaEntitiesFromInputStream(is, prefix);

            } catch (FileNotFoundException e) {
                logger.error("Load system field error", e);
                return;
            }
        }

    }

    private void buildEntityFields(String uuid, MetaEntityImpl mei, Map<String, Object> fields) {
        for (String fn : fields.keySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> fMap = (Map<String, Object>) fields.get(fn);

            fMap.put("name", fn);
            fMap.put("uuid", uuid + "-field-" + fn);

            MetaFieldImpl<?> mfi = MetaFieldImpl.build(this, fMap);
            mei.addField(mfi);
            mfi.setMetaEntity(mei);
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

        File ytHome = config.getRuntimeHome();
        
        if (ytHome == null) {
            // load file from classpath
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            try {
                Resource[] resources = resolver.getResources("/etc/systemFields.yaml");
                for (Resource resource : resources) {
                    InputStream is = resource.getInputStream();
                    loadSysFieldsFromInputStream(is);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }            
        } else {

            File pylonHome = null;
            File appHome = null;
            for (File ah : ytHome.listFiles()) {
                if (ah.getName().equals("pylon")) {
                    pylonHome = ah;
                }

                if (ah.getName().startsWith("app-")) {
                    appHome = ah;
                }
            }

            logger.info("PYLON HOME " + pylonHome);
            logger.info("APP   HOME " + appHome);


            loadSysFields(pylonHome);
            loadSysFields(appHome);
        }

    }

    private void loadSysFieldsFromInputStream(InputStream is) {
        Yaml yaml = new Yaml();
        @SuppressWarnings("unchecked")
        Map<String, Object> m = (Map<String, Object>) yaml.load(is);
        logger.info("Load system fields " + m);
        @SuppressWarnings("unchecked")
        Map<String, Object> fields = (Map<String, Object>) m.get("systemFields");

        for (String fn : fields.keySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> fMap = (Map<String, Object>) fields.get(fn);
            fMap.put("name", fn);
            fMap.put("uuid", "-");
            fMap.put("type", "system");
            this.systemFields.put(fn, MetaFieldImpl.build(this, fMap));
        }
    }
    
    private void loadSysFields(File appHome) {
        File ytEtcDir = new File(appHome, "etc");

        logger.warn("Load system meta fields " + ytEtcDir);

        File sysFieldFile = new File(ytEtcDir, "systemFields.yaml");

        logger.warn("Load system meta fields " + sysFieldFile + " exists " + sysFieldFile.exists());

        if (sysFieldFile.exists()) {
            
            try {
                InputStream is = new FileInputStream(sysFieldFile); 
                loadSysFieldsFromInputStream(is);
                //logger.warn("System fields " + this.systemFields);
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
        String displayName = (String) row.get("displayName");
        if (displayName == null && displayName.equals("")) {
            displayName = meName;
        }

        logger.info("Try to build MetaEntity " + meName + " with UUID " + meUuid);

        MetaEntity me = this.getMetaEntity("metaField");
        List<Map<String, Object>> fieldRows = dbStore.fetchList(me, "metaEntityUuid = ?",
                new Object[] { meUuid });

        logger.info("Get field data for " + meName + " fieldData " + fieldRows);

        MetaEntityImpl mei = new MetaEntityImpl(meUuid, meName, displayName,"usr_", this.isLowerCase());

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
        l.addAll(this.userEntities.values());
        return l;
    }

    @Override
    public MetaEntity getMetaEntity(String name) {
        MetaEntity me = this.userEntities.get(name);

        if (me == null) {
            me = this.mfEntities.get(name);
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
