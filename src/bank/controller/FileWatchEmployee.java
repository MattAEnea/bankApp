package bank.controller;

import bank.database.StoreEmployee;
import bank.database.TranslateXML;
import bank.beans.Employee;

import java.nio.file.WatchService;
import java.nio.file.FileSystems;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.nio.file.StandardWatchEventKinds;

public class FileWatchEmployee implements Runnable
{
	private String user;
	private String pass;

	public FileWatchEmployee(String user, String pass)
	{
		this.user = user;
		this.pass = pass;
	}

	private void watchForEmployeeXML()
	{
		try
		{
			//set up watch
			WatchService watcher = FileSystems.getDefault().newWatchService();
			//directory path to xml files for employees
			Path dir = Paths.get("xml/employees/");
	
			//check for new and modified files
			dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, 
					StandardWatchEventKinds.ENTRY_MODIFY);

			//initialize objects for while loop
			WatchKey key;
			StoreEmployee employee = new StoreEmployee(user, pass);
			TranslateXML xml = new TranslateXML();
			key = watcher.take();
			for (WatchEvent<?> event : key.pollEvents())
			{
				//cast file name into string
				String fileName = event.context().toString();
				//add full path to name
				String filePath = "xml/employees/" + fileName;
				//pass full name into xml file reader into object
				Employee newEmpl = xml.readEmployeeXML(filePath);
				//store employee in database]
				employee.insertEmployee(newEmpl);
			}

			
		}
		catch(Exception e)
		{
			System.out.println("File watching failed");
			e.printStackTrace();
		}
	}

	public void run()
	{
		try
		{
			watchForEmployeeXML();
		}
		catch(Exception e)
		{
			System.out.println("Thread failed");
			e.printStackTrace();
		}
	}
}