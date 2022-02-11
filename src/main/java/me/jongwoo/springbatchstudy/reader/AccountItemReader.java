package me.jongwoo.springbatchstudy.reader;

import me.jongwoo.springbatchstudy.domain.Account;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccountItemReader implements ItemReader<Account> {

    private List<Account> accounts;
    private int curIndex;

    private String [] firstNames = {"Michael", "Warren", "Ann", "Terrence", "Erica", "Laura", "Steve", "Larry"};
    private String middleInitial = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String [] lastNames = {"Gates", "Darrow", "Donnelly", "Jobs", "Buffett", "Ellison", "Obama"};
    private String [] streets = {"4th Street", "Wall Street", "Fifth Avenue", "Mt. Lee Drive", "Jeopardy Lane",
                                "Infinite Loop Drive", "Farnam Street", "Isabella Ave", "S. Greenwood Ave"};
    private String [] cities = {"Chicago", "New York", "Hollywood", "Aurora", "Omaha", "Atherton"};
    private String [] states = {"IL", "NY", "CA", "NE"};

    private Random generator = new Random();

    public AccountItemReader() {
        curIndex = 0;

        accounts = new ArrayList<>();

        for(int i = 0; i < 100; i++) {
            accounts.add(buildCustomer());
        }
    }


    @Override
    public Account read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Account account = null;

        if(curIndex < accounts.size()){
            account = accounts.get(curIndex);
            curIndex++;
        }

        return account;
    }

    private Account buildCustomer() {
        Account account = new Account();

        account.setId((long) generator.nextInt(Integer.MAX_VALUE));
        account.setFirstName(
                firstNames[generator.nextInt(firstNames.length - 1)]);
        account.setMiddleInitial(
                String.valueOf(middleInitial.charAt(
                        generator.nextInt(middleInitial.length() - 1))));
        account.setLastName(
                lastNames[generator.nextInt(lastNames.length - 1)]);
        account.setAddress(generator.nextInt(9999) + " " +
                streets[generator.nextInt(streets.length - 1)]);
        account.setCity(cities[generator.nextInt(cities.length - 1)]);
        account.setState(states[generator.nextInt(states.length - 1)]);
        account.setZipCode(String.valueOf(generator.nextInt(99999)));

        return account;
    }
}
