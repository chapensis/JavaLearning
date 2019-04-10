package com.yangchang.imooczkcurator.curator;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

/**
 * CuratorOperator
 *
 * @author yangchang
 * @date 2019/1/1
 */
public class MyCuratorWatcher implements CuratorWatcher {
    @Override
    public void process(WatchedEvent watchedEvent) throws Exception {
        System.out.println("触发MyCuratorWatcher,节点路径为:" + watchedEvent.getPath());
    }
}
