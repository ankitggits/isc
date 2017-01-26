package no.sample.isc.events;

import no.sample.isc.persistance.logging.events.EventConfig;
import no.sample.isc.persistance.logging.events.EventPublisher;
import no.sample.isc.testclient.common.model.TestComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Ankit on 26-01-2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EventConfig.class})
public class EventDrivenTest {

    @Autowired
    EventPublisher publisher;

    @Test
    public void publishTest(){
        publisher.publish(new TestComponent("hiiiii"));
    }

}
