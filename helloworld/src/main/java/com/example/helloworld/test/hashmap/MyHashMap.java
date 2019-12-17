package com.example.helloworld.test.hashmap;

import java.util.ArrayList;
import java.util.List;

/**
 * 自己实现一个hashmap
 *
 * @param <K>
 * @param <V>
 */
public class MyHashMap<K, V> implements MyMap<K, V> {

    // 数组的默认初始化长度
    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;

    // 阈值比例
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private int defaultInitSize;

    private float defaultLoadFactor;

    // map中entry的数量
    private int entryUseSize;

    private Entry<K, V>[] table = null;

    public MyHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
     * HashMap的初始化
     *
     * @param defaultInitSize
     * @param defaultLoadFactor
     */
    public MyHashMap(int defaultInitSize, float defaultLoadFactor) {
        this.defaultInitSize = defaultInitSize;
        this.defaultLoadFactor = defaultLoadFactor;

        this.table = new Entry[this.defaultInitSize];
    }

    /**
     * 放入键值对，如果已经存在相同的键，则返回键的旧值
     *
     * @param k
     * @param v
     * @return
     */
    @Override
    public V put(K k, V v) {
        V oldValue = null;
        // 是否需要扩容?
        // 扩容完毕 肯定需要重新散列
        if (entryUseSize >= defaultInitSize * defaultLoadFactor) {
            resize(2 * defaultInitSize);
        }
        // 得到hash值，计算出数组中的位置
        int index = hash(k) & (defaultInitSize - 1);
        if (table[index] == null) {
            table[index] = new Entry<>(k, v, null);
            entryUseSize++;
        } else {
            Entry<K, V> entry = table[index];
            Entry<K, V> e = entry;
            while (e != null) {
                // 遍历每一个entry，看是否存在已经有的key
                if (k == e.getKey() || k.equals(e.getKey())) {
                    oldValue = e.value;
                    e.value = v;
                    return oldValue;
                }
                e = e.next;
            }
            // 如果遍历完所有的entry没有找到指定的key,则采用头插法插入新数据
            table[index] = new Entry<>(k, v, entry);
            entryUseSize++;
        }
        return oldValue;
    }

    @Override
    public V get(K k) {
        int index = hash(k) & (defaultInitSize - 1);
        if (table[index] == null) {
            return null;
        }
        Entry<K, V> entry = table[index];
        do {
            if (entry.key == k || entry.getKey().equals(k)) {
                return entry.value;
            } else {
                entry = entry.next;
            }
        } while (entry != null);
        return null;
    }

    private int hash(K k) {
        int hashcode = k.hashCode();
        hashcode ^= (hashcode >>> 20) ^ (hashcode >>> 12);
        return hashcode ^ (hashcode >>> 7) ^ (hashcode >>> 4);
    }

    private void resize(int i) {
        Entry[] newTable = new Entry[i];
        defaultInitSize = i;
        entryUseSize = 0;
        rehash(newTable);
    }

    private void rehash(Entry<K, V>[] newTable) {
        // 得到原来老的entry集合，注意遍历单链表
        List<Entry<K, V>> entries = new ArrayList<>();
        for (Entry entry : table) {
            if (entry != null) {
                do {
                    entries.add(entry);
                    entry = entry.next;
                } while (entry != null);
            }
        }

        // 使用新的table
        if (newTable.length > 0) {
            table = newTable;
        }

        // 所谓重新hash就是重新PUT entry到新的hashmap中
        for (Entry<K, V> entry : entries) {
            put(entry.getKey(), entry.getValue());
        }
    }

    class Entry<K, V> implements MyMap.Entry<K, V> {
        private K key;
        private V value;

        private Entry<K, V> next;

        public Entry() {

        }

        public Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }
    }
}
