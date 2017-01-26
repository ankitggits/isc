package no.sample.isc.persistance.logging.events;

import no.sample.isc.testclient.common.model.TestComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ankit on 26-01-2017.
 */
@RestController
public class EventController {

    @Autowired
    EventPublisher publisher;

    @RequestMapping("/event")
    @ResponseStatus(HttpStatus.OK)
    public void publish(){
        publisher.publish(new TestComponent("hiiiii"));
    }

}
