package com.myh.delayed.service.impl;

import com.myh.delayed.config.DelayedQueueConfig;
import com.myh.delayed.model.DelayedObject;
import com.myh.delayed.service.IDlyProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class DlyProcessorService implements IDlyProcessorService {

    @Resource(name = "delayedQueue")
    private DelayedQueueConfig delayedQueue;

    @Override
    public void process(long id) {
        log.info(">>>  消息到期，处理业务数据;");
    }

    @Override
    public void boot() {
        log.info(">>> 当消息到期，会触发监听重新初始化+项目启动，初始化延时队列;");
        DelayedObject dq = new DelayedObject();
        delayedQueue.addDelayQueue(dq);//这是单条消息队列添加,如果需要初始化多条，遍历循环即可。
    }
}