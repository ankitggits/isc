package no.sample.isc.binder.servicebus.component;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import no.sample.isc.core.component.IMessageTemplate;
import no.sample.isc.core.component.ListenerRegistry;
import no.sample.isc.core.component.ServiceExecutor;
import no.sample.isc.core.component.ValueUpdateListener;
import no.sample.isc.core.domain.GenericComponent;
import no.sample.isc.core.domain.MessageEntity;
import no.sample.isc.core.domain.MessageIdentifier;
import no.sample.isc.binder.servicebus.util.MessageUtility;
import no.sample.isc.binder.servicebus.util.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import rx.Observable;
import rx.Subscriber;

import javax.jms.BytesMessage;
import javax.jms.Topic;

@Component
public class MessageTemplate implements IMessageTemplate {

	@Autowired
	ListenerRegistry listenerRegistry;

	@Autowired
	@Qualifier("sbTopic")
	Topic topic;

	@Autowired
	JmsTemplate jmsTemplate;

	@Value("${current.domain}")
	private String currentDomain;

	@Autowired
	ServiceExecutor serviceExecutor;

	@Autowired
	ServerInfo serverInfo;

	@Override
	public void send(String opCode, GenericComponent component) {
		validateParam(opCode, component);
		String correlationID = generateCorrelationID();
		try{
			System.out.println("Sent to:-" + topic.getTopicName() +" ,with correlation :"+ correlationID +" , from : "+ currentDomain);
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
				ValueUpdateListener listener = new ValueUpdateListener() {

					@Override
					public void onValueChanged(GenericComponent changedComponent) {
						if (subscriber.isUnsubscribed()) {
							listenerRegistry.unregisterListener(this);

						} else {
							subscriber.onNext(changedComponent);
						}

						subscriber.onCompleted();
					}

					@Override
					public MessageIdentifier getIdentifier() {
						return identifier;
					}
				};
				listenerRegistry.registerListener(listener);
				try{
					System.out.println("Sent to:-" + topic.getTopicName() +" ,with correlation :"+ correlationID +" , from : "+ currentDomain);
					jmsTemplate.send(topic, createMessage(opcode, correlationID, component));
				} catch (Exception e) {
					System.out.print("ServiceException encountered: ");
					System.out.println(e.getMessage());
				}
			}
		});
	}

	public String generateCorrelationID() {
		return UUID.randomUUID().toString();
	}

	private void validateParam(String opCode, GenericComponent component) {
		Assert.notNull(opCode, "Operation code must not be null");
		Assert.notNull(component, "Component must not be null");
	}

	@Override
	public void sendCallback(MessageEntity messageEntity) {
		validateParamForCallback(messageEntity);
		System.out.println("Acknowledgement Sent to:-" + topic +" ,with correlation :"+ messageEntity.getJMSCorrelationID() +" , from : "+ currentDomain);
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

	private BrokeredMessage createBrokeredMessage(String opcode, String correlationID, GenericComponent component, String replyTo, String sourceAppId) throws IOException {
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
	}

	public MessageCreator createMessage(String opcode, String correlationID, GenericComponent component) {

		MessageCreator messageCreator = (session) -> {
			BytesMessage message = session.createBytesMessage();
			message.setJMSCorrelationID(correlationID);
			message.setStringProperty("event", opcode);
			message.setStringProperty("sourceAppId", String.valueOf(serverInfo.getPort()));
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
			message.setStringProperty("sourceAppId", entity.getSourceAppId());
			message.setJMSCorrelationID(entity.getJMSCorrelationID());
			entity.getComponent().setAckSentTime(new Date().getTime());
			message.writeBytes(MessageUtility.serialize(entity));
			return message;
		};
		return messageCreator;
	}

}
