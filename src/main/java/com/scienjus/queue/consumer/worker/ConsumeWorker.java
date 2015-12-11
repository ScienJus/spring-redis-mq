package com.scienjus.queue.consumer.worker;


import com.scienjus.queue.consumer.Consumer;
import com.scienjus.queue.consumer.model.ConsumeHandlerMethod;
import com.scienjus.queue.model.Message;
import com.scienjus.queue.producer.annotation.ToQueue;

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
        Message message;
        while ((message = consumer.getMessage(topic)) != null) {
            try {
                if (message.getExpireAt() != ToQueue.ExpireTime.NEVER_EXPIRES && message.getExpireAt() < System.currentTimeMillis()) {
                    //说明这是一个过期任务，记录日志后丢弃掉
                    continue;
                }
                if (method.getReturnType().isAssignableFrom(Boolean.TYPE)) {
                    if (!((boolean) method.invoke(bean, message.getContent()))) {
                        //如果消息执行失败，重试
                        consumer.retry(topic, message);
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
