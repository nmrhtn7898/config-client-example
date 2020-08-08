package com.example.springcloudconfigclient.interceptor;

import com.example.springcloudconfigclient.util.UserContext;
import com.example.springcloudconfigclient.util.UserContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * RestTemplate 인스턴스를 통해 서비스 인스턴스들로 라우팅 되는 요청을 인터셉트하여 헤더를 설정
 */
public class UserContextInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        UserContext context = UserContextHolder.getContext();
        headers.add(UserContext.CORRELATION_ID, context.getCorrelationId());
        headers.add(UserContext.AUTH_TOKEN, context.getAuthToken());
        return execution.execute(request, body);
    }

}