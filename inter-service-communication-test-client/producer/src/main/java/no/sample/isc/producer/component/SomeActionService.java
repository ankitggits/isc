package no.sample.isc.producer.component;

import no.sample.isc.binder.component.IService;
import no.sample.isc.core.domain.GenericComponent;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.util.Date;

/**
 * Created by Ankit on 13-01-2017.
 */
@Component("performsomeaction")
public class SomeActionService implements IService {

    @Override
    public Observable<GenericComponent> execute(GenericComponent component) {
        return Observable.create(subscriber -> {
            TestComponent testComponent = (TestComponent) component;
            testComponent.setVal("resolved::->"+component.getVal());
            testComponent.setRecTime(new Date().getTime());
        });
    }
}
