package no.sample.isc.persistance.logging.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Ankit on 26-01-2017.
 */
@Setter
@Getter
public class DeltaEvent implements IEvent{
    public String val;
}
