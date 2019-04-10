package com.yangchang.imooczkcurator.curator;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * MyWatcher
 *
 * @author yangchang
 * @date 2019/1/1
 */
public class MyWatcher implements Watcher {
    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("触发MyWatcher,节点路径为:" + watchedEvent.getPath());
    }
}
