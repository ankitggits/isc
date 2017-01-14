package no.sample.isc.core.component;

import no.sample.isc.core.domain.GenericComponent;
import rx.Observable;

public interface IService {

	default Observable<GenericComponent> serve(GenericComponent component){
		return Observable.create((onSubscriber) ->{
			onSubscriber.onNext(this.execute(component));
			onSubscriber.onCompleted();
		});
	}

	GenericComponent execute(GenericComponent genericComponent);
}
