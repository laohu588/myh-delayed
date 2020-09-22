package com.myh.delayed.config;

import com.myh.delayed.service.IDlyProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Slf4j
public class InitDelayedQueue {

    private IDlyProcessorService dlyProcessorService;// 消息到期，处理相关的业务;
    private Thread takeQueue;// 获取消息队列;
    private ZkClient zkClient;
    private String zkListenerKey;

    /**
     * 项目启动，进行初始化操作;
     */
    @PostConstruct
    public void init() {

        // 1、启动线程，处理到期消息;
        takeQueue = new Thread(new TakeDelayedQueueThread(dlyProcessorService, zkClient, zkListenerKey));
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

    public Thread getTakeQueue() {
        return takeQueue;
    }

    public void setTakeQueue(Thread takeQueue) {
        this.takeQueue = takeQueue;
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
