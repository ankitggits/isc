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

    private static final String process = "alpha";
    private static final String sink = "delta-done";

    public static void main(String[] args) {

        System.setProperty("server.port", ServerInfo.port);
        System.setProperty("current.domain", "delta");
        System.setProperty("current.event.process", process);
        System.setProperty("current.event.sink", sink);
        SpringApplication.run(Application.class, args);
    }

    @Bean(process)
    public SomeActionService someActionService1(){
        return new SomeActionService();
    }
}
