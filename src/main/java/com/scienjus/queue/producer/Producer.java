package com.scienjus.queue.producer;

import com.scienjus.queue.model.Message;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public interface Producer {

    void sendMessage(String topic, Message message);
}
