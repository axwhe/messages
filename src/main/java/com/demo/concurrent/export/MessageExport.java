package com.demo.concurrent.export;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.demo.concurrent.message.Message;

public class MessageExport extends MessageLog implements Runnable{

   private final int timeInterval = 1000*60*15;
   private boolean blnExit;
   private boolean bLastWrite = false;
   private final ConcurrentLinkedQueue<Message> exportQueue;
   private final String fileName;
   private final Object lockObj = new Object();
 
   public MessageExport(final ConcurrentLinkedQueue<Message> exportQueue, String fileName){
      this.exportQueue = exportQueue;
      this.fileName = fileName;
   }
   
   public void setExitCondition(boolean blnDoExit){
		bLastWrite = blnDoExit;
		synchronized (lockObj){
			lockObj.notify();
		}
		//no need calling Thread.currentThread().interrupt();
   }
   
   public void run() {
	    System.out.println("Message Export Started");	   
		while (!blnExit) {
			try {
				
				if (exportQueue.size() > 0) {
					writeLog(exportQueue, fileName, ";");
					if(bLastWrite){
						blnExit = true;
					}
				}
				else{
					synchronized (lockObj){
						lockObj.wait(timeInterval);
					}
				}
				
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			}catch(final Exception e){
				e.printStackTrace();
			}
		}
		System.out.println("Message Export Stopped");				
   }
}

