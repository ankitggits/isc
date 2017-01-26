package no.sample.isc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by svn_admin on 17/01/2017.
 */
//@RestController
public class ConfigTestController {

    @Value("${alpha.client.test}")
    String value;

    @Autowired
    ApplicationConfig applicationConfig;

    @RequestMapping("/config")
    public String getConfig(){
        return value;
    }
}
