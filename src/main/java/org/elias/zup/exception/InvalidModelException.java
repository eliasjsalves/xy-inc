package org.elias.zup.exception;

import org.bson.Document;

@SuppressWarnings("serial")
public class InvalidModelException extends AbstractException{

	public InvalidModelException(String collectionName, Document source) {
		super("The model '"+collectionName+"' cannot be created", 403, source);
	}

	public InvalidModelException(String collectionName) {
		super("The model '"+collectionName+"' does not exist", 403, null);
	}
}
