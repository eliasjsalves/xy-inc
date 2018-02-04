package org.elias.zup.exception;

import org.bson.Document;

@SuppressWarnings("serial")
public class ObjectNotFoundException extends AbstractException {

	public ObjectNotFoundException(String collectionName, Document source) {
		super("The object of type '" + collectionName +"' has not found in database", 404, source);
	}

}
