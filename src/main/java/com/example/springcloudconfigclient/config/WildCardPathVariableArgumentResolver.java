package com.example.springcloudconfigclient.config;

import com.example.springcloudconfigclient.annotation.WildCardPathVariable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

import java.util.logging.Handler;

import static org.springframework.util.AntPathMatcher.DEFAULT_PATH_SEPARATOR;
import static org.springframework.web.servlet.HandlerMapping.*;

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
