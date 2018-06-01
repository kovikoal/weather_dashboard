package com.site;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.apache.log4j.Logger;

class database {

    private static final Logger DataBase = Logger.getLogger(database.class);

      static String mongoData() {

         try {
             MongoClient mongoClient = new MongoClient("localhost", 27017);
             MongoDatabase database = mongoClient.getDatabase("dashboardDB");
             DataBase.info("Connected to database");

             MongoCollection<Document> collection = database.getCollection("Visits");
             Document number = collection.find(new Document("Dashboard", "True")).first();

             if (number == null) {
                 Document start = new Document("Dashboard", "True").append("Visits", 0);
                 collection.insertOne(start);
                 DataBase.info("Created collection");
                 return "0";
             } else {
                 BasicDBObject newDocument =
                         new BasicDBObject().append("$inc",
                                 new BasicDBObject().append("Visits", 1));

                 collection.updateOne(new BasicDBObject().append("Dashboard", "True"), newDocument);
                 JsonParser parser = new JsonParser();
                 JsonObject main = parser.parse(number.toJson()).getAsJsonObject();
                 String answer = main.get("Visits").getAsString();
                 DataBase.info("New connection");
                 return answer;
             }

         } catch (Exception e) {
             DataBase.error("FAILED TO CONNECT TO THE DB");
             return null;
         }
     }
}


