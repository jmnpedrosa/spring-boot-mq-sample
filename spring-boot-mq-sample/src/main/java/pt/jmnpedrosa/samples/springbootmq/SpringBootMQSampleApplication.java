package pt.jmnpedrosa.samples.springbootmq;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageListener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import com.ibm.mq.jms.MQTopicConnectionFactory;

@SpringBootApplication(scanBasePackages = {"pt.jmnpedrosa.samples.springbootmq"})
@EnableJms
public class SpringBootMQSampleApplication {
  
  @Value("${jms.sender.connection.host_name}")
  private String hostname;
  
  @Value("${jms.sender.connection.port}")
  private int port;
  
  @Value("${jms.sender.connection.user_name}")
  private String username;
  
  @Value("${jms.sender.connection.password}")
  private String password;
  
  @Value("${jms.sender.connection.mq.transport_type}")
  private int transportType;
  
  @Value("${jms.sender.connection.mq.queue_manager}")
  private String queueManager;
  
  @Value("${jms.sender.connection.mq.channel}")
  private String channel;
  
  @Value("${jms.sender.queue}")
  private String queue;
  
  @Value("${jms.sender.encoding}")
  private String encoding;

  public static void main(String[] args) {
    SpringApplication.run(SpringBootMQSampleApplication.class, args);
  }

  @Bean
  public JmsListenerContainerFactory<?> mqContainerFactory(ConnectionFactory connectionFactory,
                                                  DefaultJmsListenerContainerFactoryConfigurer configurer) {
      DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
      factory.setConnectionFactory(mqTopicConnectionFactory());
      configurer.configure(factory, connectionFactory);
      return factory;
  }
  
  @Bean
  public MQTopicConnectionFactory mqTopicConnectionFactory() {
    MQTopicConnectionFactory factory = null;
    try {
        factory = new MQTopicConnectionFactory();
        factory.setHostName(hostname);
        factory.setPort(port);
        factory.setTransportType(transportType);
        factory.setQueueManager(queueManager);
        factory.setChannel(channel);
    }
    catch (JMSException e) {
        System.out.println(e);
    }
    return factory;
  }
  
  @Bean
  public AbstractMessageListenerContainer listenerContainer() {
    AbstractMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
    listenerContainer.setPubSubDomain(true);  
    listenerContainer.setConnectionFactory(mqTopicConnectionFactory());  
    listenerContainer.setDestinationName(queue);  
    listenerContainer.setMessageListener(mqTopicListener());
    listenerContainer.start(); // This starts the message listener
    return listenerContainer;
  }
  
  @Bean
  public MessageListener mqTopicListener() {
    return new MQTopicListener();
  }
}
