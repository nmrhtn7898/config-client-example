package com.example.springcloudconfigclient.clients;

import com.example.springcloudconfigclient.entity.Organization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient("testservice2")
public interface OrganizationFeignClient {

//    @GetMapping(value = "/v1/organizations/{organizationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
//    Organization getOrganization(@PathVariable Long organizationId);

}
