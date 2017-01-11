package no.sample.isc.core.listener;

import no.vipps.platform.messaging.domain.MessageEntity;

@FunctionalInterface
public interface MessageListener {

	void listen(MessageEntity messageEntity);
}
