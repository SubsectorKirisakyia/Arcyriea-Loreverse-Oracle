package com.arcyriea_loreverse.oracle_cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

import java.io.IOException;

@EnableRetry
@SpringBootApplication
public class OracleCloudApplication {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(OracleCloudApplication.class, args);
	}
}
