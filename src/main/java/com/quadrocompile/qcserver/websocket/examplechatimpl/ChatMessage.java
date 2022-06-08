package com.quadrocompile.qcserver.websocket.examplechatimpl;

import org.json.JSONObject;

public class ChatMessage {

    String id;
    int author;
    int room;
    String message;
    long timestamp;

    public ChatMessage(String id, int author, int room, String message, long timestamp) {
        this.id = id;
        this.author = author;
        this.room = room;
        this.message = message;
        this.timestamp = timestamp;
    }

    public JSONObject serialize(){
        JSONObject serialized = new JSONObject();
        serialized.put("id", id);
        serialized.put("room", room);
        serialized.put("author", author);
        serialized.put("message", message);
        serialized.put("timestamp", timestamp);
        return serialized;
    }

}
