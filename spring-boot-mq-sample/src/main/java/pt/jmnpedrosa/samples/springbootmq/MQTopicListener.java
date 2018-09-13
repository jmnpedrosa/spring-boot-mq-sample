package pt.jmnpedrosa.samples.springbootmq;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class MQTopicListener implements MessageListener {

  private static final Logger LOG = LoggerFactory.getLogger(MQTopicListener.class);

  @Override  
  public void onMessage(Message message) {
    
    String msgText = null;
    
    try {
        msgText = ((TextMessage) message).getText();
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        
        Document doc = db.parse(new InputSource(new StringReader(msgText)));
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(doc), new StreamResult(out));

        LOG.info("Received new message: " + out.toString());
        
    } catch (Exception e) {
      LOG.error("Error processing received message!", e);
    }
    
    try {
      LOG.info("Sending acknowledge to JMS Queue.");
      message.acknowledge();
    } catch (JMSException e) {
      LOG.error("Error acknowledging message.\n", e);
    }
    
  }  
  
}
