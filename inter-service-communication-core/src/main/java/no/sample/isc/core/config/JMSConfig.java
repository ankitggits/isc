package no.sample.isc.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.NamingException;

@Configuration
@EnableJms
public class JMSConfig {

	@Value("${mq.pubsub:true}")
	private boolean pubsub;
	
	@Autowired
	@Qualifier("mqConnectionFactory")
	private ConnectionFactory connectionFactory;
	
	@Bean
	JmsTemplate jmsTemplate() throws JMSException, NamingException{
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setDestinationResolver(new DynamicDestinationResolver());
		jmsTemplate.setPubSubDomain(pubsub);
		return jmsTemplate;
	}
}
