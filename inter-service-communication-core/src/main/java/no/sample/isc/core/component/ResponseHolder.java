package no.sample.isc.core.component;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import no.vipps.platform.messaging.domain.GenericComponent;

@NoArgsConstructor
@AllArgsConstructor
public class ResponseHolder {
	
	private final Deferred<Long, Long, String> deferred = new DeferredObject<>();
	
	//boolean isCompleted;
	GenericComponent component;
	
	public Promise<Long, Long, String> promise() {
		return deferred.promise();
	}
}
