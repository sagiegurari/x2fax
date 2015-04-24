package org.fax4j.x2fax.http.servlet;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.fax4j.FaxException;
import org.fax4j.FaxJob;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This servlet enables to send faxes based on data provided in the HTTP request.<br>
 * This class also provides (via VendorPolicy class) the ability to restrict access, bill customers and so on,
 * before the fax is submitted.
 * 
 * @author 	Sagie Gur-Ari
 * @version 1.03
 * @since	0.01
 */
public class Web2FaxHttpServlet extends AbstractWeb2FaxHttpServlet
{
	/**Default UID*/
	private static final long serialVersionUID=1L;

	/**
	 * This is the class constructor.
	 */
	public Web2FaxHttpServlet()
	{
		super();
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
	@Override
	protected void writeTextResponse(FaxJob faxJob,HttpServletResponse response,Writer writer) throws ServletException,IOException
	{
		String text=this.createJSONResponse(faxJob);
		writer.write(text);
	}
	
	/**
	 * This function create a JSON response.
	 * 
	 * @param 	faxJob
	 * 			The fax job
	 * @return	The JSON string
	 * @throws	ServletException
	 * 			If the request for the POST could not be handled
	 * @throws	IOException
	 * 			If an input or output error is detected when the servlet handles the request
	 */
	protected String createJSONResponse(FaxJob faxJob) throws ServletException,IOException
	{
		//create JSON object with the fax job data
		JSONObject jsonObject=new JSONObject();
		try
		{
			jsonObject.put("FaxJobID",faxJob.getID());
		}
		catch(JSONException exception)
		{
			throw new FaxException("Unable to create JSON response data.",exception);
		}
		
		//convert to string
		String jsonString=jsonObject.toString();

		return jsonString;
	}
}