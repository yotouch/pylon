package com.yotouch.base.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestHelloController {
    
    @RequestMapping("/rh") 
    public String rh() {
        return "rh";
    }

}
