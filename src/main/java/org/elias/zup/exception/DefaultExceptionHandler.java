package org.elias.zup.exception;

import java.util.Arrays;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.bson.Document;

/**
 * Class that handles all exceptions thrown by application, converting the
 * details to the standard error format specified by the <b>jsonapi.org</b>
 * 
 * Ex:
 * 
 *  HTTP/1.1 422 Unprocessable Entity
 *	Content-Type: application/vnd.api+json
 *	
 *	{
 *	  "errors": [
 *	    {
 *	      "status": "422",
 *	      "source": { "pointer": "/data/attributes/first-name" },
 *	      "title":  "Invalid Attribute",
 *	      "detail": "First name must contain at least three characters."
 *	    }
 *	  ]
 *	}
 * 
 * @see http://jsonapi.org/examples/#error-objects
 * 
 * @author eliasj
 *
 */
@Provider
public class DefaultExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

    	if(exception instanceof AbstractException) {
    		AbstractException abstractException = (AbstractException) exception;
    		
    		Document errorDoc = new Document();
    		errorDoc.append("status", String.valueOf(abstractException.getHttpCode()));
    		errorDoc.append("source", abstractException.getSource()); //document
    		errorDoc.append("detail", abstractException.getMessage());
    		
    		Document responseDoc = new Document();
    		responseDoc.append("errors", Arrays.asList(errorDoc));
    		
    		return Response.status(abstractException.getHttpCode()).entity(responseDoc).type(MediaType.APPLICATION_JSON).build();
    	}
    	
    	Document errorDoc = new Document();
		errorDoc.append("status", "500");
		errorDoc.append("source", exception.getLocalizedMessage());
		errorDoc.append("detail", exception.getMessage());
		
		Document responseDoc = new Document();
		responseDoc.append("errors", Arrays.asList(errorDoc));
		
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseDoc).type(MediaType.APPLICATION_JSON).build();
    }
}