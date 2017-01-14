package no.sample.isc.binder.servicebus.util;

import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.*;
import no.sample.isc.binder.servicebus.util.ServerInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.jms.Topic;

import javax.jms.Topic;

/**
 * Created by svn_admin on 13/01/2017.
 */
@Component
public class ServerInfo implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

    @Value("${current.domain}")
    private String currentDomain;

    @Value("${servicebus.sshkey}")
    private String sshKey;

    @Autowired
    Topic topic;

    private int port;

    public int getPort() {
        return port;
    }

    @Override
    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
        final com.microsoft.windowsazure.Configuration config = ServiceBusConfiguration.configureWithSASAuthentication("samplemsgservicebus",
                "RootManageSharedAccessKey", sshKey, ".servicebus.windows.net");

        final ServiceBusContract service = ServiceBusService.create(config);

        port = event.getEmbeddedServletContainer().getPort();
        try {
            String topicName = topic.getTopicName();

            String processingSubscriptionName = currentDomain + "-processing-subscription";
            String processingRuleName = currentDomain + "-processing-rule";
            try {
                GetSubscriptionResult getProcessingSubscriptionInfo = service.getSubscription(topicName, processingSubscriptionName);
            } catch (Exception exception) {
                SubscriptionInfo subInfo = new SubscriptionInfo(processingSubscriptionName);
                CreateSubscriptionResult result = service.createSubscription(topicName, subInfo);
            } finally {
                RuleInfo ruleInfo = new RuleInfo(processingRuleName);
                ruleInfo = ruleInfo.withSqlExpressionFilter("event ='performsomeaction'");
                CreateRuleResult ruleResult = service.createRule(topicName, processingSubscriptionName, ruleInfo);
                service.deleteRule(topicName, processingSubscriptionName, "$default");
            }

            String sinkSubscriptionName = currentDomain + "-" + port + "-sink-subscription";
            String sinkRuleName = currentDomain + "-" + port + "-sink-rule";
            try {
                GetSubscriptionResult getReplySubscriptionInfo = service.getSubscription(topicName, sinkSubscriptionName);
            } catch (Exception exception) {
                SubscriptionInfo subInfo = new SubscriptionInfo(sinkSubscriptionName);
                CreateSubscriptionResult result = service.createSubscription(topicName, subInfo);
            } finally {
                RuleInfo ruleInfo = new RuleInfo(sinkRuleName);
                ruleInfo = ruleInfo.withSqlExpressionFilter("(sourceAppId = '" + port + "') AND (event ='performsomeaction-done')");
                CreateRuleResult ruleResult = service.createRule(topicName, sinkSubscriptionName, ruleInfo);
                service.deleteRule(topicName, sinkSubscriptionName, "$default");
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
