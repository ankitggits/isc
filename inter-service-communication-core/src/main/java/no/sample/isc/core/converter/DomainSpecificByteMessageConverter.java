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
public class DomainSpecificByteMessageConverter implements MessageConverter {

	@Value("${current.domain}")
	private String currentDomain;

	@Override
	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
		System.out.println("Acknowledge Rec to:-" + currentDomain +" ,with correlation :"+ message.getJMSCorrelationID() +" , on domain: "+ currentDomain +" and instance: "+ ServerInfo.port);
		MessageEntity genericMessage = MessageUtility.getEntity((BytesMessage) message);
		genericMessage.setJMSCorrelationID(message.getJMSCorrelationID());
		genericMessage.getComponent().setAckRecTime(new Date().getTime());
		return genericMessage;
	}

	@Override
	public Message toMessage(Object obj, Session arg1) throws JMSException, MessageConversionException {
		return null;
	}

}
