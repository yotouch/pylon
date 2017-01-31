package com.yotouch.base.workflow;

import com.yotouch.base.util.WebUtil;
import com.yotouch.core.config.Configure;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.WorkflowAction;
import com.yotouch.core.workflow.WorkflowState;
import jodd.io.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scripting.bsh.BshScriptEvaluator;
import org.springframework.scripting.groovy.GroovyScriptEvaluator;
import org.springframework.scripting.support.StaticScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ScriptActionHandlerHelper {

    @Autowired
    private Configure config;
    
    @Autowired
    private WebUtil webUtil;

    private String[] postfixes = {"bsh", "groovy"};

    public ScriptActionHandlerHelper() {

    }

    
    public Object doEvalStateScript(DbSession dbSession, WorkflowState workflowState, String scriptName) {
        String fname = this.config.getRuntimeHome() + "/wf_scripts/" + workflowState.getWorkflow().getName().toLowerCase() + "/state/" + workflowState.getName().toLowerCase() + "/" + scriptName.toLowerCase();

        FileScript fileScript = new FileScript(fname).invoke();
        String pf = fileScript.getPf();
        String scripts = fileScript.getScripts();

        Object ret = null;
        if (!StringUtils.isEmpty(pf) && !StringUtils.isEmpty(scripts)) {
            Map<String, Object> args = new HashMap<>();
            args.put("dbSession", dbSession);
            args.put("workflowState", workflowState);
            args.put("webUtil", webUtil);

            ret = runScript(pf, scripts, args);
        }
        
        return ret;
        
    }

    public Object doEvalActionScript(DbSession dbSession, WorkflowAction workflowAction, Entity entity, String scriptType, Map<String, Object> params) {
        String fname = this.config.getRuntimeHome() + "/wf_scripts/" + workflowAction.getWorkflow().getName().toLowerCase() + "/action/" + workflowAction.getName().toLowerCase() + "/" + scriptType.toLowerCase();
        FileScript fileScript = new FileScript(fname).invoke();
        String pf = fileScript.getPf();
        String scripts = fileScript.getScripts();

        Object ret = null;
        if (!StringUtils.isEmpty(pf) && !StringUtils.isEmpty(scripts)) {
            Map<String, Object> args = new HashMap<>();
            args.put("dbSession", dbSession);
            args.put("workflowAction", workflowAction);
            args.put("webUtil", webUtil);
            args.put("entity", entity);
            args.put("args", params);
            
            for (String key: params.keySet()) {
                args.put(key, params.get(key));    
            }

            ret = runScript(pf, scripts, args);
        }

        return ret;
    }

    private Object runScript(String pf, String scripts, Map<String, Object> args) {
        if ("bsh".equals(pf)) {
            BshScriptEvaluator bse = new BshScriptEvaluator();
            StaticScriptSource sss = new StaticScriptSource(scripts);

            return bse.evaluate(sss, args);
        } else if ("groovy".equals(pf)) {
            GroovyScriptEvaluator gse = new GroovyScriptEvaluator();
            StaticScriptSource sss = new StaticScriptSource(scripts);
            return gse.evaluate(sss, args);
        }
        return null;
    }

    private class FileScript {
        private String fname;
        private String pf;
        private String scripts;

        public FileScript(String fname) {
            this.fname = fname;
        }

        public String getPf() {
            return pf;
        }

        public String getScripts() {
            return scripts;
        }

        public FileScript invoke() {
            pf = "";
            scripts = null;
            for (String p: postfixes) {
                File f = new File(fname + "." + p);
                if (f.exists() && f.isFile()) {
                    try {
                        scripts = FileUtil.readString(f);
                        pf = p;
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return this;
        }
    }
}
