# Spring Cloud Client Server Example

1\. 설정 방법 
* bootstrap.yml 설정 파일에 아래와 같이 클라이언트 애플리케이션 이름, 프로파일, 컨피그 서버 호스트 설정
* pom.xml spring cloud starter config(client) 의존성 및 버전 관리 설정  

###### bootstrap.yml

```$xslt
spring:
  application:
    name: testservice
  profiles:
    active: default
  cloud:
    config:
      uri: http://localhost:8888
```

###### pom.xml

```$xslt
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
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
읽어 오기 위한 설정 값을(현재 애플리케이션 이름, 프로파일, 컨피그 서버 호스트) 설정한다. 하지만 반드시 bootstrap.yml
파일에 설정하지 않고 application.yml 파일에 작성하여도 상관 없다.
* bootstrap.yml, application.yml에 모두 동일한 설정 값을 지정하면 bootstrap -> application 순으로 읽으므로
application.yml 설정 파일에 있는 값이 overwrite 하고 그 값을 사용한다.
* 기존에 bootstrap.yml, application.yml 파일에 작성된 설정 값을 외부 컨피그 서버를 통해 동일한 설정 값을 가져오는 경우
외부 컨피그 서버에서 가져온 설정 값으로 overwrite 하고 그 값을 사용한다.


```