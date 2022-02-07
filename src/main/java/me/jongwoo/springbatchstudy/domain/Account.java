package me.jongwoo.springbatchstudy.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Getter
@Setter
@ToString
//@XmlRootElement
public class Account {

    private Long id;
    private String firstName;
    private String middleInitial;
    private String lastName;
//    private String addressNumber;
    private String address;
//    private String street;
    private String city;
    private String state;
    private String zipCode;

//    private List<Transaction> transactions;
//
//    @XmlElementWrapper(name = "transactions")
//    @XmlElement(name = "transaction")
//    public void setTransactions(List<Transaction> transactions) {
//        this.transactions = transactions;
//    }

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
