package no.sample.isc.testclient.gateway.controller;

import no.sample.isc.testclient.gateway.service.LoadBalancerClientURLProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by svn_admin on 15/01/2017.
 */
@RestController
public class GatewayController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired(required = false)
    DiscoveryClient discoveryClient;

    @Autowired
    LoadBalancerClientURLProvider loadBalancerClientURLProvider;

    @RequestMapping("/{event}/{value}")
    public ResponseEntity<?> invokeServerWithRibbon(@PathVariable("event") String event, @PathVariable("value") String value){
        return restTemplate.getForEntity(loadBalancerClientURLProvider.provide(event, value), String.class);
    }
}
