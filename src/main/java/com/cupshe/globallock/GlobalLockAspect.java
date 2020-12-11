package com.cupshe.globallock;

import com.cupshe.globallock.config.RedissonConfig;
import com.cupshe.globallock.exception.TryLockTimeoutException;
import com.cupshe.globallock.util.AspectMethodHelper;
import com.cupshe.globallock.util.KeyProcessor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * GlobalLockAspect
 *
 * @author zxy
 */
@Slf4j
@Aspect
@ConditionalOnBean(RedissonClient.class)
@AutoConfigureAfter(RedissonConfig.class)
public class GlobalLockAspect {

    private final RedissonClient redissonClient;

    public GlobalLockAspect(@Qualifier("redissonClient") RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(com.cupshe.globallock.GlobalLock)")
    public Object around(@NonNull ProceedingJoinPoint point) throws Throwable {
        AnnotationAttribute attr = AspectMethodHelper.getAnnotationAttribute(point);
        RLock lock = redissonClient.getLock(getRedisLockKey(point, attr));
        boolean locked = attr.policy.tryOrLock(lock, attr.waitTime, attr.leaseTime, attr.timeUnit);

        try {
            if (locked) {
                return point.proceed(point.getArgs());
            }

            throw new TryLockTimeoutException();
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private String getRedisLockKey(ProceedingJoinPoint point, AnnotationAttribute attr) {
        Map<String, Object> params = AspectMethodHelper.getMappingOfParameters(point);
        String result = KeyProcessor.getLockKey(attr.namespace, attr.key, params);
        log.info("The key of the global-lock is: [{}]", result);
        return result;
    }
}
