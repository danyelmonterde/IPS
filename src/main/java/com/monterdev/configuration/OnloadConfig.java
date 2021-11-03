package com.monterdev.configuration;

import com.monterdev.model.*;
import com.monterdev.repository.CategoryRepository;
import com.monterdev.repository.ItemsRepository;
import com.monterdev.repository.SubCategoryDetailsRepository;
import com.monterdev.repository.SubCategoryHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class OnloadConfig {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryDetailsRepository subCategoryDetailsRepository;

    @Autowired
    private SubCategoryHeaderRepository subCategoryHeaderRepository;

    @Autowired
    private ItemsRepository itemsRepository;

    @Bean
    public RequisitionIssueSlip requisitionIssueSlip() {
        return new RequisitionIssueSlip();
    }

    @Bean
    public Item item() {
        //set default category for loading
        return new Item().builder().item_name("").build();
    }

    @Bean
    public Iterable<ItemCategory> category() {
        return categoryRepository.findAll();
    }

    @Bean
    public Iterable<ItemSubCategoryHeader> subCategoryHeader() {
        return subCategoryHeaderRepository.findAll();
    }

    @Bean
    public Iterable<ItemSubCategoryDetail> subCategoryDetail() {
        return subCategoryDetailsRepository.findAll();
    }

    private ScheduledExecutorService timer;

    @Bean
    @Qualifier("itemLists")
    public List<Item> itemLists() {
        Iterable<Item> initialItemList = itemsRepository.findAll();
        List<Item> itemLists = new ArrayList<>();
        initialItemList.forEach(itemLists::add);
        return itemLists;
    }

    @Bean(name = "mysqlDatasource")
    public DataSource dataSource() {
        System.out.println("datasource creation");
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url("jdbc:mysql://localhost:3306/iqwddbv2?&serverTimezone=UTC");
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("root");
        return dataSourceBuilder.build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[]{"com.monterdev.model"});

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");

        return properties;
    }

    @Bean
    public Customer customer() {
        return new Customer();
    }

    @Bean
    public SelectedRisTemplate selectedRISTemplate() {
        return new SelectedRisTemplate();
    }

    @Bean
    public List<RisTypeFields> risTypeFields() {
        return new ArrayList<>();
    }
}
