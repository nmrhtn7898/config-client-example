package com.example.springcloudconfigclient.config;

import com.example.springcloudconfigclient.util.UserContextHolder;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.properties.HystrixProperty;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 히스트릭스 회로 차단을 적용한 프록시 메소드를 실행하는 쓰레드 풀에서 관리하는 쓰레드에 컨텍스트를 전파하기 위함
 * 애플리케이션에서 하나의 히스트릭스 병행성 전략을 사용할 수 있기 때문에,
 */
public class ThreadLocalAwareStrategy extends HystrixConcurrencyStrategy {

    private final HystrixConcurrencyStrategy hystrixConcurrencyStrategy;

    public ThreadLocalAwareStrategy(HystrixConcurrencyStrategy hystrixConcurrencyStrategy) {
        this.hystrixConcurrencyStrategy = hystrixConcurrencyStrategy;
    }

    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return hystrixConcurrencyStrategy != null ?
                hystrixConcurrencyStrategy.getBlockingQueue(maxQueueSize) :
                super.getBlockingQueue(maxQueueSize);
    }

    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(HystrixRequestVariableLifecycle<T> rv) {
        return hystrixConcurrencyStrategy != null ?
                hystrixConcurrencyStrategy.getRequestVariable(rv) :
                super.getRequestVariable(rv);
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixProperty<Integer> corePoolSize,
                                            HystrixProperty<Integer> maximumPoolSize, HystrixProperty<Integer> keepAliveTime,
                                            TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        return hystrixConcurrencyStrategy != null ?
                hystrixConcurrencyStrategy.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue) :
                super.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * 히스트릭스를 적용한 메소드를 감싸는 Callable을 추가로 Callable로 감싸 커스텀한 전후 처리를 할 수 있도록 제공되는 메소드
     * 해당 메소드 수행은 부모 쓰레드에서 수행되기 때문에 부모 쓰레드의 컨텍스트를 생성자로 전달하여 히스트릭스 쓰레드로 전파할 수 있음
     * @param callable
     * @param <T>
     * @return
     */
    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return hystrixConcurrencyStrategy != null ?
                hystrixConcurrencyStrategy.wrapCallable(new DelegatingUserContextCallable<>(callable, UserContextHolder.getContext())) :
                super.wrapCallable(new DelegatingUserContextCallable<>(callable, UserContextHolder.getContext()));
    }

}
