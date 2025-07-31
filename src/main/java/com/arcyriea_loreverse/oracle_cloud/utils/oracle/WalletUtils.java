package com.arcyriea_loreverse.oracle_cloud.utils.oracle;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WalletUtils {

    private static final String TEMP_DIR_NAME = "oracle_wallet_cached";
    private static final String[] WALLET_FILES = {
            "cwallet.sso", "ewallet.p12", "sqlnet.ora", "ewallet.pem",
            "tnsnames.ora", "truststore.jks", "keystore.jks", "ojdbc.properties"
    };

    private static final Map<String, Path> walletPaths = new ConcurrentHashMap<>();

    /**
     * Initializes and extracts a named wallet directory (e.g., "oracle_wallet_atp").
     *
     * @param walletResourceDir The folder name inside `resources/`, e.g. `oracle_wallet_atp`
     * @return Path to the extracted wallet directory
     */
    public static Path initWallet(String walletResourceDir) {
        if (walletPaths.containsKey(walletResourceDir)) {
            return walletPaths.get(walletResourceDir);
        }

        synchronized (WalletUtils.class) {
            if (walletPaths.containsKey(walletResourceDir)) {
                return walletPaths.get(walletResourceDir);
            }

            try {
                Path baseTempDir = Paths.get(System.getProperty("java.io.tmpdir"));
                Path walletDir = baseTempDir.resolve(TEMP_DIR_NAME).resolve(walletResourceDir);

                if (!Files.exists(walletDir)) {
                    Files.createDirectories(walletDir);
                }

                extractWallet(walletResourceDir, walletDir);

                walletPaths.put(walletResourceDir, walletDir);
                System.out.println("Oracle wallet extracted to: " + walletDir.toAbsolutePath());

                return walletDir;
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialize Oracle wallet: " + walletResourceDir, e);
            }
        }
    }

    private static void extractWallet(String resourceFolder, Path targetDir) throws IOException {
        ClassLoader classLoader = WalletUtils.class.getClassLoader();

        for (String fileName : WALLET_FILES) {
            Path filePath = targetDir.resolve(fileName);
            if (Files.notExists(filePath)) {
                String resourcePath = resourceFolder + "/" + fileName;
                try (InputStream in = classLoader.getResourceAsStream(resourcePath)) {
                    if (in != null) {
                        Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        System.err.println("Missing wallet file in resources: " + resourcePath);
                    }
                }
            }
        }
    }
}
