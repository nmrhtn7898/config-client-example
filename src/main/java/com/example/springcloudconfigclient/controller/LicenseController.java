package com.example.springcloudconfigclient.controller;

import com.example.springcloudconfigclient.entity.License;
import com.example.springcloudconfigclient.service.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/organizations/{organizationId}/licenses")
@RequiredArgsConstructor
public class LicenseController {

    private final LicenseService licenseService;

    @GetMapping
    public ResponseEntity getLicenses(@PathVariable Long organizationId) {
        List<License> licenses = licenseService.getLicensesByOrganizationId(organizationId);
        return ResponseEntity.ok(licenses);
    }

    @GetMapping("/{licenseId}/{clientType}")
    public ResponseEntity getLicense(@PathVariable Long organizationId, @PathVariable Long licenseId,
                                     @PathVariable String clientType) {
        License license = licenseService.getLicenseWithOrganization(licenseId, organizationId, clientType);
        return ResponseEntity.ok(license);
    }

    @PutMapping("/")
    public ResponseEntity mergeLicense(@RequestBody License request) {
        License license = licenseService.mergeLicense(request);
        return ResponseEntity.ok(license);
    }

    @DeleteMapping("/{licenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLicense(@PathVariable Long licenseId) {
        licenseService.deleteLicense(licenseId);
    }

}



