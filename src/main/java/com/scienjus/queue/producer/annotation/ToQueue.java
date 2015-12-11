package com.scienjus.queue.producer.annotation;

import java.lang.annotation.*;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToQueue {

    String topic();

    int expire() default ExpireTime.NEVER_EXPIRES;

    class ExpireTime {
        public static final int NEVER_EXPIRES = -1;
    }
}
