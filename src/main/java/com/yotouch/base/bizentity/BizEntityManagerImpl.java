package com.yotouch.base.bizentity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.WorkflowManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.yotouch.core.Consts;
import com.yotouch.core.config.Configure;
import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.entity.EntityManagerImpl;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.entity.MetaEntityImpl;
import com.yotouch.core.entity.MetaFieldImpl;
import com.yotouch.core.workflow.Workflow;
import com.yotouch.core.workflow.WorkflowManager;

@Service
public class BizEntityManagerImpl implements BizEntityManager {
    
    private static final Logger logger = LoggerFactory.getLogger(BizEntityManagerImpl.class);
        
    @Autowired
    private EntityManager entityMgr;
    
    @Autowired
    private WorkflowManager wfMgr;
    
    @Autowired
    private Configure config;
    
    @Autowired
    private DbSession dbSession;
    
    private Map<String, BizMetaEntityImpl> entityNamedMap;
    
    public BizEntityManagerImpl() {
    }
    
    public void reload() {
        ((WorkflowManagerImpl)wfMgr).reload();
        this.init();
    }

    @PostConstruct
    public void init() {
        
        this.entityNamedMap = new HashMap<>();

        File ytHome = config.getRuntimeHome();

        for (File ah: ytHome.listFiles()) {
            if (ah.isDirectory()) {
                if (ah.getName().startsWith("addon-")
                        || ah.getName().startsWith("app-")) {
                    parseBizEntityConfigFile(new File(ah, "etc/bizEntities.yaml"));
                }
            }
        }
        
        loadDbBizEntity();

        ((EntityManagerImpl)this.entityMgr).rebuildDb();

    }

    private void loadDbBizEntity() {
        List<Entity> beList = dbSession.queryRawSql(
                "bizEntity",
                "status = ?",
                new Object[]{ Consts.STATUS_NORMAL }
        ); 
        
        for (Entity be: beList) {
            Entity wfEntity = be.sr(dbSession, "workflow");
            String meUuid = be.v("metaEntity");

            Workflow wf = wfMgr.getWorkflow(wfEntity.v("name"));
            MetaEntity me = entityMgr.getMetaEntity(meUuid);
            
            fillWfFields(me);

            BizMetaEntityImpl bme = new BizMetaEntityImpl(wf, me);
            this.entityNamedMap.put(me.getName(), bme);   
        }
    }

    private void parseBizEntityConfigFile(File bizEntitiesFile) {
        logger.info("Parse BizEntity " + bizEntitiesFile);

        if (bizEntitiesFile.exists()) {
            Yaml yaml = new Yaml();
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> m = (Map<String, Object>) yaml.load(new FileInputStream(bizEntitiesFile));

                @SuppressWarnings("unchecked")
                List<Map<String, String>> bizEntities =  (List<Map<String, String>>) m.get("bizEntities");

                if (bizEntities == null) {
                    return;
                }


                for (Map<String, String> beMap: bizEntities) {
                    logger.info("BeMap " + beMap);

                    String workflowName = beMap.get("workflow");
                    String entityName = beMap.get("entity");

                    Workflow wf = wfMgr.getWorkflow(workflowName);
                    MetaEntity me = entityMgr.getMetaEntity(entityName);

                    fillWfFields(me);

                    BizMetaEntityImpl bme = new BizMetaEntityImpl(wf, me);
                    this.entityNamedMap.put(me.getName(), bme);
                }

                //((EntityManagerImpl)this.entityMgr).rebuildDb();

            } catch (FileNotFoundException e) {
                logger.error("Load system field error", e);
                return;
            }
        }
    }

    private void fillWfFields(MetaEntity me) {
        
        MetaEntityImpl mei = (MetaEntityImpl) me;
        
        if (me.getMetaField(Consts.BIZ_ENTITY_FIELD_WORKFLOW) == null) {
            
            Map<String, Object> fr = new HashMap<>();
            fr.put("name", Consts.BIZ_ENTITY_FIELD_WORKFLOW);
            fr.put("dataType", Consts.META_FIELD_DATA_TYPE_UUID);
            
            MetaFieldImpl<?> mfi = MetaFieldImpl.build(entityMgr, fr);
            mei.addField(mfi);
            mfi.setMetaEntity(mei);
            
        }
        
        if (me.getMetaField(Consts.BIZ_ENTITY_FIELD_STATE) == null) {
            Map<String, Object> fr = new HashMap<>();
            fr.put("name", Consts.BIZ_ENTITY_FIELD_STATE);
            fr.put("dataType", Consts.META_FIELD_DATA_TYPE_UUID);
            
            MetaFieldImpl<?> mfi = MetaFieldImpl.build(entityMgr, fr);
            mei.addField(mfi);
            mfi.setMetaEntity(mei);
        }
        
    }

    @Override
    public BizMetaEntity getBizMetaEntityByEntity(String entityName) {
        return this.entityNamedMap.get(entityName);
    }    

}
