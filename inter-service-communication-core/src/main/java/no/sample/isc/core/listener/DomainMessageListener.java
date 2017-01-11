package no.sample.isc.core.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.SmartValidator;

import no.vipps.platform.messaging.component.MessageTemplate;
import no.vipps.platform.messaging.component.ServiceExecutor;
import no.vipps.platform.messaging.domain.GenericComponent;
import no.vipps.platform.messaging.domain.MessageEntity;
import rx.Observable;

@Component
public class DomainMessageListener implements MessageListener{

	@Autowired
	SmartValidator validator;

	@Autowired
	ServiceExecutor executor;
	
	@Autowired
	MessageTemplate template;

	@Override
	public void listen(MessageEntity messageEntity) {
		Observable<GenericComponent> obsComponent = executor.execute(messageEntity.getOpCode(), messageEntity.getComponent());
		obsComponent.subscribe((component)->{
			messageEntity.setComponent(component);
			template.sendCallback(messageEntity);
		});
	}

	
}
