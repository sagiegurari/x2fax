package org.fax4j.x2fax.cli;

import java.util.Properties;
import org.fax4j.FaxJob;
import org.fax4j.bridge.ContextFaxBridge;
import org.fax4j.bridge.FaxBridgeFlowHelper;
import org.fax4j.bridge.FaxBridgeFlowHelper.FlowResponse;
import org.fax4j.bridge.process.Process2FaxBridge;

/**
 * This class runs the CLI2Fax flow.<br>
 * This class does not provide vendor policy capability as it is intended to
 * be used internally inside the IT department and not published as a utility to
 * end customers.
 * 
 * @author 	Sagie Gur-Ari
 * @version 1.01
 * @since	0.01
 */
public class CLI2FaxRunner
{
	/**The flow helper*/
	protected FaxBridgeFlowHelper<String[]> flowHelper;

	/**
	 * This is the class constructor.
	 */
	public CLI2FaxRunner()
	{
		super();
	}

	/**
	 * This function initializes the runner.
	 */
    public void initialize()
    {
    	//get configuration
		Properties configuration=this.getFaxBridgeConfiguration();
    	
    	//get type
    	String type=this.getFaxClientSpiType();

    	//create fax bridge
    	ContextFaxBridge<String[]> faxBridge=this.createFaxBridge();
    	
    	//create flow helper
    	this.flowHelper=new FaxBridgeFlowHelper<String[]>(faxBridge,type,configuration,this);
    }
    
    /**
     * This function returns the configuration used to initialize the fax bridge.
     * 
     * @return	The configuration used to initialize the fax bridge
     */
    protected Properties getFaxBridgeConfiguration()
    {
    	//get configuration
		Properties configuration=System.getProperties();
    	
    	return configuration;
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
	protected ContextFaxBridge<String[]> createFaxBridge()
	{
		return new Process2FaxBridge();
	}
	
	/**
	 * This function handles the process output.
	 * 
	 * @param	faxJob
	 * 			The fax job
	 */
	protected void handleOutput(FaxJob faxJob)
	{
		System.out.println("fax.operation.done=true");
		System.out.println("fax.job.id="+faxJob.getID());
		System.out.println(faxJob);
	}

	/**
	 * Runs the CLI2Fax flow.
	 * 
	 * @param	commandLineArguments
	 * 			The command line arguments
	 * @return	The fax job
	 */
	public FaxJob submitFaxJob(String[] commandLineArguments)
	{
    	//submit fax
    	FlowResponse flowResponse=this.flowHelper.submitFaxJob(commandLineArguments,commandLineArguments,false);
    	
		//get fax job
		FaxJob faxJob=flowResponse.getFaxJob();

    	//handle output
		this.handleOutput(faxJob);

		return faxJob;
	}
}