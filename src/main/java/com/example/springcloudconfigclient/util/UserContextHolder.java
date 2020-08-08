package com.example.springcloudconfigclient.util;


import org.springframework.util.Assert;

/**
 * 각 쓰레드마다 사용하는 고유한 해시 맵 저장소에 접근하고 관리하는 ThreadLocal 인스턴스에 UserContext 객체를 관리
 */
public class UserContextHolder {

    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();

    public static UserContext getContext(){
        UserContext context = userContext.get();
        if (context == null) {
            context = new UserContext();
            userContext.set(context);
        }
        return context;
    }

    public static void setContext(UserContext context) {
        Assert.notNull(context, "Only non-null UserContext instances are permitted");
        userContext.set(context);
    }

}
