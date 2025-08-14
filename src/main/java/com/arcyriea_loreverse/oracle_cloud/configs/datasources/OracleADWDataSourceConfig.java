package com.arcyriea_loreverse.oracle_cloud.configs.datasources;

import com.arcyriea_loreverse.oracle_cloud.properties.OracleADWProperties;
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
        basePackages = "com.arcyriea_loreverse.oracle_cloud.oracle_adw.repository",
        entityManagerFactoryRef = "oracleADWEntityManager",
        transactionManagerRef = "oracleADWTransactionManager"
)
@EnableConfigurationProperties(OracleADWProperties.class)
public class OracleADWDataSourceConfig {

    private final OracleADWProperties oracleProperties;

    public OracleADWDataSourceConfig(OracleADWProperties properties){
        this.oracleProperties = properties;
    }

    @Bean
    public DataSource oracleADWDataSource() {
        Path adwWalletPath = oracleProperties.isWallet() ? WalletUtils.initWallet("oracle_wallet/adw/") : null;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(oracleProperties.getUrl() + (adwWalletPath != null ? "?TNS_ADMIN=" + adwWalletPath.toAbsolutePath().toString().replace("\\", "/") : ""));
        config.setUsername(oracleProperties.getUsername());
        config.setPassword(oracleProperties.getPassword());
        config.setDriverClassName(oracleProperties.getDriverClassName());
        return new HikariDataSource(config);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean oracleADWEntityManager(
            EntityManagerFactoryBuilder builder) {
        Map<String, Object> jpaProperties = new HashMap<>(oracleProperties.getJpa().getProperties());

        return builder
                .dataSource(oracleADWDataSource())
                .packages("com.arcyriea_loreverse.oracle_cloud.oracle_adw.entity")
                .persistenceUnit("oracle-adw")
                .properties(jpaProperties)
                .build();
    }

    @Bean
    public PlatformTransactionManager oracleADWTransactionManager(
            @Qualifier("oracleADWEntityManager") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}

