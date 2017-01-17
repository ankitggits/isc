package no.sample.isc.persistance.logging.component;

import lombok.Getter;
import lombok.Setter;
import no.sample.isc.core.domain.GenericComponent;
import no.sample.isc.core.domain.Status;

/**
 * Created by svn_admin on 16/01/2017.
 */
@Getter
@Setter
public class LoggingEntity {

    private String JMSCorrelationID;
    private String sourceAppId;
    private String originator;
    private String event;
    private Status status;
    private long sentTime;
    private long recTime;
    private long ackSentTime;
    private long ackRecTime;
}
