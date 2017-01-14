package no.sample.isc.binder.amq.config;


import no.sample.isc.core.listener.DomainMessageListener;
import no.sample.isc.core.listener.DomainSpecificMessageListener;
import no.sample.isc.core.converter.DomainObjectMessageConverter;
import no.sample.isc.core.converter.DomainSpecificObjectMessageConverter;
import no.sample.isc.core.exception.JMSErrorHandler;
import no.sample.isc.core.exception.JMSExceptionListener;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.ConnectionFactory;

import static no.sample.isc.binder.servicebus.util.MessageConstants.QUEUE;


@Configuration
@PropertySource({"classpath:application-jms.properties", "classpath:application-event-mapping.properties" })
public class AMQConfig{

	@Value("${current.app}")
	private String app;
	
	@Value("${current.domain}")
	private String domain;
	
	@Autowired
	private Environment environment;

	@Autowired
    DomainMessageListener domainReceiver;

	@Autowired
    DomainSpecificMessageListener domainSpecificReceiver;

	@Autowired
	DomainObjectMessageConverter domainObjectMessageConverter;

	@Autowired
	DomainSpecificObjectMessageConverter domainSpecificObjectMessageConverter;

	@Bean
	public DefaultMessageListenerContainer domainListenerContainer() {
		return new DefaultMessageListenerContainer() {
			{
				setConnectionFactory(connectionFactory());
				setMessageListener(new MessageListenerAdapter(domainReceiver) {
					{
						setMessageConverter(domainObjectMessageConverter);
						setDefaultListenerMethod(domainReceiver.getClass().getMethods()[0].getName());
					}
				});
				setDestinationName(environment.getProperty(domain.concat(QUEUE)));
				setMessageSelector("appId='ALL'");
				setExceptionListener(new JMSExceptionListener());
				setErrorHandler(new JMSErrorHandler());
				setConcurrentConsumers(1);
				setPubSubDomain(false);
				setDestinationResolver(new DynamicDestinationResolver());
			}
		};
	}

	@Bean
	public DefaultMessageListenerContainer domainSpecificListenerContainer() {
		return new DefaultMessageListenerContainer() {
			{
				setConnectionFactory(connectionFactory());
				setMessageListener(new MessageListenerAdapter(domainSpecificReceiver) {
					{
						setMessageConverter(domainSpecificObjectMessageConverter);
						setDefaultListenerMethod(domainSpecificReceiver.getClass().getMethods()[0].getName());
					}
				});
				setDestinationName(environment.getProperty(domain.concat(QUEUE)));
				setMessageSelector("appId='" + app + "'");
				setExceptionListener(new JMSExceptionListener());
				setErrorHandler(new JMSErrorHandler());
				setConcurrentConsumers(1);
				setPubSubDomain(false);
				setDestinationResolver(new DynamicDestinationResolver());
			}
		};
	}
	
	@Bean(name = "mqConnectionFactory")
	ConnectionFactory connectionFactory() {
		return new CachingConnectionFactory(new ActiveMQConnectionFactory("tcp://localhost:61616"));
	}
	
}
