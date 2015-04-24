package org.fax4j.x2fax.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import org.fax4j.FaxJob;
import org.fax4j.FaxJobStatus;
import org.fax4j.common.LogLevel;
import org.fax4j.common.Logger;
import org.fax4j.common.SimpleLogger;
import org.fax4j.spi.AbstractFax4JClientSpi;
import org.fax4j.spi.FaxClientSpiFactory;
import org.fax4j.spi.FaxJobMonitorImpl;

/**
 * Helper for JUnit test cases.
 * 
 * @author 	Sagie Gur-Ari
 */
public final class TestUtil
{
	/**
	 * This is the class constructor.
	 */
	private TestUtil()
	{
		super();
	}
	
	/**
	 * Test method. 
	 * 
	 * @param 	className
	 * 			The SPI class name
	 * @param 	configuration
	 * 			The configuration
	 * @return	The configuration
	 */
	public static Properties createFaxClientSpiConfiguration(String className,Properties configuration)
	{
		Properties updatedConfiguration=configuration;
		if(updatedConfiguration==null)
		{
			updatedConfiguration=new Properties();
		}
		updatedConfiguration.setProperty("org.fax4j.proxy.enabled","false");
		updatedConfiguration.setProperty("org.fax4j.spi.type.map.test",className);
		updatedConfiguration.setProperty(FaxClientSpiFactory.DEFAULT_SPI_TYPE_PROPERTY_KEY,"test");
		
		return updatedConfiguration;
	}
	
	/**
	 * Test method. 
	 * 
	 * @param 	configuration
	 * 			The configuration
	 * @return	The configuration
	 */
	public static Properties createEmptyFaxClientSpiConfiguration(Properties configuration)
	{
		return TestUtil.createFaxClientSpiConfiguration(EmptyFaxClientSpi.class.getName(),configuration);
	}
	
	/**
	 * Helper class 
	 * 
	 * @author 	Sagie Gur-Ari
	 */
	public static class EmptyFaxClientSpi extends AbstractFax4JClientSpi
	{
		/**
		 * This is the class constructor.
		 */
		public EmptyFaxClientSpi()
		{
			super();
		}

		/**
		 * This is the class constructor.
		 * 
		 * @param	init
		 * 			True to run the init
		 */
		public EmptyFaxClientSpi(boolean init)
		{
			this();
			if(init)
			{
				Logger logger=new SimpleLogger();
				logger.setLogLevel(LogLevel.NONE);
				this.initialize(new HashMap<String,String>(),logger,new FaxJobMonitorImpl());
			}
		}

		/**
		 * Returns the configuration.
		 *  
		 * @return	The configuration
		 */
		public Map<String,String> getAllConfiguration()
		{
			return this.getConfiguration();
		}

		/**
		 * Empty method.
		 */
		@Override
		protected void initializeImpl()
		{
			//empty
		}

		/**
		 * This function returns true if the fax monitor events are supported by this SPI.
		 * 
		 * @return	True if the fax monitor events are supported by this SPI
		 */
		@Override
		public boolean isFaxMonitorEventsSupported()
		{
			return true;
		}
		
		/**
		 * This function will submit a new fax job.<br>
		 * The fax job ID may be populated by this method in the provided
		 * fax job object.
		 * 
		 * @param 	faxJob
		 * 			The fax job object containing the needed information
		 */
		@Override
		protected void submitFaxJobImpl(FaxJob faxJob)
		{
			faxJob.setID(String.valueOf((new Random()).nextInt()));
		}
		
		/**
		 * This function will suspend an existing fax job.
		 * 
		 * @param 	faxJob
		 * 			The fax job object containing the needed information
		 */
		@Override
		protected void suspendFaxJobImpl(FaxJob faxJob)
		{
			//empty
		}
		
		/**
		 * This function will resume an existing fax job.
		 * 
		 * @param 	faxJob
		 * 			The fax job object containing the needed information
		 */
		@Override
		protected void resumeFaxJobImpl(FaxJob faxJob)
		{
			//empty
		}
		
		/**
		 * This function will cancel an existing fax job.
		 * 
		 * @param 	faxJob
		 * 			The fax job object containing the needed information
		 */
		@Override
		protected void cancelFaxJobImpl(FaxJob faxJob)
		{
			//empty
		}
		
		/**
		 * This function returns the fax job status.<br>
		 * Not all SPIs support extraction of the fax job status.<br>
		 * In case the SPI is unable to extract or does not support extracting
		 * of the fax job status, it will return the UNKNOWN status.
		 * 
		 * @param 	faxJob
		 * 			The fax job object containing the needed information
		 * @return	The fax job status
		 */
		@Override
		protected FaxJobStatus getFaxJobStatusImpl(FaxJob faxJob)
		{
			String status=faxJob.getProperty("test.status",null);
			FaxJobStatus jobStatus=null;
			if(status==null)
			{
				jobStatus=FaxJobStatus.PENDING;
			}
			else
			{
				FaxJobStatus lastStatus=FaxJobStatus.valueOf(status);
				switch(lastStatus)
				{
					case PENDING:
						jobStatus=FaxJobStatus.IN_PROGRESS;
						break;
					default:
						jobStatus=FaxJobStatus.UNKNOWN;
						break;
				}
			}
			status=jobStatus.name();
			faxJob.setProperty("test.status",status);

			return jobStatus;
		}
	}
}