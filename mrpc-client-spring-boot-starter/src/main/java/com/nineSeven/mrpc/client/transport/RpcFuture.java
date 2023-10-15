package com.nineSeven.mrpc.client.transport;

import lombok.Data;

import java.util.concurrent.*;

@Data
public class RpcFuture<T> implements Future<T> {

    private T response;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    /**
     * response不为空表示完成
     * @return
     */
    @Override
    public boolean isDone() {
        return this.response != null;
    }

    /**
     * countDownLatch减到0表示数据获取成功
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public T get() throws InterruptedException, ExecutionException {
        countDownLatch.await();
        return response;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if(countDownLatch.await(timeout, unit)) {
            return response;
        }
        return null;
    }

    public void countDown() {
        this.countDownLatch.countDown();
    }
}
