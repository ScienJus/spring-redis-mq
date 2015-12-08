package com.scienjus.queue.consumer.worker;


import com.scienjus.queue.consumer.Consumer;
import com.scienjus.queue.consumer.domain.ConsumeHandlerMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public class ConsumeWorker {

    private ConsumeHandlerMethod consumeHandlerMethod;

    private Consumer consumer;

    public ConsumeWorker(ConsumeHandlerMethod consumeHandlerMethod, Consumer consumer) {
        this.consumeHandlerMethod = consumeHandlerMethod;
        this.consumer = consumer;
    }

    public void invoke() {
        Object bean = consumeHandlerMethod.getBean();
        Method method = consumeHandlerMethod.getMethod();
        String topic = consumeHandlerMethod.getTopic();
        //获取消息
        Object message;
        while ((message = consumer.getMessage(topic)) != null) {
            try {
                method.invoke(bean, message);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
