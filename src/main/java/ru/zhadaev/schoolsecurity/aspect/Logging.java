package ru.zhadaev.schoolsecurity.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.valueOf;

@Aspect
@Component
public class Logging {
    private final ConcurrentHashMap<String, Logger> loggerMap = new ConcurrentHashMap<>();

    private Logger logger(String fqName) {
        return loggerMap.computeIfAbsent(fqName, LoggerFactory::getLogger);
    }

    @Pointcut("within(ru.zhadaev..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void apiPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)")
    public void blAndDaoPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Around("applicationPackagePointcut() && apiPointcut()")
    public Object logAroundApi(ProceedingJoinPoint joinPoint) throws Throwable {
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
        if (logger(declaringTypeName).isInfoEnabled()) {
            logger(declaringTypeName).info("Enter: {}() with argument[s] = {}", joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        }
        try {
            long start = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            logger(declaringTypeName).info("Exit: {}() with result = {}. Execution time : {} ms", joinPoint.getSignature().getName(), result, elapsedTime);
            return result;
        } catch (IllegalArgumentException e) {
            logger(declaringTypeName).error("Illegal argument: {} in {}()", Arrays.toString(joinPoint.getArgs()), joinPoint.getSignature().getName());
            throw e;
        }
    }

    @Around("applicationPackagePointcut() && blAndDaoPointcut()")
    public Object logAroundOther(ProceedingJoinPoint joinPoint) throws Throwable {
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
        if (logger(declaringTypeName).isDebugEnabled()) {
            logger(declaringTypeName).debug("Enter: {}() with argument[s] = {}", joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        }
        try {
            long start = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            logger(declaringTypeName).debug("Exit: {}() with result = {}. Execution time : {} ms", joinPoint.getSignature().getName(), result, elapsedTime);
            return result;
        } catch (IllegalArgumentException e) {
            logger(declaringTypeName).error("Illegal argument: {} in {}()", Arrays.toString(joinPoint.getArgs()), joinPoint.getSignature().getName());
            throw e;
        }
    }

    @AfterThrowing(pointcut = "applicationPackagePointcut() && (apiPointcut() || blAndDaoPointcut())", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        String fqName = joinPoint.getSignature().getDeclaringTypeName();
        if (logger(fqName).isTraceEnabled()) {
            logger(fqName).error("Exception in {}(): {}. Cause: {}", joinPoint.getSignature().getName(), valueOf(e), valueOf(e.getCause()), e);
        } else if (logger(fqName).isDebugEnabled()) {
            logger(fqName).error("Exception in {}(): {}. Cause: {}", joinPoint.getSignature().getName(), valueOf(e), valueOf(e.getCause()));
        }
    }
}
