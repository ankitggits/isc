package no.sample.isc.core.component;

import no.sample.isc.core.domain.GenericComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.SmartValidator;
import rx.Observable;

@Component
public class ServiceExecutor {

	@Autowired
	ApplicationContext context;

	//@Autowired
	SmartValidator validator;
	
	public Observable<GenericComponent> execute(String opCode, GenericComponent component){
		IService service = (IService) context.getBean(opCode);
//		BindingResult result = new BeanPropertyBindingResult(component, component.getClass().getName());
//		validator.validate(component, result);
//		if(result.hasErrors()){
//			return new GenericError(result.getFieldErrors());
//		}
		return service.serve(component);
	}
	
}
