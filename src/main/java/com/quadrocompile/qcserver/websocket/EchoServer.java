package com.quadrocompile.qcserver.websocket;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebSocket
public class EchoServer {

    private static final Logger log = Logger.getLogger(EchoServer.class);

    private static Set<Session> registeredSessions = new HashSet<>();
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    static {
        executor.scheduleAtFixedRate(() -> {
            registeredSessions.forEach(session -> {
                JSONObject resp = new JSONObject();
                resp.put("source", "server");
                resp.put("content", "You are still connected to EchoServer!");
                session.getRemote().sendStringByFuture(resp.toString());
            });
        }, 5, 5, TimeUnit.SECONDS);
    }

    private Session session;

    private void cleanUp() {
        try {
            registeredSessions.remove(session);
            session.close();
            session = null;
        } finally {
        }
    }

    private void setup(Session session) {
        this.session = session;
        registeredSessions.add(this.session);
        //send("Welcome to echo server!");

        JSONObject resp = new JSONObject();
        resp.put("source", "server");
        resp.put("content", "Welcome to EchoServer!");
        send(resp.toString());
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
    public void onMessage(String message) {
        log.info("WebSocket message: '" + message + "'");
        // send("ECHO>>" + message);

        JSONObject resp = new JSONObject();
        resp.put("source", "server");
        resp.put("content", message);
        resp.put("userid", 123);
        send(resp.toString());
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
