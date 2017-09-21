package com.demo.concurrent.message;

import java.sql.Timestamp;

public interface Message {

	Long getMessageId();
	void setMessageId(Long id);

	Timestamp getMessageTimestamp();
	void setMessageTimestamp(Timestamp timestamp);
	
	void setMessageBody(String s);
	String getMessageBody();
	
	void setMessageSource(String s);
	String getMessageSource();	
	
	String toString();
}
