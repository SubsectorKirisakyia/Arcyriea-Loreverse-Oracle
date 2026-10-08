package com.arcyriea_loreverse.oracle_cloud.configs;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;

@Aspect
@Component
public class DatabaseRetryAspect {

    private static final int MAX_ATTEMPTS = 3;
    private static final long DELAY_MS = 3000; // 3 seconds delay

    // Define individual pointcuts for each service package
    @Pointcut("execution(public * com.arcyriea_loreverse.oracle_cloud.crud.services.mysql..*.*(..))")
    public void mysqlServices() {}

    @Pointcut("execution(public * com.arcyriea_loreverse.oracle_cloud.crud.services.mongo..*.*(..))")
    public void mongoServices() {}

    @Pointcut("execution(public * com.arcyriea_loreverse.oracle_cloud.crud.services.always..*.*(..))")
    public void alwaysServices() {}

    // Combine them with || inside @Around
    @Around("mysqlServices() || mongoServices() || alwaysServices()")
    public Object retryDatabaseOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        int attempt = 0;
        while (true) {
            try {
                attempt++;
                return joinPoint.proceed(); // Execute service method
            } catch (DataAccessException | CannotCreateTransactionException e) {
                if (attempt >= MAX_ATTEMPTS) {
                    throw e;
                }
                System.err.println("[DB Retry Aspect] Database operation failed (attempt " + attempt + "/" + MAX_ATTEMPTS + "). Retrying in 3s...");
                Thread.sleep(DELAY_MS);
            }
        }
    }
}
