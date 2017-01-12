package no.sample.isc.binder.servicebus.component;

import java.util.Date;
import java.util.UUID;

import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import no.sample.isc.binder.component.IMessageTemplate;
import no.sample.isc.binder.component.ServiceExecutor;
import no.sample.isc.core.component.ListenerRegistry;
import no.sample.isc.core.component.ValueUpdateListener;
import no.sample.isc.core.domain.GenericComponent;
import no.sample.isc.core.domain.MessageEntity;
import no.sample.isc.core.domain.MessageIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import org.springframework.util.Assert;
import rx.Observable;
import rx.Subscriber;

import javax.jms.Queue;

@Component
public class MessageTemplate implements IMessageTemplate {

	@Autowired
	ListenerRegistry listenerRegistry;

	@Autowired
	@Qualifier("sbRequest")
	Queue queueRequest;

	@Autowired
	@Qualifier("sbReply")
	Queue queueReply;

	@Autowired
	@Qualifier("sbTemplate")
	JmsTemplate jmsTemplate;

	@Value("${current.domain}")
	private String currentDomain;

	@Autowired
	ServiceExecutor serviceExecutor;

	private static com.microsoft.windowsazure.Configuration config = ServiceBusConfiguration.configureWithSASAuthentication("samplemsgservicebus",
			"RootManageSharedAccessKey", "rVG96oOwNkLqoZC1Py+5YY5yKTSdNh42PT5/KVNTlqg=", ".servicebus.windows.net");

	private ServiceBusContract service = ServiceBusService.create(config);

	@Override
	public void send(String opCode, GenericComponent component) {
		validateParam(opCode, component);
		String correlationID = generateCorrelationID();
		String requestedDomain = opCode.split("-")[0];
		if (currentDomain.equals(requestedDomain)) {
			serviceExecutor.execute(opCode, component);
		} else {
			try{
				BrokeredMessage message = new BrokeredMessage(component.getVal());
				message.setCorrelationId(correlationID);
				service.sendQueueMessage(queueRequest.getQueueName(), message);
			} catch (Exception e) {
				System.out.print("ServiceException encountered: ");
				System.out.println(e.getMessage());
			}
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
				try{
					BrokeredMessage message = new BrokeredMessage(component.getVal());
					message.setCorrelationId(correlationID);
					service.sendQueueMessage(queueRequest.getQueueName(), message);
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
		System.out.println("Acknowledgement Sent to:-" + messageEntity.getReplyTo() +" ,with correlation :"+ messageEntity.getJMSCorrelationID() +" , from : "+ currentDomain);
		try{
			BrokeredMessage message = new BrokeredMessage(messageEntity.getComponent().getVal());
			message.setCorrelationId(messageEntity.getJMSCorrelationID());
			messageEntity.getComponent().setAckSentTime(new Date().getTime());
			service.sendQueueMessage(queueReply.getQueueName(), message);
		} catch (Exception e) {
			System.out.print("ServiceException encountered: ");
			System.out.println(e.getMessage());
		}
	}

	private void validateParamForCallback(MessageEntity messageEntity) {
		Assert.notNull(messageEntity, "message Entity must not be null");
		Assert.notNull(messageEntity.getJMSCorrelationID(), "correlation must not be null");
		Assert.notNull(messageEntity.getReplyTo(), "Destination must not be null");
	}

}
