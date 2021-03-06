package no.sample.isc.core.converter;

import no.sample.isc.core.domain.MessageEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.Date;

@Component
public class DomainSpecificObjectMessageConverter implements MessageConverter {

	@Value("${current.domain}")
	private String currentDomain;
	
	@Override
	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
		System.out.println("Acknowledge Rec --> with correlation :"+ message.getJMSCorrelationID() +" , on : "+ currentDomain);
		MessageEntity genericMessage = (MessageEntity) ((ObjectMessage) message).getObject();
		genericMessage.setJMSCorrelationID(message.getJMSCorrelationID());
		genericMessage.getComponent().setAckRecTime(new Date().getTime());
		return genericMessage;
	}

	@Override
	public Message toMessage(Object obj, Session arg1) throws JMSException, MessageConversionException {
		return null;
	}

}
