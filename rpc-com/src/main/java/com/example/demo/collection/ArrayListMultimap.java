package com.example.demo.collection;

import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ArrayListMultimap<K, V> {

    /**
     * 可修改value集合
     */
    private ConcurrentHashMap<K, List<V>> map;

    /**
     * 不可修改value集合
     */
    private ConcurrentHashMap<K, List<V>> unmodifyValueMap;

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
     * @param size
     */
    public ArrayListMultimap(Integer size) {
        this.map = new ConcurrentHashMap<>(size);
        this.unmodifyValueMap = new ConcurrentHashMap<>(size);
    }

    /**
     * 创建方法
     * @return
     */
    public static ArrayListMultimap create() {
        return create(8);
    }

    /**
     * 创建方法
     * @return
     */
    public static ArrayListMultimap create(Integer size) {
        return new ArrayListMultimap(size);
    }

    public List<V> get(K key) {
        return unmodifyValueMap.get(key);
    }

    /**
     * 放入map
     * @param key
     * @param value
     * @return
     */
    public synchronized List<V> put(K key, V value) {
        //放入map值
        List<V> result = putMap(key, value);
        //放入不可修改map值
        unmodifyValueMap.put(key, unmodify(result));
        //重新生成数据
        revertKeyAndValueList();
        return result;
    }

    /**
     * 放入值
     * @param key
     * @param value
     * @return
     */
    private synchronized List<V> putMap(K key, V value) {
        List<V> result = map.getOrDefault(key, new ArrayList<>());
        result.add(value);
        map.putIfAbsent(key, result);
        return result;
    }

    /**
     * 删除kv
     *
     * @param key
     * @return
     */
    public List<V> remove(K key) {
        map.remove(key);
        //重新生成数据
        revertKeyAndValueList();
        return unmodifyValueMap.remove(key);
    }

    /**
     * 删除元素
     *
     * @param key
     * @param v
     * @return
     */
    public synchronized List<V> removeElement(K key, V v) {
        List<V> vs = map.get(key);
        if (!CollectionUtils.isEmpty(vs) && vs.contains(v)) {
            //删除数据
            vs.remove(v);
            unmodifyValueMap.put(key, unmodify(vs));
            //重新生成数据
            revertKeyAndValueList();
            return vs;
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
        values = unmodify(map.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
    }

    /**
     * 不可修改集合
     * @param list
     * @return
     */
    private List unmodify(List list) {
        return Collections.unmodifiableList(list);
    }

}
