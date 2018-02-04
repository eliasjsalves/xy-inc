package org.elias.zup.api;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bson.Document;
import org.elias.zup.db.DBService;

/**
 * Class that exposes resources for the the data control, respecting the entities created by the management api.<br>
 * Any exception throw in this logic is intercepted by the default exception handler
 * and returned as specified in the json error standard (http://jsonapi.org/examples/#error-objects)
 * 
 * @author eliasj
 */
@Path("/data")
public class DataApi {

	/**
	 * List all objects and attributes of specified collection
	 * @param collection name
	 * @return an array of objects
	 */
	@GET
	@Path("/{collection}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response list(@PathParam("collection") String collection) {
		List<Document> list = DBService.listAllDocumentsByCollection(collection);
		if(list.isEmpty())
			return Response.status(Status.NO_CONTENT).build();
		return Response.status(Status.OK).entity(list).build();
	}

	/**
	 * Gets an specified object based on type (collection name) and id
	 * @param collection type
	 * @param id of the entity
	 * @return the entity and status 200 if the entity has been found,
	 * otherwise status 204
	 */
	@GET
	@Path("/{collection}/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response get(@PathParam("collection") String collection, @PathParam("id") String id) {
		Document document = DBService.getDocumentByCollectionAndId(collection, id);
		if(document == null)
			return Response.status(Status.NOT_FOUND).build();
		return Response.status(Status.OK).entity(document).build();
	}

	/**
	 * Saves an object of specified collection if the structure respects the defined schema.
	 * An automatic random id is generated for each insertion
	 * 
	 * @param collectionName the collection name
	 * @param obj a json representing the object
	 * @return http 200 if ok, otherwise http 400 with detailed error
	 */
	@POST
	@Path("/{collection}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response save(@PathParam("collection") String collection, String obj) {
		Document doc = Document.parse(obj);
		DBService.save(collection, doc);
		return Response.status(Status.OK).build();
	}

	/**
	 * Updates an object of specified collection if the structure respects the defined schema.
	 * 
	 * @param collectionName the collection name
	 * @param id the unique id of the object
	 * @param obj a json representing the object
	 * @return http 200 if ok, 404 if obj not exists, otherwise http 400 with detailed error
	 */
	@PUT
	@Path("/{collection}/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response update(@PathParam("collection") String collection, @PathParam("id") String id, String obj) {
		Document doc = Document.parse(obj);
		DBService.update(collection, id, doc);
		return Response.status(Status.OK).build();
	}

	/**
	 * Delete an object of specified collection and specified id.
	 * 
	 * @param collectionName the collection name
	 * @param obj a json representing the object
	 * @return http 200 if ok, 404 if obj not exists, otherwise http 400 with detailed error
	 */
	@DELETE
	@Path("/{collection}/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response delete(@PathParam("collection") String collection, @PathParam("id") String id) {
		DBService.deleteById(collection, id);
		return Response.status(Status.OK).build();
	}

}
