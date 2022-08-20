package com.quadrocompile.qcserver.websocket.examplechatimpl;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChatUser {
    int id;
    String name;
    ChatStatus status;
    Session session = null;
    Set<Integer> rooms = new HashSet<>();

    Set<Integer> flaggedUsers = new HashSet<>();
    Set<String> flaggedMessages = new HashSet<>();
    Set<Integer> blockedUsers = new HashSet<>();
    Set<String> blockedMessages = new HashSet<>();

    Map<Integer, String> readMessageMap = new HashMap<>();

    public ChatUser(int id, String name, ChatStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatStatus getStatus() {
        return status;
    }

    public void setStatus(ChatStatus status) {
        this.status = status;
        ChatServer.sendUserStatusToGroup(this.id, status, rooms);
    }

    public Session getSession() {
        return session;
    }

    public Session setSession(Session session) {
        Session oldSession = this.session;
        this.session = session;
        return oldSession;
    }

    public void joinRoom(int roomID){
        this.rooms.add(roomID);
    }
    public void leaveRoom(int roomID){
        this.rooms.remove(roomID);
    }

    public Set<Integer> getRooms() {
        return rooms;
    }

    public String getLastReadMessageID(int roomID){
        return readMessageMap.get(roomID);
    }

    public void setMessageRead(int roomID, String messageID){
        this.readMessageMap.put(roomID, messageID);
    }

    public void flagMessage(String messageID){
        this.flaggedMessages.add(messageID);
    }
    public void blockMessage(String messageID){
        this.blockedMessages.add(messageID);
    }
    public void flagUser(int userID){
        this.flaggedUsers.add(userID);
    }
    public void blockUser(int userID){
        this.blockedUsers.add(userID);
    }

    public boolean isMessageFlagged(String messageID){
        return this.flaggedMessages.contains(messageID);
    }
    public boolean isMessageBlocked(String messageID){
        return this.blockedMessages.contains(messageID);
    }
    public boolean isUserFlagged(int userID){
        return this.flaggedUsers.contains(userID);
    }
    public boolean isUserBocked(int userID){
        return this.blockedUsers.contains(userID);
    }

    public JSONObject serializeFlags(){
        JSONObject serialized = new JSONObject();

        JSONArray flaggedMessages = new JSONArray();
        this.flaggedMessages.forEach( id -> flaggedMessages.put(id));
        serialized.put("FLAGGED_MESSAGES", flaggedMessages);

        JSONArray blockedMessages = new JSONArray();
        this.blockedMessages.forEach( id -> blockedMessages.put(id));
        serialized.put("BLOCKED_MESSAGES", blockedMessages);

        JSONArray flaggedUsers = new JSONArray();
        this.flaggedUsers.forEach( id -> flaggedUsers.put(id));
        serialized.put("FLAGGED_USERS", flaggedUsers);

        JSONArray blockedUsers = new JSONArray();
        this.blockedUsers.forEach( id -> blockedUsers.put(id));
        serialized.put("BLOCKED_USERS", blockedUsers);

        JSONObject readMessages = new JSONObject(readMessageMap);
        serialized.put("READ_MESSAGES", readMessages);

        return serialized;
    }

    public void applyFlags(JSONObject serialized){
        JSONArray flaggedMessages = serialized.getJSONArray("FLAGGED_MESSAGES");
        for (int i = 0; i < flaggedMessages.length(); i++) {
            this.flaggedMessages.add(flaggedMessages.getString(i));
        }
        JSONArray blockedMessages = serialized.getJSONArray("BLOCKED_MESSAGES");
        for (int i = 0; i < blockedMessages.length(); i++) {
            this.blockedMessages.add(blockedMessages.getString(i));
        }

        JSONArray flaggedUsers = serialized.getJSONArray("FLAGGED_USERS");
        for (int i = 0; i < flaggedUsers.length(); i++) {
            this.flaggedUsers.add(flaggedUsers.getInt(i));
        }
        JSONArray blockedUsers = serialized.getJSONArray("BLOCKED_USERS");
        for (int i = 0; i < blockedUsers.length(); i++) {
            this.blockedUsers.add(blockedUsers.getInt(i));
        }

        JSONObject readMessages = serialized.has("READ_MESSAGES") ? serialized.getJSONObject("READ_MESSAGES") : new JSONObject();
        for(String key : readMessages.keySet()){
            this.readMessageMap.put(Integer.parseInt(key), readMessages.getString(key));
        }
    }
}
