package no.sample.isc.testclient.consumer.component;

import no.sample.isc.binder.servicebus.util.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ankit on 13-01-2017.
 */
@RestController
@RequestMapping("/ping")
public class PingController{

    @Autowired
    ServerInfo serverInfo;

    @RequestMapping
    @ResponseStatus(HttpStatus.OK)
    public String index() {
        return "welcome to consumer-"+serverInfo.getPort();
    }

}
