package me.jongwoo.springbatchstudy.batch;

import me.jongwoo.springbatchstudy.domain.Account;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRowMapper implements RowMapper<Account> {

    @Override
    public Account mapRow(ResultSet rs, int rowNum) throws SQLException {

        Account account = new Account();
        account.setId(rs.getLong("id"));
        account.setAddress(rs.getString("address"));
        account.setCity(rs.getString("city"));
        account.setFirstName(rs.getString("firstName"));
        account.setLastName(rs.getString("lastName"));
        account.setMiddleInitial(rs.getString("middleInitial"));
        account.setState(rs.getString("state"));
        account.setZipCode(rs.getString("zipCode"));

        return account;
    }
}
