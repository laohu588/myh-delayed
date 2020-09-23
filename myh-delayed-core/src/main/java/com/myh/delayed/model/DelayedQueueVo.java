package com.myh.delayed.model;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延时队列对象;
 * 
 * @author myh
 * @date 2019/12/27
 * @copyright copyright (c) 2019
 * @company www.duia.com
 */
public class DelayedQueueVo implements Delayed {

    private long activeTime;// 存活时长，单位：秒;
    private int queueId;// 队列消息ID;
    private long expirationTime;

    public DelayedQueueVo(long expirationTime, int queueId) {
        super();
        this.activeTime = expirationTime * 1000 + System.currentTimeMillis();
        this.queueId = queueId;
        this.expirationTime = expirationTime;

    }

    public long getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(long activeTime) {
        this.activeTime = activeTime;
    }

    public long getDelay(TimeUnit unit) {

        long d = unit.convert(this.activeTime - System.currentTimeMillis(), unit);
        return d;

    }

    /**
     * 按剩余时间排序;
     */
    @Override
    public int compareTo(Delayed o) {
        long d = (getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        if (d == 0) {
            return 0;
        } else {
            if (d < 0) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DelayedQueueVo) {
            DelayedQueueVo delayedObjectVo = (DelayedQueueVo)obj;
            return delayedObjectVo.getQueueId() == this.queueId;

        }
        return super.equals(obj);
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    @Override
    public String toString() {
        return "DelayedObjectVo [activeTime=" + activeTime + ", queueId=" + queueId + ", expirationTime="
            + expirationTime + "]";
    }

}
