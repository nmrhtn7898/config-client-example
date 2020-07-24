package com.example.springcloudconfigclient;

import com.example.springcloudconfigclient.entity.License;
import com.example.springcloudconfigclient.service.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TestDataInitializer implements org.springframework.boot.ApplicationRunner {

    private final LicenseService licenseService;


    @Value("${name.firstname}")
    private String firstname;

    @Value("${name.lastname}")
    private String lastname;

    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {
        License license1 = License.builder()
                .name("라이센스1")
                .type("타입1")
                .max(5)
                .allocated(10)
                .comment(firstname + lastname)
                .organizationId(1L)
                .build();

        License license2 = License.builder()
                .name("라이센스2")
                .type("타입2")
                .max(9)
                .allocated(4)
                .comment(firstname + lastname)
                .organizationId(2L)
                .build();

        licenseService.mergeLicense(license1);
        licenseService.mergeLicense(license2);
    }
}
