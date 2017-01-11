package no.sample.isc.core.converter;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import no.vipps.platform.messaging.domain.MessageEntity;

@Component
public class DomainMessageConverter implements MessageConverter{
	
	@Value("${current.domain}")
	private String currentDomain;
	
	@Override
	public MessageEntity fromMessage(Message message) throws JMSException, MessageConversionException {
		System.out.println("Received to:-" + message.getJMSDestination().toString() +" ,with correlation :"+ message.getJMSCorrelationID() +" ,on : "+ currentDomain);
		MessageEntity genericMessage = (MessageEntity) ((ObjectMessage) message).getObject();
		genericMessage.setJMSCorrelationID(message.getJMSCorrelationID());
		genericMessage.setSourceAppId(message.getStringProperty("sourceAppId"));
		genericMessage.setReplyTo(message.getStringProperty("replyTo"));
		genericMessage.getComponent().setRecTime(new Date().getTime());
		return genericMessage;
	}
	
	@Override
	public Message toMessage(Object obj, Session session) throws JMSException, MessageConversionException {
		return null;
	}
}