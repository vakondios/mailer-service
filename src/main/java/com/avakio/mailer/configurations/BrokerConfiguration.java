package com.avakio.mailer.configurations;

import com.avakio.mailer.properties.BrokerProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class BrokerConfiguration {

    private final BrokerProperties brokerProperties;
    private final String brokerUrl;

    @Autowired
    public BrokerConfiguration(BrokerProperties brokerProperties) {
        this.brokerProperties = brokerProperties;
        this.brokerUrl = "tcp://"+brokerProperties.getNetwork()+":" + brokerProperties.getPort();
    }

    /* Active MQ Embedded Broker configuration */
    @SneakyThrows
    @Bean
    public BrokerService broker() {
        final BrokerService broker = new BrokerService();
        broker.addConnector(brokerUrl);
        broker.setBrokerId(UUID.randomUUID().toString());
        broker.setBrokerName(brokerProperties.getName());
        broker.setPersistent(brokerProperties.isPersistent());
        broker.setDeleteAllMessagesOnStartup(brokerProperties.isDeleteAllMessagesOnStartup());
        broker.setUseJmx(true);
        broker.setUseAuthenticatedPrincipalForJMSXUserID(true);

        // Protect with Username & Password
        List<AuthenticationUser> users = new ArrayList<>();
        users.add(new AuthenticationUser(brokerProperties.getUsername(), brokerProperties.getPassword(), "users"));
        SimpleAuthenticationPlugin authenticationPlugin = new SimpleAuthenticationPlugin(users);
        authenticationPlugin.setAnonymousAccessAllowed(false);
        BrokerPlugin[] plugins = new BrokerPlugin[] {authenticationPlugin};
        broker.setPlugins(plugins);

        return broker;
    }
}
