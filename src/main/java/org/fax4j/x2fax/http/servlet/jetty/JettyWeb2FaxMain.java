package org.fax4j.x2fax.http.servlet.jetty;

import javax.servlet.http.HttpServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.fax4j.x2fax.http.servlet.Web2FaxHttpServlet;

/**
 * This class starts up a jetty web server to enable to provide
 * a web2fax bridge.
 * 
 * @author 	Sagie Gur-Ari
 * @version 1.0
 * @since	0.01
 */
public final class JettyWeb2FaxMain
{
	/**
	 * This is the class constructor.
	 */
	private JettyWeb2FaxMain()
	{
		super();
	}

	/**
	 * The main method. 
	 * 
	 * @param	commandLineArguments
	 * 			The command line arguments
	 * @throws	Exception
	 * 			Any exception while setting up and starting the Jetty server
	 */
	public static void main(String[] commandLineArguments) throws Exception
	{
		//get port
		String value=System.getProperty("fax4j.http.port","80");
		int port=Integer.parseInt(value);

		//create server
		Server server=new Server(port);
		
        ServletContextHandler context=new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
 
        //add fax4j servlet
        HttpServlet servlet=new Web2FaxHttpServlet();
        context.addServlet(new ServletHolder(servlet),"/Web2Fax/*");
 
        server.start();
        server.join();
	}
}