package no.sample.isc.core.listener;

import no.sample.isc.core.component.IMessageTemplate;
import no.sample.isc.core.component.ServiceExecutor;
import no.sample.isc.core.domain.GenericComponent;
import no.sample.isc.core.domain.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.SmartValidator;
import rx.Observable;

@Component
public class DomainMessageListener implements MessageListener{

	//@Autowired
	SmartValidator validator;

	@Autowired
	ServiceExecutor executor;
	
	@Autowired
	IMessageTemplate template;

	@Override
	public void listen(MessageEntity messageEntity) {
		Observable<GenericComponent> obsComponent = executor.execute(messageEntity.getOpCode(), messageEntity.getComponent());
		obsComponent.subscribe((component)->{
			messageEntity.setComponent(component);
			template.sendCallback(messageEntity);
		});

	}
}
