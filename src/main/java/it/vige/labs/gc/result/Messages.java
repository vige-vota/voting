package it.vige.labs.gc.result;

import java.util.List;

public class Messages {

	private List<Message> messages;
	
	public Messages(List<Message> messages) {
		super();
		this.messages = messages;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
}
