package com.cupshe.globallock;

import org.redisson.api.RLock;

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
            if (leaseTime == NON_TIMEOUT) {
                lock.lock();
            } else {
                lock.lock(leaseTime, timeUnit);
            }

            return true;
        }
    };

    /*** non-timeout */
    public static final long NON_TIMEOUT = -1L;

    abstract boolean tryOrLock(RLock lock, long waitTime, long leaseTime, TimeUnit timeUnit);
}
