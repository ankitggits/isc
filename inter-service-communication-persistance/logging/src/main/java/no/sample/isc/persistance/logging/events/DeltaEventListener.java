package no.sample.isc.persistance.logging.events;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created by Ankit on 26-01-2017.
 */
@Component
public class DeltaEventListener {

    //@Async
    @EventListener
    void handleAsync(GenericEventualEvent<DeltaEvent> event) {
        System.out.println(event);
    }
}
