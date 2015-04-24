package org.fax4j.x2fax.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.fax4j.util.IOHelper;

/**
 * Generic launcher for a sub java process. 
 * 
 * @author 	Sagie Gur-Ari
 * @version 1.01
 * @since	0.01
 */
public final class JavaLauncher
{
	/**
	 * This is the class constructor.
	 */
	private JavaLauncher()
	{
		super();
	}
	
	/**
	 * The main function that starts the sub process.
	 * 
	 * @param	arguments
	 * 			The command line arguments
	 * @throws 	IOException
	 * 			If unable to start the process 
	 */
	public static void main(String[] arguments) throws IOException
	{
		if((arguments!=null)&&(arguments.length==1)&&(arguments[0].equals("-help")))
		{
			File instructionsFile=new File("./Instructions.txt");
			if(instructionsFile.exists())
			{
				String text=IOHelper.readTextFile(instructionsFile);
				System.out.println(text);
			}
			else
			{
				System.out.println("Help information not available.");
			}
		}
		else	//start java process
		{
			JavaLauncher.invokeJava(arguments);
		}
		
		//exit parent process
		System.exit(0);
	}

	/**
	 * This function that starts the java sub process.
	 * 
	 * @param	arguments
	 * 			The command line arguments
	 * @throws 	IOException
	 * 			If unable to start the process 
	 */
	private static void invokeJava(String[] arguments) throws IOException
	{
		//get main class
		String className=System.getProperty("org.fax4j.x2fax.launcher.class.name");
		
		//get jar directory path
		String jarDirectoryPath=System.getProperty("org.fax4j.x2fax.launcher.jar.dir","./lib");
		File jarDirectory=new File(jarDirectoryPath);
		FilenameFilter filter=new FilenameFilter()
		{
			public boolean accept(File dir,String name)
			{
				return name.endsWith(".jar");
			}
		};
		File[] files=jarDirectory.listFiles(filter);
		
		StringBuilder buffer=new StringBuilder();
		buffer.append(".");
		for(int index=0;index<files.length;index++)
		{
			buffer.append(File.pathSeparator);
			buffer.append(files[index]);
		}
		String classpath=buffer.toString();
		
		//get java executable
		String javaHome=System.getProperty("java.home");
		String javaExe=javaHome+File.separator+"bin"+File.separator+"java";
		
		//create command
		List<String> command=new LinkedList<String>();
		command.add(javaExe);
		command.add("-cp");
		command.add(classpath);
		command.add(className);
		if(arguments!=null)
		{
			Collections.addAll(command,arguments);
		}

		//start process
		ProcessBuilder processBuilder=new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true);
		Process process=processBuilder.start();
		InputStream inputStream=process.getInputStream();
		Reader reader=IOHelper.createReader(inputStream,IOHelper.getDefaultEncoding());

		char[] data=new char[100];
		int read=-1;
		try
		{
			do
			{
				//read next buffer
				read=reader.read(data);
				
				if(read!=-1)
				{
					//write to system out
					char[] fullData=Arrays.copyOfRange(data,0,read);
					System.out.println(new String(fullData));
				}
			}while(read!=-1);
		}
		catch(IOException exception)
		{
			//ignore
		}

		System.out.println("Exit Code: "+process.exitValue());
	}
}