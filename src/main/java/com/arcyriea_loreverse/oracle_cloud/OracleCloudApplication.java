package com.arcyriea_loreverse.oracle_cloud;

import com.arcyriea_loreverse.oracle_cloud.oracle.WalletUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class OracleCloudApplication {

	public static void main(String[] args) throws IOException {
		WalletUtils.initWalletPath();
		SpringApplication.run(OracleCloudApplication.class, args);
	}

}
