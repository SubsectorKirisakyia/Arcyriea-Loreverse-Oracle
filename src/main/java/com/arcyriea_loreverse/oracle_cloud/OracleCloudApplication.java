package com.arcyriea_loreverse.oracle_cloud;

import com.arcyriea_loreverse.oracle_cloud.properties.TwitchProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;

import java.io.IOException;

@EnableRetry
@SpringBootApplication
@EnableConfigurationProperties(TwitchProperties.class)
public class OracleCloudApplication {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(OracleCloudApplication.class, args);
	}
}
