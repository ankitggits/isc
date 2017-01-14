package no.sample.isc.testclient.common.service;

import no.sample.isc.core.component.IService;
import no.sample.isc.core.domain.GenericComponent;
import no.sample.isc.testclient.common.model.TestComponent;

import java.util.Date;

/**
 * Created by Ankit on 13-01-2017.
 */
public class SomeActionService implements IService {

    @Override
    public TestComponent execute(GenericComponent component) {
        TestComponent testComponent = (TestComponent) component;
        testComponent.setVal("resolved::->" + component.getVal());
        testComponent.setRecTime(new Date().getTime());
        return testComponent;
    }
}
