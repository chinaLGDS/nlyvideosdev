package com.nly.cofig;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;


@Component
public class ZKApi {

    private static final Logger logger = LoggerFactory.getLogger(ZKApi.class);


    @Autowired(required = true)
    private ZooKeeper zkClient;

    /**
     * 判断指定节点是否存在
     * @Param path
     * @param needWatch 指定是否复用zookeeper中默认的Watcher
     * @return
     */
    public Stat exists(String path, Boolean needWatch){
        try {
            return zkClient.exists(path,needWatch);
        } catch (Exception e) {
            logger.error("断指定节点是否存在异常{},{}",path,e);
            return null;
        }
    }

    /**
     * 检测节点是否存在  并设置监听事件
     * 三种监听类型 创建 , 删除 ,  更新
     *
     * @param path
     * @param watcher  传入指定的监听类
     * @return
     */

    public Stat exists(String path, Watcher watcher){
        try{
            return zkClient.exists(path, watcher);

        } catch (Exception e) {
            logger.error("断指定节点是否存在异常{},{}",path,e);
            return null;
        }
    }


    /**
     * 创建持久化节点
     * @param path
     * @param data
     * @return
     */
    public boolean createNode(String path, String data){
        try{
             zkClient.create(path,data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
             return true;
        } catch (Exception e) {
            logger.error("创建持久化节点异常{},{},{}",path,data,e);
            return false;
        }
    }

    /**
     * 修改持久化节点
     * @param path
     * @param data
     * @return
     */
    public boolean updateNode(String path , String data){
        try{
            zkClient.setData(path,data.getBytes(),-1);
            return true;
        } catch (Exception e) {
            logger.error("修改持久化节点异常{},{},{}",path,data,e);
            return false;
        }
    }

    /**
     *
     * 删除持久化节点
     * @param path
     * @return
     */

    public boolean deleteNode (String path){
        try{
            //version 参数指定要更新的数据版本 ，如果version和真实版本不同
            //更新操作将会失败。指定version为 -1 则忽略版本检查
            zkClient.delete(path,-1);
            return true;
        } catch (Exception e) {
            logger.error("删除持久化节点异常{},{}",path,e);
            return false;
        }
    }

    /**
     * 获取当前节点的子节点(不包含孙子节点)
     * @param path 父节点path
     */
    public List<String> getChildren(String path) throws KeeperException, InterruptedException{
        List<String> list = zkClient.getChildren(path, false);
        return list;
    }

    /**
     * 获取指定节点的值
     * @param path
     * @return
     */
    public  String getData(String path,Watcher watcher){
        try {
            Stat stat=new Stat();
            byte[] bytes=zkClient.getData(path,watcher,stat);
            return  new String(bytes);
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }


    /**
     * 测试方法  初始化
     */
    @PostConstruct
    public  void init(){
        String path="/zk-watcher-3";
        logger.info("【执行初始化测试方法。。。。。。。。。。。。】");
        logger.info(logger.getName());
        createNode(path,"测试");
        String value=getData(path,new WatcherApi());
        logger.info("【执行初始化测试方法getData返回值。。。。。。。。。。。。】={}",value);

        // 删除节点出发 监听事件
//        deleteNode(path);

    }


}
