package com.cupshe.globallock;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TestComponent
 *
 * @author zxy
 */
@Component
public class TestComponent {

    @SneakyThrows
    @GlobalLock(namespace = "SYSTEM:MODULE:LOCK", key = "${id}:${port}", leaseTime = 1000L)
    public boolean tryWaitWithTimeout(Long id, String port, List<String> names) {
        return execute(id, port, names);
    }

    @SneakyThrows
    @GlobalLock(namespace = "SYSTEM:MODULE:LOCK", key = "${id}:${port}", leaseTime = -1L)
    public boolean tryWaitCompletion(Long id, String port, List<String> names) {
        return execute(id, port, names);
    }

    @SneakyThrows
    @GlobalLock(namespace = "SYSTEM:MODULE:LOCK", key = "${id}:${port}", leaseTime = 1000L,
            policy = LockedPolicy.BLOCKING)
    public boolean blockingWithTimeout(Long id, String port, List<String> names) {
        return execute(id, port, names);
    }

    @GlobalLock(namespace = "SYSTEM:MODULE:LOCK", key = "${id}:${port}", leaseTime = -1L,
            policy = LockedPolicy.BLOCKING)
    public boolean blockingCompletion(Long id, String port, List<String> names) {
        return execute(id, port, names);
    }

    @SneakyThrows
    private boolean execute(Long id, String port, List<String> names) {
        String thread = Thread.currentThread().getName();
        System.err.printf("current thread: %s, id: %d, port: %s, names: %s%n", thread, id, port, names);
        TimeUnit.SECONDS.sleep(3L);
        return true;
    }
}
