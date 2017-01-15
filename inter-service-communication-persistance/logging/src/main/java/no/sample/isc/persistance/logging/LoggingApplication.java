package no.sample.isc.persistance.logging;

import no.sample.isc.persistance.logging.component.DefaultSubscription;
import no.sample.isc.persistance.logging.component.LogMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Created by Ankit on 15-01-2017.
 */
@SpringBootApplication
@PropertySource("file:C:\\configuration\\isc-config.properties")
public class LoggingApplication {

    public static void main(String[] args) {
        System.setProperty("server.port", "1234");
        System.setProperty("current.domain", "logging");
        System.setProperty("current.event.log", "");
        SpringApplication.run(LoggingApplication.class, args);
    }

    @Autowired
    private LogMessageListener logReceiver;

    private final static Properties properties;

    static {
        properties = new Properties();
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory");
        properties.put(Context.PROVIDER_URL, "file:C:\\configuration\\isc-config.properties");
    }

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
    public MessageListenerContainer domainListenerContainer(DefaultSubscription subscriptionInitializer) throws Exception {
        subscriptionInitializer.initializeDomainListenerSubscription();

        DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setPubSubDomain(true);
        messageListenerContainer.setConcurrency("1");
        messageListenerContainer.setConnectionFactory(sbConnectionFactory());
        messageListenerContainer.setMessageListener(logReceiver);
        messageListenerContainer.setDestination(topic());
        messageListenerContainer.setDurableSubscriptionName("logging-subscription");
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
