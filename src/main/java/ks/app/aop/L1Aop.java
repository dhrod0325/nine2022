package ks.app.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;

@Component
@Aspect
public class L1Aop {
    private final Logger logger = LogManager.getLogger();

    @Around("@annotation(LogTime)")
    public Object aopLogTime(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String title = joinPoint.getTarget().getClass().getName() + "." + method.getName();

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        Object proceed = joinPoint.proceed();

        stopWatch.stop();

        joinPoint.getTarget();

        long tm = stopWatch.getTotalTimeMillis();
        String msg = "{} - 진행시간 : {}ms";

        if (tm > 100) {
            logger.warn(msg, title, tm);
        } else {
            logger.info(msg, title, tm);
        }

        return proceed; // 결과 리턴
    }
}
