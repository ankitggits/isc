package no.sample.isc.producer.component;

import no.sample.isc.binder.component.IMessageTemplate;
import no.sample.isc.core.domain.GenericComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

/**
 * Created by Ankit on 13-01-2017.
 */
@RestController
@RequestMapping("/ping")
public class PingController {

    @Autowired
    IMessageTemplate messageTemplate;

    @RequestMapping
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping("/consumer/{val}")
    public DeferredResult<String> pingConsumer(@PathVariable String val) {
        Observable<GenericComponent> observable = messageTemplate.sendAndReceiveObservable("consumer-performsomeaction", new TestComponent(val));
        DeferredResult<String> deffered = new DeferredResult<String>(90000L);
        observable.subscribe(m -> deffered.setResult(m.getVal()), e -> deffered.setErrorResult(e));
        return deffered;
    }

    @RequestMapping("/producer/{val}")
    public DeferredResult<String> pingProducer(@PathVariable String val) {
        Observable<GenericComponent> observable = messageTemplate.sendAndReceiveObservable("producer-performsomeaction", new TestComponent(val));
        DeferredResult<String> deffered = new DeferredResult<String>(90000L);
        observable.subscribe(m -> deffered.setResult(m.getVal()), e -> deffered.setErrorResult(e));
        return deffered;
    }

}
