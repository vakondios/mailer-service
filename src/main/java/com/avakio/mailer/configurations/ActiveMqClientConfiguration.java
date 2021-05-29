package com.avakio.mailer.configurations;

import com.avakio.mailer.properties.BrokerProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;

@Component
@Slf4j
public class ActiveMqClientConfiguration {

    private final BrokerProperties brokerProperties;
    private final String brokerUrl;

    @Autowired
    public ActiveMqClientConfiguration(BrokerProperties brokerProperties) {
        this.brokerProperties = brokerProperties;
        this.brokerUrl = "tcp://"+brokerProperties.getNetwork()+":" +brokerProperties.getPort();

        if (log.isDebugEnabled()) log.debug("Component Initialized.");
    }

    @Bean
    public ConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory activeMQConnectionFactory  = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        activeMQConnectionFactory.setUserName(brokerProperties.getUsername());
        activeMQConnectionFactory.setPassword(brokerProperties.getPassword());
        return  activeMQConnectionFactory;
    }

    @SuppressWarnings("SpringConfigurationProxyMethods")
    @Bean(name="jmsQueueTemplate")
    public JmsTemplate jmsQueueTemplate(){
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        if (brokerProperties.getTimeToLive()>0) {
            jmsTemplate.setPubSubDomain(true);  // enable for Pub Sub to topic. Not Required for Queue.
            jmsTemplate.setExplicitQosEnabled(true);
            jmsTemplate.setTimeToLive(brokerProperties.getTimeToLive());
        }
        return jmsTemplate;
    }

    @SuppressWarnings("SpringConfigurationProxyMethods")
    @Bean(name="jmsTopicTemplate")
    public JmsTemplate jmsTopicTemplate(){
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setPubSubDomain(true);
        if (brokerProperties.getTimeToLive()>0) {
            jmsTemplate.setExplicitQosEnabled(true);
            jmsTemplate.setTimeToLive(brokerProperties.getTimeToLive());
        }
        return jmsTemplate;
    }

    @SuppressWarnings("SpringConfigurationProxyMethods")
    @Bean(name="jmsTopicListenerContainerFactory")
    public DefaultJmsListenerContainerFactory jmsTopicListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("1-1");
        factory.setPubSubDomain(true);

        return factory;
    }

    @SuppressWarnings("SpringConfigurationProxyMethods")
    @Bean(name="jmsQueueListenerContainerFactory")
    public DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("1-1");

        return factory;
    }
}
