package com.yotouch.base.web.captcha;

import com.yotouch.base.web.controller.BaseController;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import org.patchca.color.SingleColorFactory;
import org.patchca.filter.predefined.CurvesRippleFilterFactory;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.utils.encoder.EncoderHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class CaptchaController extends BaseController {
    
    @RequestMapping({"/captcha/gen", "/api/captcha/gen"})
    public void genCaptcha(
            @RequestParam(value = "height", defaultValue = "100") int height,
            @RequestParam(value = "width", defaultValue = "200") int width,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
        cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));
        cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));

        cs.setHeight(height);
        cs.setWidth(width);

        ServletOutputStream fos = response.getOutputStream();
        String challenge = EncoderHelper.getChallangeAndWriteImage(cs, "png", fos);

        DbSession dbSession = this.getDbSession();
        String bid = webUtil.getBrowserId(request);
        Entity cp = dbSession.queryOneByField("captcha", "bid", bid);
        if (cp == null) {
            cp = dbSession.newEntity("captcha");
        }
        cp.setValue("bid", bid);
        cp.setValue("challenge", challenge);
        dbSession.save(cp);
        fos.flush();
        
        response.setContentType("image/png");
    }
    
}
