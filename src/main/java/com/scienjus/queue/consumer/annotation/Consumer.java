package com.scienjus.queue.consumer.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Consumer {
}
