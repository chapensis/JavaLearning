package com.example.helloworld.test.cache;

import org.apache.commons.lang3.concurrent.Computable;

import java.util.Map;
import java.util.concurrent.*;

public class Memoizerl<A, V> implements Computable<A, V> {
    /**
     * 设置缓存参数的任务
     */
    private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memoizerl(Computable<A, V> c) {
        this.c = c;
    }

    /**
     * 每个没有命中缓存的对象都可以计算
     * @param arg
     * @return
     * @throws InterruptedException
     */
    @Override
    public V compute(A arg) throws InterruptedException {
        while (true) {
            Future<V> f = cache.get(arg);
            if (f == null) {
                Callable<V> eval = () -> c.compute(arg);
                FutureTask<V> ft = new FutureTask<>(eval);
                // 但是只有没有设置获取任务的时候，才能第一次设置获取任务
                f = cache.putIfAbsent(arg, ft);
                if (f == null) {
                    f = ft;
                    ft.run();
                }
                try {
                    return f.get();
                } catch (CancellationException e) {
                    cache.remove(arg, f);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
