package com.yotouch.base.bizentity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.model.EntityModel;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.util.EntityUtil;
import com.yotouch.core.workflow.WorkflowManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
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
    
    private Map<String, BizMetaEntityImpl> wfNamedMap;
    
    public BizEntityManagerImpl() {
    }
    
    public void reload() {
        ((WorkflowManagerImpl)wfMgr).reload();
        this.init();
    }

    @PostConstruct
    public void init() {

        this.entityNamedMap = new HashMap<>();
        this.wfNamedMap = new HashMap<>();

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath*:/etc/bizEntities.yaml");
            for (Resource resource : resources) {
                InputStream is = resource.getInputStream();
                parseBizEntityInputStream(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File ytHome = config.getRuntimeHome();
        if (ytHome != null) {
            for (File ah : ytHome.listFiles()) {
                if (ah.isDirectory()) {
                    if (ah.getName().startsWith("addon-")
                            || ah.getName().startsWith("app-")) {
                        parseBizEntityConfigFile(new File(ah, "etc/bizEntities.yaml"));
                    }
                }
            }
        }

        String pylonTest = System.getProperty("PYLON_TEST");
        if (pylonTest != null && pylonTest.toLowerCase().equals("true")) {
            parseBizEntityConfigFile(new File(ytHome, "etc/bizEntities.yaml"));
        }

        loadDbBizEntity();

        ((EntityManagerImpl) this.entityMgr).rebuildDb();

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
            this.wfNamedMap.put(wf.getName(), bme);
        }
    }
    
    private void parseBizEntityInputStream(InputStream is) {
        Yaml yaml = new Yaml();
        @SuppressWarnings("unchecked")
        Map<String, Object> m = (Map<String, Object>) yaml.load(is);

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
            this.wfNamedMap.put(wf.getName(), bme);
        }

        //((EntityManagerImpl)this.entityMgr).rebuildDb();
    }

    private void parseBizEntityConfigFile(File bizEntitiesFile) {
        logger.info("Parse BizEntity " + bizEntitiesFile);

        if (bizEntitiesFile.exists()) {
            
            try {
                InputStream is = new FileInputStream(bizEntitiesFile);
                parseBizEntityInputStream(is);
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

    @Override
    public BizMetaEntity getBizMetaEntityByWorkflow(String wfName) {
        return this.wfNamedMap.get(wfName);
    }

    /**
     * 获取新的workflowEntityModel 获取的时候就已经prepare好了 不用再prepare了
     *
     * @param workflowName
     * @return
     */
    @Override
    public <M extends EntityModel> M getEntityModelByWorkflow(String workflowName) {
        BizMetaEntityImpl bizMetaEntity = this.wfNamedMap.get(workflowName);

        Workflow workflow = bizMetaEntity.getWorkflow();

        M entityModel = EntityUtil.getEntityModel(bizMetaEntity.getMetaEntity().getName());

        entityModel.setWfWorkflow(workflowName);
        entityModel.setWfState("");
        entityModel.setWorkflow(workflow);

        return entityModel;
    }


}
