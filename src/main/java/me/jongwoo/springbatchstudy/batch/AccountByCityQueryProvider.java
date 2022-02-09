package me.jongwoo.springbatchstudy.batch;

import org.springframework.batch.item.database.orm.AbstractJpaQueryProvider;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class AccountByCityQueryProvider extends AbstractJpaQueryProvider {

    private String cityName;

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public Query createQuery() {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery("select a " +
                                            "from Account a " +
                                            "where a.city = :city")
        .setParameter("city", cityName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(cityName,"city name is required");
    }
}
