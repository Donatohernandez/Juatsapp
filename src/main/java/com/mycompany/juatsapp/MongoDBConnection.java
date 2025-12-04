/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.juatsapp;
import com.mongodb.client.*;
import org.bson.Document;

/**
 *
 * @author luisf.salido
 */

public class MongoDBConnection {

    private static MongoClient client;
    private static MongoDatabase database;

    public static MongoDatabase getDatabase() {
        if (database == null) {
            client = MongoClients.create("mongodb://localhost:27017");
            database = client.getDatabase("juatsappDB");
        }
        return database;
    }

    public static MongoCollection<Document> getCollection(String name) {
        return getDatabase().getCollection(name);
    }
}