package me.jongwoo.springbatchstudy;

import me.jongwoo.springbatchstudy.repository.CustomerRepository;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    ApplicationContext applicationContext;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setProvider(new BouncyCastleProvider());
        encryptor.setPoolSize(2);
        encryptor.setPassword("jw");
        encryptor.setAlgorithm("PBEWithSHA256And128BitAES-CBC-BC");

//        Resource[] resources = applicationContext.getResources("classpath:/input/customerMultiFormat*");
//        List<String> collect = Arrays.stream(resources).map(Resource::getFilename).collect(Collectors.toList());
//        collect.forEach(System.out::println);

//        Customer customer = Customer.createCustomer(
//                "lim",
//                "jong",
//                "woo",
//                "1111",
//                "2222",
//                "333",
//                "Y", "whddn528@email.com", false);
//
//        Customer savedCustomer = customerRepository.save(customer);
//
//
//        System.out.println("=============");
//        System.out.println(savedCustomer);
//        System.out.println("=============");


    }


}
