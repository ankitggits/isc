package no.sample.isc.persistance.logging.component;

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
