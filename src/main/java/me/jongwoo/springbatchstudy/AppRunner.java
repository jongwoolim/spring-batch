package me.jongwoo.springbatchstudy;

import me.jongwoo.springbatchstudy.domain.Customer;
import me.jongwoo.springbatchstudy.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Customer customer = Customer.createCustomer(
                "lim",
                "jong",
                "woo",
                "1111",
                "2222",
                "333",
                "Y", "whddn528@email.com", false);

        Customer savedCustomer = customerRepository.save(customer);


        System.out.println("=============");
        System.out.println(savedCustomer);
        System.out.println("=============");


    }


}
