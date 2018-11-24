package com.quadrocompile.qcserver.sessions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QCSession {

    private String sessionID;
    private final String remoteHost;
    private final String userAgent;

    private Map<String, String> sessionValues;

    public QCSession(String sessionID, String remoteHost, String userAgent){
        this.sessionID = sessionID;
        this.sessionValues = new ConcurrentHashMap<>();
        this.remoteHost = remoteHost;
        this.userAgent = userAgent;
    }

    public String getSessionID(){
        return sessionID;
    }
    public void updateSessionID(String sessionID){
        this.sessionID = sessionID;
    }

    public void setValue(String key, String value){
        sessionValues.put(key, value);
    }
    public String getValue(String key){
        return sessionValues.get(key);
    }
    public String getValue(String key, String defaultValue){
        return sessionValues.getOrDefault(key, defaultValue);
    }


    public String getRemoteHost(){
        return remoteHost;
    }
    public String getUserAgent(){
        return userAgent;
    }
    public boolean verifyOrigin(String remoteHost, String userAgent){
        return this.remoteHost.equals(remoteHost) && this.userAgent.equals(userAgent);
    }
}
