package com.example.Commerce.Aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.example.Commerce.Controllers..*)")
    public void controllerLayer() {}

    @Before("controllerLayer()")
    public void logBeforeControllerMethods(JoinPoint joinPoint) {
        log.info("Entering method: {}", joinPoint.getSignature().getName());
    }

    @After("controllerLayer()")
    public void logAfterControllerMethods(JoinPoint joinPoint) {
        log.info("Exiting method: {}", joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "controllerLayer()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.error("Exception in method: {} with message: {}", joinPoint.getSignature().getName(), error.getMessage());
    }
}
