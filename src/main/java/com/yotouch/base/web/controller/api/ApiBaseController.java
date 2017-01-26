package com.yotouch.base.web.controller.api;

import com.yotouch.base.web.controller.BaseController;

import java.util.HashMap;
import java.util.Map;

public class ApiBaseController extends BaseController {
    
    protected Map<String, Object> getRetMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("errorCode", 0);
        m.put("errorMsg", "");

        Map<String, Object> rm = new HashMap<>();
        m.put("result", rm);
        
        return m;
    }
    
}
