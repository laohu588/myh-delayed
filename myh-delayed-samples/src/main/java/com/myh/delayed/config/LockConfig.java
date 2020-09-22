package com.myh.delayed.config;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LockConfig implements InitializingBean {

    private String zkAddress;

    @Override
    public void afterPropertiesSet() throws Exception {
        zkAddress = "192.168.1.1:2181";
        log.info(">>>lock zookeeper msg：" + zkAddress);
    }

    @Bean(name = "queueZkClient")
    public ZkClient getZkClient() {
        ZkClient zkClient = new ZkClient(zkAddress, 30000, 10000);
        return zkClient;
    }

}