package com.example.geodata.cache;

import com.example.geodata.cache.impl.LRUCache;
import com.example.geodata.entity.City;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LRUCacheCity extends LRUCache<Integer, City> {

    public LRUCacheCity(@Value("${LRUCache.capacity}") final int capacity) {
        super(capacity);
    }

}
