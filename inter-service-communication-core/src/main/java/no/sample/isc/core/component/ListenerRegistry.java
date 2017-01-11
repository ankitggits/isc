package no.sample.isc.core.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.vipps.platform.messaging.util.MessageStore;

@Component
public class ListenerRegistry {
    
	@Autowired
	MessageStore store;
	
    public void registerListener(ValueUpdateListener listener) {
    	store.put(listener.getIdentifier(), listener);
    };
    
    public void unregisterListener(ValueUpdateListener listener) {
    	store.remove(listener.getIdentifier());
    };
}
