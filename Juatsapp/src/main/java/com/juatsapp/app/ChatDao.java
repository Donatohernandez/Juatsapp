package com.juatsapp.app;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.all;
import static com.mongodb.client.model.Filters.eq;

/**
 * DAO para la colección de chats.
 */
public class ChatDao {

    private final MongoCollection<Document> collection;

    public ChatDao() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("chats");
    }

    /**
     * Crea un nuevo chat con la lista de participantes indicada.
     */
    public Chat createChat(List<String> participantIds, String createdByUserId) {
        Chat chat = new Chat(participantIds, createdByUserId);

        Document doc = new Document();
        doc.append("participantIds", participantIds);
        doc.append("createdByUserId", createdByUserId);
        doc.append("createdAt", chat.getCreatedAt());

        collection.insertOne(doc);
        ObjectId id = doc.getObjectId("_id");
        chat.setId(id.toHexString());
        return chat;
    }

    /**
     * Obtiene los chats en los que participa un usuario.
     */
    public List<Chat> findChatsForUser(String userId) {
        List<Chat> chats = new ArrayList<>();

        try (MongoCursor<Document> cursor = collection.find(all("participantIds", userId)).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Chat chat = new Chat();
                ObjectId id = doc.getObjectId("_id");
                if (id != null) {
                    chat.setId(id.toHexString());
                }
                chat.setParticipantIds((List<String>) doc.get("participantIds"));
                chat.setCreatedByUserId(doc.getString("createdByUserId"));
                chat.setCreatedAt(doc.getDate("createdAt"));
                chats.add(chat);
            }
        }

        return chats;
    }

    /**
     * Busca un chat por su identificador.
     */
    public Chat findById(String chatId) {
        Document doc = collection.find(eq("_id", new ObjectId(chatId))).first();
        if (doc == null) {
            return null;
        }
        Chat chat = new Chat();
        chat.setId(chatId);
        chat.setParticipantIds((List<String>) doc.get("participantIds"));
        chat.setCreatedByUserId(doc.getString("createdByUserId"));
        chat.setCreatedAt(doc.getDate("createdAt"));
        return chat;
    }
}
