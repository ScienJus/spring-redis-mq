package com.scienjus.queue.producer.worker;

import com.scienjus.queue.model.Message;
import com.scienjus.queue.producer.Producer;
import com.scienjus.queue.producer.annotation.ToQueue;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
@Aspect
public class ProducerWorker {

    private Producer producer;

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    @Around("@annotation(toQueue)")
    public Object around(ProceedingJoinPoint point, ToQueue toQueue) {
        Object content = null;
        try {
            content = point.proceed();
            String topic = toQueue.topic();
            long expireAt =
                    toQueue.expire() == ToQueue.ExpireTime.NEVER_EXPIRES ?
                            ToQueue.ExpireTime.NEVER_EXPIRES :
                            System.currentTimeMillis() + toQueue.expire() * 1000;
            producer.sendMessage(topic, new Message(content, expireAt));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return content;
    }

}
