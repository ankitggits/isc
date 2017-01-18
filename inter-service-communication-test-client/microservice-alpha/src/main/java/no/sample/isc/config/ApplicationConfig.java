package no.sample.isc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Created by svn_admin on 17/01/2017.
 */
@Getter
@Setter
@Component
//@RefreshScope
@ConfigurationProperties("alpha.client")
public class ApplicationConfig {
        private String test;
}
