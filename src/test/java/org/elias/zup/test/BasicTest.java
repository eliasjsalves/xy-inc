package org.elias.zup.test;

import org.bson.Document;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class BasicTest {
	
	@Test
	public void testDatabase() {
		//creating a Mongo client 
		MongoClient mongo = new MongoClient("localhost", 27017);

		//accessing the database 
		MongoDatabase database = mongo.getDatabase("xy-inc");

		//create a collection 
		database.createCollection("sampleCollection"); 

		//drop collection
		MongoCollection<Document> collection = database.getCollection("sampleCollection");
		collection.drop();
		
		mongo.close();
	}
}	
