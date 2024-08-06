package kr.or.kosa.cmsplusmain;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(kr.or.kosa.cmsplusmain.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogExecutionTime logExecutionTime = signature.getMethod().getAnnotation(LogExecutionTime.class);
        String serviceName = logExecutionTime.value();
        String methodName = signature.getMethod().getName();

        int iterations = 10;
        long totalDuration = 0;
        long[] durations = new long[iterations];
        Object result = null;

        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();
            result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            durations[i] = duration;
            totalDuration += duration;
            log.info("Execution {} : {} Test #{} [{} ms]", serviceName, methodName, i + 1, duration);
        }

        long averageDuration = totalDuration / iterations;
        log.info("Completed {} : {} Average Time [{} ms]", serviceName, methodName, averageDuration);

        return result;
    }
}
