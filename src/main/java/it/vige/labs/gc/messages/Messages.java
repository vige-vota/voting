package it.vige.labs.gc.messages;

import java.util.List;

public class Messages {

	private List<Message> messages;
	
	private boolean ok;
	
	public Messages() {
		
	}
	
	public Messages(boolean ok, List<Message> messages) {
		this.ok = ok;
		this.messages = messages;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}
}
