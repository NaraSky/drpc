package com.rain.rpc.common.id;

import java.util.concurrent.atomic.AtomicLong;

public class IdFactory {
    private final static AtomicLong REQUEST_ID = new AtomicLong(0);

    public static long getRequestId() {
        return REQUEST_ID.incrementAndGet();
    }
}
