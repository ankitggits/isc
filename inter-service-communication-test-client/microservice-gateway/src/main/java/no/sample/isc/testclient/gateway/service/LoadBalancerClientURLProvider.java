package no.sample.isc.testclient.gateway.service;

import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;

import javax.naming.ServiceUnavailableException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by svn_admin on 15/01/2017.
 */
@Component
public class LoadBalancerClientURLProvider {

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    public String provide(String event, String value) {
        List<String> services = discoveryClient.getServices();
        Iterator<String> iterator = services.iterator();
        System.out.print("available services are:::-> ");
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }

        ServiceInstance serviceInstance = this.loadBalancerClient.choose(event);
        if (serviceInstance != null) {
            String url = String.format("http://%s:%d/ping-pong/".concat(event+"/").concat(value), serviceInstance.getHost(), serviceInstance.getPort());
            System.out.println("Service to call::->"+ url);
            return url;
        }else{
            throw new RuntimeException("Unable to locate a "+event+" service");
        }
    }
}
