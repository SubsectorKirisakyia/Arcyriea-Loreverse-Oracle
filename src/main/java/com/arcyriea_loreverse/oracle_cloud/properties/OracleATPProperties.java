package com.arcyriea_loreverse.oracle_cloud.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "custom.datasource.oracle-atp")
public class OracleATPProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private boolean wallet;
    private JpaProperties jpa;
}

