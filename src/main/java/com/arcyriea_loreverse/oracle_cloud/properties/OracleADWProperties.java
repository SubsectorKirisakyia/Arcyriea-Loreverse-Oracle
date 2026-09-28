package com.arcyriea_loreverse.oracle_cloud.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "custom.datasource.oracle-adw")
public class OracleADWProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private boolean wallet;
    private JpaProperties jpa;

    @Getter
    @Setter
    public static class JpaProperties {
        private MySQLProperties.JpaProperties.Hibernate hibernate;              // matches "hibernate:"
        private boolean showSql;                  // matches "show-sql:"
        private String databasePlatform;          // matches "database-platform:"

        @Getter
        @Setter
        public static class Hibernate {
            private String ddlAuto;               // matches "ddl-auto:"
        }

        public Map<String, Object> getProperties() {
            Map<String, Object> map = new HashMap<>();
            if (hibernate != null && hibernate.getDdlAuto() != null) {
                map.put("hibernate.hbm2ddl.auto", hibernate.getDdlAuto());
            }
            map.put("hibernate.show_sql", String.valueOf(showSql));
            if (databasePlatform != null) {
                map.put("hibernate.dialect", databasePlatform);
            }
            return map;
        }
    }
}

