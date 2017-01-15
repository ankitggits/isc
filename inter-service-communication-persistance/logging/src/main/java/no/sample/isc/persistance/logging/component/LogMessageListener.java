package no.sample.isc.persistance.logging.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.SmartValidator;
import rx.Observable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

@Component
public class LogMessageListener implements MessageListener{

	@Override
	public void onMessage(Message message) {
		try {
			System.out.println("Event::-> "+message.getStringProperty("event")+" , Correlation::-> "+ message.getJMSCorrelationID()+ ", Originator::-> "+ message.getStringProperty("originator")+ ", Source::-> "+ message.getStringProperty("originator"));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
