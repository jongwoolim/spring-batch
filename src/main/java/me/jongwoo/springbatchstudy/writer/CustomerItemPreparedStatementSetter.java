package me.jongwoo.springbatchstudy.writer;

import me.jongwoo.springbatchstudy.domain.Customer3;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerItemPreparedStatementSetter implements ItemPreparedStatementSetter<Customer3> {

    @Override
    public void setValues(Customer3 customer3, PreparedStatement ps) throws SQLException {

        // 아이템에서 추출한 적절한 값을 일반적인 PreparedStatement API 사용
        ps.setString(1, customer3.getFirstName());
        ps.setString(2, customer3.getMiddleInitial());
        ps.setString(3, customer3.getLastName());
        ps.setString(4, customer3.getAddress());
        ps.setString(5, customer3.getCity());
        ps.setString(6, customer3.getState());
        ps.setString(7, customer3.getZip());
    }
}
