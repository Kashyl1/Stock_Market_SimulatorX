package com.example.backend.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAspect {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionTimeAspect.class);

    @Around("@annotation(logExecTime)")
    public Object measureMethodExecutionTime(ProceedingJoinPoint pjp, LogExecutionTime logExecTime) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed(); // wywołanie metody docelowej
        } finally {
            long end = System.currentTimeMillis();
            double seconds = (end - start) / 1000.0;
            String methodName = pjp.getSignature().toShortString();
            logger.info("{} executed in {} seconds", methodName, seconds);
        }
    }
}

