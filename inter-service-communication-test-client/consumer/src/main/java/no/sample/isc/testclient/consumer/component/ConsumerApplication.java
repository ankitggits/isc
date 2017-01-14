package no.sample.isc.testclient.consumer.component;

import no.sample.isc.binder.servicebus.config.ServiceBusConfig;
import no.sample.isc.core.config.JMSConfig;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Created by Ankit on 13-01-2017.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"no.sample.isc"})
public class ConsumerApplication {

    public static void main(String[] args) {
        System.setProperty("server.port", "0");
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
