package com.juatsapp.app;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Clase de utilidad para gestionar la conexión a MongoDB.
 * Implementa un cliente singleton sencillo que es reutilizado
 * por los distintos DAOs de la aplicación.
 */
public class MongoConnection {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "juatsapp";

    private static MongoClient mongoClient;

    private MongoConnection() {
    }

    /**
     * Obtiene una instancia única de {@link MongoClient} para la aplicación.
     */
    public static MongoClient getClient() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
        }
        return mongoClient;
    }

    /**
     * Devuelve la base de datos principal utilizada por Juatsapp.
     */
    public static MongoDatabase getDatabase() {
        return getClient().getDatabase(DATABASE_NAME);
    }
}
