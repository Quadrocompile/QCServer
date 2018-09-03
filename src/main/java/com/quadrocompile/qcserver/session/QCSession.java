package com.quadrocompile.qcserver.session;

import java.util.HashSet;
import java.util.Set;

public class QCSession {

    private final String user;
    private final String sessionID;
    private final boolean authenticated;
    private Set<String> roles;
    private long expires;
    private long expiresMax;

    public QCSession(String user, String sessionID, boolean authenticated, Set<String> roles, long ttl){
        this(user, sessionID, authenticated, roles, ttl, -1);
    }

    public QCSession(String user, String sessionID, boolean authenticated, Set<String> roles, long ttl, long ttlMax){
        this.user = user;
        this.sessionID = sessionID;
        this.authenticated = authenticated;
        this.roles = new HashSet<>();
        this.roles.addAll(roles);

        this.expires = System.currentTimeMillis() + ttl;

        if(ttlMax != -1){
            this.expiresMax = System.currentTimeMillis() + ttlMax;
        }
    }

    public String getUser(){
        return user;
    }

    public String getSessionID(){
        return sessionID;
    }

    public boolean isAuthenticated(){
        return authenticated;
    }

    public Set<String> getRoles(){
        return roles;
    }

    public boolean isAlive(){
        return true;
    }
    /*
    public boolean isAlive(){
       return System.currentTimeMillis() < expires;
    }
    */


    public boolean isExpired(){ return false; }
    /*
    public boolean isExpired(){
        return System.currentTimeMillis() > expiresMax;
    }
    */

    public void renew(long ttl){
        expires = System.currentTimeMillis() + ttl;
    }

    public void renewExpiration(long ttl){
        expiresMax = System.currentTimeMillis() + ttl;
    }

}
