package me.jongwoo.springbatchstudy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data @NoArgsConstructor @AllArgsConstructor
public class Customer2 {

    @NotNull(message = "First name is required")
    @Pattern(regexp = "[a-zA-Z]+", message = "First name must be alphabetical")
    private String firstName;

    @Size(min = 1, max = 1)
    @Pattern(regexp = "[a-zA-Z]", message = "Middle initial must be alphabetical")
    private String middleInitial;

    @NotNull(message = "Last name is required")
    @Pattern(regexp = "[a-zA-Z]+", message = "Last name must be alphabetical")
    private String lastName;

    @NotNull(message = "Address is required")
    @Pattern(regexp = "[0-9a-zA-Z\\. ]+")
    private String address;

    @NotNull(message = "City is required")
    @Pattern(regexp = "[a-zA-Z\\. ]+")
    private String city;

    @NotNull(message = "State is required")
    @Size(min = 2, max = 2)
    @Pattern(regexp = "[A-Z]{2}")
    private String state;

    @NotNull(message = "Zip is required")
    @Size(min = 5, max = 5)
    @Pattern(regexp = "\\d{5}")
    private String zip;

    public Customer2(Customer2 customer2){
        this.firstName = customer2.getFirstName();
        this.middleInitial = customer2.getMiddleInitial();
        this.lastName = customer2.getLastName();
        this.address = customer2.getAddress();
        this.city = customer2.getCity();
        this.state = customer2.getState();
        this.zip = customer2.getZip();
    }
}
