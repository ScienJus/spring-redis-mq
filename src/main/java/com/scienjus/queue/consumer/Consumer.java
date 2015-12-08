package com.scienjus.queue.consumer;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public interface Consumer {

    Object getMessage(String topic);
}
