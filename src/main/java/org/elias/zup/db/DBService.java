package org.elias.zup.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;
import org.elias.zup.exception.FieldNotFoundException;
import org.elias.zup.exception.InvalidModelException;
import org.elias.zup.exception.InvalidObjectException;
import org.elias.zup.exception.ModelNotFoundException;
import org.elias.zup.exception.ObjectNotFoundException;
import org.elias.zup.util.Properties;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.ValidationOptions;

/**
 * Class that handles the communication with database
 * 
 * @author eliasj
 *
 */
public class DBService {

	private static MongoDatabase database;
	private static Set<String> collectionsNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>()); //thread safe
	
	static {
		@SuppressWarnings("resource")
		MongoClient mongoClient = new MongoClient(Properties.getStringProperty("db.host"), Properties.getIntegerProperty("db.port"));
		database = mongoClient.getDatabase(Properties.getStringProperty("db.name"));
		
		//list and load all collections initially to increase performance
		listAllCollections();
	}
	
	/**********************************************************************************************
	 * 
	 * MANAGEMENT
	 * 
	 *********************************************************************************************/

	/**
	 * List all existing collections
	 * @return
	 */
	public static List<Document> listAllCollections() {
		List<Document> allCollections = new ArrayList<Document>();
		for (Document collection : database.listCollections()) {
			allCollections.add(collection);
			collectionsNames.add(collection.getString("name"));
		}
		return allCollections;
	}
	

	/**
	 * List collection by given name
	 * @param collectionName
	 * @return
	 */
	public static Document listCollection(String collectionName) {
		
		validateModelName(collectionName);
		
		for (Document collection : listAllCollections()) {
			if(collection.get("name").equals(collectionName)) {
				return collection;
			}
		}
		return null;
	}
	
	/**
	 * Try to create a new collection by name and fields with types
	 * @param collectionName
	 * @param fields
	 */
	public static void submitCollectionStructure(String collectionName, Map<String, String> fields) {

		Document validator = new Document();

		for(String key : fields.keySet()) {
			String fieldType = fields.get(key);
			validator.append(key, new Document("$type", fieldType));
		}
		
		ValidationOptions validationOptions = new ValidationOptions();
		validationOptions.validator(validator);
		
		CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions();
		createCollectionOptions.validationOptions(validationOptions);
		
		try {
			database.createCollection(collectionName, createCollectionOptions);
		} catch (MongoException e) {
			throw new InvalidModelException(collectionName, new Document("trace", e.getMessage()));
		}
		collectionsNames.add(collectionName);
	}
	

	/**
	 * Drop a collection if exists
	 * @param collectionName
	 */
	public static void dropCollection(String collectionName) {

		validateModelName(collectionName);
		
		MongoCollection<Document> collection = database.getCollection(collectionName);
		if(collection == null)
			throw new ModelNotFoundException(collectionName);
		collection.drop();
		collectionsNames.remove(collectionName);
	}
	
	
	/**********************************************************************************************
	 * 
	 * DATA
	 * 
	 **********************************************************************************************/
	
	/**
	 * List all documents by collection name, if exists
	 * TODO paginating the results
	 * 
	 * @param collectionName
	 * @return
	 */
	public static List<Document> listAllDocumentsByCollection(String collectionName) {
		
		validateModelName(collectionName);

		MongoCollection<Document> collection = database.getCollection(collectionName);
		MongoCursor<Document> it = collection.find().iterator();
		
		List<Document> allDocuments = new ArrayList<Document>();
		while (it.hasNext()) {
			allDocuments.add(it.next());
		}
		it.close();
		
		return allDocuments;
	}
	
	/**
	 * Gets a specific object by model name and id
	 * 
	 * @param collectionName
	 * @param id
	 * @return
	 */
	public static Document getDocumentByCollectionAndId(String collectionName, String id) {
		
		validateModelName(collectionName);

		BasicDBObject query = new BasicDBObject();
	    query.put("_id", id);

	    MongoCollection<Document> collection = database.getCollection(collectionName);
	    FindIterable<Document> dbObj = collection.find(query);
	    return dbObj.first();
	}
	
	/**
	 * Verifies if the specified collection exists and try to insert respecting the schema
	 * @param collectionName
	 * @param newObject document with fields
	 */
	public static void save(String collectionName, Document newObject) {
		
		validateModelName(collectionName);
			
		//this random strategy is considered mathematically safe
		newObject.append("_id", UUID.randomUUID().toString()); 

		MongoCollection<Document> collection = database.getCollection(collectionName);
		try {
			collection.insertOne(newObject);
		} catch (MongoException e) {
			throw new InvalidObjectException(newObject);
		}
	}

	/**
	 * Update a specific object by specified type and id, if the field names and values respects the schema
	 * 
	 * @param collectionName name of model
	 * @param id identifier of object
	 * @param doc fields that will be updates
	 * @throws ObjectNotFoundException
	 */
	public static void update(String collectionName, String id, Document doc) throws ObjectNotFoundException {
		
		validateModelName(collectionName);

		//validade if document exists
		Document documentByCollectionAndId = getDocumentByCollectionAndId(collectionName, id);
		if(documentByCollectionAndId == null)
			throw new ObjectNotFoundException(collectionName, new Document("_id", id));
		
		//validate if there's no new fields
		for(Entry<String, Object> updateField : doc.entrySet())
			if(!documentByCollectionAndId.containsKey(updateField.getKey()))
				throw new FieldNotFoundException(new Document(updateField.getKey(), updateField.getValue()));
		
		BasicDBObject updateDoc = new BasicDBObject();
		updateDoc.append("$set", doc); 
		
		BasicDBObject queryDoc = new BasicDBObject();
	    queryDoc.put("_id", id);
		
	    UpdateOptions options = new UpdateOptions();
	    options.bypassDocumentValidation(false);
	    options.upsert(false);
	    
		MongoCollection<Document> collection = database.getCollection(collectionName);
		try {
			collection.updateOne(queryDoc, updateDoc, options);
		} catch (MongoException e) {
			throw new InvalidObjectException(doc);
		}
	}

	/**
	 * Try to delete the specified object by type and id
	 * @param collectionName
	 * @param id
	 */
	public static void deleteById(String collectionName, String id) {
		
		validateModelName(collectionName);
		
		BasicDBObject query = new BasicDBObject();
	    query.put("_id", id);
		
		MongoCollection<Document> collection = database.getCollection(collectionName);
		if(collection == null)
			throw new ObjectNotFoundException(collectionName, new Document("_id", id));
		collection.deleteOne(query);
		
	}
	
	private static void validateModelName(String collectionName) {
		if(!collectionsNames.contains(collectionName))
			throw new ModelNotFoundException(collectionName);
	}
}
