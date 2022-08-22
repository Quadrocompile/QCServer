package com.quadrocompile.qcserver.websocket.examplechatimpl;

import java.util.*;

public class ChatRoom {

    public static class OlderMessageCollection{
        private final boolean containsMessages;
        private final boolean hasOlderMessages;
        private final List<ChatMessage> fetchedMessages;
        public OlderMessageCollection(boolean containsMessages, boolean hasOlderMessages, List<ChatMessage> fetchedMessages) {
            this.containsMessages = containsMessages;
            this.hasOlderMessages = hasOlderMessages;
            this.fetchedMessages = fetchedMessages;
        }
        public boolean containsMessages() {
            return containsMessages;
        }
        public boolean hasOlderMessages() {
            return hasOlderMessages;
        }
        public List<ChatMessage> getFetchedMessages() {
            return fetchedMessages;
        }
    }

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
    private void addMessageWithoutSending(ChatMessage message){
        this.messages.add(message);
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

    public OlderMessageCollection fetchOlderMessages(String messageID, int numMessages){
        LinkedList<ChatMessage> fetchedMessages = new LinkedList<>();

        int messageCounter = -1;
        int messageIndex = -1;

        if(messageID!=null){
            for (int i = messages.size(); i > 0; --i) {
                ChatMessage m = messages.get(i-1);
                if(m.id.equals(messageID)){
                    messageCounter = 0; // Start fetching messages
                }
                else if(messageCounter > -1){
                    fetchedMessages.addFirst(m);
                    messageIndex = i-1;
                    ++messageCounter;
                    if(messageCounter == numMessages){
                        break;
                    }
                }
            }
        }

        OlderMessageCollection resp = new OlderMessageCollection(
                !fetchedMessages.isEmpty(),
                messageIndex > 0,
                fetchedMessages
        );
        return resp;
    }


    // Test the fetchMessage function
    /*
    public static void main(String[] args) {
        ChatRoom r = new ChatRoom(1, "TestRoom");
        for (int i = 0; i < 25; i++) {
            ChatMessage m = new ChatMessage(String.valueOf(i), 0,0, "MSG::"+i, i, false);
            r.addMessageWithoutSending(m);
        }

        OlderMessageCollection r1 = r.fetchOlderMessages("17", 5);
        OlderMessageCollection r2 = r.fetchOlderMessages("12", 5);
        OlderMessageCollection r3 = r.fetchOlderMessages("7", 5);
        OlderMessageCollection r4 = r.fetchOlderMessages("2", 5);
        OlderMessageCollection r5 = r.fetchOlderMessages("0", 5);
        OlderMessageCollection r6 = r.fetchOlderMessages("XX", 5);

        System.out.println("");
    }
     */

}
