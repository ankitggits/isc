package no.sample.isc.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by svn_admin on 17/01/2017.
 */
@Component
public class ConfigPropertyInitializer implements InitializingBean{

    @Autowired
    private ApplicationConfig appConfig;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("application config property::-> "+ appConfig.getTest());
    }
}
