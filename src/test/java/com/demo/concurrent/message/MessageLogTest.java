package com.demo.concurrent.message;

import java.sql.Timestamp;

import com.demo.concurrent.export.MessageLog;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import junit.framework.TestCase;


/**
 * Based on Test Driving Development (TDD) which is one of most important part of software development
 * to insure a quality code.
 * <p>
 * 
 * @author Andrew He
 *
 */
public class MessageLogTest  extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	 public void testLogWriteReadCase() throws Exception {
		 Message msg1 = new StringMessage();
		 msg1.setMessageId(1L);
		 msg1.setMessageSource("Source 1");
		 msg1.setMessageTimestamp(new Timestamp(System.currentTimeMillis()));
		 msg1.setMessageBody("this is message boady 1");
		 
		 Message msg2 = new StringMessage();
		 msg2.setMessageId(2L);
		 msg2.setMessageSource("Source 2");
		 msg2.setMessageTimestamp(new Timestamp(System.currentTimeMillis()));
		 msg2.setMessageBody("this is message boady 2");

		 
		 ConcurrentLinkedQueue<Message> queue = new  ConcurrentLinkedQueue<Message>();
		 queue.add(msg1);
		 queue.add(msg2);
		 
		 MessageLog.writeLog(queue, "test1.csv", ";", false);
		 
		 List<Message> messages = MessageLog.readLog("test1.csv", ";");

		 assertEquals(messages.get(0).toString(), msg1.toString());
		 assertEquals(messages.get(1).toString(), msg2.toString());		 		 
		 
	 }

}
