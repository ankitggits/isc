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
public class DomainObjectMessageConverter implements MessageConverter{
	
	@Value("${current.domain}")
	private String currentDomain;
	
	@Override
	public MessageEntity fromMessage(Message message) throws JMSException, MessageConversionException {
		System.out.println("Received to:-" + message.getJMSDestination() +" ,with correlation :"+ message.getJMSCorrelationID() +" ,on : "+ currentDomain);
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