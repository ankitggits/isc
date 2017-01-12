package no.sample.isc.binder.component;

import no.sample.isc.core.domain.GenericComponent;
import rx.Observable;

@FunctionalInterface
public interface IService {

	rx.Observable<GenericComponent> execute(GenericComponent component);
}
