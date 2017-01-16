package no.sample.isc.binder.servicebus.component;

import java.util.Date;
import java.util.UUID;

import no.sample.isc.core.component.IMessageTemplate;
import no.sample.isc.core.component.ListenerRegistry;
import no.sample.isc.core.component.ServiceExecutor;
import no.sample.isc.core.component.ValueUpdateListener;
import no.sample.isc.core.domain.GenericComponent;
import no.sample.isc.core.domain.MessageEntity;
import no.sample.isc.core.domain.MessageIdentifier;
import no.sample.isc.core.util.MessageUtility;
import no.sample.isc.core.util.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import org.springframework.util.Assert;
import rx.Observable;
import rx.Subscriber;

import javax.jms.BytesMessage;
import javax.jms.Topic;

@Component
public class MessageTemplate implements IMessageTemplate {

	@Autowired
	private ListenerRegistry listenerRegistry;

	@Autowired
	private Topic topic;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${current.domain}")
	private String currentDomain;

	@Autowired
	private ServiceExecutor serviceExecutor;

	@Override
	public void send(String opCode, GenericComponent component) {
		validateParam(opCode, component);
		String correlationID = generateCorrelationID();
		try{
			System.out.println("Sent --> with correlation :"+ correlationID +" , from domain: "+ currentDomain +" and instance: "+ ServerInfo.port);
			jmsTemplate.send(topic, createMessage(opCode, correlationID, component));
		} catch (Exception e) {
			System.out.print("ServiceException encountered: ");
			System.out.println(e.getMessage());
		}
	}

	@Override
	public Observable<GenericComponent> sendAndReceiveObservable(String opcode, GenericComponent component) {
		String correlationID = generateCorrelationID();
		MessageIdentifier identifier = new MessageIdentifier(correlationID, "TEST");
		return Observable.create(new Observable.OnSubscribe<GenericComponent>() {

			@Override
			public void call(Subscriber<? super GenericComponent> subscriber) {
				ValueUpdateListener listener = getListener(subscriber, listenerRegistry, identifier);
				listenerRegistry.registerListener(listener);
				try{
					System.out.println("Sent --> with correlation :"+ correlationID +" , from domain: "+ currentDomain +" and instance: "+ ServerInfo.port);
					jmsTemplate.send(topic, createMessage(opcode, correlationID, component));
				} catch (Exception e) {
					System.out.print("ServiceException encountered: ");
					System.out.println(e.getMessage());
				}
			}
		});
	}

	private void validateParam(String opCode, GenericComponent component) {
		Assert.notNull(opCode, "Operation code must not be null");
		Assert.notNull(component, "Component must not be null");
	}

	@Override
	public void sendCallback(MessageEntity messageEntity) {
		validateParamForCallback(messageEntity);
		System.out.println("Acknowledgement Sent --> with correlation :"+ messageEntity.getJMSCorrelationID() +" , from domain: "+ currentDomain +" and instance: "+ ServerInfo.port);
		try{
			jmsTemplate.send(topic, createCallbackMessage(messageEntity));
		} catch (Exception e) {
			System.out.print("ServiceException encountered: ");
			System.out.println(e.getMessage());
		}
	}

	private void validateParamForCallback(MessageEntity messageEntity) {
		Assert.notNull(messageEntity, "message Entity must not be null");
		Assert.notNull(messageEntity.getJMSCorrelationID(), "correlation must not be null");
	}

	public MessageCreator createMessage(String opcode, String correlationID, GenericComponent component) {

		MessageCreator messageCreator = (session) -> {
			BytesMessage message = session.createBytesMessage();
			message.setJMSCorrelationID(correlationID);
			message.setStringProperty("event", opcode);
			message.setStringProperty("originator", ServerInfo.port);
			message.setStringProperty("sourceAppId", null);
			component.setSentTime(new Date().getTime());
			message.writeBytes(MessageUtility.serialize(new MessageEntity(opcode,component)));
			return message;
		};
		return messageCreator;
	}

	public MessageCreator createCallbackMessage(MessageEntity entity) {

		MessageCreator messageCreator = (session) -> {
			BytesMessage message = session.createBytesMessage();
			entity.setOpCode(entity.getOpCode().concat("-done"));
			message.setStringProperty("event", entity.getOpCode());
			message.setStringProperty("originator", ServerInfo.port);
			message.setStringProperty("sourceAppId", entity.getSourceAppId());
			message.setJMSCorrelationID(entity.getJMSCorrelationID());
			entity.getComponent().setAckSentTime(new Date().getTime());
			message.writeBytes(MessageUtility.serialize(entity));
			return message;
		};
		return messageCreator;
	}

}











	/*private BrokeredMessage createBrokeredMessage(String opcode, String correlationID, GenericComponent component, String replyTo, String sourceAppId) throws IOException {
		component.setSentTime(new Date().getTime());
		BrokeredMessage message = new BrokeredMessage(MessageUtility.serialize(new MessageEntity(opcode, component)));
		message.setCorrelationId(correlationID);
		message.setProperty("appId", "ALL");
		if (!StringUtils.isEmpty(replyTo)) {
			Assert.notNull(sourceAppId, "Source app Id code must be present when callback is expected");
			message.setProperty("sourceAppId", sourceAppId);
			message.setProperty("replyTo", replyTo);
		}
		return message;
	}

	private BrokeredMessage createBrokeredMessageForCallBack(MessageEntity entity) throws IOException {
		entity.getComponent().setAckSentTime(new Date().getTime());
		BrokeredMessage message = new BrokeredMessage(MessageUtility.serialize(entity));
		message.setProperty("appId", entity.getSourceAppId());
		message.setCorrelationId(entity.getJMSCorrelationID());
		return message;
	}*/
