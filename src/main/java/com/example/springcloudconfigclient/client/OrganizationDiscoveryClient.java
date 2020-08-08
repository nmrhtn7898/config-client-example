package com.example.springcloudconfigclient.client;

//@Component
//@RequiredArgsConstructor
public class OrganizationDiscoveryClient {

    /*private final DiscoveryClient discoveryClient;

    public Organization getOrganization(Long organizationId) {
        RestTemplate restTemplate = new RestTemplate();
        List<ServiceInstance> instances = discoveryClient.getInstances("testservice2");

        if (instances.isEmpty()) {
            return null;
        }

        String instanceUri = instances.get(0).getUri().toString();
        String serviceUri = String.format("%s/v1/organizations/%s", instanceUri, organizationId);

        ResponseEntity<Organization> exchange = restTemplate
                .exchange(serviceUri, HttpMethod.GET, null, Organization.class, organizationId);

        return exchange.getBody();
    }*/

}
