package com.yotouch.base.web;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.zxing.WriterException;

import com.yotouch.base.service.BarcodeService;

@Controller
public class BarcodeController {

    @Autowired
    private BarcodeService barcodeService;

    @RequestMapping("/barcode/qrcode")
    public @ResponseBody byte[] qrcode(
            @RequestParam(value = "height", defaultValue = "200") int height,
            @RequestParam("text") String text,
            HttpServletResponse response
    ) throws IOException, WriterException {
        response.setContentType("image/png");
        return barcodeService.genQrCode(text, height);
    }


}
