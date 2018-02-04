package org.elias.zup.test;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DbTest {
	public static void main(String[] args) {
		// Creating a Mongo client 
		MongoClient mongo = new MongoClient("localhost", 27017);

		// Creating Credentials 
		MongoCredential credential = MongoCredential.createCredential("sampleUser", "myDb", "password".toCharArray());
		System.out.println("Connected to the database successfully");

		//Accessing the database 
		MongoDatabase database = mongo.getDatabase("xy-inc");

		//Creating a collection 
		//	      database.createCollection("sampleCollection"); 
		System.out.println("Collection created successfully");

		MongoCollection<Document> collection = database.getCollection("students");
		System.out.println("Collection sampleCollection selected successfully");

		Document document = new Document("name", "Jaum Cabrones").append("year", 20).append("gender", "m").append("cpf", 11122233344l);
		collection.insertOne(document);
		System.out.println("Document inserted successfully");
		
		mongo.close();
	}
}
