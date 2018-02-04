package org.elias.zup.exception;

@SuppressWarnings("serial")
public class ModelNotFoundException extends AbstractException {

	public ModelNotFoundException(String collectionName) {
		super("The model '" + collectionName +"' does not exist", 404, null);
	}

}
