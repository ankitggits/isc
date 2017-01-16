import no.sample.isc.core.util.ServerInfo;
import no.sample.isc.testclient.common.service.SomeActionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Ankit on 13-01-2017.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"no.sample.isc"})
@EnableDiscoveryClient
public class Application {

    private static final String process = "delta";

    public static void main(String[] args) {
        System.setProperty("server.port", ServerInfo.port);
        System.setProperty("current.domain", "alpha");
        System.setProperty("current.event.process", process);
        SpringApplication.run(Application.class, args);
    }

    @Bean(process)
    public SomeActionService someActionService(){
        return new SomeActionService();
    }
}
