package com.example.geodata.cache;

import com.example.geodata.cache.impl.LRUCache;
import com.example.geodata.entity.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LRUCacheLanguage extends LRUCache<Integer, Language> {

    public LRUCacheLanguage(@Value("${LRUCache.capacity}") final int capacity) {
        super(capacity);
    }

}
