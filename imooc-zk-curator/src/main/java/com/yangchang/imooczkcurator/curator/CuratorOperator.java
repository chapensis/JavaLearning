package com.yangchang.imooczkcurator.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * CuratorOperator
 *
 * @author yangchang
 * @date 2019/1/1
 */
public class CuratorOperator {

    public CuratorFramework client = null;

    public static final String zkServerPath = "192.168.0.104:2181";

    /**
     * 实例化zk客户端
     */
    public CuratorOperator() {

        /**
         * 同步创建zk实例，原生api是异步的
         * curator链接zookeeper的策略:ExponentialBackoffRetry
         * baseSleepTimeMs:初始sleep的时间
         * maxRetries：最大重试次数
         * maxSleepMs：最大重试时间
         */
        // RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);

        /**
         * curator链接zookeeper的策略:RetryNTimes
         * n：重试的次数
         * sleepMsBetweenRetries:每次重试间隔的时间
         */
        RetryPolicy retryPolicy = new RetryNTimes(3, 5000);

        /**
         * 重试一次
         * sleepMsBetweenRetries:每次重试间隔的时间
         */
        // RetryPolicy retryPolicy2 = new RetryOneTime(5000);

        /**
         * 永远重试，不推荐使用
         */
        // RetryPolicy retryPolicy3 = new RetryForever(retryIntervalMs);

        /**
         * maxElapsedTimeMs:最大重试时间
         * sleepMsBetweenRetries:每次重试间隔
         * 重试时间超过maxElapsedTimeMs后就不再重试
         */
        // RetryPolicy retryPolicy4 = new RetryUntilElapsed(2000, 3000);

        client = CuratorFrameworkFactory.builder()
                .connectString(zkServerPath)
                .sessionTimeoutMs(10000)
                .retryPolicy(retryPolicy)
                // 每次创建或者删除都会基于该工作站
                .namespace("workspace")
                .build();
        client.start();
    }

    /**
     * 关闭客户端
     */
    public void closeZKClient() {
        if (client != null) {
            this.client.close();
        }
    }

    public static void main(String[] args) throws Exception {
        // 实例化
        CuratorOperator cto = new CuratorOperator();
        boolean isZkCuratorStarted = cto.client.isStarted();
        System.out.println("当前客户的状态：" + (isZkCuratorStarted ? "连接中" : "已关闭"));

        // 创建节点
        String nodePath = "/super/imooc";
//        byte[] data = "superme".getBytes();
//        cto.client.create().creatingParentsIfNeeded()
//                .withMode(CreateMode.PERSISTENT)
//                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
//                .forPath(nodePath, data);

        // 更新节点数据
//        byte[] newData = "batman".getBytes();
//        cto.client.setData()
//                .withVersion(0)
//                .forPath(nodePath, newData);

        // 删除节点数据
//        cto.client.delete()
//                // 如果删除失败，那么在后端还是会继续删除，直到成功
//                .guaranteed()
//                // 如果有子字节，就删除
//                .deletingChildrenIfNeeded()
//                .withVersion(1)
//                .forPath(nodePath);

        // 读取节点数据
//        Stat stat = new Stat();
//        byte[] data = cto.client.getData()
//                // 要想获取版本号等信息，必须使用该属性
//                .storingStatIn(stat)
//                .forPath(nodePath);
//        System.out.println("节点" + nodePath + "的数据为:" + new String(data));
//        System.out.println("该节点的版本号为:" + stat.getVersion());

        // 查询子节点
//        List<String> childNodes = cto.client.getChildren()
//                .forPath(nodePath);
//        System.out.println("开始打印子节点：");
//        childNodes.forEach(System.out::println);

        // 判断节点是否存在
        Stat statExist = cto.client.checkExists().forPath(nodePath + "/abc");
        System.out.println(statExist);

        // watcher事件，当使用usingWatcher的时候，监听只会触发一次，监听完毕后就销毁
        // cto.client.getData().usingWatcher(new MyCuratorWatcher()).forPath(nodePath);
        // cto.client.getData().usingWatcher(new MyWatcher()).forPath(nodePath);

        // 为节点添加watcher
        // NodeCache:监听数据节点的变更，会触发事件
//        final NodeCache nodeCache = new NodeCache(cto.client, nodePath);
//        // buildInitial:初始化的时候获取node的值并缓存,写了true就去拿初值
//        nodeCache.start(true);
//        if (nodeCache.getCurrentData() != null) {
//            System.out.println("节点初始化数据为：" + new String(nodeCache.getCurrentData().getData()));
//        } else {
//            System.out.println("节点初始化数据为空...");
//        }
//
//        nodeCache.getListenable().addListener(new NodeCacheListener() {
//            @Override
//            public void nodeChanged() throws Exception {
//                try {
//                    String data = new String(nodeCache.getCurrentData().getData());
//                    System.out.println("节点路径：" + nodeCache.getCurrentData().getPath() + " 数据：" + data);
//                } catch (Exception e) {
//                    System.out.println("nodeChanged error:" + e.toString());
//                }
//            }
//        });

        // 为子节点添加watcher
        // PathChildrenCache:监听数据节点的增删改，会触发事件
        String childNodePathCache = nodePath;
        final PathChildrenCache childrenCache = new PathChildrenCache(cto.client, childNodePathCache, true);

        /**
         * StartMode:初始化方式
         * POST_INITIALIZED_EVENT:异步初始化，初始化后会触发事件
         * NORMAL:异步初始化
         * BUILD_INITIAL_CACHE:同步初始化
         */
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        List<ChildData> childDataList = childrenCache.getCurrentData();
        System.out.println("当前数据节点的子节点数据列表:");
        childDataList.forEach(x -> {
            String childData = new String(x.getData());
            System.out.println(childData);
        });

        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)) {
                    System.out.println("子节点初始化OK");
                } else if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                    System.out.println("添加子节点：" + pathChildrenCacheEvent.getData().getPath());
                    System.out.println("子节点数据：" + new String(pathChildrenCacheEvent.getData().getData()));
                }
                if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                    System.out.println("删除子节点：" + pathChildrenCacheEvent.getData().getPath());
                } else if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                    System.out.println("修改子节点路径：" + pathChildrenCacheEvent.getData().getPath());
                    System.out.println("修改子节点数据：" + new String(pathChildrenCacheEvent.getData().getData()));
                }
            }
        });

        Thread.sleep(300000);

        cto.closeZKClient();
        boolean isZkCuratorStarted2 = cto.client.isStarted();
        System.out.println("当前客户的状态：" + (isZkCuratorStarted2 ? "连接中" : "已关闭"));

    }
}
