package org.elias.zup.exception;

import org.bson.Document;

@SuppressWarnings("serial")
public abstract class AbstractException extends RuntimeException {

	private int httpCode;
	private Document source;
	
	public AbstractException(String message, int httpCode, Document source) {
		super(message);
		this.httpCode = httpCode;
		this.source = source;
	}

	public int getHttpCode() {
		return this.httpCode;
	}

	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

	public Document getSource() {
		return this.source;
	}

	public void setSource(Document source) {
		this.source = source;
	}
}
