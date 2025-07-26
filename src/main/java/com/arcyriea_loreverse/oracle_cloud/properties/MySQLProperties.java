package com.arcyriea_loreverse.oracle_cloud.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "custom.datasource.mysql")
public class MySQLProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private JpaProperties jpa;
}

