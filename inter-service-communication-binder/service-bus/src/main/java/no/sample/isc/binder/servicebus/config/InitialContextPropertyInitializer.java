package no.sample.isc.binder.servicebus.config;

import java.util.Properties;

import javax.naming.Context;

public class InitialContextPropertyInitializer {

	public final static Properties properties;

	static {
		properties = new Properties();
		properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory");
		properties.put(Context.PROVIDER_URL, "classpath:application-jms.properties");
	}

}
