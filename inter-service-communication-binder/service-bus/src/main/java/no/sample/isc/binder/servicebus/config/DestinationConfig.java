package no.sample.isc.binder.servicebus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Created by svn_admin on 13/01/2017.
 */
@Configuration
public class DestinationConfig {

/*    @Bean(name="sbRequest")
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
            queue = (Queue) context.lookup("servicebussample");
            return queue;
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return queue;
    }*/

    @Bean(name="sbTopic")
    public Topic topicRequest() {
        Topic topic = null;
        try {
            Context context = new InitialContext(InitialContextPropertyInitializer.properties);
            topic = (Topic) context.lookup("servicebus");
            return topic;
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return topic;
    }

}
