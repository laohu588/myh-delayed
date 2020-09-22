package com.myh.delayed.service;

/**
 * 消息到期，处理相关的业务接口;<br>
 * 针对业务的接口;
 * 
 * @author myh
 * @date 2019/12/27
 * @copyright copyright (c) 2019
 * @company www.duia.com
 */
public interface IDlyProcessorService {

    /**
     * 消息到期，处理业务;<br>
     * 要处理的业务记得加锁，解决分布式幂等性问题;
     * 
     * @param id 消息主键ID;
     */
    public void process(long id);

    /**
     * 项目启动，初始化延时队列;
     */
    public void boot();

}
