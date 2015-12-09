package com.scienjus.queue.util;

import java.io.Serializable;

/**
 * @author XieEnlong
 * @date 2015/12/9.
 */
public class Message implements Serializable {

    private Object content;

    private int failureTimes;

    public Message(Object content, int failureTimes) {
        this.content = content;
        this.failureTimes = failureTimes;
    }

    public Message(Object content) {
        this(content, 0);
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public int getFailureTimes() {
        return failureTimes;
    }

    public void setFailureTimes(int failureTimes) {
        this.failureTimes = failureTimes;
    }
}
