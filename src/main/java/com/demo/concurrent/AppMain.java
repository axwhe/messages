package com.demo.concurrent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is a typical concurrent producer/consumer problem. So we follow
 * the producer/consumer design pattern which is a pre-designed solution to 
 * separate the two main components by placing a queue in the middle. 
 */
public class AppMain {

	public static int concurrentConnections = 2;
	public static int numberOfProducerThreads = 3;
	private static volatile boolean isStopped    = false;
	private static int serverPort = 55555;
	
	/**
	 * 
	 * @param numberOfThreads
	 */
	public AppMain(final int numberOfThreads) {

		String fileName = "test.csv";
		MessageProcessor.Processor(numberOfThreads, fileName);
		Server server = new Server();
		new Thread(server).start();
	}

	
	/**
	 * Make it as a service application 
	 * @param args
	 */
	public static void main(final String args[]) {
	    int len = args.length;
    	
	    if(len>0){
            if(args[0].equalsIgnoreCase("start")){
        		new AppMain(numberOfProducerThreads);
            }
            else if(args[0].equalsIgnoreCase("restart")){
	            stop();
	            try {
                    Thread.sleep(1000);
            		new AppMain(numberOfProducerThreads);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
	        }
	        else if(args[0].equalsIgnoreCase("stop")){
	            stop();
	            return;
	        }
	    }else{
    		new AppMain(numberOfProducerThreads);
	    }
	}

    	
    static void stop(){
    		try{
                  //shutdown from localhost only			 
    			  Socket clientSocket = new Socket("localhost", serverPort);
    			  DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
    	
    			  
    			  System.out.println("Shutdown Message Log Service...");
    		
    			  String request = "SHUTDOWN";
    			  
    			  outToServer.writeBytes(request);
    			  outToServer.flush();
    			  
    			  clientSocket.close();

                  Thread.sleep(500);
    			  //2. trigger shutdown
    			  Socket clientSocketShutshut = new Socket("localhost", serverPort);
    			  //outToServer = new DataOutputStream(clientSocket.getOutputStream());
    			  //outToServer.writeBytes(request);
    			  //outToServer.flush();
    			  clientSocketShutshut.close();
    			  
    		}catch(Exception e){
    			  System.out.println("connection to server failed!");
    		}
     }
    
    static synchronized boolean isStop(){
        return isStopped;
    }

    static synchronized void setStop(){
        isStopped = true;
    }
    
	static final class Server implements Runnable{

	    protected ServerSocket serverSocket = null;
	    protected Thread       runningThread= null;
	    protected ExecutorService threadPool = Executors.newFixedThreadPool(concurrentConnections);
	    
	    public Server(){
	    }
	
	    public void run(){
	        synchronized(this){
	            this.runningThread = Thread.currentThread();
	        }
	        openServerSocket();

	        while(!isStopped()){
	            Socket clientSocket = null;
	            try {
	                clientSocket = this.serverSocket.accept();
	            } catch (IOException e) {
	                if(isStopped()) {
	                    System.out.println("Server Stopped.") ;
	                }
                    break;
	            }
	            
	            if(!isStopped())
	            	this.threadPool.execute(new WorkerThread(clientSocket));
	        }
		    stop();
	        this.threadPool.shutdown();
	        System.out.println("Server Stopped.") ;
	    }
	
	
	    private synchronized boolean isStopped() {
	        return isStopped;
	    }
	
	    public synchronized void stop(){
	        isStopped = true;
	        try {
	            this.serverSocket.close();
	        } catch (IOException e) {
	            throw new RuntimeException("Error closing server", e);
	        }
	    }
	
	    private void openServerSocket() {
	        try {
	            this.serverSocket = new ServerSocket(serverPort);
	        } catch (IOException e) {
	            throw new RuntimeException("Cannot open port " + Integer.toString(serverPort), e);
	        }
	    }
	}

	static class WorkerThread implements Runnable{

	    protected Socket clientSocket = null;

	    public WorkerThread(Socket clientSocket) {
	        this.clientSocket = clientSocket;
	    }

	    public void run() {
	    	String request;
	        try {
	            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

	            while(!isStop()){
	            	request = inFromClient.readLine();
	            	if(request.equalsIgnoreCase("SHUTDOWN")){
	            		System.out.println("Shutdown ...");
	      			  	MessageProcessor.Stop();
	            		setStop();
	            		break;
	            	}
	            }
	            
	            inFromClient.close();
	         } catch (IOException e) {
	            System.out.println("Request processed with error!");
		        e.printStackTrace();
	        }
	    }
	}	
}