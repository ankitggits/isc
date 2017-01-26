package no.sample.isc.persistance.logging.events;

import no.sample.isc.core.domain.GenericComponent;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

/**
 * Created by Ankit on 26-01-2017.
 */
class GenericEventualEvent<T extends IEvent> implements ResolvableTypeProvider {

    private T source;

    public GenericEventualEvent(T source) {
        this.source = source;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(),
                ResolvableType.forInstance(source));
    }


}