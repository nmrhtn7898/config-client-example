package com.example.springcloudconfigclient;

import com.example.springcloudconfigclient.interceptor.UserContextInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// @EnableDiscoveryClient // OrganizationDiscoveryClient, 유레카 서버에 등록된 서비스 인스턴스 디스커버리, 직접 호출 서비스를 찾고 선택 떄문에 리본의 클라이언트측 로드밸런싱 불가
// @EnableFeignClients // OrganizationFeignClient, 서비스 인스턴스 디스커버리, 리본의 클라이언트측 로드밸런싱 가능, 높은 추상화 레벨
@EnableCircuitBreaker // 히스트릭스 회로 차단기 패턴 적용, 장애서비스가 쓰레드 및 DB 커넥션을 오래 점유함으로써 서버가 다운되는 것을 막기 위한 회복 패턴
@EnableEurekaClient // 유레카 클라이언트 등록
@EnableHystrixDashboard
@EnableResourceServer
@SpringBootApplication
public class SpringCloudConfigClientApplication {

    @LoadBalanced // 스프링 RestTemplate 리본 라이브러리의 클라이언트측 로드밸런싱 적용
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(new UserContextInterceptor()); // 서비스 인스턴스들로 라우팅 되는 요청을 인터셉트하여 헤더를 설정하는 인터셉터 설정
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

/*    @LoadBalanced
    @Bean
    public OAuth2RestTemplate restTemplate(UserInfoRestTemplateFactory factory) {
        OAuth2RestTemplate restTemplate = factory.getUserInfoRestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(new UserContextInterceptor());
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }*/

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigClientApplication.class, args);
    }

}

