package me.jongwoo.springbatchstudy.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Customer {

    @Id @GeneratedValue
    private Long customerId;

    private String middleName;
    private String lastName;

    private  String firstName;

    private String ssn;
    private String email;

    private String homePhone;
    private String cellPhone;
    private String workPhone;

    private boolean notification_pref;



    public static Customer createCustomer(
            String firstName, String middleName, String lastName,
            String ssn, String email,
            String homePhone, String cellPhone, String workPhone,
            boolean notification_pref){

        return Customer.builder()
                    .firstName(firstName)
                    .middleName(middleName)
                    .lastName(lastName)
                    .ssn(ssn)
                    .email(email)
                    .homePhone(homePhone)
                    .cellPhone(cellPhone)
                    .workPhone(workPhone)
                    .notification_pref(notification_pref)
                    .build();
    }


    @Builder
    public Customer(String firstName, String middleName, String lastName,
                    String ssn, String email,
                    String homePhone, String cellPhone, String workPhone, boolean notification_pref) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.ssn = ssn;
        this.email = email;
        this.homePhone = homePhone;
        this.cellPhone = cellPhone;
        this.workPhone = workPhone;
        this.notification_pref = notification_pref;
    }
}
