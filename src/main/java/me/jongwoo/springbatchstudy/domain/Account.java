package me.jongwoo.springbatchstudy.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Account {

    private String firstName;
    private String middleInitial;
    private String lastName;
//    private String addressNumber;
    private String address;
//    private String street;
    private String city;
    private String state;
    private String zipCode;

    public Account(){

    }

    public Account(String firstName, String middleInitial, String lastName, String address, String city, String state, String zipCode) {
        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    //    public Account(String firstName, String middleInitial, String lastName,
//                   String addressNumber, String street, String city,
//                   String state, String zipCode) {
//        this.firstName = firstName;
//        this.middleInitial = middleInitial;
//        this.lastName = lastName;
//        this.addressNumber = addressNumber;
//        this.street = street;
//        this.city = city;
//        this.state = state;
//        this.zipCode = zipCode;
//    }
}
