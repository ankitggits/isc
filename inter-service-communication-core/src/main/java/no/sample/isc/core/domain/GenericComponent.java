package no.sample.isc.core.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class GenericComponent implements Serializable{

	private static final long serialVersionUID = 1L;
	
	long sentTime;
	long recTime;
	long ackSentTime;
	long ackRecTime;
}
