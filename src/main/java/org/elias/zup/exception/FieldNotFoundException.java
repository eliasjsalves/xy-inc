package org.elias.zup.exception;

import org.bson.Document;

@SuppressWarnings("serial")
public class FieldNotFoundException extends AbstractException {

	public FieldNotFoundException(Document source) {
		super("The specified field has not found in the model", 400, source);
	}

}
