package com.myh.delayed.config;

import com.myh.delayed.model.DelayedObject;
import com.myh.delayed.model.vo.DelayedQueueVo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.DelayQueue;

/**
 * 延时队列数据管理;
 */
@Slf4j
public class DelayedQueueHandle {

    /**
     * 负责保存限时消息队列;
     */
    public static DelayQueue<DelayedQueueVo> delayQueue = new DelayQueue<DelayedQueueVo>();

    /**
     * 往列列里面添加队列消息;
     *
     * @param delayedObject
     */
    public static void addDelayQueue(DelayedObject delayedObject) {
        DelayedQueueVo itemVo = new DelayedQueueVo(delayedObject.getDelayTime(), delayedObject.getId());
        delayQueue.put(itemVo);
        log.info(">>> 添加队列[延时时长：{}秒],延时队列详情：", delayedObject.getDelayTime(), delayedObject.toString());

    }

    /**
     * 查询所有的延时队列消息;
     */
    public static List<DelayedQueueVo> selDelayQueue() {

        List<DelayedQueueVo> delayQueueList = new ArrayList<>();

        Iterator<DelayedQueueVo> it = delayQueue.iterator();

        while (it.hasNext()) {
            DelayedQueueVo obj = it.next();
            delayQueueList.add(obj);
        }

        return delayQueueList;

    }

    /**
     * 从延时队列当中移除指定的延时队列对象;
     */
    public static boolean removeDelayQueue(DelayedObject delayedObject) {

        DelayedQueueVo itemVo = new DelayedQueueVo(delayedObject.getDelayTime(), delayedObject.getId());

        boolean result = delayQueue.remove(itemVo);

        log.info(">>> 从队列中移除消息队列对象：{},移除结果：{}", delayedObject.toString(), result);

        return result;
    }


}