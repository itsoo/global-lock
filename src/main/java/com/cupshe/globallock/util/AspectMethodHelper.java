package com.cupshe.globallock.util;

import com.cupshe.globallock.AnnotationAttribute;
import com.cupshe.globallock.GlobalLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AspectMethodHelper
 *
 * @author zxy
 */
public class AspectMethodHelper {

    public static String[] getMethodParameterNames(ProceedingJoinPoint point) {
        return getMethodSignature(point).getParameterNames();
    }

    public static Map<String, Object> getMappingOfParameters(ProceedingJoinPoint point) {
        return getMappingOfParameters(getMethodParameterNames(point), point.getArgs());
    }

    public static Map<String, Object> getMappingOfParameters(String[] params, Object[] args) {
        Assert.isTrue(params.length == args.length, "The params does not match the values.");
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < params.length; i++) {
            result.put(params[i], args[i]);
        }

        return result;
    }

    public static AnnotationAttribute getAnnotationAttribute(ProceedingJoinPoint point) {
        GlobalLock ann = AnnotationUtils.getAnnotation(getMethodOfJoinPoint(point), GlobalLock.class);
        Assert.notNull(ann, "@GlobalLock annotation cannot be null.");
        return AnnotationAttribute.annotationAttributeBuilder()
                .setKey(ann.key())
                .setNamespace(ann.namespace())
                .setLeaseTime(ann.leaseTime())
                .setWaitTime(ann.waitTime())
                .setTimeUnit(ann.timeUnit())
                .setPolicy(ann.policy())
                .build();
    }

    private static Method getMethodOfJoinPoint(ProceedingJoinPoint point) {
        return getMethodSignature(point).getMethod();
    }

    private static MethodSignature getMethodSignature(ProceedingJoinPoint point) {
        Signature result = point.getSignature();
        Assert.isTrue(result instanceof MethodSignature, "@GlobalLock annotation can only be marked on methods.");
        return (MethodSignature) result;
    }
}
