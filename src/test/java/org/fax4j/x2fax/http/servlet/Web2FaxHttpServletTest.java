package org.fax4j.x2fax.http.servlet;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import javax.servlet.ServletConfig;
import org.fax4j.FaxClient;
import org.fax4j.bridge.FaxBridge;
import org.fax4j.bridge.http.HTTP2FaxBridge;
import org.fax4j.spi.FaxClientSpi;
import org.fax4j.util.ReflectionHelper;
import org.fax4j.x2fax.test.TestUtil;
import org.fax4j.x2fax.test.TestUtil.EmptyFaxClientSpi;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

/**
 * Test Class 
 * 
 * @author 	Sagie Gur-Ari
 */
public class Web2FaxHttpServletTest
{
	/**The servlet to test*/
	private Web2FaxHttpServlet servlet;	

	/**
	 * Sets up the test objects.
	 * 
	 * @throws 	Exception
	 * 			Any exception
	 */
	@SuppressWarnings("serial")
	@Before
	public void setUp() throws Exception
	{
		this.servlet=new Web2FaxHttpServlet()
		{
			@SuppressWarnings({"boxing","unchecked"})
			@Override
		    public ServletConfig getServletConfig()
		    {
				Properties configuration=TestUtil.createEmptyFaxClientSpiConfiguration(null);
				
				ServletConfig config=Mockito.mock(ServletConfig.class);
				Enumeration<Object> enumeration=Mockito.mock(Enumeration.class);
				OngoingStubbing<Boolean> hasMoreElementsStub=Mockito.when(enumeration.hasMoreElements());
				for(int index=0;index<configuration.size();index++)
				{
					hasMoreElementsStub=hasMoreElementsStub.thenReturn(Boolean.TRUE);
				}
				hasMoreElementsStub.thenReturn(Boolean.FALSE);
				Mockito.when(config.getInitParameterNames()).thenReturn(enumeration);
				Entry<Object,Object> entry=null;
				Iterator<Entry<Object,Object>> iterator=configuration.entrySet().iterator();
				OngoingStubbing<Object> nextElementStub=Mockito.when(enumeration.nextElement());
				while(iterator.hasNext())
				{
					entry=iterator.next();
					nextElementStub=nextElementStub.thenReturn(entry.getKey());
					Mockito.when(config.getInitParameter((String)entry.getKey())).thenReturn((String)entry.getValue());
				}
				
				return config;
		    }
		};
	}

	/**
	 * Test 
	 * 
	 * @throws 	Exception
	 * 			Any exception
	 */
	@Test
	public void initializeTest() throws Exception
	{
		this.servlet.init();
		FaxBridge faxBridge=this.servlet.flowHelper.getFaxBridge();
		Assert.assertNotNull(faxBridge);
		Assert.assertEquals(HTTP2FaxBridge.class,faxBridge.getClass());
		FaxClient faxClient=faxBridge.getFaxClient();
		FaxClientSpi faxClientSpi=(FaxClientSpi)ReflectionHelper.getField(FaxClient.class,"FAX_CLIENT_SPI").get(faxClient);
		Assert.assertNotNull(faxClientSpi);
		Assert.assertEquals(EmptyFaxClientSpi.class,faxClientSpi.getClass());
	}
}