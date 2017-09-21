package com.demo.concurrent.message;

import java.sql.Timestamp;

public class StringMessage implements Message {
	private String messageSource;
	private Long messageId;
	private Timestamp messageTimestamp;
	private String messageBody;
	
	public StringMessage(){
		
	}
	
	public void setMessageSource(String s){
		messageSource = s;
	}
	
	public String getMessageSource(){
		return messageSource;
	}
	
	public void setMessageId(Long id){
		messageId = id;
	}
	
	public Long getMessageId()
	{
		return messageId;
	}

	public void setMessageTimestamp(Timestamp timestamp){
		messageTimestamp = timestamp;
	}
	
	public Timestamp getMessageTimestamp(){
		return messageTimestamp;
	}
	
	
	public void setMessageBody(String s){
		messageBody = s;
	}
	
	public String getMessageBody(){
		return messageBody;
	}
	
	public String toString(){
		return 	getMessageSource() + " " + getMessageId() + " " + getMessageTimestamp() + " " + getMessageBody();
	}

}
