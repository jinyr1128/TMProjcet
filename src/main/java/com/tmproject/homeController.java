package com.tmproject;

import org.springframework.web.bind.annotation.GetMapping;

public class homeController {

    @GetMapping("/api/member/loginPage")
    public String tmpLoginPage(){
        return "index";
    }
}
