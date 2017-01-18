package no.sample.isc.persistance.logging.component;

import no.sample.isc.core.domain.MessageEntity;
import no.sample.isc.core.util.MessageUtility;
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

/**
 * Created by svn_admin on 16/01/2017.
 */
@Component
public class MessageConvertor implements MessageConverter{

        @Value("${current.domain}")
        private String currentDomain;

        @Override
        public LoggingEntity fromMessage(Message message) throws JMSException {
            MessageEntity genericMessage = MessageUtility.getEntity((BytesMessage) message);
            genericMessage.getComponent().setAckRecTime(new Date().getTime());

            LoggingEntity loggingEntity = new LoggingEntity();
            loggingEntity.setEvent(genericMessage.getOpCode());
            loggingEntity.setOriginator(message.getStringProperty("originator"));
            loggingEntity.setJMSCorrelationID(message.getJMSCorrelationID());
            loggingEntity.setSourceAppId(message.getStringProperty("sourceAppId"));
            loggingEntity.setSentTime(genericMessage.getComponent().getSentTime());
            loggingEntity.setRecTime(genericMessage.getComponent().getRecTime());
            loggingEntity.setAckSentTime(genericMessage.getComponent().getAckSentTime());
            loggingEntity.setAckRecTime(genericMessage.getComponent().getAckRecTime());

            return loggingEntity;
        }

        @Override
        public Message toMessage(Object obj, Session session) throws JMSException, MessageConversionException {
        return null;
    }

}
