package com.juatsapp.app;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * DAO para la colección de mensajes.
 */
public class MessageDao {

    private final MongoCollection<Document> collection;

    public MessageDao() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("messages");
    }

    /**
     * Inserta un nuevo mensaje en un chat.
     */
    public Message addMessage(String chatId, String senderId, String text) {
        Message message = new Message(chatId, senderId, text);

        Document doc = new Document();
        doc.append("chatId", chatId);
        doc.append("senderId", senderId);
        doc.append("text", text);
        doc.append("timestamp", message.getTimestamp());

        collection.insertOne(doc);
        ObjectId id = doc.getObjectId("_id");
        message.setId(id.toHexString());
        return message;
    }

    /**
     * Devuelve todos los mensajes de un chat, ordenados por fecha.
     */
    public List<Message> getMessagesForChat(String chatId) {
        List<Message> messages = new ArrayList<>();

        try (MongoCursor<Document> cursor = collection
                .find(eq("chatId", chatId))
                .sort(Sorts.ascending("timestamp"))
                .iterator()) {

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Message message = new Message();
                ObjectId id = doc.getObjectId("_id");
                if (id != null) {
                    message.setId(id.toHexString());
                }
                message.setChatId(doc.getString("chatId"));
                message.setSenderId(doc.getString("senderId"));
                message.setText(doc.getString("text"));
                message.setTimestamp(doc.getDate("timestamp"));
                messages.add(message);
            }
        }

        return messages;
    }
}
