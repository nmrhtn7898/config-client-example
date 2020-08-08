package com.example.springcloudconfigclient.config;

import com.example.springcloudconfigclient.util.UserContext;
import com.example.springcloudconfigclient.util.UserContextHolder;
import org.springframework.util.Assert;

import java.util.concurrent.Callable;

public final class DelegatingUserContextCallable<T> implements Callable<T> {

    private final Callable<T> delegate;

    private UserContext userContext;

    /**
     * 사용자 정의 Callable 구현체에 히스트릭스 적용된 메소드 호출(Callable로 감싸진) 쓰레드에 컨텍스트를 전파
     *
     * @param delegate    히스트릭스 적용 메소드를 호출하는 callable
     * @param userContext 유저 컨텍스트
     */
    public DelegatingUserContextCallable(Callable<T> delegate, UserContext userContext) {
        Assert.notNull(delegate, "delegate cannot be null");
        Assert.notNull(userContext, "userContext cannot be null");
        this.delegate = delegate;
        this.userContext = userContext;
    }

    public DelegatingUserContextCallable(Callable<T> delegate) {
        this(delegate, UserContextHolder.getContext());
    }

    /**
     * 히스트릭스 적용 메소드 호출 전 쓰레드풀 쓰레드로 호출되는 메소드
     * 실제 히스트릭스 적용된 메소드를 해당 클래스가 위임하여 생성시 전달 받은 컨텍스트를 히스트릭스 쓰레드에 컨텍스트 전파
     *
     * @return
     * @throws Exception
     */
    public T call() throws Exception {
        UserContextHolder.setContext(userContext); // 히스트릭스 쓰레드로 실행 시, 생성시 전달 받은 부모 쓰레드의 컨텍스트를 히스트릭스 쓰레드에 설정
        try {
            return delegate.call();
        } finally {
            this.userContext = null; // 수행 후, 쓰레드 풀에 쓰레드 반환 시 컨텍스트를 남기지 않기 위함
        }
    }

}