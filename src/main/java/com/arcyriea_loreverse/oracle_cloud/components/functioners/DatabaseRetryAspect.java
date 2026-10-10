package com.arcyriea_loreverse.oracle_cloud.components.functioners;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;

@Aspect
@Component
@Order(1)
public class DatabaseRetryAspect {

    private static final int MAX_ATTEMPTS = 3;
    private static final long BACKOFF_DELAY_MS = 3000; // 3 seconds

    @Pointcut("execution(public * com.arcyriea_loreverse.oracle_cloud.crud.services.mysql..*.*(..)) || " +
            "execution(public * com.arcyriea_loreverse.oracle_cloud.crud.services.mongo..*.*(..)) || " +
            "execution(public * com.arcyriea_loreverse.oracle_cloud.crud.services.always..*.*(..))")
    public void targetDatabaseServices() {}

    @Around("targetDatabaseServices()")
    public Object retryDatabaseOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        int attempt = 0;

        while (true) {
            try {
                attempt++;
                return joinPoint.proceed();
            } catch (DataAccessException | CannotCreateTransactionException ex) {
                if (attempt >= MAX_ATTEMPTS) {
                    System.err.printf("[DB Retry Aspect] Exceeded max attempts (%d) for %s. Propagating exception.%n",
                            MAX_ATTEMPTS, joinPoint.getSignature().toShortString());
                    throw ex;
                }

                System.err.printf("[DB Retry Aspect] Transient DB failure on attempt %d/%d for %s. Retrying in %d ms... Cause: %s%n",
                        attempt, MAX_ATTEMPTS, joinPoint.getSignature().toShortString(), BACKOFF_DELAY_MS, ex.getMessage());

                try {
                    Thread.sleep(BACKOFF_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ex;
                }
            }
        }
    }
}
