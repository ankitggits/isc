package no.sample.isc.binder.servicebus.config;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import no.sample.isc.core.converter.DomainByteMessageConverter;
import no.sample.isc.core.converter.DomainSpecificByteMessageConverter;
import no.sample.isc.binder.servicebus.util.SubscriptionInitializer;
import no.sample.isc.core.listener.DomainMessageListener;
import no.sample.isc.core.listener.DomainSpecificMessageListener;
import no.sample.isc.core.exception.JMSExceptionListener;
import no.sample.isc.core.util.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import java.util.Properties;

@Configuration
@PropertySource("file:C:\\configuration\\isc-config.properties")
public class ServiceBusConfig {

	public final static Properties properties;

	static {
		properties = new Properties();
		properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory");
		properties.put(Context.PROVIDER_URL, "file:C:\\configuration\\isc-config.properties");
	}

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

	@Value("${current.domain}")
	private String currentDomain;

	@Bean(name="sbConnectionFactoryBean")
	public ConnectionFactory sbConnectionFactoryBean() throws Exception{
		ConnectionFactory connectionFactory = null;
		try {			
			Context context = new InitialContext(properties);
			connectionFactory = (ConnectionFactory) context.lookup("sbFactoryLookup"); 
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return connectionFactory;
	}

	@Bean(name="mqConnectionFactory")
	@Primary
	public ConnectionFactory sbConnectionFactory() throws Exception {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(sbConnectionFactoryBean());
		connectionFactory.setSessionCacheSize(10);
		return connectionFactory;
	}

	@Bean(name="domainListenerContainer")
	public MessageListenerContainer domainListenerContainer(SubscriptionInitializer subscriptionInitializer) throws Exception {

		subscriptionInitializer.initializeDomainListenerSubscription();

		DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
		messageListenerContainer.setPubSubDomain(true);
		messageListenerContainer.setConcurrency("1");
		messageListenerContainer.setConnectionFactory(sbConnectionFactory());
		messageListenerContainer.setMessageListener(
				new MessageListenerAdapter(domainReceiver) {
					{
						setMessageConverter(domainByteMessageConverter);
						setDefaultListenerMethod(domainReceiver.getClass().getMethods()[0].getName());
					}
				});
		messageListenerContainer.setDestination(topic());
		messageListenerContainer.setDurableSubscriptionName(currentDomain+"-processing-subscription");
		messageListenerContainer.setExceptionListener(exceptionLstnr);
		return messageListenerContainer;
	}

	@Bean(name="domainSpecificListenerContainer")
	public MessageListenerContainer domainSpecificListenerContainer(SubscriptionInitializer subscriptionInitializer) throws Exception {

		subscriptionInitializer.initializeInstanceListenerSubscription();

		DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
		messageListenerContainer.setPubSubDomain(true);
		messageListenerContainer.setConcurrency("1");
		messageListenerContainer.setConnectionFactory(sbConnectionFactory());
		messageListenerContainer.setMessageListener(
				new MessageListenerAdapter(domainSpecificReceiver) {
					{
						setMessageConverter(domainSpecificByteMessageConverter);
						setDefaultListenerMethod(domainSpecificReceiver.getClass().getMethods()[0].getName());
					}
				});
		messageListenerContainer.setDestination(topic());
		messageListenerContainer.setDurableSubscriptionName(currentDomain + "-" + ServerInfo.port + "-sink-subscription");
		messageListenerContainer.setExceptionListener(exceptionLstnr);
		return messageListenerContainer;
	}

	@Bean
	public Topic topic() {
		Topic topic = null;
		try {
			Context context = new InitialContext(properties);
			topic = (Topic) context.lookup("servicebus");
			return topic;
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return topic;
	}

}