package me.jongwoo.springbatchstudy.service;

import me.jongwoo.springbatchstudy.repository.Customer4;
import org.springframework.stereotype.Service;

public class CustomService {


    public void serviceMethod(String message){
        System.out.println(message);
        System.out.println("Service method was called");
    }

    public void logCustomer(Customer4 cust){
        System.out.println("i just saved " + cust);
    }

    public void logCustomerAddress(String address, String city, String state, String zip){
        System.out.println(String.format("i just saved the address:\n%s\n%s, %s\n%s", address, city, state, zip));
    }
}
