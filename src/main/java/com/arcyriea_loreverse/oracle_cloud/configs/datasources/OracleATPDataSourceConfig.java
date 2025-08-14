package com.arcyriea_loreverse.oracle_cloud.configs.datasources;

import com.arcyriea_loreverse.oracle_cloud.properties.OracleATPProperties;
import com.arcyriea_loreverse.oracle_cloud.utils.oracle.WalletUtils;
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
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "spring.datasource.oracle.enabled", havingValue="true")
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.arcyriea_loreverse.oracle_cloud.oracle_atp.repository",
        entityManagerFactoryRef = "oracleATPEntityManager",
        transactionManagerRef = "oracleATPTransactionManager"
)
@EnableConfigurationProperties(OracleATPProperties.class)
public class OracleATPDataSourceConfig {

    private final OracleATPProperties oracleProperties;

    public OracleATPDataSourceConfig(OracleATPProperties properties){
        this.oracleProperties = properties;
    }

    @Bean
    public DataSource oracleATPDataSource() {
        Path atpWalletPath = oracleProperties.isWallet() ? WalletUtils.initWallet("oracle_wallet/atp/") : null;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(oracleProperties.getUrl() + (atpWalletPath != null ? "?TNS_ADMIN=" + atpWalletPath.toAbsolutePath().toString().replace("\\", "/") : ""));
        config.setUsername(oracleProperties.getUsername());
        config.setPassword(oracleProperties.getPassword());
        config.setDriverClassName(oracleProperties.getDriverClassName());
        return new HikariDataSource(config);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean oracleATPEntityManager(
            EntityManagerFactoryBuilder builder) {
        Map<String, Object> jpaProperties = new HashMap<>(oracleProperties.getJpa().getProperties());

        return builder
                .dataSource(oracleATPDataSource())
                .packages("com.arcyriea_loreverse.oracle_cloud.oracle_atp.entity")
                .persistenceUnit("oracle-atp")
                .properties(jpaProperties)
                .build();
    }

    @Bean
    public PlatformTransactionManager oracleATPTransactionManager(
            @Qualifier("oracleATPEntityManager") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}

