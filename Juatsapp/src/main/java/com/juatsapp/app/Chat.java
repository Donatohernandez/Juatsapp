package com.juatsapp.app;

import java.util.Date;
import java.util.List;

/**
 * Representa un chat entre dos o más usuarios.
 */
public class Chat {

    private String id;                 // _id de MongoDB como String
    private List<String> participantIds; // IDs de usuarios participantes
    private String createdByUserId;    // ID del usuario que creó el chat
    private Date createdAt;

    public Chat() {
    }

    public Chat(List<String> participantIds, String createdByUserId) {
        this.participantIds = participantIds;
        this.createdByUserId = createdByUserId;
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Chat (" + (participantIds != null ? participantIds.size() : 0) + " participantes)";
    }
}
