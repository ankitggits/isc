package no.sample.isc.core.util;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import no.vipps.platform.messaging.component.ValueUpdateListener;
import no.vipps.platform.messaging.domain.MessageIdentifier;

@Component
public class MessageStore extends HashMap<MessageIdentifier, ValueUpdateListener> {

	private static final long serialVersionUID = 1L;

	@Override
	public ValueUpdateListener put(MessageIdentifier key, ValueUpdateListener value) {
		return super.put(key, value);
	}
	
	@Override
	public boolean containsKey(Object key) {
        return super.containsKey((MessageIdentifier)key);
    }
	
	@Override
	public ValueUpdateListener remove(Object key) {
        return super.remove((MessageIdentifier)key);
    }
	
	@Override
	 public ValueUpdateListener get(Object key) {
        return super.get((MessageIdentifier) key);
    }
	
//	private void validateKey(Object key){
//		if(!(key instanceof MessageIdentifier)){
//			throw new RuntimeException("Not a valid message identifier");
//		}
//	}

}
