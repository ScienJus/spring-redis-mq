package com.scienjus.queue.consumer.domain;

import java.lang.reflect.Method;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public class ConsumeHandlerMethod {

    private String topic;

    private Method method;

    private Object bean;

    public ConsumeHandlerMethod(String topic, Method method, Object bean) {
        this.topic = topic;
        this.method = method;
        this.bean = bean;
    }

    public String getTopic() {
        return topic;
    }

    public Method getMethod() {
        return method;
    }

    public Object getBean() {
        return bean;
    }
}
