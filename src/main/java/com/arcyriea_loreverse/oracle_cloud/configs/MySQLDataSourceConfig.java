package com.arcyriea_loreverse.oracle_cloud.configs;

import com.arcyriea_loreverse.oracle_cloud.properties.MySQLProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.arcyriea_loreverse.oracle_cloud.mysql.repository",
        entityManagerFactoryRef = "mysqlEntityManager",
        transactionManagerRef = "mysqlTransactionManager"
)
@EnableConfigurationProperties(MySQLProperties.class)
public class MySQLDataSourceConfig {

    @Value("${custom.jpa.mysql-dialect}")
    private String mysqlDialect;

    private final MySQLProperties mysqlProperties;

    public MySQLDataSourceConfig(MySQLProperties mysqlProperties) {
        this.mysqlProperties = mysqlProperties;
    }

    @Primary
    @Bean
    public DataSource mysqlDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(mysqlProperties.getUrl());
        config.setUsername(mysqlProperties.getUsername());
        config.setPassword(mysqlProperties.getPassword());
        config.setDriverClassName(mysqlProperties.getDriverClassName());
        return new HikariDataSource(config);
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean mysqlEntityManager(
            EntityManagerFactoryBuilder builder) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", mysqlDialect);

        return builder
                .dataSource(mysqlDataSource())
                .packages("com.arcyriea_loreverse.oracle_cloud.mysql.entity")
                .persistenceUnit("mysql")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager mysqlTransactionManager(
            @Qualifier("mysqlEntityManager") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}

