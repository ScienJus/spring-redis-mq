package com.scienjus.queue.consumer.annotation;

import java.lang.annotation.*;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnMessage {

    String value();

    int maxRetryTimes() default RetryTimes.USE_DEFAULT_MAX_RETRY_TIMES;

    class RetryTimes {
        public static final int USE_DEFAULT_MAX_RETRY_TIMES = -1;
    }
}
