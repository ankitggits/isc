package no.sample.isc.binder.servicebus.config;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import no.sample.isc.core.exception.JMSExceptionListener;
import no.sample.isc.core.listener.MessageListener;
import no.sample.isc.core.util.MessageStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

@Configuration
public class ServiceBusConfig {

	@Autowired
	MessageStore store;

	@Autowired
	MessageListener msgLstnr;

	@Autowired
	JMSExceptionListener exceptionLstnr;

	@Bean(name="sbConnectionFactoryBean")
	public ConnectionFactory sbConnectionFactoryBean() throws Exception{
		ConnectionFactory connectionFactory = null;
		try {			
			Context context = new InitialContext(InitialContextPropertyInitializer.properties);
			connectionFactory = (ConnectionFactory) context.lookup("sbFactoryLookup"); 
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return connectionFactory;
	}

	@Bean(name="sbRequest")
	public Queue queueRequest() {
		Queue queue = null;
		try {
			Context context = new InitialContext(InitialContextPropertyInitializer.properties);
			queue = (Queue) context.lookup("servicebusrequest");
			return queue;
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return queue;
	}

	@Bean(name="sbReply")
	public Queue queueReply() {
		Queue queue = null;
		try {
			Context context = new InitialContext(InitialContextPropertyInitializer.properties);
			queue = (Queue) context.lookup("servicebusreply");
			return queue;
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return queue;
	}
	
	@Bean(name="mqConnectionFactory")
	public ConnectionFactory sbConnectionFactory() throws Exception {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(sbConnectionFactoryBean());
		connectionFactory.setSessionCacheSize(10);
		return connectionFactory;
	}

	/*@Bean(name = "sbTemplate")
	public JmsTemplate sbTemplate() throws Exception {
		return new JmsTemplate(sbConnectionFactory());
	}*/

	@Bean(name="sbListenerContainer")
	public MessageListenerContainer sbListenerContainer() throws Exception {
		DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
		messageListenerContainer.setConcurrency("1-5");
		messageListenerContainer.setConnectionFactory(sbConnectionFactory());
		messageListenerContainer.setMessageListener(msgLstnr);
		messageListenerContainer.setDestination(queueReply());
		messageListenerContainer.setExceptionListener(exceptionLstnr);
		return messageListenerContainer;
	}

}