package com.scienjus.queue.consumer.worker;


import com.scienjus.queue.consumer.Consumer;
import com.scienjus.queue.consumer.domain.ConsumeHandlerMethod;
import com.scienjus.queue.util.Message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public class ConsumeWorker {

    private ConsumeHandlerMethod consumeHandlerMethod;

    private Consumer consumer;

    private int maxRetryTimes;

    public ConsumeWorker(ConsumeHandlerMethod consumeHandlerMethod, Consumer consumer, int maxRetryTimes) {
        this.consumeHandlerMethod = consumeHandlerMethod;
        this.consumer = consumer;
        this.maxRetryTimes = maxRetryTimes;
    }

    public void invoke() {
        Object bean = consumeHandlerMethod.getBean();
        Method method = consumeHandlerMethod.getMethod();
        String topic = consumeHandlerMethod.getTopic();
        //获取消息
        Message message;
        while ((message = consumer.getMessage(topic)) != null) {
            try {
                if (method.getReturnType().isAssignableFrom(Boolean.TYPE)) {
                    boolean isSuccess = (boolean) method.invoke(bean, message.getContent());
                    if (!isSuccess) {
                        //记录日志
                        if (message.getFailureTimes() < maxRetryTimes) {
                            consumer.retry(topic, message);
                        }
                    }
                } else {
                    method.invoke(bean, message);
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
