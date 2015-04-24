package org.fax4j.x2fax.cli;

/**
 * This class enables to send faxes via CLI.<br>
 * This class does not provide vendor policy capability as it is intended to
 * be used internally inside the IT department and not published as a utility to
 * end customers.
 * 
 * @author 	Sagie Gur-Ari
 * @version 1.0
 * @since	0.01
 */
public final class CLI2FaxMain
{
	/**
	 * This is the class constructor.
	 */
	private CLI2FaxMain()
	{
		super();
	}

	/**
	 * The main method. 
	 * 
	 * @param	commandLineArguments
	 * 			The command line arguments
	 */
	public static void main(String[] commandLineArguments)
	{
		//create runner
		CLI2FaxRunner runner=new CLI2FaxRunner();
		runner.initialize();

		//submit fax
		runner.submitFaxJob(commandLineArguments);

		System.exit(0);
	}
}