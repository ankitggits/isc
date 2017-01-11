package no.sample.isc.core.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Error implements Serializable{
	private static final long serialVersionUID = 1L;
	String errorCode;
	String errorMessage;
}
