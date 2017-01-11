package no.sample.isc.core.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.vipps.platform.messaging.component.ValueUpdateListener;
import no.vipps.platform.messaging.domain.MessageEntity;
import no.vipps.platform.messaging.domain.MessageIdentifier;
import no.vipps.platform.messaging.util.MessageStore;

@Component
public class DomainSpecificMessageListener implements MessageListener{

	@Autowired
	private MessageStore messageStore;

	@Override
	public void listen(MessageEntity messageEntity) {
		MessageIdentifier identifier = new MessageIdentifier(messageEntity.getJMSCorrelationID(), "TEST");
		ValueUpdateListener updateListener = messageStore.get(identifier);
		updateListener.onValueChanged(messageEntity.getComponent());
	}

}
