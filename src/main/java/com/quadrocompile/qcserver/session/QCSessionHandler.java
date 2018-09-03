package com.quadrocompile.qcserver.session;

import com.quadrocompile.qcserver.QCServer;
import com.quadrocompile.qcserver.security.QCAuthenticationService;
import com.quadrocompile.qcserver.security.RandomString;
import com.quadrocompile.qcserver.security.credentials.QCCredentials;
import com.quadrocompile.qcserver.util.QCUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class QCSessionHandler {
    protected String SESSION_COOKIE_IDENTIFIER = "j_qcSessionID";

    protected long gcThreshold = 60 * 10 * 1000;
    protected long gcPerformed = -1L;

    protected Map<String, QCSession> sessionMap = new ConcurrentHashMap<>();

    protected RandomString sessionIDGenerator;
    protected QCAuthenticationService authenticationService;
    protected long defaultTTL = 60 * 2 * 1000;
    protected long defaultMaxTTL = defaultTTL;

    public QCSessionHandler(QCAuthenticationService service){
        this.sessionIDGenerator = new RandomString();
        this.authenticationService = service;
    }

    public QCSession createSession(HttpServletRequest request, HttpServletResponse response, QCCredentials credentials){
        return createSession(request, response, credentials, defaultTTL, sessionIDGenerator.nextString());
    }

    public QCSession createSession(HttpServletRequest request, HttpServletResponse response, QCCredentials credentials, String sessionID){
        return createSession(request, response, credentials, defaultTTL, sessionID);
    }

    public QCSession createSession(HttpServletRequest request, HttpServletResponse response, QCCredentials credentials, long ttl) {
        String sessionID = null;
        System.out.println(">>QC>> createSession#3");

        QCSession session = null;
        if(authenticationService != null && authenticationService.authenticate(credentials)){
            sessionID = sessionIDGenerator.nextString();
            Set<String> roles = authenticationService.getRolesForUser(credentials);
            session = new QCSession(credentials.getUser(), sessionID, true, roles, ttl);
            Cookie sessionCookie = new Cookie(SESSION_COOKIE_IDENTIFIER, sessionID);
            sessionCookie.setPath("/");
            response.addCookie(sessionCookie);
            sessionMap.put(sessionID, session);
            System.out.println(">>QC>> Created session [" + session + "] with session id [" + sessionID + "]");
        }
        else{
            System.out.println(">>QC>> Authentication failed");
        }

        System.out.println(">>QC>> Returning session [" + session + "] with session id [" + sessionID + "]");
        return session;
    }
    /*
    public QCSession createSession(HttpServletRequest request, HttpServletResponse response, QCCredentials credentials, long ttl){
        QCSession session = null;

        System.out.println("Attempting to create session");
        if(authenticationService != null && authenticationService.authenticate(credentials)){
            String sessionID = sessionIDGenerator.nextString();
            Set<String> roles = authenticationService.getRolesForUser(credentials);
            session = new QCSession(credentials.getUser(), sessionID, true, roles, ttl);
            Cookie sessionCookie = new Cookie(SESSION_COOKIE_IDENTIFIER, sessionID);
            sessionCookie.setPath("/");
            response.addCookie(sessionCookie);
            sessionMap.put(sessionID, session);
        }

        System.out.println("Returning new session [" + session + "]");
        return session;
    }
    */

    public QCSession createSession(HttpServletRequest request, HttpServletResponse response, QCCredentials credentials, long ttl, String sessionID){
        System.out.println(">>QC>> createSession#4");

        QCSession session = null;
        if(authenticationService != null && authenticationService.authenticate(credentials)){
            Set<String> roles = authenticationService.getRolesForUser(credentials);
            session = new QCSession(credentials.getUser(), sessionID, true, roles, ttl);
            Cookie sessionCookie = new Cookie(SESSION_COOKIE_IDENTIFIER, sessionID);
            sessionCookie.setPath("/");
            response.addCookie(sessionCookie);
            sessionMap.put(sessionID, session);
            System.out.println(">>QC>> Created session [" + session + "] with session id [" + sessionID + "]");
        }
        else{
            System.out.println(">>QC>> Authentication failed");
        }

        System.out.println(">>QC>> Returning session [" + session + "] with session id [" + sessionID + "]");
        return session;
    }
    /*
    public QCSession createSession(HttpServletRequest request, HttpServletResponse response, QCCredentials credentials, long ttl, String sessionID){
        QCSession session = null;

        System.out.println("Attempting to create session");
        if(authenticationService != null && authenticationService.authenticate(credentials)){
            Set<String> roles = authenticationService.getRolesForUser(credentials);
            session = new QCSession(credentials.getUser(), sessionID, true, roles, ttl);
            Cookie sessionCookie = new Cookie(SESSION_COOKIE_IDENTIFIER, sessionID);
            sessionCookie.setPath("/");
            response.addCookie(sessionCookie);
            sessionMap.put(sessionID, session);
        }

        System.out.println("Returning new session [" + session + "]");
        return session;
    }
    */

    public QCSession getSession(HttpServletRequest request){
        QCSession session = null;

        String sessionID = QCUtil.getCookieValue(SESSION_COOKIE_IDENTIFIER, request);
        System.out.println(">>QC>> Get session for session id [" + sessionID + "]");
        if(sessionID != null && !sessionID.isEmpty()){
            session = sessionMap.get(sessionID);
        }

        System.out.println(">>QC>> Returning session [" + session + "] for session id [" + sessionID + "]");
        return session;
    }
    /*
    public QCSession getSession(HttpServletRequest request){
        QCSession session = null;

        System.out.println("Test if gc should be performed");
        if(gcPerformed + gcThreshold < System.currentTimeMillis()){
            synchronized (QCSessionHandler.class){
                System.out.println("Perform gc");
                if(gcPerformed + gcThreshold < System.currentTimeMillis()){

                    Iterator<Map.Entry<String, QCSession>> it = sessionMap.entrySet().iterator();
                    while(it.hasNext()){
                        Map.Entry<String, QCSession> entry = it.next();
                        if(!entry.getValue().isAlive()){
                            it.remove();
                        }
                    }

                    gcPerformed = System.currentTimeMillis();
                }
            }
        }

        System.out.println("Get cookie session");
        String sessionID = QCUtil.getCookieValue(SESSION_COOKIE_IDENTIFIER, request);
        if(sessionID != null && !sessionID.isEmpty()){
            System.out.println("Get Session");
            session = sessionMap.get(sessionID);
            if(session != null && session.isExpired() && !renewExpiredSession(session)){
                System.out.println("Session is expired. Remove session");
                sessionMap.remove(sessionID);
                session = null;
            }
        }

        System.out.println("Returning session [" + session + "]");
        return session;
    }
    */


    public boolean renewExpiredSession(QCSession session){
        return true;
    }
    /*
    public boolean renewExpiredSession(QCSession session){
        System.out.println("Session renewed");
        session.renewExpiration(defaultMaxTTL);
        return true;
    }
    */

    public void destroySession(HttpServletRequest request, HttpServletResponse response){
        String sessionID = QCUtil.getCookieValue(SESSION_COOKIE_IDENTIFIER, request);
        System.out.println(">>QC>> Destroy session [" + sessionID + "]");
        if(sessionID != null && !sessionID.isEmpty()){
            System.out.println(">>QC>> Remove session [" + sessionID + "]");
            sessionMap.remove(sessionID);
            System.out.println(">>QC>> Unset cookie [" + SESSION_COOKIE_IDENTIFIER + "]");
            response.addCookie(new Cookie(SESSION_COOKIE_IDENTIFIER, ""));
        }
    }

}
