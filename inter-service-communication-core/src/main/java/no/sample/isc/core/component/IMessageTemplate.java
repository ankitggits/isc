package no.sample.isc.core.component;

import no.sample.isc.core.domain.GenericComponent;
import no.sample.isc.core.domain.MessageEntity;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import rx.Observable;

import javax.jms.ObjectMessage;
import java.util.Date;
import java.util.UUID;

public interface IMessageTemplate {

	void send(String opCode, GenericComponent component);

	Observable<GenericComponent> sendAndReceiveObservable(String opcode, GenericComponent component);

	// GenericComponent sendAndReceive(String opcode, GenericComponent component);

	default MessageCreator createMessage(String opcode, String correlationID, GenericComponent component, String replyTo, String sourceAppId) {

		MessageCreator messageCreator = (session) -> {
			ObjectMessage message = session.createObjectMessage();
			message.setJMSCorrelationID(correlationID);
			message.setStringProperty("appId", "ALL");
			component.setSentTime(new Date().getTime());
			message.setObject(new MessageEntity(opcode, component));
			if (!StringUtils.isEmpty(replyTo)) {
				Assert.notNull(sourceAppId, "Source app Id code must be present when callback is expected");
				message.setStringProperty("sourceAppId", sourceAppId);
				message.setStringProperty("replyTo", replyTo);
			}
			return message;
		};
		return messageCreator;
	}
	
	default MessageCreator createCallbackMessage(MessageEntity entity) {

		MessageCreator messageCreator = (session) -> {
			ObjectMessage message = session.createObjectMessage();
			message.setStringProperty("appId", entity.getSourceAppId());
			message.setJMSCorrelationID(entity.getJMSCorrelationID());
			entity.getComponent().setAckSentTime(new Date().getTime());
			message.setObject(entity);
			return message;
		};
		return messageCreator;
	}

	default String generateCorrelationID() {
		return UUID.randomUUID().toString();
	}

	void sendCallback(MessageEntity messageEntity);

}
