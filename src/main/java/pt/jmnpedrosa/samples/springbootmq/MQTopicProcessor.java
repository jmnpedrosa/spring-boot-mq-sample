package pt.jmnpedrosa.samples.springbootmq;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Service
public class MQTopicProcessor {
	
	private static final Logger LOG = LoggerFactory.getLogger(MQTopicProcessor.class);
	
	public void processMessage(String msgText) {
		
		// Read the XML message.
	    Writer out = null;
	    try {
	      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	      dbf.setValidating(false);
	      DocumentBuilder db = dbf.newDocumentBuilder();
	      
	      Document doc = db.parse(new InputSource(new StringReader(msgText)));
	      Transformer tf = TransformerFactory.newInstance().newTransformer();
	      tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	      tf.setOutputProperty(OutputKeys.INDENT, "yes");
	      out = new StringWriter();
	      tf.transform(new DOMSource(doc), new StreamResult(out));
	    } catch (IllegalArgumentException | ParserConfigurationException 
	        | SAXException | IOException | TransformerException e) {
	      LOG.error("Error processing message.", e);
	    }
	    
	    String xmlMessage = out.toString();
	    LOG.debug("Received new XML message: " + xmlMessage);
	    
	}

}
