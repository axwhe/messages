package com.demo.concurrent.producer;

import java.sql.Timestamp;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.demo.concurrent.consumer.MessageConsumer;
import com.demo.concurrent.export.MessageExport;
import com.demo.concurrent.message.Message;
import com.demo.concurrent.message.StringMessage;

public class MessageProducer implements Runnable {
	  private final ConcurrentLinkedQueue<Message> queue;
	  private boolean blnExit = false;
	  private final MessageConsumer consumer;
	  private final MessageExport messageExport;
	  private final String messageSource;

	  public MessageProducer(final ConcurrentLinkedQueue<Message> queue, final MessageConsumer consumer, final MessageExport messageExport, final String messageSource){
	      this.queue = queue;
	      this.consumer = consumer;
	      this.messageExport = messageExport;
	      this.messageSource = messageSource;
	   }
	
	  public void setExitCondition(boolean blnDoExit){
			blnExit = blnDoExit;
	   }
	  
	   public void run() {
		  long i = 0;	   
	      System.out.println("Producer Started");
		  final Random generator = new Random();
	  
	      while (!blnExit) {
				try {
					i++;
					Message msg = new StringMessage();
					msg.setMessageSource(messageSource);
					msg.setMessageId(Long.valueOf(i));
					msg.setMessageTimestamp(new Timestamp(System.currentTimeMillis()));
					msg.setMessageBody("Message Body " + Long.valueOf(i).toString());
					long threadId = Thread.currentThread().getId();

		        	System.out.println("Message Producer: " + msg.toString() + " with thread id " + Long.valueOf(threadId));
					queue.add(msg);
					Thread.sleep(generator.nextInt(1000) + 1000);
					//if(i>3) break;
				} catch (final InterruptedException ex) {
					ex.printStackTrace();
				}			  
		  }
	      
		// WAIT UNTIL THE QUEUE IS consumed and EMPTY
		while (queue.size() > 0) {
			try {
				Thread.sleep(200);
				System.out.println("Producer waiting to end.");
			} catch (final InterruptedException e) {
				break;
			}
		}
	     
		// SEND TO Consumer THE EXIT CONDITION	
		consumer.setExitCondition(true);

		// SEND TO MessageExport THE EXIT CONDITION
		messageExport.setExitCondition(true);
		
	    System.out.println("Producer Stopped");
	  }
}
