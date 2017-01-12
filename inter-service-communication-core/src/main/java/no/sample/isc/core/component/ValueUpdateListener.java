package no.sample.isc.core.component;

import no.sample.isc.core.domain.GenericComponent;
import no.sample.isc.core.domain.MessageIdentifier;


public interface ValueUpdateListener {

	void onValueChanged(GenericComponent component);
	
	MessageIdentifier getIdentifier();

}
