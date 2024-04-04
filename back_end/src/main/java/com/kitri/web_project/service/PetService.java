package com.kitri.web_project.service;

import com.kitri.web_project.mappers.PetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetService {
    @Autowired
    PetMapper petMapper;

}
