package com.juatsapp.app;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;

import static com.mongodb.client.model.Filters.eq;

/**
 * DAO para la colección de usuarios.
 */
public class UserDao {

    private final MongoCollection<Document> collection;

    public UserDao() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("users");
    }

    public void registerUser(User user) {
        Document doc = new Document();
        doc.append("phone", user.getPhone());
        doc.append("passwordHash", user.getPasswordHash());
        doc.append("birthDate", user.getBirthDate());
        doc.append("address", user.getAddress());
        doc.append("sex", user.getSex());
        doc.append("createdAt", user.getCreatedAt() != null ? user.getCreatedAt() : new Date());
        doc.append("updatedAt", user.getUpdatedAt() != null ? user.getUpdatedAt() : new Date());

        collection.insertOne(doc);
        ObjectId id = doc.getObjectId("_id");
        user.setId(id.toHexString());
    }

    public User findByPhone(String phone) {
        Document doc = collection.find(eq("phone", phone)).first();
        if (doc == null) {
            return null;
        }

        User user = new User();
        ObjectId id = doc.getObjectId("_id");
        if (id != null) {
            user.setId(id.toHexString());
        }
        user.setPhone(doc.getString("phone"));
        user.setPasswordHash(doc.getString("passwordHash"));
        user.setBirthDate(doc.getDate("birthDate"));
        user.setAddress(doc.getString("address"));
        user.setSex(doc.getString("sex"));
        user.setCreatedAt(doc.getDate("createdAt"));
        user.setUpdatedAt(doc.getDate("updatedAt"));

        return user;
    }

    /**
     * Busca un usuario por su identificador de MongoDB.
     */
    public User findById(String id) {
        Document doc = collection.find(eq("_id", new ObjectId(id))).first();
        if (doc == null) {
            return null;
        }

        User user = new User();
        ObjectId objectId = doc.getObjectId("_id");
        if (objectId != null) {
            user.setId(objectId.toHexString());
        }
        user.setPhone(doc.getString("phone"));
        user.setPasswordHash(doc.getString("passwordHash"));
        user.setBirthDate(doc.getDate("birthDate"));
        user.setAddress(doc.getString("address"));
        user.setSex(doc.getString("sex"));
        user.setCreatedAt(doc.getDate("createdAt"));
        user.setUpdatedAt(doc.getDate("updatedAt"));

        return user;
    }

    /**
     * Actualiza los datos básicos de un usuario existente en la base de datos.
     */
    public void updateUser(User user) {
        if (user.getId() == null) {
            return;
        }

        ObjectId objectId = new ObjectId(user.getId());

        Document update = new Document();
        update.append("birthDate", user.getBirthDate());
        update.append("address", user.getAddress());
        update.append("sex", user.getSex());
        update.append("updatedAt", user.getUpdatedAt() != null ? user.getUpdatedAt() : new Date());

        collection.updateOne(eq("_id", objectId), new Document("$set", update));
    }
}
