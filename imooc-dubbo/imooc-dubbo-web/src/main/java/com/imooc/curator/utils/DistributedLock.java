package com.imooc.curator.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import java.util.concurrent.CountDownLatch;

public class DistributedLock {

    private CuratorFramework client = null;

    // 用于挂起当前请求，并且等待上一个分布式锁释放
    private static CountDownLatch zkLocklatch = new CountDownLatch(1);

    // 分布式锁的总节点名
    private static final String ZK_LOCK_PROJECT = "imooc-locks";

    // 分布式锁节点
    private static final String DISTRIBUTED_LOCK = "distributed_lock";

    /**
     * 构造函数
     *
     * @param client
     */
    public DistributedLock(CuratorFramework client) {
        this.client = client;
    }

    /**
     * 初始化锁
     */
    public void init() {
        // 使用命名空间
        client = client.usingNamespace("ZKLocks-Namespace");

        /**
         * 创建zk锁的总节点，相当于eclipse的工作空间下的项目
         *     ZKLocks-Namespace
         *          |
         *           —— imooc-locks
         *                 |
         *                  —— distributed_lock
         */
        try {
            if (client.checkExists().forPath("/" + ZK_LOCK_PROJECT) == null) {
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath("/" + ZK_LOCK_PROJECT);
            }
            // 针对zk的分布式锁节点，创建相应的watcher事件监听
            addWatcherToLock("/" + ZK_LOCK_PROJECT);
        } catch (Exception e) {

        }
    }

    public void getLock() {
        // 使用死循环，当且仅当上一个锁释放并且当前请求获得锁成功后才会跳出
        while (true) {
            try {
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath("/" + ZK_LOCK_PROJECT + "/" + DISTRIBUTED_LOCK);
                System.out.println("获得分布式锁成功");
                // 如果锁的节点能够被创建成功，则锁没有被占用
                return;
            } catch (Exception e) {
                System.out.println("获得分布式锁失败");
                try {
                    // 如果没有获得到锁（可能释放锁的时候，多个等待线程被通知，当前线程没有抢到锁），需要设置同步资源值
                    if (zkLocklatch.getCount() <= 0) {
                        zkLocklatch = new CountDownLatch(1);
                    }
                    // 阻塞线程
                    zkLocklatch.await();
                } catch (InterruptedException ie) {

                }
            }
        }
    }

    /**
     * 释放分布式锁
     * @return
     */
    public boolean releaseLock() {
        try {
            if (client.checkExists().forPath("/" + ZK_LOCK_PROJECT + "/" + DISTRIBUTED_LOCK) != null) {
                client.delete().forPath("/" + ZK_LOCK_PROJECT + "/" + DISTRIBUTED_LOCK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("分布式锁释放完毕");
        return true;
    }

    /**
     * 创建watcher监听
     *
     * @param path
     * @throws Exception
     */
    public void addWatcherToLock(String path) throws Exception {
        final PathChildrenCache cache = new PathChildrenCache(client, path, true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                    String path = pathChildrenCacheEvent.getData().getPath();
                    System.out.println("上一个会话已释放锁或者会话已断开，节点路径为：" + path);
                    // 一个节点下面可能有多个锁，判断是不是当前的锁
                    if (path.contains(DISTRIBUTED_LOCK)) {
                        System.out.println("释放计数器，让当前请求来获得分布式锁...");
                        zkLocklatch.countDown();
                    }
                }
            }
        });
    }
}
