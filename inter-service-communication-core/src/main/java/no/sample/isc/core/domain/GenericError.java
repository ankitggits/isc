package no.sample.isc.core.domain;

import java.lang.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.FieldError;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericError extends GenericComponent{

	private static final long serialVersionUID = 1L;
	List<java.lang.Error> errors;
	
	public GenericError(List<FieldError> fieldErrors){
		errors = new ArrayList<>();
		for(FieldError error:fieldErrors){
			errors.add(new java.lang.Error(error.getField()));
		}
	}

	@Override
	public String getVal() {
		return null;
	}
}


