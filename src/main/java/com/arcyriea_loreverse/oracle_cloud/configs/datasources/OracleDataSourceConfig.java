package com.arcyriea_loreverse.oracle_cloud.configs.datasources;

import com.arcyriea_loreverse.oracle_cloud.properties.OracleProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
        basePackages = "com.arcyriea_loreverse.oracle_cloud.oracle.repository",
        entityManagerFactoryRef = "oracleEntityManager",
        transactionManagerRef = "oracleTransactionManager"
)
@EnableConfigurationProperties(OracleProperties.class)
public class OracleDataSourceConfig {

    @Value("${custom.jpa.oracle-dialect}")
    private String oracleDialect;

    private final OracleProperties oracleProperties;

    public OracleDataSourceConfig(OracleProperties properties){
        this.oracleProperties = properties;
    }

    @Bean
    public DataSource oracleDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(oracleProperties.getUrl());
        config.setUsername(oracleProperties.getUsername());
        config.setPassword(oracleProperties.getPassword());
        config.setDriverClassName(oracleProperties.getDriverClassName());
        return new HikariDataSource(config);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean oracleEntityManager(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(oracleDataSource())
                .packages("com.arcyriea_loreverse.oracle_cloud.oracle.entity")
                .persistenceUnit("oracle")
                .properties(Map.of("hibernate.dialect", oracleDialect))
                .build();
    }

    @Bean
    public PlatformTransactionManager oracleTransactionManager(
            @Qualifier("oracleEntityManager") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}

