package bank.controller;

import bank.database.StoreCustomer;
import bank.database.TranslateXML;
import bank.beans.Customer;

import java.nio.file.WatchService;
import java.nio.file.FileSystems;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.nio.file.StandardWatchEventKinds;

public class FileWatchCustomer implements Runnable
{
	private String user;
	private String pass;

	public FileWatchCustomer(String user, String pass)
	{
		this.user = user;
		this.pass = pass;
	}

	private void watchForCustomerXML()
	{
		try
		{
			//set up watch
			WatchService watcher = FileSystems.getDefault().newWatchService();
			//directory path to xml files for customers
			Path dir = Paths.get("xml/customers/");
	
			//check for new and modified files
			dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, 
					StandardWatchEventKinds.ENTRY_MODIFY);

			//initialize objects for while loop
			WatchKey key;
			StoreCustomer customer = new StoreCustomer(user, pass);
			TranslateXML xml = new TranslateXML();
			key = watcher.take();
			for (WatchEvent<?> event : key.pollEvents())
			{
				//cast file name into string
				String fileName = event.context().toString();
				//add full path to name
				String filePath = "xml/customers/" + fileName;
				//pass full name into xml file reader into object
				Customer newCust = xml.readCustomerXML(filePath);
				//store customer in database
				customer.insertCustomer(newCust);
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
			watchForCustomerXML();
		}
		catch(Exception e)
		{
			System.out.println("Thread failed");
			e.printStackTrace();
		}
	}
}