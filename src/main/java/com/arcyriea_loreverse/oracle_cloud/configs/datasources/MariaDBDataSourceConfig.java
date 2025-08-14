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
import org.springframework.transaction.annotation.EnableTransactionManagement; // Import this

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.arcyriea_loreverse.oracle_cloud.mariadb.repository",
        entityManagerFactoryRef = "mariadbEntityManager",
        transactionManagerRef = "mariadbTransactionManager"
)
@EnableConfigurationProperties(MariaDBProperties.class)
public class MariaDBDataSourceConfig {

    private final MariaDBProperties mariaDBProperties;

    public MariaDBDataSourceConfig(MariaDBProperties mariaDBProperties) {
        this.mariaDBProperties = mariaDBProperties;
    }

    @Bean
    public DataSource mariadbDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(mariaDBProperties.getUrl());
        config.setUsername(mariaDBProperties.getUsername());
        config.setPassword(mariaDBProperties.getPassword());
        config.setDriverClassName(mariaDBProperties.getDriverClassName());

        //For Heliohost
        config.setMaximumPoolSize(5);     // Keep it tiny
        config.setMinimumIdle(0);         // Don't keep idle connections
        config.setIdleTimeout(30000);     // 30s before closing idle conns
        config.setMaxLifetime(60000);     // 1min before recycling
        config.setConnectionTimeout(5000);// Fail fast if DB is busy
        return new HikariDataSource(config);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean mariadbEntityManager(
            EntityManagerFactoryBuilder builder) {
        Map<String, Object> jpaProperties = new HashMap<>(mariaDBProperties.getJpa().getProperties());

        return builder
                .dataSource(mariadbDataSource()) // Referencing the DataSource bean
                .packages("com.arcyriea_loreverse.oracle_cloud.mariadb.entity")
                .persistenceUnit("mariadb") // Unique persistence unit name
                .properties(jpaProperties) // Pass the map of JPA properties
                .build();
    }

    @Bean
    public PlatformTransactionManager mariadbTransactionManager(
            @Qualifier("mariadbEntityManager") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
