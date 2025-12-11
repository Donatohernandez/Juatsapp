package com.juatsapp.app;

import java.util.Date;

/**
 * Representa un mensaje enviado dentro de un chat.
 */
public class Message {

    private String id;       // _id de MongoDB como String
    private String chatId;   // ID del chat al que pertenece
    private String senderId; // ID del usuario que envía el mensaje
    private String text;     // Contenido del mensaje
    private Date timestamp;  // Fecha y hora de envío

    public Message() {
    }

    public Message(String chatId, String senderId, String text) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
