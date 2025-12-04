/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.juatsapp;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

/**
 *
 * @author luisf.salido
 */
public class UserDAO {

    private final MongoCollection<Document> collection;

    public UserDAO() {
        collection = MongoDBConnection.getCollection("usuarios");
    }

    public boolean registrarUsuario(String telefono, String passEnc, String nacimiento,
                                    String direccion, String sexo) {
        Document d = new Document("telefono", telefono)
                .append("password", passEnc)
                .append("nacimiento", nacimiento)
                .append("direccion", direccion)
                .append("sexo", sexo);

        collection.insertOne(d);
        return true;
    }

    public Document iniciarSesion(String telefono, String password) {
        return collection.find(
                Filters.and(
                        Filters.eq("telefono", telefono),
                        Filters.eq("password", password)
                )
        ).first();
    }
}