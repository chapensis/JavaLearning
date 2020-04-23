package com.example.helloworld.test;

import java.util.concurrent.ThreadLocalRandom;

public class HyperLogLogTest {
    /* 低位连续0的最大数量 */
    private int maxZero;
    /* 随机数数量 */
    private int count;

    public HyperLogLogTest(int count) {
        this.count = count;
    }

    private void lowZero(long value) {
        int i = 1;
        for (; i < 32; i++) {
            /* 如果一个数右移i位后再左移i位还是保持值不变，那么它的低i位都是0 */
            if (value >> i << i != value) {
                break;
            }
        }
        /* 因为i是从1开始的，所以要减1 */
        i = i - 1;

        /**
         * 如果低位0的个数小于指定个数，则低位0个数替换成更大的个数
         */
        if (this.maxZero < i) {
            this.maxZero = i;
        }
    }

    public void random() {
        for (int i = 0; i < this.count; i++) {
            long m = ThreadLocalRandom.current().nextLong(1L << 32);
            lowZero(m);
        }
    }

    public int getMaxZero() {
        return this.maxZero;
    }

    public static void main(String[] args) {
        for (int i = 10000; i <= 100000; i += 10000) {
            HyperLogLogTest hll = new HyperLogLogTest(i);
            hll.random();
            System.out.printf("期待连续0的个数为：%.0f，统计连续0的个数为：%d", Math.log(i) / Math.log(2), hll.getMaxZero());
            System.out.println();
        }
    }
}
