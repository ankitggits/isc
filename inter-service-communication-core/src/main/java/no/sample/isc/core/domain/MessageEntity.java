package no.sample.isc.core.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import javax.jms.Message;

@Getter
@Setter
public class MessageEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient String JMSCorrelationID;
	private transient String sourceAppId;
	private String opCode;
	private Status status;
	private GenericComponent component;
	private String replyTo;

	public MessageEntity(String opCode, GenericComponent component) {
		this.opCode = opCode;
		this.component = component;
	}

	public MessageEntity(GenericComponent component) {
		status=Status.SUCCESS;
		this.component = component;
	}
	
	public MessageEntity(GenericError error){
		component=error;
		status=Status.FAILED;
	}
}
