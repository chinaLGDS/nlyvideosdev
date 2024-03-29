package com.nly.utils;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

/**
 * zookeeper相关配置
 */
@Configuration
public class ZookeeperConfig {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperConfig.class);


//    CuratorFrameworkFactory
    @Value("${zookeeper.address}")
    private String connectString;

    @Value("${zookeeper.timeout}")
    private int timeout;

    @Bean(name = "zkClient")


    public ZooKeeper zkClient(){
        ZooKeeper zooKeeper = null;
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            //连接成功后，会回调watcher监听，此连接操作是异步的。执行完new语句后，直接调用后续代码
            // 可指定多台服务地址 127.0.0.1:2181  ，127.0.0.1:2182  ，127.0.0.1:2183

            Watcher watcher = null;
            zooKeeper = new ZooKeeper(connectString, timeout, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (Event.KeeperState.SyncConnected == event.getState()){
                        countDownLatch.countDown();
                    }

                }
            });
            countDownLatch.await();
            logger.info("初始化Zookeeper 连接状态...... ={}",zooKeeper.getState());
        } catch (Exception e) {
            logger.error("初始化Zookeeper连接异常。。。={}",e);
        }
        return zooKeeper;
    }
}
