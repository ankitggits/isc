package no.sample.isc.core.converter;

import no.sample.isc.core.domain.MessageEntity;
import no.sample.isc.binder.servicebus.util.MessageUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.Date;

@Component
public class DomainByteMessageConverter implements MessageConverter{
	
	@Value("${current.domain}")
	private String currentDomain;
	
	@Override
	public MessageEntity fromMessage(Message message) throws JMSException {
		System.out.println("Received to:-" + currentDomain +" ,with correlation :"+ message.getJMSCorrelationID() +" ,on : "+ null);
		MessageEntity genericMessage = MessageUtility.getEntity((BytesMessage) message);
		genericMessage.setJMSCorrelationID(message.getJMSCorrelationID());
		genericMessage.setSourceAppId(message.getStringProperty("sourceAppId"));
		genericMessage.getComponent().setRecTime(new Date().getTime());
		return genericMessage;

	}
	
	@Override
	public Message toMessage(Object obj, Session session) throws JMSException, MessageConversionException {
		return null;
	}
}