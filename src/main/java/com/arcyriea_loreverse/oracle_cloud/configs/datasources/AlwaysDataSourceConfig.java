package com.arcyriea_loreverse.oracle_cloud.configs.datasources;

import com.arcyriea_loreverse.oracle_cloud.properties.AlwaysProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "spring.datasource.always.enabled", havingValue="true")
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.arcyriea_loreverse.oracle_cloud.crud.repositories.always",
        entityManagerFactoryRef = "alwaysEntityManager",      // renamed
        transactionManagerRef = "alwaysTransactionManager"    // renamed
)
@EnableConfigurationProperties(AlwaysProperties.class)       // fixed
public class AlwaysDataSourceConfig {

    private final AlwaysProperties alwaysProperties;

    public AlwaysDataSourceConfig(AlwaysProperties alwaysProperties) {
        this.alwaysProperties = alwaysProperties;
    }

    @Bean
    public DataSource alwaysDataSource() {                    // renamed
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(alwaysProperties.getUrl());
        config.setUsername(alwaysProperties.getUsername());
        config.setPassword(alwaysProperties.getPassword());
        config.setDriverClassName(alwaysProperties.getDriverClassName());

        config.setMaximumPoolSize(5);     // Keep it tiny
        config.setMinimumIdle(1);         // Keep at least 1 idle connection
        config.setIdleTimeout(30000);     // 30s before closing idle conns
        config.setMaxLifetime(60000);     // 1min before recycling
        config.setConnectionTimeout(5000);// Fail fast if DB is busy

        return new HikariDataSource(config);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean alwaysEntityManager(   // renamed
                                                                         EntityManagerFactoryBuilder builder) {
        Map<String, Object> jpaProperties = new HashMap<>(alwaysProperties.getJpa().getProperties());

        return builder
                .dataSource(alwaysDataSource())
                .packages("com.arcyriea_loreverse.oracle_cloud.crud.entities.always")
                .persistenceUnit("always")                    // renamed
                .properties(jpaProperties)
                .build();
    }

    @Bean
    public PlatformTransactionManager alwaysTransactionManager(          // renamed
                                                                         @Qualifier("alwaysEntityManager") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}