package me.jongwoo.springbatchstudy.service;

import org.springframework.stereotype.Service;

public class CustomService {


    public void serviceMethod(String message){
        System.out.println(message);
        System.out.println("Service method was called");
    }
}
