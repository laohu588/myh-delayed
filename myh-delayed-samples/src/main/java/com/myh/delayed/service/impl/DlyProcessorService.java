package com.myh.delayed.service.impl;

import com.myh.delayed.config.DelayedQueueHandle;
import com.myh.delayed.model.DelayedObject;
import com.myh.delayed.service.IDlyProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DlyProcessorService implements IDlyProcessorService {

    private DelayedQueueHandle delayedQueueHandle;

    private List<DelayedObject> delayedObjectList = new ArrayList<>();

    public DlyProcessorService() {
        DelayedObject dq = new DelayedObject();
        dq.setDelayTime(30);
        dq.setId(100);

        DelayedObject dq2 = new DelayedObject();
        dq2.setDelayTime(40);
        dq2.setId(101);
        delayedObjectList.add(dq);
        delayedObjectList.add(dq2);
    }

    /**
     * 当延时队列消息到期，处理相关业务<br>
     * 注意：
     * 1、如果服务是集群部署，要处理的业务记得加锁，保证节点只有一个能执行;相反，不用加锁;
     * 2、插件自带基于zookeeper实现的分布锁;
     *
     * @param id 消息主键ID;
     */
    @Override
    public void process(long id) {

        log.info(">>>  消息到期，处理业务数据 start：{}", id);

        if (id == 100) {
            delayedObjectList.remove(0);
        } else {
            delayedObjectList.remove(0);
        }

        log.info(">>>  消息到期，处理业务数据 end：{}", id);
    }

    /**
     * 应用于以下情况：
     * 1、当消息到期，会触发监听重新初始化延时列表数据(解决服务集群部署，保证各节点延时队列数据一致。)
     * 2、当项目启动，初始化延时队列数据;
     */
    @Override
    public void bootInit() {

        log.info(">>> 初始化延时队列;");

        //延时订单示例：计算延长时长：
        // 下单时间(2020-10-01 00:00:00)  延时时长(20m)  当前时间(2020-10-01 10:00:00)  最后需要延长时长;
        // 最后需要延长时长 = 延长时间-(当前时间 - 下单时间);

        for (int i = 0; i < delayedObjectList.size(); i++) {
            delayedQueueHandle.addDelayQueue(delayedObjectList.get(i));//这是单条消息队列添加,如果需要初始化多条，遍历循环即可。
        }

    }
}