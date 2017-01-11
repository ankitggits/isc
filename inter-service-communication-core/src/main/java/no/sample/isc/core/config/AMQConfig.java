package no.sample.isc.core.config;

import static no.vipps.platform.messaging.util.MessageConstants.QUEUE;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import no.vipps.platform.messaging.converter.DomainMessageConverter;
import no.vipps.platform.messaging.converter.DomainSpecificMessageConverter;
import no.vipps.platform.messaging.exception.JMSErrorHandler;
import no.vipps.platform.messaging.exception.JMSExceptionListener;
import no.vipps.platform.messaging.listener.DomainMessageListener;
import no.vipps.platform.messaging.listener.DomainSpecificMessageListener;

@Configuration
@Profile("local")
@PropertySource({"classpath:application-jms-local.properties", "classpath:application-event-mapping.properties" })
public class AMQConfig {

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
	DomainMessageConverter domainMessageConverter;

	@Autowired
	DomainSpecificMessageConverter domainSpecificMessageConverter;

	@Bean
	public DefaultMessageListenerContainer domainListenerContainer() {
		return new DefaultMessageListenerContainer() {
			{
				setConnectionFactory(connectionFactory());
				setMessageListener(new MessageListenerAdapter(domainReceiver) {
					{
						setMessageConverter(domainMessageConverter);
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
						setMessageConverter(domainSpecificMessageConverter);
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
