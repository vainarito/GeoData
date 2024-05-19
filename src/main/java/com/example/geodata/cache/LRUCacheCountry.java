package com.example.geodata.cache;

import com.example.geodata.cache.impl.LRUCache;
import com.example.geodata.entity.Country;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LRUCacheCountry extends LRUCache<Integer, Country> {

    public LRUCacheCountry(@Value("${LRUCache.capacity}") final int capacity) {
        super(capacity);
    }

}
