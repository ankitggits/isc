package no.sample.isc.binder.listener;

import no.sample.isc.core.component.ValueUpdateListener;
import no.sample.isc.core.domain.MessageEntity;
import no.sample.isc.core.domain.MessageIdentifier;
import no.sample.isc.core.listener.MessageListener;
import no.sample.isc.core.util.MessageStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
