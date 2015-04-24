package org.fax4j.x2fax.http.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.fax4j.FaxException;
import org.fax4j.FaxJob;
import org.fax4j.bridge.ContextFaxBridge;
import org.fax4j.bridge.FaxBridgeFlowHelper;
import org.fax4j.bridge.FaxBridgeFlowHelper.FlowResponse;
import org.fax4j.bridge.http.HTTP2FaxBridge;
import org.fax4j.spi.http.HTTPRequest;
import org.fax4j.spi.http.HTTPRequest.ContentPart;
import org.fax4j.spi.http.HTTPRequest.ContentPartType;
import org.fax4j.util.IOHelper;

/**
 * This servlet enables to send faxes based on data provided in the HTTP request.<br>
 * This class also provides (via VendorPolicy class) the ability to restrict access, bill customers and so on,
 * before the fax is submitted.<br>
 * This class does not provide complete implementation of the servlet and requires extending.
 * 
 * @author 	Sagie Gur-Ari
 * @version 1.0
 * @since	0.07
 */
public abstract class AbstractWeb2FaxHttpServlet extends HttpServlet
{
	/**The flow helper*/
	protected transient FaxBridgeFlowHelper<HTTPRequest> flowHelper;
	/**Default UID*/
	private static final long serialVersionUID=1L;

	/**
	 * This is the class constructor.
	 */
	public AbstractWeb2FaxHttpServlet()
	{
		super();
	}

	/**
	 * This function initializes the servlet.
	 * 
	 * @throws	ServletException
	 * 			Any error while initialization
	 */
	@Override
    public void init() throws ServletException
    {
    	//get configuration
    	Properties configuration=this.getFaxBridgeConfiguration();
    	
    	//get type
    	String type=this.getFaxClientSpiType();
    	
    	//create fax bridge
    	ContextFaxBridge<HTTPRequest> faxBridge=this.createFaxBridge();
    	
    	//create flow helper
    	this.flowHelper=new FaxBridgeFlowHelper<HTTPRequest>(faxBridge,type,configuration,this);
    }
    
    /**
     * This function returns the configuration used to initialize the fax bridge.
     * 
     * @return	The configuration used to initialize the fax bridge
     */
    protected Properties getFaxBridgeConfiguration()
    {
    	//get servlet configuration
    	ServletConfig servletConfig=this.getServletConfig();
    	
    	//get configuration
    	Properties configuration=new Properties();
    	Enumeration<?> enumeration=servletConfig.getInitParameterNames();
    	String key=null;
    	String value=null;
    	while(enumeration.hasMoreElements())
    	{
    		//get key/value
    		key=(String)enumeration.nextElement();
    		value=servletConfig.getInitParameter(key);
    		
    		//put in configuration
    		configuration.setProperty(key,value);
    	}

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
	protected ContextFaxBridge<HTTPRequest> createFaxBridge()
	{
		return new HTTP2FaxBridge();
	}

	/**
	 * This function converts the servlet request to a HTTP request object. 
	 * 
	 * @param	request
	 * 			The servlet request
	 * @param	httpRequest
	 * 			The HTTP request to populate
	 * @throws	IOException
	 * 			If an input or output error is detected when the servlet handles the request
	 */
	protected void convertSinglePartRequest(HttpServletRequest request,HTTPRequest httpRequest) throws IOException
	{
		//get input stream
		InputStream inputStream=request.getInputStream();

		//read data
		byte[] content=IOHelper.readStream(inputStream);

		//set content
		httpRequest.setContent(content);
	}

	/**
	 * This function creates and returns a new file item factory.
	 * 	
	 * @return	The file item factory
	 */
	protected FileItemFactory createFileItemFactory()
	{
		FileItemFactory factory=new DiskFileItemFactory();
		return factory;
	}
	
	/**
	 * This function converts the servlet request to a HTTP request object. 
	 * 
	 * @param	request
	 * 			The servlet request
	 * @param	httpRequest
	 * 			The HTTP request to populate
	 * @throws	IOException
	 * 			If an input or output error is detected when the servlet handles the request
	 */
	protected void convertMultiPartRequest(HttpServletRequest request,HTTPRequest httpRequest) throws IOException
	{
		ContentPart<?>[] content=null;
		try
		{
			FileItemFactory factory=this.createFileItemFactory();
			ServletFileUpload multiPartRequestHelper=new ServletFileUpload(factory);
			List<FileItem> partsList=multiPartRequestHelper.parseRequest(request);
			
			//init content array
			content=new ContentPart<?>[partsList.size()];
			
			Iterator<FileItem> iterator=partsList.iterator();
			FileItem fileItem=null;
			int index=0;
			String name=null;
			byte[] data=null;
			while(iterator.hasNext())
			{
				//get next item
				fileItem=iterator.next();
				
				//add to parts list
				name=fileItem.getFieldName();
				data=fileItem.get();
				content[index]=new ContentPart<byte[]>(name,data,ContentPartType.BINARY);
				
				//advance index
				index++;
			}
		}
		catch(FileUploadException exception)
		{
			throw new FaxException("Unable to part multi part request.",exception);
		}

		//set content
		httpRequest.setContent(content);
	}
	
	/**
	 * This function converts the servlet request to a HTTP request object. 
	 * 
	 * @param	request
	 * 			The servlet request
	 * @return	The HTTP request
	 * @throws	IOException
	 * 			If an input or output error is detected when the servlet handles the request
	 */
	protected HTTPRequest convertRequest(HttpServletRequest request) throws IOException
	{
		//init request
		HTTPRequest httpRequest=new HTTPRequest();

		//read content
		int length=request.getContentLength();
		if(length>0)
		{
			boolean multiPartRequest=ServletFileUpload.isMultipartContent(request);
			if(multiPartRequest)
			{
				this.convertMultiPartRequest(request,httpRequest);
			}
			else
			{
				this.convertSinglePartRequest(request,httpRequest);
			}
		}
		
		//set values
		httpRequest.setResource(request.getContextPath());
		httpRequest.setParametersText(request.getQueryString());

		return httpRequest;
	}
	
	/**
	 * This function sends the HTTP response after sending the fax.
	 * 
	 * @param 	faxJob
	 * 			The fax job
	 * @param	response
	 * 			The HTTP response
	 * @throws	ServletException
	 * 			If the request for the POST could not be handled
	 * @throws	IOException
	 * 			If an input or output error is detected when the servlet handles the request
	 */
	protected void sendResponse(FaxJob faxJob,HttpServletResponse response) throws ServletException,IOException
	{
		//set status
		response.setStatus(HttpServletResponse.SC_OK);

		//set response content type
		response.setContentType("text/plain");

		//get writer
		Writer writer=response.getWriter();
		
		//write fax job details
		this.writeTextResponse(faxJob,response,writer);
		
		//commit response
		response.flushBuffer();
	}

	/**
	 * This function handles the HTTP request and submits the fax.
	 * 
	 * @param	request
	 * 			The HTTP request
	 * @param	response
	 * 			The HTTP response
	 * @throws	ServletException
	 * 			If the request for the POST could not be handled
	 * @throws	IOException
	 * 			If an input or output error is detected when the servlet handles the request
	 */
    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
    	//create request data holder
    	RequestDataHolder requestDataHolder=new RequestDataHolder(request,response);

		//convert request
		HTTPRequest httpRequest=this.convertRequest(request);

    	//submit fax
    	FlowResponse flowResponse=this.flowHelper.submitFaxJob(httpRequest,requestDataHolder,true);
    	
    	if(flowResponse.isContinueFlow())
    	{
    		//get fax job
    		FaxJob faxJob=flowResponse.getFaxJob();

    		//send response
    		this.sendResponse(faxJob,response);
    	}
    }
	
	/**
	 * This function writes the response text to the HTTP response stream.
	 * 
	 * @param 	faxJob
	 * 			The fax job
	 * @param	response
	 * 			The HTTP response
	 * @param 	writer
	 * 			The writer used to write the output to
	 * @throws	ServletException
	 * 			If the request for the POST could not be handled
	 * @throws	IOException
	 * 			If an input or output error is detected when the servlet handles the request
	 */
	protected abstract void writeTextResponse(FaxJob faxJob,HttpServletResponse response,Writer writer) throws ServletException,IOException;

	/**
	 * Holds the request data for the vendor policy.
	 * 
	 * @author 	Sagie Gur-Ari
	 * @version 1.0
	 * @since	0.01
	 */
    public static class RequestDataHolder
    {
    	/**The HTTP request*/
    	private final HttpServletRequest REQUEST;
    	/**The HTTP response*/
    	private final HttpServletResponse RESPONSE;
    	
    	/**
    	 * This is the class constructor.
    	 *
		 * @param	request
		 * 			The HTTP request
		 * @param	response
		 * 			The HTTP response
    	 */
    	public RequestDataHolder(HttpServletRequest request,HttpServletResponse response)
    	{
    		super();

    		this.REQUEST=request;
    		this.RESPONSE=response;
    	}
    	
    	/**
    	 * Returns the http request. 
    	 * 
    	 * @return	The http request
    	 */
    	public HttpServletRequest getHttpServletRequest()
    	{
    		return this.REQUEST;
    	}
    	
    	/**
    	 * Returns the http response. 
    	 * 
    	 * @return	The http response
    	 */
    	public HttpServletResponse getHttpServletResponse()
    	{
    		return this.RESPONSE;
    	}
    }
}