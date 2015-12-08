package com.scienjus.queue.producer;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public interface Producer {

    void sendMessage(String topic, Object message);
}
