package com.yotouch.base.web.controller;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.yotouch.base.service.PasswordChecker;
import com.yotouch.base.service.UserService;
import com.yotouch.core.Consts;
import com.yotouch.core.ErrorCode;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class LoginController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Value("${defaultHome:/}")
    private String defaultHome;
    
    @Autowired(required =  false)
    private PasswordChecker passwordChecker;

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String login(
            @RequestParam(value="errorCode", defaultValue="0") int errorCode,
            @RequestParam(value="backUrl", defaultValue="") String backUrl,
            Model model,
            HttpServletRequest request
    ) {

        String mainCompany = Consts.DEFAULT_COMPANY_NAME;
        model.addAttribute("companyName", mainCompany);

        Entity user = (Entity) request.getAttribute("loginUser");
        logger.info("Try to get user " + user);
        if (user != null) {
            if (!StringUtils.isEmpty(backUrl)) {
                return "redirect:" + backUrl;
            } else {
                return "redirect:" + defaultHome;
            }
        }

        model.addAttribute("errorCode", errorCode);

        return "/common/login";
    }

    @RequestMapping(value="/login", method = RequestMethod.POST )
    public String doLogin(
            @RequestParam(value="name", defaultValue = "") String name,
            @RequestParam(value="phone", defaultValue = "") String phone,
            @RequestParam(value="type", defaultValue = "name") String type,
            @RequestParam(value="password") String password,
            @RequestParam(value="backUrl", defaultValue="") String backUrl,
            HttpServletRequest request,
            RedirectAttributes redirectAttr,
            Model model,
            HttpServletResponse response
    ) {


        DbSession dbSession = this.getDbSession();

        String checkKey = phone;
        Entity user = null;
        if (type.equalsIgnoreCase("name")) {
            user = dbSession.queryOneRawSql("user", "name=? AND status = ?", new Object[]{name, Consts.STATUS_NORMAL});
            checkKey = name;
        } else {
            user = dbSession.queryOneRawSql("user", "phone=? AND status = ?", new Object[]{phone, Consts.STATUS_NORMAL});
        }

        boolean isLogined = false;
        if (user == null) {
            if ("admin".equals(checkKey)) {
                user = dbSession.newEntity("user");
                user.setValue("name", "admin");
                user.setValue("phone", "admin");
                user = dbSession.save(user);
                user.setValue("password", userService.genPassword(user, password));
                user = dbSession.save(user);
                userService.seedLoginCookie(response, user);
                isLogined = true;
            }

            redirectAttr.addAttribute("errorCode", ErrorCode.NO_SUCH_USER);

        } else {

            boolean rightPwd = false;
            if (passwordChecker != null) {
                rightPwd = passwordChecker.checkPassword(user, password);
            } else {
                String userPwd = user.v("password");
                if (userPwd.startsWith("plain:")) {
                    userPwd = userPwd.replace("plain:", "");
                    userPwd = userService.genPassword(user, userPwd);
                }

                String md5Pwd = userService.genPassword(user, password);
                rightPwd = md5Pwd.equals(userPwd);
            }
            

            if (!rightPwd) {
                redirectAttr.addAttribute("errorCode", ErrorCode.LOGIN_FAILED_WRONG_PASSWORD);
            } else {
                userService.seedLoginCookie(response, user);
                isLogined = true;
            }
        }


        if (isLogined) {
            if (StringUtils.isEmpty(backUrl)) {
                backUrl = defaultHome;
            }
            model.addAttribute("toUrl", backUrl);
            return "/common/jsRedirect";
        } else {
            
            if (StringUtils.isEmpty(backUrl)) {
                backUrl = defaultHome;
            }

            redirectAttr.addAttribute("backUrl", backUrl);
            
            return "redirect:/login";
        }
    }

    @RequestMapping(value="/logout")
    public String logout(
            @RequestParam(value = "backUrl", defaultValue = "") String backUrl,
            HttpServletResponse response,
            Model model
    ){
        Cookie cookie = new Cookie("userToken", "");
        cookie.setPath("/");
        response.addCookie(cookie);

        cookie = new Cookie(Consts.COOKIE_NAME_WX_USER_UUID, "");
        cookie.setPath("/");
        response.addCookie(cookie);
        
        String bkUrl = defaultHome;
        if (!StringUtils.isEmpty(bkUrl)) {
            List<String> urlList = Splitter.on("/").trimResults().omitEmptyStrings().splitToList(backUrl);
            backUrl = Joiner.on("/").skipNulls().join(urlList);
            backUrl = java.net.URLEncoder.encode(backUrl);
            bkUrl = backUrl;
        }
        
        
        if (StringUtils.isEmpty(bkUrl)) {
            bkUrl = "/";
        }
        
        model.addAttribute("toUrl", bkUrl);
        return "/common/jsRedirect";
    }


}
