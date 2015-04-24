package org.fax4j.x2fax.email.james;

import java.util.Iterator;
import java.util.Properties;
import org.fax4j.FaxClient;
import org.fax4j.bridge.FaxBridge;
import org.fax4j.bridge.email.EMail2FaxBridge;
import org.fax4j.spi.FaxClientSpi;
import org.fax4j.util.ReflectionHelper;
import org.fax4j.x2fax.test.TestUtil;
import org.fax4j.x2fax.test.TestUtil.EmptyFaxClientSpi;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Class 
 * 
 * @author 	Sagie Gur-Ari
 */
public class EMail2FaxJamesMailetTest
{
	/**The mailet to test*/
	private EMail2FaxJamesMailet mailet;	

	/**
	 * Sets up the test objects.
	 * 
	 * @throws 	Exception
	 * 			Any exception
	 */
	@Before
	public void setUp() throws Exception
	{
		this.mailet=new EMail2FaxJamesMailet()
		{
			@Override
		    public Iterator<?> getInitParameterNames()
		    {
				Properties configuration=TestUtil.createEmptyFaxClientSpiConfiguration(null);
				return configuration.keySet().iterator();
		    }
			@Override
		    public String getInitParameter(String key)
		    {
				Properties configuration=TestUtil.createEmptyFaxClientSpiConfiguration(null);
				return configuration.getProperty(key);
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
		this.mailet.init();
		FaxBridge faxBridge=this.mailet.flowHelper.getFaxBridge();
		Assert.assertNotNull(faxBridge);
		Assert.assertEquals(EMail2FaxBridge.class,faxBridge.getClass());
		FaxClient faxClient=faxBridge.getFaxClient();
		FaxClientSpi faxClientSpi=(FaxClientSpi)ReflectionHelper.getField(FaxClient.class,"FAX_CLIENT_SPI").get(faxClient);
		Assert.assertNotNull(faxClientSpi);
		Assert.assertEquals(EmptyFaxClientSpi.class,faxClientSpi.getClass());
	}
}