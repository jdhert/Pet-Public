package com.kitri.web_project.controller;

import com.kitri.web_project.mappers.UserMapper;
import com.kitri.web_project.dto.login.RequestClient;
import com.kitri.web_project.dto.login.ResponseUser;
import com.kitri.web_project.service.ApiService;
import com.kitri.web_project.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/signup")
public class SignUpController {

    @Autowired
    UserMapper userMapper;


    private final SignUpService signUpService;

    @Autowired
    public SignUpController(SignUpService signUpService){
        this.signUpService = signUpService;
    }
    //회원가입
    @PostMapping
    public boolean signup(@RequestBody RequestClient requestClient, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return false;
        ResponseUser responseUser = userMapper.findMember(requestClient.getName(), requestClient.getEmail());
        if (responseUser != null)
            return false;
        else if(!(requestClient.getPassword().equals(requestClient.getPasswordVerify())))
            return false;
        else {
            userMapper.signup(requestClient.getEmail(), requestClient.getName(), requestClient.getPassword(), requestClient.getAddress(), requestClient.getImgPath(), false);
            return true;
        }
    }

    @GetMapping("/name_check")
    public boolean nameCheck(String name){
        return signUpService.nameCheck(name);
    }
}

