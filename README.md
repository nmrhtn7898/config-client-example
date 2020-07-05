# Spring Cloud Client Server Example

1\. 설정 방법 
* bootstrap.yml 설정 파일에 아래와 같이 클라이언트 애플리케이션 이름, 프로파일, 컨피그 서버 호스트 설정
* application.yml 설정 파일에 아래와 같이 애플리케이션 구동 포트, 스프링 actuator refresh 엔드포인트
를 통해 컨피그 서버 외부 설정 파일 변경 적용 가능 하도록 설정
* pom.xml spring cloud starter config(client), spring security rsa  의존성 및 버전 관리 설정  

###### bootstrap.yml

```$xslt
encrypt:
  key: nuguribom [컨피그 서버에서 받은 암호화 프로퍼티 복호화 키]
spring:
  application:
    name: testservice
  profiles:
    active: default
  cloud:
    config:
      uri: http://localhost:8888
```

###### application.yml

```$xslt
server:
  port: 8000
management:
  endpoints:
    web:
      exposure:
        include: refresh,env [애플리케이션 모니터링 refresh, env 엔드포인트 허용]
```

###### pom.xml

```$xslt
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-rsa</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

2\. 작동 방식  
* [Spring Cloud Config Server](https://github.com/nmrhtn7898/config-server) 엔드 포인트를 통해 외부 설정 정보를 읽는다.
* bootstrap.yml 파일에서 설정한 application.name, profiles.active 값으로 컨피그 서버에서 
/{application-name}/{profiles} 엔드포인트로 접근하여 현재 애플리케이션의 구동 프로파일의 설정 값을 읽음.
* bootstrap.yml 파일은 application.yml 파일 로드전에 읽으며 주로 애플리케이션 구동에 필요한 외부 설정 값을 
읽어 오기 위한 spring cloud 설정 값을(현재 애플리케이션 이름, 프로파일, 컨피그 서버 호스트) 설정한다.
* bootstrap.yml, application.yml에 모두 동일한 설정 값을 지정하면 bootstrap -> application 순으로 읽으므로
application.yml 설정 파일에 있는 값이 overwrite 하고 그 값을 사용한다.
* 기존에 bootstrap.yml, application.yml 파일에 작성된 설정 값을 외부 컨피그 서버를 통해 동일한 설정 값을 가져오는 경우
외부 컨피그 서버에서 가져온 설정 값으로 overwrite 하고 그 값을 사용한다.
* 외부 설정 파일의 암호화 프로퍼티의 경우 컨피그 서버에서 복호화 하지않고 클라이언트에게 복호화를 위임한 경우 {cipher} prefix 
적용되어 있으며 클라이언트에서 encrypt.key 사용하여 복호화를 수행한다.

3\. 사용 방법
* 외부 컨피그 서버에서 읽어온 설정 파일들의 내용을 접근할 수 있도록 /property/** 엔드포인트 노출.
* ex) /property/name/firstname, /property/name/lastname, /property/name/message 와 같은 형태로 사용 가능.
* 프로퍼티 접근 엔드포인트 호출 URL PATH를 사용하여 프로퍼티 값을 읽은 후 응답한다.

###### [프로퍼티 접근 엔드포인트](https://github.com/nmrhtn7898/config-client-example/blob/master/src/main/java/com/example/springcloudconfigclient/controller/PropertyController.java)
```$xslt

    @GetMapping("/property/**")
    public String getProperty(@WildCardPathVariable String[] keys) {
        String key = Arrays.stream(keys)
                .reduce((x, y) -> x + "." + y)
                .orElse("");
        String property = environment.getProperty(key);
        property = hasText(property) ? property : "not exist property!!";
        return property;
    }

```

###### [프로퍼티 엔드포인트 Argument Resolver](https://github.com/nmrhtn7898/config-client-example/blob/master/src/main/java/com/example/springcloudconfigclient/config/WildCardPathVariableArgumentResolver.java)

```$xslt

@Component
public class WildCardPathVariableArgumentResolver implements HandlerMethodArgumentResolver {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 해당 애노테이션 적용된 메소드 파라미터에서 지원하는 ArgumentResolver
        return parameter.hasParameterAnnotation(WildCardPathVariable.class);
    }

    @Override
    public Object resolveArgument(MethodParameter mp, ModelAndViewContainer mavc,
                                  NativeWebRequest nwr, WebDataBinderFactory wdbf) throws Exception {
        HttpServletRequest request = (HttpServletRequest) nwr.getNativeRequest();
        // 기존 핸들러 메소드 경로 PATH ex => /property/**
        String pattern = (String) request.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE);
        // 핸들러 메소드에게 매핑된 요청 받은 실제 URL PATH ex => /property/spring/application/name
        String path = (String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        // 핸들러 메소드 경로 패턴과 요청 받은 URL 비교하여 패턴에 해당하는 PATH 부분만 추출 ex => spring/application/name
        String variablePath = antPathMatcher.extractPathWithinPattern(pattern, path);
        // spring/application/name => ["spring", "application", "name"]
        return variablePath.split(DEFAULT_PATH_SEPARATOR);
    }
}

```
 