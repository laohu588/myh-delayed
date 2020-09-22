package com.myh.delayed.config;

import com.myh.delayed.service.IDlyProcessorService;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class DelayedConfig {

    @Resource(name = "queueZkClient")
    private ZkClient zkClient;

    private static final String zkListenerKey = "myh_lock001";

    @Autowired
    private IDlyProcessorService dlyProcessorService;

    @Bean("delayedQueue")
    public DelayedQueueConfig delayedQueue() {
        DelayedQueueConfig config = new DelayedQueueConfig();
        config.setDlyProcessorService(dlyProcessorService);//处理消息到期的业务类接口;
        config.setZkClient(zkClient);//建立zkclient连接的对象;
        config.setZkListenerKey(zkListenerKey);//当消息队列发生变量，zk监听的节点名称,请以"/"为前缀;
        return config;
    }

}