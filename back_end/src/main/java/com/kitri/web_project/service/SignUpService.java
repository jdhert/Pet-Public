package com.kitri.web_project.service;

import com.kitri.web_project.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignUpService {
    @Autowired
    UserMapper userMapper;

    public boolean nameCheck(String name){
        return userMapper.nameCheck(name) == null;
    }
}
