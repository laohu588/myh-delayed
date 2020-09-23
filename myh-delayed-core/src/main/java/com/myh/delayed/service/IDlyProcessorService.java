package com.myh.delayed.service;

/**
 * 消息到期，处理相关的业务接口;<br>
 * 针对业务的接口;
 * 
 * @author myh
 * @date 2019/12/27
 * @copyright copyright (c) 2019
 */
public interface IDlyProcessorService {

    /**
     * 当延时队列消息到期，处理相关业务<br>
     * 注意：如果服务是集群部署，要处理的业务记得加锁，保证节点只有一个能执行;相反，不用加锁;
     *
     * @param id 消息主键ID;
     */
    public void process(long id);

    /**
     * 应用于以下情况：
     * 1、当消息到期，会触发监听重新初始化延时列表数据(解决服务集群部署，保证各节点延时队列数据一致。)
     * 2、当项目启动，初始化延时队列数据;
     */
    public void bootInit();

}
