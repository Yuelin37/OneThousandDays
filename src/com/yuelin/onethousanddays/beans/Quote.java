package com.yuelin.onethousanddays.beans;

public class Quote {
	private int quote_id;
	private String quote;
	private String author;
	private String source;
	private String Tags;
	public int getQuote_id() {
		return quote_id;
	}
	public void setQuote_id(int quote_id) {
		this.quote_id = quote_id;
	}
	public String getQuote() {
		return quote;
	}
	public void setQuote(String quote) {
		this.quote = quote;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTags() {
		return Tags;
	}
	public void setTags(String tags) {
		Tags = tags;
	}
	
	
}
