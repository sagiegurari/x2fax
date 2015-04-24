package org.fax4j.x2fax.cli;

import java.io.File;
import java.util.Properties;
import org.fax4j.FaxClient;
import org.fax4j.FaxJob;
import org.fax4j.bridge.FaxBridge;
import org.fax4j.bridge.process.Process2FaxBridge;
import org.fax4j.spi.FaxClientSpi;
import org.fax4j.util.IOHelper;
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
public class CLI2FaxRunnerTest
{
	/**The runner to test*/
	private CLI2FaxRunner runner;

	/**
	 * Sets up the test objects.
	 * 
	 * @throws 	Exception
	 * 			Any exception
	 */
	@Before
	public void setUp() throws Exception
	{
		this.runner=new CLI2FaxRunner()
		{
			@Override
		    protected Properties getFaxBridgeConfiguration()
		    {
		    	return TestUtil.createEmptyFaxClientSpiConfiguration(null);
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
		this.runner.initialize();
		FaxBridge faxBridge=this.runner.flowHelper.getFaxBridge();
		Assert.assertNotNull(faxBridge);
		Assert.assertEquals(Process2FaxBridge.class,faxBridge.getClass());
		FaxClient faxClient=faxBridge.getFaxClient();
		FaxClientSpi faxClientSpi=(FaxClientSpi)ReflectionHelper.getField(FaxClient.class,"FAX_CLIENT_SPI").get(faxClient);
		Assert.assertNotNull(faxClientSpi);
		Assert.assertEquals(EmptyFaxClientSpi.class,faxClientSpi.getClass());
	}

	/**
	 * Test 
	 * 
	 * @throws 	Exception
	 * 			Any exception
	 */
	@Test
	public void getFaxClientSpiTypeTest() throws Exception
	{
		String output=this.runner.getFaxClientSpiType();
		Assert.assertNull(output);
	}

	/**
	 * Test 
	 * 
	 * @throws 	Exception
	 * 			Any exception
	 */
	@Test
	public void submitFaxJobTest() throws Exception
	{
		this.runner.initialize();

		File file=File.createTempFile("temp_",".txt");
		file.deleteOnExit();
		IOHelper.writeTextFile("abc",file);

		String[] input=new String[]{"-target_address","12345","-file",file.getPath()};
		FaxJob faxJob=this.runner.submitFaxJob(input);
		Assert.assertNotNull(faxJob);
		Assert.assertNotNull(faxJob.getFile());
		Assert.assertTrue(faxJob.getFile().exists());
		
		file.delete();
	}
}