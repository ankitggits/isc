package no.sample.isc.persistance.logging.events;

import no.sample.isc.core.domain.GenericComponent;
import no.sample.isc.testclient.common.model.TestComponent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Created by Ankit on 26-01-2017.
 */
@Component
public class EventPublisher {

    private final ApplicationEventPublisher publisher;

    @Autowired
    public EventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(GenericComponent component) {
        DeltaEvent deltaEvent = new DeltaEvent();
        BeanUtils.copyProperties(component,deltaEvent);
        publisher.publishEvent(new GenericEventualEvent(new DeltaEvent()));
    }

}
