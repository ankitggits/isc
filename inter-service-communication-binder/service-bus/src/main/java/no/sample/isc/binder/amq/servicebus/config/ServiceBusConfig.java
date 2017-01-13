package no.sample.isc.binder.amq.servicebus.config;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import no.sample.isc.binder.amq.listener.DomainMessageListener;
import no.sample.isc.binder.amq.listener.DomainSpecificMessageListener;
import no.sample.isc.core.converter.DomainByteMessageConverter;
import no.sample.isc.core.converter.DomainSpecificByteMessageConverter;
import no.sample.isc.core.exception.JMSExceptionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

@Configuration
public class ServiceBusConfig {

	@Autowired
    DomainMessageListener domainReceiver;

	@Autowired
	JMSExceptionListener exceptionLstnr;

	@Autowired
	DomainSpecificMessageListener domainSpecificReceiver;

	@Autowired
	DomainByteMessageConverter domainByteMessageConverter;

	@Autowired
	DomainSpecificByteMessageConverter domainSpecificByteMessageConverter;

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
	@Primary
	public ConnectionFactory sbConnectionFactory() throws Exception {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(sbConnectionFactoryBean());
		connectionFactory.setSessionCacheSize(10);
		return connectionFactory;
	}

	@Bean(name="sbListenerContainer")
	public MessageListenerContainer sbListenerContainer() throws Exception {
		DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
		messageListenerContainer.setConcurrency("1");
		messageListenerContainer.setConnectionFactory(sbConnectionFactory());
		messageListenerContainer.setMessageListener(
			new MessageListenerAdapter(domainReceiver) {
				{
					setMessageConverter(domainByteMessageConverter);
					setDefaultListenerMethod(domainReceiver.getClass().getMethods()[0].getName());
				}
		});
		messageListenerContainer.setDestination(queueRequest());
		messageListenerContainer.setExceptionListener(exceptionLstnr);
		return messageListenerContainer;
	}

}