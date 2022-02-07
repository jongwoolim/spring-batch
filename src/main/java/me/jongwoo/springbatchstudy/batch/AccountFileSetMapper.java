package me.jongwoo.springbatchstudy.batch;

import me.jongwoo.springbatchstudy.domain.Account;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class AccountFileSetMapper implements FieldSetMapper<Account> {

    @Override
    public Account mapFieldSet(FieldSet fieldSet) throws BindException {

        Account account = new Account();
        account.setAddress(fieldSet.readString("addressNumber") + fieldSet.readString("street"));
        account.setCity(fieldSet.readString("city"));
        account.setFirstName(fieldSet.readString("firstName"));
        account.setLastName(fieldSet.readString("lastName"));
        account.setMiddleInitial(fieldSet.readString("middleInitial"));
        account.setState(fieldSet.readString("state"));
        account.setZipCode(fieldSet.readString("zipCode"));
        return account;
    }
}
