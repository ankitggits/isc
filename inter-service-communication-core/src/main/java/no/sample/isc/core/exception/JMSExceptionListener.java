package no.sample.isc.core.exception;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMSExceptionListener implements ExceptionListener {
	
	private final static Logger logger = LoggerFactory.getLogger(JMSExceptionListener.class);

	//private ExceptionListener exceptionListener = null;

//	public JmsExceptionListener() {
//	}

//	public JmsExceptionListener(ExceptionListener exceptionListener) {
//	    super();
//	    this.exceptionListener = exceptionListener;
//	}

	public synchronized void onException(JMSException e) {
	    logger.error("JMS exception has occurred: ", e);

	    Exception ex = e.getLinkedException();
	    if (ex != null) {
	        logger.error("JMS Linked exception: ", ex);
	    }

//	    if (exceptionListener != null) {
//	        exceptionListener.onException(e);
//	    }
	}
}