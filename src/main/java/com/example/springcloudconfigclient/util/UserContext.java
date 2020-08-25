package com.example.springcloudconfigclient.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class UserContext {

    public static final String CORRELATION_ID = "nuguri-correlation-id";

    public static final String USER_ID = "nuguri-user-id";

    public static final String ORG_ID = "nuguri-org-id";

    private String correlationId;

    private String authToken;

    private String userId;

    private String orgId;

}