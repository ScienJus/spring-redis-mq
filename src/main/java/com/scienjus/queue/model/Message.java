package com.scienjus.queue.model;

import com.scienjus.queue.producer.annotation.ToQueue;

import java.io.Serializable;

/**
 * @author XieEnlong
 * @date 2015/12/9.
 */
public class Message implements Serializable {

    private Object content;

    private long expireAt;

    public Message(Object content, long expireAt) {
        this.content = content;
        this.expireAt = expireAt;
    }

    public Message(Object content) {
        this(content, ToQueue.ExpireTime.NEVER_EXPIRES);
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }
}
