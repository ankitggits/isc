package no.sample.isc.core.component;

import no.sample.isc.core.domain.GenericComponent;
import rx.Observable;

import java.util.Observable;

@FunctionalInterface
public interface IService {

	Observable<GenericComponent> execute(GenericComponent component);
}
