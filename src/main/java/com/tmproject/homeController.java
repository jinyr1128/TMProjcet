package com.tmproject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class homeController {

    @GetMapping("/api/member/loginPage")
    public String tmpLoginPage(){
        return "oauthLogin";
    }
}
