package no.sample.isc.persistance.logging.component;

import no.sample.isc.persistance.logging.events.EventPublisher;
import no.sample.isc.testclient.common.model.TestComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class LogMessageListener{

	public void listen(LoggingEntity logEntity) {
		long ping = logEntity.getRecTime()-logEntity.getSentTime();
		long pong = logEntity.getAckRecTime()-logEntity.getAckSentTime();
		long timeTaken = pong + ping;
		System.out.println("Event::-> "+logEntity.getEvent()+" , Correlation::-> "+ logEntity.getJMSCorrelationID()+ ", Originator::-> "+ logEntity.getOriginator()+ ", Source::-> "+ logEntity.getSourceAppId()+ " , Message-travel ping::-> "+ping+", pong::-> "+pong+" , ping-pong::-> "+ timeTaken);
	}
}
