package com.yotouch.base.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorldController {
    

    @RequestMapping(value="/hello")
    public @ResponseBody String helloWorld() {
        
        return "hello";
        
    }
    
    @RequestMapping("/world")
    public String hello(Model model) {
        
        model.addAttribute("name", "张三6");
        
        return "world";
    }

    @RequestMapping("/admin/list")
    public String list(Model model) {
        return "admin/list";
    }

    @RequestMapping("/admin/edit")
    public String edit(Model model) {
        return "admin/edit";
    }

}
