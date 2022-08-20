package com.quadrocompile.qcserver.websocket.examplechatimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatRoom {
    int id;
    String name;
    Set<Integer> users = new HashSet<>();
    List<ChatMessage> messages = new ArrayList<>();

    public ChatRoom(int id, String name) {
        this.id = id;
        this.name = name;
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

    public void userJoined(int userID){
        this.users.add(userID);
        ChatServer.sendUserJoinedToGroup(userID, this.id, users);
    }

    public boolean hasMoreMessages(int maxNum){
        return messages.size() > maxNum;
    }
    public List<ChatMessage> getMessages(int maxNum){
        List<ChatMessage> sublist = messages.subList(Math.max(messages.size()-maxNum, 0), messages.size());
        return sublist;
    }

    public int countUnreadMessages(String messageID){
        if(messageID!=null){
            for (int i = messages.size(); i > 0; --i) {
                ChatMessage m = messages.get(i-1);
                if(m.id.equals(messageID)){
                    return messages.size()-i;
                }
            }
        }
        return messages.size();
    }

    public void userLeft(int userID){
        this.users.remove(userID);
        ChatServer.sendUserLeftToGroup(userID, this.id, users);
    }

    public boolean containsUser(int userID){
        return users.contains(userID);
    }

    public void addMessage(ChatMessage message){
        addMessage(message, true);
    }
    public void addMessage(ChatMessage message, boolean serialize){
        this.messages.add(message);
        ChatServer.sendMessageToGroup(message, users);
        if(serialize){
            ChatServer.serialize();
        }
    }

    public void deleteMessage(int userID, String messageID){
        for(ChatMessage message : this.messages){
            if(message.id.equals(messageID)){
                message.flagDeleted = true;
                message.message = "";
            }
        }
        //this.messages.removeIf( m -> m.author==userID && m.id.equals(messageID) );
        ChatServer.sendMessageDeleteToGroup(messageID, this.id, this.users);
        ChatServer.serialize();
    }

}
