package com.demo.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.demo.concurrent.consumer.MessageConsumer;
import com.demo.concurrent.export.MessageExport;
import com.demo.concurrent.message.Message;
import com.demo.concurrent.producer.MessageProducer;

public class MessageProcessor {

	private static int maxNumberOfProducerThreads=10; //resource constrains
	/**
	 * the messages must be written in order of arrival from the components.
	 * the component must not delay the originating component and should handle incoming message as quickly as possible
	 * So ConcurrentLinkedQueue fits the requirement very well with better performance than using BlockingQueue
	 */
	private static volatile ConcurrentLinkedQueue<Message> sharedQueue = new ConcurrentLinkedQueue<Message>();
    private static volatile ConcurrentLinkedQueue<Message> exportQueue = new ConcurrentLinkedQueue<Message>();

	private static ExecutorService producerThreadPool = Executors.newFixedThreadPool(maxNumberOfProducerThreads + 2);
	
	private static final List<MessageProducer> producers = new ArrayList<MessageProducer>();
	
	public static void Stop(){
		for(int i=0;i<producers.size(); i++){
			MessageProducer messageProducer = producers.get(i);
			messageProducer.setExitCondition(true);
		}
	}
	
	public static void Processor(final int numberOfThreads, final String fileName){

		if (numberOfThreads <= 0 || numberOfThreads > maxNumberOfProducerThreads) 
			throw new IllegalArgumentException("The number of threads should be a number between 1 and 100");
		

		//one consumer
		MessageConsumer logConsumer = new MessageConsumer(sharedQueue, exportQueue);
		producerThreadPool.execute(logConsumer);
		
		//message log export
		MessageExport messageExport = new MessageExport(exportQueue, fileName);
		producerThreadPool.execute(messageExport);

		String [] msgSources = new String[numberOfThreads];
		for(int i=0;i<numberOfThreads;i++){
			msgSources[i] = "MessageSource" + String.format("%d", i+1);
		}

		/**
		 * there are several components that will generate certain log messages
		 * so we need multiple producers  
		 */
		for(int i=0;i<numberOfThreads;i++){
			final MessageProducer messageProducer = new MessageProducer(sharedQueue, logConsumer, messageExport, msgSources[i]);
			producerThreadPool.execute(messageProducer);
			producers.add(messageProducer);
		}
	
		producerThreadPool.shutdown();
	}
}
