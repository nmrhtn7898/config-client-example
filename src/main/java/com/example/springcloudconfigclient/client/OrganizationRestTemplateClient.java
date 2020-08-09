package com.example.springcloudconfigclient.client;

import com.example.springcloudconfigclient.entity.Organization;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OrganizationRestTemplateClient {

    private final RestTemplate restTemplate;

    // 히스트릭스 회로 차단기 적용 메소드, 프록시 및 별도 쓰레드 풀로 해당 메소드의 호출을 관리
    // 기본 1000밀리초(1초) 지연시 호출 중단, HystrixRuntimeException, DB 및 다른 서비스 호출 시 동일한 애노테이션 적용 방식으로 사용
    // 별도 프로퍼티 없이 사용시 회로 차단 적용 모든 메소드들을 동일 쓰레드 풀로 사용(벌크 헤드 패턴 적용 필요)
    @HystrixCommand(
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "12000"), // 타임 아웃 12초 적용
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"), // 히스트릭스가 호출 차단을 고려하는데 필요한 일정 주기에서의 최소 호출 횟수(10) 설정, 호출이 실패해도 호출 횟수가 설정한 횟수(10)이하라면 차단하지 않음
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "75"), // 기본 차단 고려 호출 수를 넘었을 때, 회로 차단을 고려하는데 필요한 전체 실패(타임아웃, 익셉션, Http500)비율 설정
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "7000"), // 차단 후, 히스트릭스가 차단된 서비스의 호출을 허용하여 반복적으로 회복 상태를 확인하는 시간 간격 설정(기본 5000 밀리초, 5초)
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "15000"), // 히스트릭스가 서비스 호출 문제를 반복적으로 모니터링하는 시간 간격 설정 (기본 10000 밀리초, 1초)
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "5") // metrics.rollingStats.timeInMilliseconds에서 설정한 시간 동안 호출 통계를 수집할 버킷 갯수 ex) 15초 / 5 = 3, 3초 간격으로 균등한 통계를 5개의 버킷에 수집
            }, // 회로가 차단되면 metrics.rollingStats.timeInMilliseconds에서 설정한 주기마다 서비스 호출을 모니터링하는 싸이클이 아닌 circuitBreaker.sleepWindowInMilliseconds에서 설정한 주기마다 서비스 호출을 허용하고 호출이 성공하면 회로 차단기를 초기화 하고 다시 호출을 허용하는 싸이클 수행
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30"), // 해당 서비스 호출에 사용 될 쓰레드 풀의 쓰레드 갯수 정의(벌크 헤드 패턴)
                    @HystrixProperty(name = "maxQueueSize", value = "10") // 쓰레드 풀 앞 단에 배치 할 요청 Queue의 사이즈를 정의(벌크 헤드 패턴), 가용한 쓰레드 풀을 초과하는 경우 Queue에서 대기하고 요청 수가 Queue를 초과하는 경우 Queue에 여유가 생길때 까지 쓰레드 풀에 대한 추가 요청 실패
            },
            threadPoolKey = "licenseByOrganizationIdTP", // 해당 회로차단 서비스 호출에 사용 될 쓰레드 풀 고유명 정의(벌크 헤드 패턴)
            fallbackMethod = "buildFallBack" // 자원(DB, 서비스) 타임 아웃 및 실패 시 호출되는 메소드 적용(폴백 패턴)
    )
    public Organization getOrganization(Long organizationId) {
        ResponseEntity<Organization> exchange = restTemplate
                .exchange("http://testservice2/v1/organizations/{organizationId}", HttpMethod.GET, null, Organization.class, organizationId);
        return exchange.getBody();
    }

    // 히스트릭스 폴백 메소드, 회로차단이 적용된 메소드와 동일한 클래스에 위치해야한다.
    // 다른 DB를 조회하거나 다른 서비스를 호출하는 등의 처리를 할 수 있다, 단 폴백에서의 처리가 또 다른 폴백을 일크실 수 있다는 점을 고려해야함
    // 예를 들어 항상 최신의 고객 데이터를 조회하여 제공하지만 장애가 있는 경우, 다른 서비스나 DB의 이전의 요약되거나 정리된 고객 정보를 제공하는 경우
    public Organization buildFallBack(Long organizationId) {
        Organization organization = new Organization();
        organization.setName("Sorry!! can not found organization in available");
        return organization;
    }

}
