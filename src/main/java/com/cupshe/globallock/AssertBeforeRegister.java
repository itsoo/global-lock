package com.cupshe.globallock;

import lombok.SneakyThrows;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static com.cupshe.globallock.LockedPolicy.NON_TIMEOUT;
import static com.cupshe.globallock.util.KeyProcessor.EXPRESSION_DELIMITER_PREFIX;
import static com.cupshe.globallock.util.KeyProcessor.EXPRESSION_DELIMITER_SUFFIX;

/**
 * AssertBeforeRegister
 *
 * @author zxy
 */
public class AssertBeforeRegister implements BeanPostProcessor {

    @Override
    @SneakyThrows
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) {
        if (isIgnoredBean(beanName, bean)) {
            return bean;
        }

        for (Method method : ReflectionUtils.getAllDeclaredMethods(bean.getClass())) {
            GlobalLock ann = AnnotationUtils.getAnnotation(method, GlobalLock.class);
            if (ann != null) {
                assertTrue(Modifier.isPublic(method.getModifiers()), method, "access modifier must be public.");
                assertTrue(ann.leaseTime() >= NON_TIMEOUT, method, "'leaseTime' must ranged [-1, Long.MAX_VALUE].");
                assertKeyFormats(ann.key(), method);
            }
        }

        return bean;
    }

    private boolean isIgnoredBean(String beanName, Object bean) {
        return beanName.startsWith("org.springframework")
                || beanName.contains("&")
                || bean instanceof BeanFactory
                || bean instanceof FactoryBean;
    }

    private void assertTrue(boolean expr, Method method, String message) {
        Assert.isTrue(expr, method.toGenericString() + ": " + message);
    }

    private void assertKeyFormats(String key, Method method) {
        int i = StringUtils.countOccurrencesOf(key, EXPRESSION_DELIMITER_PREFIX);
        int j = StringUtils.countOccurrencesOf(key, EXPRESSION_DELIMITER_SUFFIX);
        assertTrue(i == j, method, "'key' format error, please check '" +
                EXPRESSION_DELIMITER_PREFIX + "' or '" +
                EXPRESSION_DELIMITER_SUFFIX + "'.");
    }
}
