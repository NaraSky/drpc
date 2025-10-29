package com.rain.rpc.consumer.common.future;

import com.rain.rpc.common.threadpool.ClientThreadPool;
import com.rain.rpc.consumer.common.callback.AsyncRpcCallback;
import com.rain.rpc.protocol.RpcProtocol;
import com.rain.rpc.protocol.request.RpcRequest;
import com.rain.rpc.protocol.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

public class RpcFuture extends CompletableFuture<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcFuture.class);

    private Sync sync;
    private RpcProtocol<RpcRequest> requestRpcProtocol;
    private RpcProtocol<RpcResponse> responseRpcProtocol;
    private long startTime;

    // Response time threshold in milliseconds
    private long responseTimeThreshold = 5000;

    /**
     * List of callbacks for asynchronous invocations.
     * When the response arrives, all registered callbacks will be executed.
     */
    private List<AsyncRpcCallback> pendingCallbacks = new ArrayList<>();
    
    /**
     * Lock to protect concurrent access to callbacks list and related operations.
     */
    private ReentrantLock lock = new ReentrantLock();

    public RpcFuture(RpcProtocol<RpcRequest> requestRpcProtocol) {
        this.sync = new Sync();
        this.requestRpcProtocol = requestRpcProtocol;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        if (this.responseRpcProtocol != null) {
            return this.responseRpcProtocol.getBody().getResult();
        } else {
            return null;
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (this.responseRpcProtocol != null) {
                return this.responseRpcProtocol.getBody().getResult();
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("Timeout exception. Request id: " + this.requestRpcProtocol.getHeader().getRequestId() + ". Request class name: " + this.requestRpcProtocol.getBody().getClassName() + ". Request method: " + this.requestRpcProtocol.getBody().getMethodName());
        }
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    public void done(RpcProtocol<RpcResponse> responseRpcProtocol) {
        this.responseRpcProtocol = responseRpcProtocol;
        // Release blocked threads (for synchronous calls)
        sync.release(1);
        // Execute registered callbacks (for asynchronous calls)
        invokeCallbacks();
        long responseTime = System.currentTimeMillis() - startTime;
        // Threshold
        if (responseTime > this.responseTimeThreshold) {
            LOGGER.warn("Service response time is too slow. Request id = " + responseRpcProtocol.getHeader().getRequestId() + ". Response Time = " + responseTime + "ms");
        }
    }

    /**
     * Executes a callback in a separate thread pool.
     * This enables asynchronous processing of RPC responses without blocking the I/O thread.
     * 
     * @param callback the callback to execute
     */
    private void runCallback(final AsyncRpcCallback callback) {
        final RpcResponse response = responseRpcProtocol.getBody();
        ClientThreadPool.submit(() -> {
            if (!response.isError()) {
                callback.onSuccess(response.getResult());
            } else {
                callback.onException(new RuntimeException("Response error", new Throwable(response.getError())));
            }
        });
    }

    public RpcFuture addCallback(AsyncRpcCallback callback) {
        lock.lock();
        try {
            if (isDone()) {
                // If response already received, execute callback immediately
                runCallback(callback);
            } else {
                // Otherwise, store callback for later execution
                this.pendingCallbacks.add(callback);
            }
        } finally {
            lock.unlock();
        }
        return this;
    }

    /**
     * Invokes all registered callbacks.
     * Called when the RPC response is received to process all pending callbacks.
     */
    private void invokeCallbacks() {
        lock.lock();
        try {
            for (final AsyncRpcCallback callback : pendingCallbacks) {
                runCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        // Future status
        private final int done = 1;
        private final int pending = 0;

        protected boolean tryAcquire(int acquires) {
            return getState() == done;
        }

        protected boolean tryRelease(int releases) {
            if (getState() == pending) {
                if (compareAndSetState(pending, done)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isDone() {
            getState();
            return getState() == done;
        }
    }
}