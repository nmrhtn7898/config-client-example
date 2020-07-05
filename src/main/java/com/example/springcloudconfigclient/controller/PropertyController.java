package com.example.springcloudconfigclient.controller;

import com.example.springcloudconfigclient.annotation.WildCardPathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static org.springframework.util.StringUtils.hasText;

@RefreshScope
@RestController
public class PropertyController {

    @Autowired
    private Environment environment;

    @Value("${name.firstname}")
    private String firstname;

    @Value("${name.lastname}")
    private String lastname;

    @Value("${name.message}")
    private String message;

    @GetMapping("/property/**")
    public String getProperty(@WildCardPathVariable String[] keys) {
        String key = Arrays.stream(keys)
                .reduce((x, y) -> x + "." + y)
                .orElse("");
        String property = environment.getProperty(key);
        property = hasText(property) ? property : "not exist property!!";
        return property;
    }

}
