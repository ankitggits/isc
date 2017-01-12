package no.sample.isc.binder.component;


import no.sample.isc.core.component.ListenerRegistry;
import no.sample.isc.core.component.ValueUpdateListener;
import no.sample.isc.core.domain.GenericComponent;
import no.sample.isc.core.domain.MessageEntity;
import no.sample.isc.core.domain.MessageIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import rx.Observable;
import rx.Subscriber;

import static no.sample.isc.core.util.MessageConstants.QUEUE;
import static no.sample.isc.core.util.MessageConstants.TOPIC;

@Component
public class MessageTemplate implements IMessageTemplate {

	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${mq.timeout}")
	private long timeout;

	@Value("${mq.skip.intradomain:true}")
	private boolean intradomain;

	@Value("${current.domain}")
	private String currentDomain;

	@Value("${current.app}")
	private String currentApp;

	@Value("${mq.pubsub:true}")
	private boolean pubsub;

	@Autowired
	private Environment env;

	@Autowired
	ServiceExecutor serviceExecutor;

	@Autowired
	ListenerRegistry listenerRegistry;

	@Override
	public Observable<GenericComponent> sendAndReceiveObservable(String opCode, GenericComponent component) {
		validateParam(opCode, component);

		String correlationID = generateCorrelationID();
		String requestedDomain = opCode.split("-")[0];

		if (intradomain && currentDomain.equals(requestedDomain)) {

			return serviceExecutor.execute(opCode, component);
		} else {
			MessageIdentifier identifier = new MessageIdentifier(correlationID, "TEST");

			return Observable.create(new Observable.OnSubscribe<GenericComponent>() {

				@Override
				public void call(Subscriber<? super GenericComponent> subscriber) {
					ValueUpdateListener listener = new ValueUpdateListener() {

						@Override
						public void onValueChanged(GenericComponent component) {
							if (subscriber.isUnsubscribed()) {
								listenerRegistry.unregisterListener(this);

							} else {
								subscriber.onNext(component);
							}

							subscriber.onCompleted();
						}

						@Override
						public MessageIdentifier getIdentifier() {
							return identifier;
						}
					};
					listenerRegistry.registerListener(listener);
					String sendTo = env.getProperty(requestedDomain.concat(pubsub?TOPIC:QUEUE));
					jmsTemplate.send(sendTo, createMessage(opCode, correlationID, component, env.getProperty(currentDomain.concat(pubsub?TOPIC:QUEUE)) , currentApp, pubsub));
					System.out.println("Sent to:-"+ sendTo+" ,with correlation :"+ correlationID +" ,from : "+currentDomain +"-"+ currentApp);
				}
			});
		}
	}

	@Override
	public void send(String opCode, GenericComponent component) {
		validateParam(opCode, component);
		String correlationID = generateCorrelationID();
		String requestedDomain = opCode.split("-")[0];
		if (currentDomain.equals(requestedDomain)) {
			serviceExecutor.execute(opCode, component);
		} else {
			jmsTemplate.send(env.getProperty(requestedDomain.concat(TOPIC)),
					createMessage(opCode, correlationID, component, null, null, pubsub));
		}
	}

	@Override
	public void sendCallback(MessageEntity messageEntity) {
		validateParamForCallback(messageEntity);
		System.out.println("Acknowledgement Sent to:-" + messageEntity.getReplyTo() +" ,with correlation :"+ messageEntity.getJMSCorrelationID() +" , from : "+ currentDomain);
		jmsTemplate.send(messageEntity.getReplyTo(), createCallbackMessage(messageEntity, pubsub));
	}

	private void validateParam(String opCode, GenericComponent component) {
		Assert.notNull(opCode, "Operation code must not be null");
		Assert.notNull(component, "Component must not be null");
	}

	private void validateParamForCallback(MessageEntity messageEntity) {
		Assert.notNull(messageEntity, "message Entity must not be null");
		Assert.notNull(messageEntity.getJMSCorrelationID(), "correlation must not be null");
		Assert.notNull(messageEntity.getReplyTo(), "Destination must not be null");
	}

}
