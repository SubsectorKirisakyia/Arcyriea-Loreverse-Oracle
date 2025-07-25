package com.arcyriea_loreverse.oracle_cloud.utils.oracle;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WalletUtils {

    private static final String TEMP_DIR_NAME = "oracle_wallet_cached";
    private static final String[] WALLET_FILES = {
            "cwallet.sso", "ewallet.p12", "sqlnet.ora",
            "tnsnames.ora", "truststore.jks", "keystore.jks"
    };

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    private static Path getPersistentWalletPath() throws IOException {
        // Use a fixed temp path across restarts
        Path baseTempDir = Paths.get(System.getProperty("java.io.tmpdir"));
        Path walletDir = baseTempDir.resolve(TEMP_DIR_NAME);

        if (!Files.exists(walletDir)) {
            Files.createDirectories(walletDir);
        }

        return walletDir;
    }

    private static void extractWalletIfMissing(Path targetDir) throws IOException {
        ClassLoader classLoader = WalletUtils.class.getClassLoader();

        for (String fileName : WALLET_FILES) {
            Path filePath = targetDir.resolve(fileName);
            if (Files.notExists(filePath)) {
                try (InputStream in = classLoader.getResourceAsStream("oracle_wallet/" + fileName)) {
                    if (in != null) {
                        Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        System.err.println("Missing resource: " + fileName);
                    }
                }
            }
        }
    }

    public static void initWalletPath() {
        if (initialized.get()) return;

        synchronized (WalletUtils.class) {
            if (initialized.get()) return;
            try {
                Path walletPath = getPersistentWalletPath();
                extractWalletIfMissing(walletPath);

                String walletAbsolutePath = walletPath.toAbsolutePath().toString();
                System.setProperty("oracle.net.tns_admin", walletAbsolutePath);
                System.setProperty("oracle.net.wallet_location", walletAbsolutePath);

                System.out.println("Oracle wallet available at: " + walletAbsolutePath);
                initialized.set(true);
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialize Oracle wallet", e);
            }
        }
    }
}
