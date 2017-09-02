package com.quadrocompile.qcserver.session;

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
    protected long defaultTTL = 60 * 30 * 1000;
    protected long defaultMaxTTL = defaultTTL;

    public QCSessionHandler(QCAuthenticationService service){
        this.sessionIDGenerator = new RandomString();
        this.authenticationService = service;
    }

    public QCSession createSession(HttpServletRequest request, HttpServletResponse response, QCCredentials credentials){
        return createSession(request, response, credentials, defaultTTL);
    }

    public QCSession createSession(HttpServletRequest request, HttpServletResponse response, QCCredentials credentials, long ttl){
        QCSession session = null;

        if(authenticationService != null && authenticationService.authenticate(credentials)){
            String sessionID = sessionIDGenerator.nextString();
            Set<String> roles = authenticationService.getRolesForUser(credentials);
            session = new QCSession(credentials.getUser(), sessionID, true, roles, ttl);
            response.addCookie(new Cookie(SESSION_COOKIE_IDENTIFIER, sessionID));
            sessionMap.put(sessionID, session);
        }

        return session;
    }

    public QCSession getSession(HttpServletRequest request){
        QCSession session = null;

        if(gcPerformed + gcThreshold < System.currentTimeMillis()){
            synchronized (QCSessionHandler.class){
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

        String sessionID = QCUtil.getCookieValue(SESSION_COOKIE_IDENTIFIER, request);
        if(sessionID != null && !sessionID.isEmpty()){
            session = sessionMap.get(sessionID);
            if(session != null && session.isExpired() && !renewExpiredSession(session)){
                sessionMap.remove(sessionID);
                session = null;
            }
        }

        return session;
    }

    public boolean renewExpiredSession(QCSession session){
        session.renewExpiration(defaultMaxTTL);
        return true;
    }

    public void destroySession(HttpServletRequest request, HttpServletResponse response){
        String sessionID = QCUtil.getCookieValue(SESSION_COOKIE_IDENTIFIER, request);
        if(sessionID != null && !sessionID.isEmpty()){
            sessionMap.remove(sessionID);
            response.addCookie(new Cookie(SESSION_COOKIE_IDENTIFIER, ""));
        }
    }

}
