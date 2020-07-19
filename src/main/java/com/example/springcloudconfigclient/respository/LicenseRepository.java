package com.example.springcloudconfigclient.respository;

import com.example.springcloudconfigclient.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LicenseRepository extends JpaRepository<License, Long> {

    List<License> findByOrganizationId(Long organizationId);

    License findByIdAndOrganizationId(Long id, Long organizationId);

}
