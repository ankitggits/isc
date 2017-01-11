package no.sample.isc.core.component;

import no.vipps.platform.messaging.domain.GenericComponent;
import no.vipps.platform.messaging.domain.MessageIdentifier;

public interface ValueUpdateListener {

	void onValueChanged(GenericComponent component);
	
	MessageIdentifier getIdentifier();

}
