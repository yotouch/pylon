package com.yotouch.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

@Service
public class ConfigureImpl implements Configure {
    
    static final private Logger logger = LoggerFactory.getLogger(ConfigureImpl.class);
    
    private File ytHome;
    
    Map<String, Object> config;
    
    ConfigureImpl() {
        
    }
    
    
    @PostConstruct
    void init() {
        /*
        logger.info("All properties " + System.getProperties());
        for (Object name: System.getProperties().keySet()) {
            logger.info("key " + name);
        }
        */
        String ytRuntimeHome = System.getProperty("YT_HOME");

        logger.info("Get YT_HOME from VM property: " + ytRuntimeHome);
        if (ytRuntimeHome == null) {

            ytRuntimeHome = System.getenv("YT_HOME");

            logger.info("Get YT_HOME from environment: " + ytRuntimeHome);

            if (ytRuntimeHome == null) {
                /*
                URL location = ConfigureImpl.class.getProtectionDomain().getCodeSource().getLocation();
                this.ytHome = new File(location.getFile());
                */
            } else {
                this.ytHome = new File(ytRuntimeHome);
            }
        } else {
            this.ytHome = new File(ytRuntimeHome);
        }

        if (this.ytHome == null) {
            this.ytHome = new File("/Users/yinwm/eclipse/projs/app-interview");
        }
        logger.warn("Init configure YT RUNTIME HOME " + this.ytHome);
        
        
        File f = new File(this.getEtcDir(), "config.yaml");
        if (f.exists() && f.isFile()) {
            Yaml yaml = new Yaml();
            try {
                
                @SuppressWarnings("unchecked")
                Map<String, Object> load = (Map<String, Object>) yaml.load(new FileInputStream(f));
                this.config = load;
                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            
        }
    }
    

    @Override
    public File getRuntimeHome() {
        return this.ytHome;
    }

    @Override
    public File getEtcDir() {
        return new File(this.ytHome, "etc");
    }

    @Override
    public Object getProp(String key) {
        return this.config.get(key);
    }
}
