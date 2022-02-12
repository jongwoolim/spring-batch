package me.jongwoo.springbatchstudy.reader;

import me.jongwoo.springbatchstudy.domain.Account;
import org.springframework.batch.item.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccountItemReader extends ItemStreamSupport implements ItemReader<Account> {

    private List<Account> accounts;
    private int curIndex;
    private String INDEX_KEY = "current.index.accounts"; // 고유 키

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

        if(curIndex== 50){
            throw new RuntimeException("This will end your execution");
        }

        if(curIndex < accounts.size()){
            account = accounts.get(curIndex);
            curIndex++;
        }

        return account;
    }

    @Override
    public void open(ExecutionContext executionContext) {
        if(executionContext.containsKey(getExecutionContextKey(INDEX_KEY))){
            int index = executionContext.getInt(getExecutionContextKey(INDEX_KEY));

            if(index == 50){
                curIndex = 51;
            }else {
                curIndex = index;
            }

        }else{
            curIndex = 0;
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
        executionContext.putInt(getExecutionContextKey(INDEX_KEY), curIndex);
    }


    @Override
    public void close() {

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
