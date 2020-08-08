package com.example.springcloudconfigclient.config;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
public class WebServerConfig implements WebMvcConfigurer {

    private final HystrixConcurrencyStrategy hystrixConcurrencyStrategy; // 기존 히스트릭스 병행성 전략 객체

    private final WildCardPathVariableArgumentResolver wcpvar;

    public WebServerConfig(@Autowired(required = false) HystrixConcurrencyStrategy hystrixConcurrencyStrategy,
                           WildCardPathVariableArgumentResolver wildCardPathVariableArgumentResolver) {
        this.hystrixConcurrencyStrategy = hystrixConcurrencyStrategy;
        this.wcpvar = wildCardPathVariableArgumentResolver;
    }

    @PostConstruct
    public void init() {
        // 커스텀하게 설정한 히스트릭스 병행성 전략을 등록하기 위해 기존 히스트릭스 플러그인 정보를 유지 보관
        HystrixEventNotifier hystrixEventNotifier = HystrixPlugins.getInstance().getEventNotifier();
        HystrixMetricsPublisher hystrixMetricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
        HystrixPropertiesStrategy hystrixPropertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();
        HystrixCommandExecutionHook hystrixCommandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();
        // 기존 히스트릭스 리셋
        HystrixPlugins.reset();
        // 유지 보관한 기존 히스트릭스 플러그인 정보를 다시 셋팅하며, 커스텀하게 설정한 병행성 전략을 등록
        HystrixPlugins.getInstance().registerConcurrencyStrategy(new ThreadLocalAwareStrategy(hystrixConcurrencyStrategy));
        HystrixPlugins.getInstance().registerEventNotifier(hystrixEventNotifier);
        HystrixPlugins.getInstance().registerMetricsPublisher(hystrixMetricsPublisher);
        HystrixPlugins.getInstance().registerPropertiesStrategy(hystrixPropertiesStrategy);
        HystrixPlugins.getInstance().registerCommandExecutionHook(hystrixCommandExecutionHook);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(wcpvar);
    }

}
