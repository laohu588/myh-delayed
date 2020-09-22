package com.myh.delayed.model;

/**
 * 延时处理的业务对象.
 * 
 * @author myh
 * @date 2019/12/27
 * @copyright copyright (c) 2019
 * @company www.duia.com
 */
public class DelayedObject {

    private int id;// 业务对象id;
    private long delayTime;// 延时的时长,单位：秒;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

}
