package no.sample.isc.binder.servicebus.util;

import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.implementation.SqlFilter;
import com.microsoft.windowsazure.services.servicebus.models.*;
import no.sample.isc.core.util.ServerInfo;
import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.SystemException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.jms.Topic;
import javax.xml.datatype.DatatypeFactory;
import java.util.Iterator;

/**
 * Created by svn_admin on 13/01/2017.
 */
@Component
public class SubscriptionInitializer implements InitializingBean{

    @Autowired
    private Topic topic;

    @Value("${current.domain}")
    private String currentDomain;

    @Value("${current.event.process}")
    private String processEvents;

    @Value("${current.event.sink}")
    private String sinkEvents;

    @Value("${servicebus.sshkey}")
    private String sshKey;

    private ServiceBusContract service;

    public void initializeDomainListenerSubscription() throws Exception{
        String topicName = topic.getTopicName();

        String processingSubscriptionName = currentDomain + "-processing-subscription";
        String processingRuleName = currentDomain +"-processing-rule";
        String expression = expression = getExpressionFromCSV(processEvents);
        try {
            GetSubscriptionResult getProcessingSubscriptionInfo = service.getSubscription(topicName, processingSubscriptionName);
            if(getProcessingSubscriptionInfo!=null){
                RuleInfo ruleInfo = new RuleInfo(processingRuleName);
                ruleInfo = ruleInfo.withSqlExpressionFilter(expression);
                ListRulesResult listRulesResult = service.listRules(topicName, processingSubscriptionName);
                Iterator<RuleInfo> iterator = listRulesResult.getItems().iterator();
                while(iterator.hasNext()){
                    RuleInfo info = iterator.next();
                    if(!((SqlFilter)info.getFilter()).getSqlExpression().equals(expression)){
                        service.deleteRule(topicName, processingSubscriptionName, info.getName());
                        service.createRule(topicName, processingSubscriptionName, ruleInfo);
                    }
                }
            }else{
                throw new RuntimeException();
            }
        } catch (Exception exception) {
            SubscriptionInfo subInfo = new SubscriptionInfo(processingSubscriptionName);
            CreateSubscriptionResult result = service.createSubscription(topicName, subInfo);
            RuleInfo ruleInfo = new RuleInfo(processingRuleName);
            ruleInfo = ruleInfo.withSqlExpressionFilter(expression);
            CreateRuleResult ruleResult = service.createRule(topicName, processingSubscriptionName, ruleInfo);
            service.deleteRule(topicName, processingSubscriptionName, "$default");
        }
        ListRulesResult listRulesResult = service.listRules(topicName, processingSubscriptionName);
        Iterator<RuleInfo> iterator = listRulesResult.getItems().iterator();
        while(iterator.hasNext()){
            RuleInfo info = iterator.next();
            System.out.println("Processing rule::::::->"+((SqlFilter)info.getFilter()).getSqlExpression());
        }

    }

    public void initializeInstanceListenerSubscription() throws Exception{

        String topicName = topic.getTopicName();

        String sinkSubscriptionName = currentDomain + "-" + ServerInfo.port + "-sink-subscription";
        String sinkRuleName = currentDomain +"-sink-rule";
        String expression = "sourceAppId = '" + ServerInfo.port.concat("' AND "+getExpressionFromCSV(sinkEvents));

        try {
            GetSubscriptionResult getSinkSubscriptionInfo = service.getSubscription(topicName, sinkSubscriptionName);
            if(getSinkSubscriptionInfo!=null){
                RuleInfo ruleInfo = new RuleInfo(sinkRuleName);

                ruleInfo = ruleInfo.withSqlExpressionFilter(expression);

                ListRulesResult listRulesResult = service.listRules(topicName, sinkSubscriptionName);
                Iterator<RuleInfo> iterator = listRulesResult.getItems().iterator();
                while(iterator.hasNext()){
                    RuleInfo info = iterator.next();
                    if(!((SqlFilter)info.getFilter()).getSqlExpression().equals(expression)){
                        service.deleteRule(topicName, sinkSubscriptionName, info.getName());
                        service.createRule(topicName, sinkSubscriptionName, ruleInfo);
                    }
                }
            }else{
                throw new RuntimeException();
            }
        } catch (Exception exception) {
            SubscriptionInfo subInfo = new SubscriptionInfo(sinkSubscriptionName);
            subInfo.setAutoDeleteOnIdle(DatatypeFactory.newInstance().newDuration(1800000l));
            CreateSubscriptionResult result = service.createSubscription(topicName, subInfo);
            RuleInfo ruleInfo = new RuleInfo(sinkRuleName);
            ruleInfo = ruleInfo.withSqlExpressionFilter(expression);
            CreateRuleResult ruleResult = service.createRule(topicName, sinkSubscriptionName, ruleInfo);
            service.deleteRule(topicName, sinkSubscriptionName, "$default");
        }
        ListRulesResult listRulesResult = service.listRules(topicName, sinkSubscriptionName);
        Iterator<RuleInfo> iterator = listRulesResult.getItems().iterator();
        while(iterator.hasNext()){
            RuleInfo info = iterator.next();
            System.out.println("Sinking rule::::::->"+((SqlFilter)info.getFilter()).getSqlExpression());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final com.microsoft.windowsazure.Configuration config = ServiceBusConfiguration.configureWithSASAuthentication("samplemsgservicebus",
                "RootManageSharedAccessKey", sshKey, ".servicebus.windows.net");

        service = ServiceBusService.create(config);
    }

    private static String getExpressionFromCSV(String commaSeperatedEvents){
        if(StringUtils.isEmpty(commaSeperatedEvents)){
            throw new RuntimeException("Empty event names");
        }
        if(!commaSeperatedEvents.contains(",")){
            return "event = ".concat("'"+commaSeperatedEvents+"'");
        }

        String[] events = commaSeperatedEvents.split(",");
        StringBuilder builder = new StringBuilder("event IN (");
        for(int i=0;i<events.length;i++){
            builder.append("'".concat(events[i]).concat("'"));
            if(i!=events.length-1){
                builder.append(",");
            }
        }
        builder.append(")");
        return builder.toString();
    }
}
