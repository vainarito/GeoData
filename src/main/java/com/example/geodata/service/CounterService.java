package com.example.geodata.service;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterService {

    private CounterService() { }

    private static final AtomicInteger count = new AtomicInteger(0);

    public static synchronized int increment() {
        return count.incrementAndGet();
    }

}
