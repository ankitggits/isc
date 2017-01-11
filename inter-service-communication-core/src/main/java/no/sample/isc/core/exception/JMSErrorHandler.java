package no.sample.isc.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.util.ErrorHandler;

public class JMSErrorHandler implements ErrorHandler{

	private final static Logger logger = LoggerFactory.getLogger(JMSErrorHandler.class);
	
	@Override
	public void handleError(Throwable t) {
		if(t instanceof MessageConversionException){
			logger.error("Message Conversion exception has occurred:  "+  t.getMessage());
		}else{
			logger.error("JMS exception has occurred: "+  t.getMessage());
		}
		t.printStackTrace();
	}

}
