package com.quadrocompile.qcserver.websocket.examplechatimpl;

import com.quadrocompile.qcserver.QCServer;
import com.quadrocompile.qcserver.htmltemplates.QCTemplateEngine;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatServer {

    private static final Logger log = Logger.getLogger(QCServer.class);


    private static Map<Integer, ChatUser> userMap = new HashMap<>();
    private static Map<Integer, ChatRoom> roomMap = new HashMap<>();

    public static void main(String[] args) throws Exception{
        // Setup default console logger
        Logger.getRootLogger().getLoggerRepository().resetConfiguration();
        ConsoleAppender console = new ConsoleAppender(); //create appender
        String PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%p|%c{1}:%L] %m%n";
        console.setLayout(new PatternLayout(PATTERN));
        console.setThreshold(Level.INFO);
        console.activateOptions();
        Logger.getRootLogger().addAppender(console);

        QCTemplateEngine.enableTemplateReloading();
        QCServer.initializeInstanceDefault();
        QCServer.initializeWebSockets(ChatServer.class);
        QCServer.getInstance().startServer();

        userMap.put(1, new ChatUser(1, "Melanie", ChatStatus.OFFLINE));
        userMap.put(2, new ChatUser(2, "Phil", ChatStatus.OFFLINE));
        userMap.put(3, new ChatUser(3, "Markus", ChatStatus.OFFLINE));
        userMap.put(4, new ChatUser(4, "Cat Bot", ChatStatus.ONLINE));
        userMap.put(5, new ChatUser(5, "Testy McTest", ChatStatus.IDLE));
        userMap.put(6, new ChatUser(6, "Unentschlossen", ChatStatus.OFFLINE));
        userMap.put(7, new ChatUser(7, "Potato interwebZ", ChatStatus.OFFLINE));

        roomMap.put(1, new ChatRoom(1, "Feuerwehr Musterstadt"));
        roomMap.get(1).userJoined(1);
        roomMap.get(1).userJoined(2);
        roomMap.get(1).userJoined(3);
        roomMap.get(1).userJoined(4);
        roomMap.get(1).userJoined(5);
        roomMap.get(1).userJoined(6);
        roomMap.get(1).userJoined(7);
        roomMap.put(2, new ChatRoom(2, "AGT Lehrgang"));
        roomMap.get(2).userJoined(1);
        roomMap.get(2).userJoined(2);
        roomMap.get(2).userJoined(3);
        roomMap.get(2).userJoined(4);
        roomMap.get(2).userJoined(6);
    }

    private static Map<Session, Integer> registeredSessions = new HashMap<>();
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    static {
        executor.scheduleAtFixedRate(() -> {
            registeredSessions.keySet().forEach(session -> {
                // TODO: Only ping sessions sent by the user are guaranteed to keep the connection alive!
                JSONObject resp = new JSONObject();
                resp.put("action", "PING");
                JSONObject payload = new JSONObject();
                payload.put("message", "ping");
                resp.put("payload", payload);
                session.getRemote().sendStringByFuture(resp.toString());
            });

            //  Some interaction
            int rnd1 = (int)(Math.random()*15)+1;
            if(rnd1 == 4){
                roomMap.get(1).addMessage(new ChatMessage(UUID.randomUUID().toString(), 4,1, "Meow 1!", System.currentTimeMillis()));
            }
            else if(rnd1 == 8){
                roomMap.get(1).addMessage(new ChatMessage(UUID.randomUUID().toString(),4, 2, "Meow 2!", System.currentTimeMillis()));
            }

            int rnd2 = (int)(Math.random()*10)+1;
            if(rnd2 == 6){
                ChatRoom room = roomMap.get(1);
                if(room.containsUser(6)){
                    roomMap.get(1).addMessage(new ChatMessage(UUID.randomUUID().toString(), 6, 1, "Ich geh dann mal :'(", System.currentTimeMillis()));
                    room.userLeft(6);
                }
                else{
                    room.userJoined(6);
                    roomMap.get(1).addMessage(new ChatMessage(UUID.randomUUID().toString(), 6, 1, "Da bin ich wieder :)", System.currentTimeMillis()));
                }
            }

            int rnd3 = (int)(Math.random()*5)+1;
            if(rnd3 == 1){
                ChatRoom room = roomMap.get(1);
                ChatUser user = userMap.get(7);
                if(user.status == ChatStatus.ONLINE){
                    user.setStatus(ChatStatus.OFFLINE);
                }
                else if(user.status == ChatStatus.OFFLINE){
                    user.setStatus(ChatStatus.ONLINE);
                }
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public static void sendMessageToGroup(ChatMessage message, Set<Integer> users){
        users.forEach( userID -> {
            ChatUser user = userMap.get(userID);
            if(user!=null && user.getSession()!=null){
                JSONObject resp = new JSONObject();
                resp.put("action", "MESSAGE");
                JSONObject serializedMessage = message.serialize();
                ChatUser author = userMap.get(message.author);
                serializedMessage.put("displayname", author.getName());
                resp.put("payload", serializedMessage);
                user.getSession().getRemote().sendStringByFuture(resp.toString());
            }
        });
    }

    public static void sendUserStatusToGroup(int userID, ChatStatus status, Set<Integer> rooms){
        Set<Integer> affectedUsers = new HashSet<>();

        rooms.forEach( roomID -> {
            ChatRoom room = roomMap.get(roomID);
            if(room!=null){
                affectedUsers.addAll(room.users);
            }
        });

        affectedUsers.forEach( uid -> {
            ChatUser user = userMap.get(uid);
            if(user!=null && user.getSession()!=null){
                JSONObject resp = new JSONObject();
                resp.put("action", "STATUSUPDATE");

                JSONObject payload = new JSONObject();
                payload.put("userid", userID);
                payload.put("status", status.toString());
                resp.put("payload", payload);
                user.getSession().getRemote().sendStringByFuture(resp.toString());
            }
        });
    }

    public static void sendUserJoinedToGroup(int userID, int roomID, Set<Integer> users){
        users.forEach( uid -> {
            ChatUser user = userMap.get(uid);
            if(user!=null && user.getSession()!=null){
                JSONObject resp = new JSONObject();
                resp.put("action", "USERJOINED");

                JSONObject payload = new JSONObject();
                payload.put("userid", userID);
                ChatUser newUser = userMap.get(userID);
                payload.put("displayname", newUser.getName());
                payload.put("roomid", roomID);
                resp.put("payload", payload);
                user.getSession().getRemote().sendStringByFuture(resp.toString());
            }
        });
    }
    public static void sendUserLeftToGroup(int userID, int roomID, Set<Integer> users){
        users.forEach( uid -> {
            ChatUser user = userMap.get(uid);
            if(user!=null && user.getSession()!=null){
                JSONObject resp = new JSONObject();
                resp.put("action", "USERLEFT");

                JSONObject payload = new JSONObject();
                payload.put("userid", userID);
                payload.put("roomid", roomID);
                resp.put("payload", payload);
                user.getSession().getRemote().sendStringByFuture(resp.toString());
            }
        });
    }



    private Session session;

    private void cleanUp() {
        try {
            int userID = registeredSessions.get(session);
            ChatUser user = userMap.get(userID);
            if(user!=null){
                user.setStatus(ChatStatus.OFFLINE);
                user.setSession(null);
            }
            registeredSessions.remove(session);
            session.close();
            session = null;
        } finally {
        }
    }

    private void setup(Session session) {
        this.session = session;
        registeredSessions.put(this.session, -1);

        JSONObject resp = new JSONObject();
        resp.put("action", "AUTHENTICATE");
        JSONObject payload = new JSONObject();
        payload.put("message", "Bitte anmelden!");
        resp.put("payload", payload);
        send(resp.toString());

        /*
        JSONObject resp = new JSONObject();
        resp.put("action", "WELCOME");
        JSONObject payload = new JSONObject();
        payload.put("message", "Willkommen zum Chat!");
        resp.put("payload", payload);
        send(resp.toString());
         */
    }

    @OnWebSocketClose
    public void onClose(int status, String reason) {
        log.info("WebSocket closed, status = '" + status + "', reason = '" + reason + "'");
        cleanUp();
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        log.error("WebSocket error", t);
        cleanUp();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        log.info("WebSocket message: '" + session.getRemoteAddress().getAddress() + "'");
        setup(session);
    }

    @OnWebSocketMessage
    public void onMessage(String serialized) {
        log.info("WebSocket message: '" + serialized + "'");
        // send("ECHO>>" + message);

        JSONObject msg = new JSONObject(serialized);

        // 905CDC11-65BA-4B9F-9EE6-EF15CCCFD488
        // D6D0FAC1-E14D-480C-9F0E-14EDFD6F36C1

        switch (msg.getString("action")){
            case "AUTHENTICATE":{
                JSONObject reqPayload = msg.getJSONObject("payload");
                String authCode = reqPayload.getString("authcode");
                if("905CDC11-65BA-4B9F-9EE6-EF15CCCFD488".equals(authCode)){
                    ChatUser user = userMap.get(1);
                    user.setSession(session);
                    user.setStatus(ChatStatus.ONLINE);
                    registeredSessions.put(session, user.getId());

                    JSONObject resp = new JSONObject();
                    resp.put("action", "SERVER_INFO");
                    JSONObject payload = new JSONObject();
                    payload.put("myid", user.getId() );
                    payload.put("myname", user.getName() );

                    Set<Integer> roomUsers = new HashSet<>();

                    JSONArray myRoomArray = new JSONArray();
                    user.getRooms().forEach( roomID -> {
                        ChatRoom room = roomMap.get(roomID);
                        JSONObject roomObject = new JSONObject();
                        roomObject.put("id", room.getId());
                        roomObject.put("name", room.getName());
                        JSONArray roomUserArray = new JSONArray();
                        room.users.forEach( userID -> {
                            ChatUser contact = userMap.get(userID);
                            if(contact!=null){
                                roomUserArray.put(userID);
                                roomUsers.add(userID);
                            }
                        });
                        roomObject.put("users", roomUserArray);
                        myRoomArray.put(roomObject);
                    });
                    payload.put("myrooms", myRoomArray);

                    JSONObject myContactsObject = new JSONObject();
                    roomUsers.forEach( userID -> {
                        ChatUser contact = userMap.get(userID);
                        if(contact!=null){
                            // Should be != null, since only existing users are added to the roomUsers set!
                            myContactsObject.put(String.valueOf(userID), contact.getName());
                        }
                    });
                    payload.put("mycontacts", myContactsObject);

                    resp.put("payload", payload);
                    send(resp.toString());
                }
                else if("D6D0FAC1-E14D-480C-9F0E-14EDFD6F36C1".equals(authCode)){
                    ChatUser user = userMap.get(2);
                    user.setSession(session);
                    user.setStatus(ChatStatus.ONLINE);
                    registeredSessions.put(session, user.getId());
                }
                else{
                    JSONObject resp = new JSONObject();
                    resp.put("action", "ERROR");
                    JSONObject payload = new JSONObject();
                    payload.put("message", "Ungültiger authcode!");
                    resp.put("payload", payload);
                    send(resp.toString());
                }
                break;
            }
            case "SEND_MESSAGE":{
                int userID = registeredSessions.get(this.session);
                if(userID==-1){
                    JSONObject resp = new JSONObject();
                    resp.put("action", "ERROR");
                    JSONObject payload = new JSONObject();
                    payload.put("message", "Bitte anmelden!");
                    resp.put("payload", payload);
                    send(resp.toString());
                }
                else{
                    JSONObject reqPayload = msg.getJSONObject("payload");
                    int roomID = reqPayload.getInt("room");
                    String message = reqPayload.getString("message");
                    ChatRoom room = roomMap.get(roomID);
                    if(room!=null && room.containsUser(userID)){
                        ChatMessage chatMessage = new ChatMessage(UUID.randomUUID().toString(), userID, roomID, message, System.currentTimeMillis());
                        room.addMessage(chatMessage);
                    }
                    else{
                        JSONObject resp = new JSONObject();
                        resp.put("action", "ERROR");
                        JSONObject payload = new JSONObject();
                        payload.put("message", "Ungültiger Raum!");
                        resp.put("payload", payload);
                        send(resp.toString());
                    }
                }
                break;
            }
            default: {
                JSONObject resp = new JSONObject();
                resp.put("action", "ERROR");
                JSONObject payload = new JSONObject();
                payload.put("message", "Ungültige action!");
                resp.put("payload", payload);
                send(resp.toString());
            }
        }
    }


    private void send(String message) {
        if (session.isOpen()) {
            try {
                session.getRemote().sendStringByFuture(message);
            } catch (Exception e) {
                log.error("Exception when sending string", e);
            }
        }
    }

}
