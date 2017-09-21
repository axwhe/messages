package com.demo.concurrent.consumer;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.demo.concurrent.message.Message;

public class MessageConsumer  implements Runnable{
   private boolean blnExit;
   private final ConcurrentLinkedQueue<Message> queue;
   private final ConcurrentLinkedQueue<Message> exportQueue;
   
   public MessageConsumer(final ConcurrentLinkedQueue<Message> queue, final ConcurrentLinkedQueue<Message> exportQueue){
      this.queue = queue;
      this.exportQueue = exportQueue; 
   }
   
   public void setExitCondition(boolean blnDoExit){
		blnExit = blnDoExit;
   }
   
   public void run() {
	   
		final Random generator = new Random();
        System.out.println("Consumer Started");
        
		while (!blnExit) {
			
			try {
				if (queue.size() > 0) {
					 Message msg;
			         while (( msg = queue.poll()) != null) {
			        	 System.out.println("Message Consumer: " + msg.toString());
			        	 exportQueue.add(msg);
			         }
					// TO BE REMOVED (ONLY SIMULATES RANDOM WORKING TIME)
					Thread.sleep(generator.nextInt(1000) + 800);
				}
				else
		            Thread.sleep(500);
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	   
		System.out.println("Consumer Stopped");		
   }
}
