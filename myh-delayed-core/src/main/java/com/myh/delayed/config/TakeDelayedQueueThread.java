package com.myh.delayed.config;

import com.myh.delayed.model.DelayedQueueVo;
import com.myh.delayed.service.IDlyProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

/**
 * 声明一个 Take Delayed Queue Thread线程;
 */
@Slf4j
public class TakeDelayedQueueThread implements Runnable {

    private IDlyProcessorService dlyProcessorService;// 消息到期，处理相关的业务;

    private ZkClient zkClient;

    private String zkListenerKey;

    public TakeDelayedQueueThread(IDlyProcessorService dlyProcessorService, ZkClient zkClient, String zkListenerKey) {
        super();
        this.dlyProcessorService = dlyProcessorService;
        this.zkClient = zkClient;
        this.zkListenerKey = zkListenerKey;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {

                DelayedQueueVo itemOrder = DelayedQueueHandle.delayQueue.take();// 弹出一个队列;

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

    public ZkClient getZkClient() {
        return zkClient;
    }

    public void setZkClient(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    public String getZkListenerKey() {
        return zkListenerKey;
    }

    public void setZkListenerKey(String zkListenerKey) {
        this.zkListenerKey = zkListenerKey;
    }
}