package com.example.springcloudconfigclient.clients;

import com.example.springcloudconfigclient.entity.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OrganizationRestTemplateClient {

    private final RestTemplate restTemplate;

    public Organization getOrganization(Long organizationId) {
        ResponseEntity<Organization> exchange = restTemplate
                .exchange("http://testservice2/v1/organizations/{organizationId}", HttpMethod.GET, null, Organization.class, organizationId);

        return exchange.getBody();
    }

}
