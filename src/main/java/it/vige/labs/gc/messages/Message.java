package it.vige.labs.gc.messages;

public class Message {

	private Severity severity;
	
	private String summary;
	
	private String detail;

	public Message(Severity severity, String summary, String detail) {
		this.severity = severity;
		this.summary = summary;
		this.detail = detail;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
}
