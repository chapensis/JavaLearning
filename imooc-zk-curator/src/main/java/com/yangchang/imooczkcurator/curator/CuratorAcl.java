package com.yangchang.imooczkcurator.curator;

import com.yangchang.imooczkcurator.zookeeper.AclUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * CuratorAcl
 *
 * @author yangchang
 * @date 2019/1/1
 */
public class CuratorAcl {
    public CuratorFramework client = null;

    public static final String zkServerPath = "192.168.0.104:2181";

    /**
     * 实例化zk客户端
     */
    public CuratorAcl() {
        /**
         * curator链接zookeeper的策略:RetryNTimes
         * n：重试的次数
         * sleepMsBetweenRetries:每次重试间隔的时间
         */
        RetryPolicy retryPolicy = new RetryNTimes(3, 5000);

        client = CuratorFrameworkFactory.builder()
                .authorization("digest", "imooc1:123456".getBytes())
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
        CuratorAcl cto = new CuratorAcl();
        boolean isZkCuratorStarted = cto.client.isStarted();
        System.out.println("当前客户的状态：" + (isZkCuratorStarted ? "连接中" : "已关闭"));

        String nodePath = "/acl/father/child/sub";

        // 2. 自定义用户认证访问
        List<ACL> acls = new ArrayList<>();
        // Id即用户
        Id imooc1 = new Id("digest", AclUtils.getDigestUserPwd("imooc1:123456"));
        Id imooc2 = new Id("digest", AclUtils.getDigestUserPwd("imooc2:123456"));
        acls.add(new ACL(ZooDefs.Perms.ALL, imooc1));
        acls.add(new ACL(ZooDefs.Perms.READ, imooc2));
        acls.add(new ACL(ZooDefs.Perms.DELETE | ZooDefs.Perms.CREATE, imooc2));

        // 创建节点
//        byte[] data = "spiderman".getBytes();
//        cto.client.create().creatingParentsIfNeeded()
//                .withMode(CreateMode.PERSISTENT)
//                // 对父节点设置权限，会一边创建一边设置权限
//                .withACL(acls, true)
//                .forPath(nodePath, data);


        cto.client.setACL().withACL(acls).forPath("/curatorNode");

        // 更新节点数据
//		byte[] newData = "batman".getBytes();
//		cto.client.setData().withVersion(0).forPath(nodePath, newData);

        // 删除节点
//		cto.client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(0).forPath(nodePath);

        // 读取节点数据
//		Stat stat = new Stat();
//		byte[] data = cto.client.getData().storingStatIn(stat).forPath(nodePath);
//		System.out.println("节点" + nodePath + "的数据为: " + new String(data));
//		System.out.println("该节点的版本号为: " + stat.getVersion());

        cto.closeZKClient();
        boolean isZkCuratorStarted2 = cto.client.isStarted();
        System.out.println("当前客户的状态：" + (isZkCuratorStarted2 ? "连接中" : "已关闭"));
    }
}
