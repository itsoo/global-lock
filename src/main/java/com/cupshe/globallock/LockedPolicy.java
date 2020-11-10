package com.cupshe.globallock;

import org.redisson.api.RLock;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * LockedPolicy
 *
 * @author zxy
 */
public enum LockedPolicy {

    /**
     * try wait policy
     */
    TRY_WAIT {
        @Override
        public boolean tryOrLock(RLock lock, long waitTime, long leaseTime, TimeUnit timeUnit) {
            checkLeaseTimeValidity(leaseTime);
            try {
                if (leaseTime == NON_TIMEOUT) {
                    return lock.tryLock(waitTime, timeUnit);
                }

                return lock.tryLock(waitTime, leaseTime, timeUnit);
            } catch (InterruptedException e) {
                return false;
            }
        }
    },

    /**
     * blocking policy
     */
    BLOCKING {
        @Override
        public boolean tryOrLock(RLock lock, long waitTime, long leaseTime, TimeUnit timeUnit) {
            checkLeaseTimeValidity(leaseTime);
            if (leaseTime == NON_TIMEOUT) {
                lock.lock();
            } else {
                lock.lock(leaseTime, timeUnit);
            }

            return true;
        }
    };

    abstract boolean tryOrLock(RLock lock, long waitTime, long leaseTime, TimeUnit timeUnit);

    /*** timeout */
    private static final long NON_TIMEOUT = -1L;

    private static void checkLeaseTimeValidity(long leaseTime) {
        Assert.isTrue(leaseTime >= NON_TIMEOUT, "Valid range of 'leaseTime' [-1, Long.MAX_VALUE].");
    }
}
