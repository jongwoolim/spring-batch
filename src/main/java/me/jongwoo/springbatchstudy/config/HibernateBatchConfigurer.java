package me.jongwoo.springbatchstudy.config;

import org.hibernate.SessionFactory;
import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class HibernateBatchConfigurer extends DefaultBatchConfigurer {

    private DataSource dataSource;
    private SessionFactory sessionFactory;
    private PlatformTransactionManager transactionManager;
    private JobRepository jobRepository;
    private JobLauncher jobLauncher;
    private JobExplorer jobExplorer;

    public HibernateBatchConfigurer(DataSource dataSource, EntityManagerFactory entityManagerFactory) {
        super(dataSource);
        this.dataSource = dataSource;
        this.sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
    }

    @PostConstruct
    public void init(){
        try{
            final HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager(this.sessionFactory);
            hibernateTransactionManager.afterPropertiesSet();
            this.transactionManager = hibernateTransactionManager;

            this.jobRepository = createJobRepository();
            this.jobExplorer = createJobExplorer();
            this.jobLauncher = createJobLauncher();
        }catch (Exception e){
            throw new BatchConfigurationException(e);
        }

    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }
}
