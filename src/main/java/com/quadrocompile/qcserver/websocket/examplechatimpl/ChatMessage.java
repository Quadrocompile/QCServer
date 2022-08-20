package com.quadrocompile.qcserver.websocket.examplechatimpl;

import org.json.JSONObject;

public class ChatMessage {

    String id;
    int author;
    int room;
    String message;
    long timestamp;
    boolean flagDeleted;

    public ChatMessage(String id, int author, int room, String message, long timestamp, boolean flagDeleted) {
        this.id = id;
        this.author = author;
        this.room = room;
        this.message = message;
        this.timestamp = timestamp;
        this.flagDeleted = flagDeleted;
    }

    public JSONObject serialize(){
        JSONObject serialized = new JSONObject();
        serialized.put("id", id);
        serialized.put("room", room);
        serialized.put("author", author);
        serialized.put("message", message);
        serialized.put("timestamp", timestamp);
        serialized.put("flag_deleted", flagDeleted);
        return serialized;
    }

    public static ChatMessage deserialize(JSONObject serialized){
        String id = serialized.getString("id");
        int author = serialized.getInt("author");
        int room = serialized.getInt("room");
        String message = serialized.getString("message");
        long timestamp = serialized.getLong("timestamp");
        boolean flagDeleted = serialized.has("flag_deleted") ? serialized.getBoolean("flag_deleted") : false;

        return new ChatMessage(id, author, room, message, timestamp, flagDeleted);
    }

}
