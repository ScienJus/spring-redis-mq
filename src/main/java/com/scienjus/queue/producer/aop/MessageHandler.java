package com.scienjus.queue.producer.aop;

import com.scienjus.queue.producer.Producer;
import com.scienjus.queue.producer.annotation.Topic;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
@Aspect
public class MessageHandler {

    private Producer producer;

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    @Around("execution(@com.scienjus.queue.producer.annotation.Topic * *(..)) && @annotation(topic)")
    public Object around(ProceedingJoinPoint point, Topic topic) {
        Object result = null;
        try {
            result = point.proceed();
            producer.sendMessage(topic.value(), result);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return result;
    }

}
