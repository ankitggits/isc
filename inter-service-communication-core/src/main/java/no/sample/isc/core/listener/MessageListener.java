package no.sample.isc.core.listener;

import no.sample.isc.core.domain.MessageEntity;

@FunctionalInterface
public interface MessageListener{

	void listen(MessageEntity messageEntity);
}
