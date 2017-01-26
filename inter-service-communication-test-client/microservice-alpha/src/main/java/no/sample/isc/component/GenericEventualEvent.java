package no.sample.isc.component;

import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

/**
 * Created by Ankit on 26-01-2017.
 */
class GenericEventualEvent<T> implements ResolvableTypeProvider {

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