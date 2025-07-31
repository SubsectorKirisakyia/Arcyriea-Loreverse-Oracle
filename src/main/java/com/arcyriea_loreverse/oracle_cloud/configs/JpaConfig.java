package com.arcyriea_loreverse.oracle_cloud.configs;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.boot.context.properties.ConfigurationProperties; // Import for @ConfigurationProperties

@Configuration
public class JpaConfig {
    // Since JpaProperties and EntityManagerFactory is part of autoconfig packages
    // that we excluded for manual configuration requirements we need to manually define them here
    @Bean
    @ConfigurationProperties("spring.jpa") // Load properties prefixed with 'spring.jpa'
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(
            JpaProperties jpaProperties) { // This JpaProperties bean is now guaranteed to be found

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(
                vendorAdapter,
                jpaProperties.getProperties(), // Pass the properties loaded from application.yml (e.g., general hibernate settings)
                null // persistenceUnitManager can be null for most common standalone uses
        );
        return builder;
    }
}
