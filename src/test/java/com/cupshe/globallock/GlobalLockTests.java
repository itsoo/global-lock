package com.cupshe.globallock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class GlobalLockTests {

    @Autowired
    private TestComponent testComponent;

    private static final int THREAD_COUNT = 5;
    private static final int MAX_WAIT = 16;

    @Test
    public void tryWaitWithTimeout() throws InterruptedException {
        List<String> list = Collections.singletonList("ZhangSan");
        ExecutorService es = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            es.execute(() -> ExecuteTemplate.doExecute(testComponent::tryWaitWithTimeout, 9L, "1521", list));
        }

        TimeUnit.SECONDS.sleep(MAX_WAIT);
    }

    @Test
    public void tryWaitCompletion() throws InterruptedException {
        List<String> list = Collections.singletonList("ZhangSan");
        ExecutorService es = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            es.execute(() -> ExecuteTemplate.doExecute(testComponent::tryWaitCompletion, 9L, "1521", list));
        }

        TimeUnit.SECONDS.sleep(MAX_WAIT);
    }

    @Test
    public void blockingWithTimeout() throws InterruptedException {
        List<String> list = Collections.singletonList("ZhangSan");
        ExecutorService es = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            es.execute(() -> ExecuteTemplate.doExecute(testComponent::blockingWithTimeout, 1L, "3306", list));
        }

        TimeUnit.SECONDS.sleep(MAX_WAIT);
    }

    @Test
    public void blockingCompletion() throws InterruptedException {
        List<String> list = Collections.singletonList("ZhangSan");
        ExecutorService es = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            es.execute(() -> ExecuteTemplate.doExecute(testComponent::blockingCompletion, 1L, "3306", list));
        }

        TimeUnit.SECONDS.sleep(MAX_WAIT);
    }

    @FunctionalInterface
    private interface Execute {
        boolean execute(Long id, String port, List<String> names);
    }

    private static class ExecuteTemplate {
        private static void doExecute(Execute e, Long id, String port, List<String> names) {
            long start = System.currentTimeMillis();
            boolean res = e.execute(id, port, names);
            long end = System.currentTimeMillis();
            String thread = Thread.currentThread().getName();
            System.out.printf("current thread: %s, result: %b, time consuming: %d%n", thread, res, end - start);
        }
    }
}
