package com.myh.delayed.config;

import com.myh.delayed.model.DelayedObject;
import com.myh.delayed.model.vo.DelayedQueueVo;
import com.myh.delayed.service.IDlyProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.DelayQueue;

/**
 * 管理整个延时消息队列的生命周期;
 * 
 * @author myh
 * @date 2019/12/27
 * @copyright copyright (c) 2019
 */
@Slf4j
public class DelayedQueueConfig {

    private IDlyProcessorService dlyProcessorService;// 消息到期，处理相关的业务;

    private String zkListenerKey;// 消息队列相关的属性配置;

    private ZkClient zkClient;

    /**
     * 负责保存限时消息队列;
     */
    private static DelayQueue<DelayedQueueVo> delayQueue = new DelayQueue<DelayedQueueVo>();

    public void addDelayQueue(DelayedObject delayedObject) {
        DelayedQueueVo itemVo = new DelayedQueueVo(delayedObject.getDelayTime(), delayedObject.getId());
        delayQueue.put(itemVo);
        log.info(">>> 添加队列[延时时长：{}秒],延时队列详情：", delayedObject.getDelayTime(), delayedObject.toString());

    }

    /**
     * 查询延时队列消息内容;
     */
    public List<DelayedQueueVo> selDelayQueue() {

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
    public boolean removeDelayQueue(DelayedObject delayedObject) {

        DelayedQueueVo itemVo = new DelayedQueueVo(delayedObject.getDelayTime(), delayedObject.getId());

        boolean result = delayQueue.remove(itemVo);

        log.info(">>> 从队列中移除消息队列对象：{},移除结果：{}", delayedObject.toString(), result);

        return result;
    }

    /**
     * 处理到期的延时消息;
     * 
     * @author myh
     * @date 2019/12/27
     * @copyright copyright (c) 2019
     * @company www.duia.com
     */
    private class TakeDelayedQueue implements Runnable {

        private IDlyProcessorService dlyProcessorService;// 消息到期，处理相关的业务;

        public TakeDelayedQueue(IDlyProcessorService dlyProcessorService) {
            super();
            this.dlyProcessorService = dlyProcessorService;
        }

        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                try {

                    DelayedQueueVo itemOrder = delayQueue.take();// 弹出一个队列;

                    if (null != itemOrder) {

                        // 1、消息到期，处理相关的业务;
                        this.dlyProcessorService.process(itemOrder.getQueueId());
                        log.info(">>> 处理一个过期的延时消息数据,详情：{}", itemOrder.toString());
                        // 2、触发zk监听，解决分布式环境下，节点延时队列数据不一致的问题。
                        triggerMonitor();

                    }
                } catch (Exception e) {
                    log.error("The thread is interrupted");
                }
            }

        }

    }

    private Thread takeQueue;// 获取消息队列;

    @PostConstruct
    public void init() {

        // 1、启动线程，处理到期消息;
        takeQueue = new Thread(new TakeDelayedQueue(dlyProcessorService));
        takeQueue.start();

        // 2、项目启动，初始化消息队列数据;
        this.dlyProcessorService.boot();

        // 3、注册zk 监听事件;
        initZkListener(dlyProcessorService);

    }

    @PreDestroy
    public void close() {
        takeQueue.interrupt();
    }

    /**
     * 建立zk监听;
     * 
     * @param dlyProcessorService
     */
    private void initZkListener(IDlyProcessorService dlyProcessorService) {
        IZkDataListener izkDataListener = new IZkDataListener() {
            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                dlyProcessorService.boot();
            }

            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                dlyProcessorService.boot();
            }
        };
        // 节点注册事件;
        zkClient.subscribeDataChanges(zkListenerKey, izkDataListener);
    }

    /**
     * 修改节点的状态:当存在的时候，进行删除操作，当不存在的时候，进行添加操作;<br>
     * 解决zk 监听处理,通知节点;
     */
    private void triggerMonitor() {
        if (zkClient.exists(zkListenerKey)) {
            zkClient.delete(zkListenerKey);
        } else {
            zkClient.createEphemeral(zkListenerKey);
        }
    }

    public IDlyProcessorService getDlyProcessorService() {
        return dlyProcessorService;
    }

    public void setDlyProcessorService(IDlyProcessorService dlyProcessorService) {
        this.dlyProcessorService = dlyProcessorService;
    }

    public String getZkListenerKey() {
        return zkListenerKey;
    }

    public void setZkListenerKey(String zkListenerKey) {
        this.zkListenerKey = zkListenerKey;
    }

    public ZkClient getZkClient() {
        return zkClient;
    }

    public void setZkClient(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

}
