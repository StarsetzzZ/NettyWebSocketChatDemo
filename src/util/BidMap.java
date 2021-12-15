package util;

import java.util.HashMap;

/**
 * @author Starset
 * @version 1.0
 * @date 2021/12/7 21:59
 */
public class BidMap<K,V> extends HashMap<K,V> {
    private final HashMap<V,K> bidMap = new HashMap<>();

    @Override
    public V put(K key, V value) {
        bidMap.put(value,key);
        return super.put(key, value);
    }

    public K getKey(V value){
        return bidMap.get(value);
    }

}
