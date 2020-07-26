package com.example.springcloudconfigclient.service;

import com.example.springcloudconfigclient.clients.OrganizationRestTemplateClient;
import com.example.springcloudconfigclient.entity.License;
import com.example.springcloudconfigclient.entity.Organization;
import com.example.springcloudconfigclient.respository.LicenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@DefaultProperties( 클래스 레벨 히스트릭스 환경 설정
//        commandProperties = {
//                @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "12000")
//        }
//)
@Service
@Transactional
@RequiredArgsConstructor
public class LicenseService {

    private final LicenseRepository licenseRepository;

    private final OrganizationRestTemplateClient organizationRestTemplateClient;

//    private final OrganizationDiscoveryClient organizationDiscoveryClient;

//    private final OrganizationFeignClient organizationFeignClient;

    public Organization getOrganization(Long organizationId, String clientType) {
        switch (clientType) {
//            case "feign":
//                return organizationFeignClient.getOrganization(organizationId);
//            case "discovery":
//                return organizationDiscoveryClient.getOrganization(organizationId);
            default:
                return organizationRestTemplateClient.getOrganization(organizationId);
        }
    }


    @Transactional(readOnly = true)
    public License getLicenseWithOrganization(Long id, Long organizationId, String clientType) {
        License license = licenseRepository.findByIdAndOrganizationId(id, organizationId);
        Organization organization = getOrganization(organizationId, clientType);
        license.setOrganization(organization);
        return license;
    }

    @Transactional(readOnly = true)
    public List<License> getLicensesByOrganizationId(Long organizationId) {
        return licenseRepository.findByOrganizationId(organizationId);
    }

    public License mergeLicense(License license) {
        return licenseRepository.save(license);
    }

    public void deleteLicense(Long id) {
        licenseRepository.deleteById(id);
    }

}
