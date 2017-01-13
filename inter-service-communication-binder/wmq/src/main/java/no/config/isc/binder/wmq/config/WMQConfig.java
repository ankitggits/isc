package no.config.isc.binder.wmq.config;

import no.sample.isc.binder.amq.listener.DomainMessageListener;
import no.sample.isc.binder.amq.listener.DomainSpecificMessageListener;
import no.sample.isc.core.converter.DomainObjectMessageConverter;
import no.sample.isc.core.converter.DomainSpecificObjectMessageConverter;
import no.sample.isc.core.exception.JMSErrorHandler;
import no.sample.isc.core.exception.JMSExceptionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.destination.JndiDestinationResolver;

import javax.jms.ConnectionFactory;

import static no.sample.isc.core.util.MessageConstants.QUEUE;

@Configuration
@ImportResource("classpath:connection-factory.xml")
@PropertySource({"classpath:application-jms.properties", "classpath:application-event-mapping.properties" })
public class WMQConfig {
	
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
	
	@Autowired
	@Qualifier("mqConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Bean
	public DefaultMessageListenerContainer domainListenerContainer() {
		return new DefaultMessageListenerContainer() {
			{
				setConnectionFactory(connectionFactory);
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
				setConcurrentConsumers(10);
				setPubSubDomain(false);
				setDestinationResolver(new JndiDestinationResolver());
			}
		};
	}

	@Bean
	public DefaultMessageListenerContainer domainSpecificListenerContainer() {
		return new DefaultMessageListenerContainer() {
			{
				setConnectionFactory(connectionFactory);
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
				setConcurrentConsumers(10);
				setPubSubDomain(false);
				setDestinationResolver(new JndiDestinationResolver());
			}
		};
	}

}
