package no.sample.isc.testclient.common.controller;

import no.sample.isc.core.component.IMessageTemplate;
import no.sample.isc.core.domain.GenericComponent;
import no.sample.isc.testclient.common.model.TestComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

/**
 * Created by Ankit on 13-01-2017.
 */
@RestController
public class PingController {

    @Autowired
    IMessageTemplate messageTemplate;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping("/ping")
    public void index() {
        messageTemplate.send("consumer-performsomeaction", new TestComponent("testing"));
    }

    @RequestMapping("/ping-pong/{event}/{val}")
    public DeferredResult<String> pingConsumer(@PathVariable("event") String event, @PathVariable("val") String val) {
        Observable<GenericComponent> observable = messageTemplate.sendAndReceiveObservable(event, new TestComponent(val));
        DeferredResult<String> deffered = new DeferredResult<String>(90000L);
        observable.subscribe(m -> deffered.setResult(m.getVal()), e -> deffered.setErrorResult(e));
        return deffered;
    }
}
