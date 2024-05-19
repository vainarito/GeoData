package com.example.geodata.cache.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.geodata.cache.impl.LRUCache;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LRUCacheTest {

    private LRUCache<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new LRUCache<>(10);
    }

    @Test
    void getAntPut() {
        String key = "#123";
        Optional<String> expectedValue = Optional.of( "value");
        cache.put(key, expectedValue.get());

        Optional<String> actualValue = cache.get(key);

        assertEquals(actualValue, expectedValue);
    }

    @Test
    void containsKey() {
        String key = "#123";
        Optional<String> expectedValue = Optional.of("value");

        cache.put(key, expectedValue.get());

        assertTrue(cache.containsKey(key));
    }

    @Test
    void remove() {
        String key = "#123";
        Optional<String> expectedValue = Optional.of("value");
        cache.put(key, expectedValue.get());

        cache.remove(key);

        assertEquals(cache.get(key), Optional.empty());
    }

}