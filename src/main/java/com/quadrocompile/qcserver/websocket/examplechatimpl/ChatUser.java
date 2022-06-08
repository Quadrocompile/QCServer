package com.quadrocompile.qcserver.websocket.examplechatimpl;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashSet;
import java.util.Set;

public class ChatUser {
    int id;
    String name;
    ChatStatus status;
    Session session = null;
    Set<Integer> rooms = new HashSet<>();

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
}
