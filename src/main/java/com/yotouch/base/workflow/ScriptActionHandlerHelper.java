package com.yotouch.base.workflow;

import com.yotouch.base.util.WebUtil;
import com.yotouch.core.config.Configure;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.workflow.WorkflowAction;
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


    public Object doEvalScript(DbSession dbSession, WorkflowAction workflowAction, Entity entity, String scriptType, Map<String, Object> params) {
        String fname = this.config.getRuntimeHome() + "/wf_scripts/" + workflowAction.getWorkflow().getName().toLowerCase() + "/actions/" + workflowAction.getName().toLowerCase() + "/" + scriptType.toLowerCase();
        String pf = "";
        String scripts = null;
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
            

            if ("bsh".equals(pf)) {
                BshScriptEvaluator bse = new BshScriptEvaluator();
                StaticScriptSource sss = new StaticScriptSource(scripts);

                return bse.evaluate(sss, args);
            } else if ("groovy".equals(pf)) {
                GroovyScriptEvaluator gse = new GroovyScriptEvaluator();
                StaticScriptSource sss = new StaticScriptSource(scripts);
                return gse.evaluate(sss, args);
            }
        }

        return null;
    }
}
