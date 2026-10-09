package com.arcyriea_loreverse.oracle_cloud.configs;

import io.sentry.SentryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentryConfig {

    @Bean
    public SentryOptions.BeforeSendCallback beforeSendCallback() {
        return (event, hint) -> {
            // Drop client disconnect exceptions to prevent log spam
            if (event.getThrowable() != null &&
                    event.getThrowable().getMessage() != null &&
                    event.getThrowable().getMessage().contains("Broken pipe")) {
                return null; // Returning null drops the event from Sentry
            }

            // Redact custom internal headers or keys before sending
            event.removeExtra("internal_secret_key");

            return event;
        };
    }
}
