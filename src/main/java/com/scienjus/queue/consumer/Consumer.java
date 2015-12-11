package com.scienjus.queue.consumer;

import com.scienjus.queue.model.Message;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public interface Consumer {

    Message getMessage(String topic);

    void retry(String topic, Message message);

}
