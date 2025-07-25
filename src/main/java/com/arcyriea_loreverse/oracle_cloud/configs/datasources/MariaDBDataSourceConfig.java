package com.arcyriea_loreverse.oracle_cloud.configs.datasources;

import com.arcyriea_loreverse.oracle_cloud.properties.MariaDBProperties;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.mariadb.repository",
        entityManagerFactoryRef = "mariadbEntityManager",
        transactionManagerRef = "mariadbTransactionManager"
)
@EnableConfigurationProperties(MariaDBProperties.class)
public class MariaDBDataSourceConfig {
    @Bean
    public DataSource mariadbDataSource(MariaDBProperties props) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getUrl());
        config.setUsername(props.getUsername());
        config.setPassword(props.getPassword());
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        return new HikariDataSource(config);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean mariadbEntityManager(
            EntityManagerFactoryBuilder builder) {
        Map<String, Object> props = Map.of("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
        return builder
                .dataSource(mariadbDataSource(null))
                .packages("com.example.mariadb.entity")
                .persistenceUnit("mariadb")
                .properties(props)
                .build();
    }

    @Bean
    public PlatformTransactionManager mariadbTransactionManager(
            @Qualifier("mariadbEntityManager") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}

