package com.yotouch.base.web.j;

import com.yotouch.base.web.controller.BaseController;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class JRoleUserController extends BaseController {
    
    @RequestMapping("/admin/j/autocomplete/user/findByInfo")
    public @ResponseBody
    List<Map<String, Object>> findByInfo(
            @RequestParam(value = "term", defaultValue = "") String term
    ) {

        DbSession dbSession = this.getDbSession();

        if (StringUtils.isEmpty(term)) {
            return new ArrayList<>();
        }

        List<Entity> userList = dbSession.queryRawSql(
                "user",
                "name LIKE ? OR phone LIKE ?",
                new Object[]{"%" + term + "%", "%" + term + "%"}
        );

        List<Map<String, Object>> retList = new ArrayList<>();
        for (Entity u : userList) {
            Map<String, Object> m = new HashMap<>();
            m.put("uuid", u.getUuid());
            m.put("value", u.v("name") + "-" + u.v("phone"));
            retList.add(m);
        }

        return retList;
    }
    
}
