package com.example.demo.collection;

import com.example.demo.dto.ImmutablePair;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ArrayListMultimap<K, V> {

    /**
     * 可修改value集合
     */
    private ConcurrentHashMap<K, ImmutablePair<List<V>, List<V>>> map;

    /**
     * key 集合
     */
    private List<K> keys;

    /**
     * value 集合
     */
    private List<V> values;


    /**
     * 构造方法
     *
     * @param size
     */
    public ArrayListMultimap(Integer size) {
        this.map = new ConcurrentHashMap<>(size);
    }

    /**
     * 创建方法
     *
     * @return
     */
    public static ArrayListMultimap create() {
        return create(8);
    }

    /**
     * 创建方法
     *
     * @return
     */
    public static ArrayListMultimap create(Integer size) {
        return new ArrayListMultimap(size);
    }

    /**
     * 获取或默认
     * @param key
     * @return
     */
    public List<V> get(K key) {
        return map.getOrDefault(key, new ImmutablePair<>(new ArrayList<>(), null)).getRight();
    }


    /**
     * 放入map
     *
     * @param key
     * @param value
     * @return
     */
    public synchronized List<V> put(K key, V value) {
        //放入map值
        List<V> result = putMap(key, value);
        //重新生成数据
        revertKeyAndValueList();
        return result;
    }

    /**
     * 放入值
     *
     * @param key
     * @param value
     * @return
     */
    private synchronized List<V> putMap(K key, V value) {
        ImmutablePair<List<V>, List<V>> pair = map.getOrDefault(key, new ImmutablePair<>(new ArrayList<>(), null));
        pair.getLeft().add(value);
        pair.setRight(unmodify(pair.getLeft()));
        map.putIfAbsent(key, pair);
        return pair.getRight();
    }

    /**
     * 删除kv
     *
     * @param key
     * @return
     */
    public synchronized List<V> remove(K key) {
        ImmutablePair<List<V>, List<V>> pair = map.remove(key);
        //重新生成数据
        revertKeyAndValueList();
        return pair.getRight();
    }

    /**
     * 删除元素
     *
     * @param key
     * @param v
     * @return
     */
    public synchronized List<V> removeElement(K key, V v) {
        ImmutablePair<List<V>, List<V>> pair = map.get(key);
        List<V> left = pair.getLeft();
        if (!CollectionUtils.isEmpty(left) && left.contains(v)) {
            //删除数据
            left.remove(v);
            pair.setRight(unmodify(left));
            //重新生成数据
            revertKeyAndValueList();
            return left;
        }
        return null;
    }


    public List<V> values() {
        return values;
    }

    public List<K> keys() {
        return keys;
    }

    public Integer keySize() {
        return keys.size();
    }

    public Integer valueSize() {
        return values.size();
    }

    /**
     * 数据修改后 重整数据
     */
    private void revertKeyAndValueList() {
        keys = unmodify(map.keySet().stream().collect(Collectors.toList()));
        values = unmodify(map.values().stream().map(ImmutablePair::getLeft)
                .flatMap(Collection::stream).collect(Collectors.toList()));
    }

    /**
     * 不可修改集合
     *
     * @param list
     * @return
     */
    private List unmodify(List list) {
        return Collections.unmodifiableList(list);
    }

}
