package no.sample.isc.persistance.logging.component;

import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.implementation.SqlFilter;
import com.microsoft.windowsazure.services.servicebus.models.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.Topic;
import java.util.Iterator;

/**
 * Created by Ankit on 15-01-2017.
 */
@Component
public class DefaultSubscription implements InitializingBean {

    @Autowired
    @Qualifier("sbTopic")
    Topic topic;

    @Value("${current.event.log}")
    private String logEvents;

    @Value("${servicebus.sshkey}")
    private String sshKey;

    private ServiceBusContract service;

    public void initializeDomainListenerSubscription() throws Exception{
        String topicName = topic.getTopicName();

        String loggingSubscriptionName = "logging-subscription";
        String loggingRuleName = "logging-rule";
        String expression = expression = getExpressionFromCSV(logEvents);
        try {
            GetSubscriptionResult getLoggingSubscriptionInfo = service.getSubscription(topicName, loggingSubscriptionName);
            if(getLoggingSubscriptionInfo!=null){
                if(StringUtils.isNotEmpty(logEvents)){
                    RuleInfo ruleInfo = new RuleInfo(loggingRuleName);
                    ruleInfo = ruleInfo.withSqlExpressionFilter(expression);
                    CreateRuleResult ruleResult = service.createRule(topicName, loggingSubscriptionName, ruleInfo);
                    service.deleteRule(topicName, loggingSubscriptionName, "$default");
                }
            }else{
                throw new RuntimeException();
            }
        } catch (Exception exception) {
            SubscriptionInfo subInfo = new SubscriptionInfo(loggingSubscriptionName);
            CreateSubscriptionResult result = service.createSubscription(topicName, subInfo);
            if(StringUtils.isNotEmpty(logEvents)){
                RuleInfo ruleInfo = new RuleInfo(loggingRuleName);
                ruleInfo = ruleInfo.withSqlExpressionFilter(expression);
                CreateRuleResult ruleResult = service.createRule(topicName, loggingSubscriptionName, ruleInfo);
                service.deleteRule(topicName, loggingSubscriptionName, "$default");
            }
        }
        ListRulesResult listRulesResult = service.listRules(topicName, loggingSubscriptionName);
        Iterator<RuleInfo> iterator = listRulesResult.getItems().iterator();
        while(iterator.hasNext()){
            RuleInfo info = iterator.next();
            System.out.println("Processing rule::::::->"+((SqlFilter)info.getFilter()).getSqlExpression());
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
            return null;
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
