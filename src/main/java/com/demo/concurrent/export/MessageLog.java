package com.demo.concurrent.export;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.demo.concurrent.message.Message;
import com.demo.concurrent.message.StringMessage;

public class MessageLog {
	private static String delimiter = "^";

	
	public static List<Message> readLog(String filename, String delim) throws Exception{
		if(delim==null || delim.trim().equals(""))
			delim=delimiter;

		String line = null;
	    List<Message> records = new ArrayList<Message>();

	    // wrap a BufferedReader around FileReader
	    BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));

	    // use the readLine method of the BufferedReader to read one line at a time.
	    // the readLine method returns null when there is nothing else to read.
	    while ((line = bufferedReader.readLine()) != null)
	    {
	    	String[] fields = line.split(delim);
	    	Message msg = new StringMessage();
	    	msg.setMessageTimestamp(Timestamp.valueOf(fields[0]));
	    	msg.setMessageSource(fields[1]);
	    	msg.setMessageId(Long.parseLong(fields[2]));
	    	msg.setMessageBody(fields[3]);
	        records.add(msg);
	    }
	  
	    // close the BufferedReader when we're done
	    bufferedReader.close();
	    return records;

	}
	
	public static void writeLog(ConcurrentLinkedQueue<Message> messages, String filename, String delim) throws Exception{
		writeLog(messages, filename, delim, true);
	}
	
	public static void writeLog(ConcurrentLinkedQueue<Message> messages, String filename, String delim, boolean append) throws Exception{

		if(delim==null || delim.trim().equals(""))
			delim=delimiter;
		
	    // wrap a BufferedReader around FileReader
	    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename, append));

	    //just complete the concurrent records
	    int size = messages.size();
	    for(int i=0;i<size;i++){

	    	Message msg = messages.remove();
       	 	System.out.println("Message Log Export: " +  msg.toString());
	    	
	    	String record = msg.getMessageTimestamp() + delim + msg.getMessageSource() + delim  +  msg.getMessageId() + delim +  msg.getMessageBody()+System.lineSeparator();
	    	bufferedWriter.write(record);
	    	
	    }
	    // close the BufferedReader when we're done
	    bufferedWriter.close();
	}
}
