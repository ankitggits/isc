package no.sample.isc.core.exception;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JMSExceptionListener implements ExceptionListener {
	
	private final static Logger logger = LoggerFactory.getLogger(JMSExceptionListener.class);

	public synchronized void onException(JMSException e) {
	    logger.error("JMS exception has occurred: ", e);

	    Exception ex = e.getLinkedException();
	    if (ex != null) {
	        logger.error("JMS Linked exception: ", ex);
	    }
	}
}