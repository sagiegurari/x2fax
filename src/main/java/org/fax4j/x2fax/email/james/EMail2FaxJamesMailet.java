package org.fax4j.x2fax.email.james;

import java.util.Iterator;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.apache.mailet.Mail;
import org.apache.mailet.base.GenericMailet;
import org.fax4j.FaxJob;
import org.fax4j.bridge.ContextFaxBridge;
import org.fax4j.bridge.FaxBridgeFlowHelper;
import org.fax4j.bridge.FaxBridgeFlowHelper.FlowResponse;
import org.fax4j.bridge.email.EMail2FaxBridge;

/**
 * This servlet enables to send faxes based on data provided in the mail request.<br>
 * This class also provides (via VendorPolicy class) the ability to restrict access, bill customers and so on,
 * before the fax is submitted.
 * 
 * @author 	Sagie Gur-Ari
 * @version 1.01
 * @since	0.01
 */
public class EMail2FaxJamesMailet extends GenericMailet
{
	/**The flow helper*/
	protected FaxBridgeFlowHelper<Message> flowHelper;

	/**
	 * This is the class constructor.
	 */
	public EMail2FaxJamesMailet()
	{
		super();
	}

	/**
	 * This function initializes the mailet.
	 * 
	 * @throws	MessagingException
	 * 			Any error while initialization
	 */
	@Override
	public void init() throws MessagingException
	{
    	//get configuration
    	Properties configuration=new Properties();
    	Iterator<?> iterator=this.getInitParameterNames();
    	String key=null;
    	String value=null;
    	while(iterator.hasNext())
    	{
    		//get key/value
    		key=(String)iterator.next();
    		value=this.getInitParameter(key);
    		
    		//put in configuration
    		configuration.setProperty(key,value);
    	}
    	
    	//get type
    	String type=this.getFaxClientSpiType();
    	
    	//create fax bridge
    	ContextFaxBridge<Message> faxBridge=this.createFaxBridge();
    	
    	//create flow helper
    	this.flowHelper=new FaxBridgeFlowHelper<Message>(faxBridge,type,configuration,this);
    }
	
	/**
	 * Returns the type of SPI to use. 
	 * 
	 * @return	The SPI type
	 */
	protected String getFaxClientSpiType()
	{
		return null;
	}
	
	/**
	 * Returns a new fax bridge. 
	 * 
	 * @return	The fax bridge
	 */
	protected ContextFaxBridge<Message> createFaxBridge()
	{
		return new EMail2FaxBridge();
	}
	
	/**
	 * This function is invoked after the fax is submitted.
	 * 
	 * @param 	mail
	 * 			The mail with the fax data
	 * @param 	faxJob
	 * 			The submitted fax job
	 */
	protected void postSubmitFaxJob(Mail mail,FaxJob faxJob)
	{
		mail.setState(Mail.GHOST);
	}

	/**
	 * This function 
	 * 
	 * @param 	mail
	 * 			The mail with the fax data
	 * @throws 	MessagingException
	 * 			Any exception while handling the mail message
	 */
	@Override
	public void service(Mail mail) throws MessagingException
	{
		//get mail message
		Message mailMessage=mail.getMessage();
		
    	//submit fax
    	FlowResponse flowResponse=this.flowHelper.submitFaxJob(mailMessage,mailMessage,true);
    	
    	if(flowResponse.isContinueFlow())
    	{
    		//get fax job
    		FaxJob faxJob=flowResponse.getFaxJob();

    		//invoke post hook
    		this.postSubmitFaxJob(mail,faxJob);
    	}
	}
}