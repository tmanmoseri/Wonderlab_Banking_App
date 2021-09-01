package com.Wonderlab.BankingApp.controller;


import com.Wonderlab.BankingApp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.Serializable;

@Controller
public class LoginController implements Serializable {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = {"/"})
    public String home(){
        return "login";
    }
    @RequestMapping(value = {"/login"})
    public String login(@RequestParam(value = "error", required = false) String error) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid Email or password!");
        }

        model.setViewName("login");

        return  "login";
    }
    @GetMapping("/profile")
    public String showProfilePage(Model model){

        userService.findByLogedinUser(model);

        return "profile";
    }
}
