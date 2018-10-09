package pt.jmnpedrosa.samples.springbootmq;

import java.util.concurrent.CompletableFuture;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class MQTopicListener implements MessageListener {

  private static final Logger LOG = LoggerFactory.getLogger(MQTopicListener.class);
  
  @Value("${message.interval.secs}")
  private int messageInterval;
  
  @Autowired
  private MQTopicProcessor topicProcessor;

  @Override  
  public void onMessage(Message message) {
    
	  //Process received message in new thread.
	  if (message instanceof TextMessage) {
		  try {
			  final String msgText = ((TextMessage) message).getText();
			  CompletableFuture.runAsync(() -> { topicProcessor.processMessage(msgText); });
		  } catch (Exception e) {
			  LOG.error("Error processing received message!", e);
		  }
	  }

	  try {
		  LOG.info("Sending acknowledge to JMS Queue.");
		  message.acknowledge();
	  } catch (JMSException e) {
		  LOG.error("Error acknowledging message.\n", e);
	  }

	  // Parametrized wait interval for message listening.
	  if (messageInterval > 0) {
		  try {
			  Thread.sleep(1000 * messageInterval);
		  } catch (InterruptedException e) {
			  LOG.error("Error sleeping PricesListener!", e);
		  }
	  }
  }
  
}
