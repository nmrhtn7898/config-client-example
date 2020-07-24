package com.example.springcloudconfigclient.service;

import com.example.springcloudconfigclient.clients.OrganizationDiscoveryClient;
import com.example.springcloudconfigclient.clients.OrganizationFeignClient;
import com.example.springcloudconfigclient.clients.OrganizationRestTemplateClient;
import com.example.springcloudconfigclient.entity.License;
import com.example.springcloudconfigclient.entity.Organization;
import com.example.springcloudconfigclient.respository.LicenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LicenseService {

    private final LicenseRepository licenseRepository;

    private final OrganizationDiscoveryClient organizationDiscoveryClient;

    private final OrganizationRestTemplateClient organizationRestTemplateClient;

    private final OrganizationFeignClient organizationFeignClient;

    private Organization getOrganizaiton(Long organizationId, String clientType) {
        switch (clientType) {
            case "rest":
                return organizationRestTemplateClient.getOrganization(organizationId);
            case "discovery":
                return organizationDiscoveryClient.getOrganization(organizationId);
            default:
                return organizationFeignClient.getOrganization(organizationId);
        }
    }


    @Transactional(readOnly = true)
    public License getLicenseWithOrganization(Long id, Long organizationId, String clientType) {
        License license = licenseRepository.findByIdAndOrganizationId(id, organizationId);
        Organization organizaiton = getOrganizaiton(organizationId, clientType);
        license.setOrganization(organizaiton);
        return license;
    }

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
