package org.elias.zup.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bson.Document;
import org.elias.zup.db.DBService;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Class that exposes resources for the the entity control of the application.<br>
 * The results follows the patterns of jsonapi.org
 * Any exception throw in this logic is intercepted by the default exception handler
 * and returned as specified in the json error standard (http://jsonapi.org/examples/#error-objects)
 * 
 * @see http://jsonapi.org/format/#crud
 * 
 * @author eliasj
 *
 */
@Path("/management")
public class ManagementApi {

	/**
	 * List all collections in database
	 * 
	 * @return all collections and attibutes
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response listAllCollections() {
		
		List<Document> allCollections = DBService.listAllCollections();
		if(allCollections.isEmpty())
			return Response.status(Status.NO_CONTENT).build();
		return Response.status(Status.OK).entity(allCollections).build();
	}

	/**
	 * List collection based on name
	 * 
	 * @param collectionName
	 * @return all attributes of specified collection or 404 if not found
	 */
	@GET
	@Path("/{collectionName}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response showCollection(@PathParam("collectionName") String collectionName) {
		
		Document collection = DBService.listCollection(collectionName);
		if (collection == null)
			return Response.status(Status.NOT_FOUND).build();
		return Response.status(Status.OK).entity(collection).build();
	}
	
	/**
	 * Create a collection based on name and fields. Each field is represented by
	 * name as key and data type as value 
	 * 
	 * @param collectionName the name of collection
	 * @param structure a key-value json structure representing the fields
	 * @return http 201 for success
	 */
	@POST
	@Path("/{collectionName}")
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response createCollection(@PathParam("collectionName") String collectionName, String structure) {

		JsonElement root = new JsonParser().parse(structure);
		JsonObject jsonObj = root.getAsJsonObject();

		Map<String, String> structureMap = new HashMap<String, String>();
		for (Entry<String, JsonElement> entry : jsonObj.entrySet()) {
			String fieldName = entry.getKey();
			String fieldType = entry.getValue().getAsString();

			validateField(fieldName, fieldType);

			structureMap.put(fieldName, fieldType);
		}
		DBService.submitCollectionStructure(collectionName, structureMap);
		return Response.status(Status.CREATED).header("Location", "/management/"+collectionName).build();
	}

	/**
	 * Drops a collection based on name (if exists)
	 * 
	 * @param collectionName the name of collection
	 * @return
	 */
	@DELETE
	@Path("/{collectionName}")
	public Response dropCollection(@PathParam("collectionName") String collectionName) {
		
		DBService.dropCollection(collectionName);
		return Response.status(Status.OK).build();
	}

	/**
	 * Validate the fields type based on MongoDB supported types
	 * 
	 * @see https://docs.mongodb.com/manual/reference/operator/query/jsonSchema/#op._S_jsonSchema
	 *
	 * @param fieldName the name of the field
	 * @param fieldType the type
	 * @throws RuntimeException if some name or type is not valid
	 */
	private static void validateField(String fieldName, String fieldType) {
		if(fieldName == null || !fieldName.matches("[a-zA-Z][a-zA-Z0-9]*"))
			throw new RuntimeException("Invalid field name: " + fieldName + ". Should start with letter and contain only letters and digits");
		if(fieldType == null || !fieldType.matches("string|double|array|bool|date|int|timestamp|long|decimal"))
			throw new RuntimeException("Invalid field type: " + fieldType + ". Possible values: string|double|array|bool|date|int|timestamp|long|decimal");
	}
}
