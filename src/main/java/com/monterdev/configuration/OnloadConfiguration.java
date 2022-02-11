package com.monterdev.configuration;

import com.monterdev.model.*;
import com.monterdev.repository.*;
import com.monterdev.util.StageLoader;
import javafx.stage.Stage;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
public class OnloadConfiguration {

    @Bean
    public User user(){
        return new User();
    }

    @Bean
    public StageLoader stageLoader() {
        return new StageLoader();
    }


    @Bean(name = "mysqlDatasource")
    public DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url("jdbc:mysql://localhost:3306/iqwddbv2?&serverTimezone=UTC");
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("root");
        return dataSourceBuilder.build();
    }

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private InventoryTypeRepository inventoryTypeRepository;

    @Autowired
    private ReportNamesRepository reportNamesRepository;

    @Autowired
    private RisTypeRepository risTypeRepository;

    @Autowired
    private RisTypeNamesRepository risTypeNamesRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Bean
    public RequisitionIssueSlip requisitionIssueSlip() {
        return new RequisitionIssueSlip();
    }

    @Bean
    public Item item() {
        //set default category for loading
        return new Item().builder().item_name("")
                .sku(0)
                .build();
    }

    @Bean
    public CapturedItem capturedItem(){
        CapturedItem capturedItem = new CapturedItem();
        capturedItem.setSku(0);
        return capturedItem;
    }


    @Bean
    @Qualifier("itemLists")
    public List<Item> itemLists() {
        Iterable<Item> initialItemList = itemsRepository.findAll();
        List<Item> itemLists = new ArrayList<>();
        initialItemList.forEach(itemLists::add);
        return itemLists;
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

    @Bean
    public List<RisTypeNames> risTemplates() {
        return risTypeNamesRepository.findAllRisTypeNames();
    }


    @Bean
    public Reports reports() {
        return new Reports();
    }

    @Bean
    public List<InventoryType> inventoryType() {
        List<InventoryType> inventoryTypeList = inventoryTypeRepository.findAllInventoryType();
        return inventoryTypeList;
    }

    @Bean
    public List<ReportNames> reportNamesList() {
        return reportNamesRepository.findAllAvailableReports();
    }


    @Bean
    public List<Unit> unitList() {
        return unitRepository.findAllItemUnits();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }


}
