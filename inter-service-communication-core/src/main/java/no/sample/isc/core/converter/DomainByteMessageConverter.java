package no.sample.isc.core.converter;

import no.sample.isc.core.util.MessageUtility;
import no.sample.isc.core.domain.MessageEntity;
import no.sample.isc.core.util.ServerInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;

@Component
public class DomainByteMessageConverter implements MessageConverter{
	
	@Value("${current.domain}")
	private String currentDomain;
	
	@Override
	public MessageEntity fromMessage(Message message) throws JMSException {
		MessageEntity genericMessage = MessageUtility.getEntity((BytesMessage) message);
		genericMessage.setJMSCorrelationID(message.getJMSCorrelationID());
		genericMessage.setSourceAppId(message.getStringProperty("originator"));
		genericMessage.getComponent().setRecTime(new Date().getTime());
		System.out.println("Received --> with correlation :"+ message.getJMSCorrelationID() +" ,on domain: "+ currentDomain +" and instance: "+ ServerInfo.port + ", from instance: "+genericMessage.getSourceAppId());
		return genericMessage;
	}
	
	@Override
	public Message toMessage(Object obj, Session session) throws JMSException, MessageConversionException {
		return null;
	}
}