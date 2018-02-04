package org.elias.zup.exception;

import org.bson.Document;

@SuppressWarnings("serial")
public class InvalidObjectException extends AbstractException{

	public InvalidObjectException(Document source) {
		super("The object content do not respect the schema", 400, source);
	}
}
