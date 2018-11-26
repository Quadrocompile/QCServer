package com.quadrocompile.qcserver.sessions;

import com.quadrocompile.qcserver.util.QCUtil;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class QCInMemorySessionHandler implements QCSessionHandler {

    private Map<String, QCSession> sessionMap;

    private SecureRandom rng = null;
    private final Object rngLock = new Object();
    private final AtomicInteger rngUsed;
    private final int prngThreshold;

    public QCInMemorySessionHandler(int reseedThreshold) throws Exception{
        sessionMap = new ConcurrentHashMap<>();

        reseed();
        rngUsed = new AtomicInteger(0);
        prngThreshold = reseedThreshold;
    }

    private void reseed(){
        try {
            // FROM https://tersesystems.com/blog/2015/12/17/the-right-way-to-use-securerandom/

            SecureRandom nativeRandom;
            try {
                nativeRandom = SecureRandom.getInstance("NativePRNGNonBlocking");
            }
            catch(Exception ignored){
                nativeRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
            }
            byte[] seed = nativeRandom.generateSeed(55); // NIST SP800-90A suggests 440 bits for SHA1 seed

            if(rng == null){
                rng = SecureRandom.getInstance("SHA1PRNG");
            }
            rng.setSeed(seed);

            byte[] initBytes = new byte[20];
            rng.nextBytes(initBytes); // SHA1PRNG, seeded properly
        }
        catch (Exception ex){
            throw new RuntimeException("Failed to reseed the SecureRandom object.", ex);
        }
    }

    private String createSessionID(String uniqueID){
        synchronized (rngLock){
            int accessed = rngUsed.incrementAndGet();
            if(accessed > prngThreshold){
                rngUsed.set(0);
                reseed();
            }

            String salt = BCrypt.gensalt(18, rng);
            return BCrypt.hashpw(uniqueID, salt);
        }
    }

    public QCSession createSession(HttpServletRequest request, HttpServletResponse response){
        String remoteAddr = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        if(userAgent == null) userAgent = "UNKNOWN_AGENT";

        String newSessionID = createSessionID(remoteAddr + "_" + userAgent);

        QCSession session = new QCSession(newSessionID, remoteAddr, userAgent);

        sessionMap.put(newSessionID, session);

        QCUtil.setCookieValue(SESSION_COOKIE_IDENTIFIER, newSessionID, "/", true, false, response);

        return session;
    }
    public QCSession createSession(String userID, HttpServletRequest request, HttpServletResponse response){

        // Remove sessions from the same user
        Set<String> sessionsToRemove = new HashSet<>();
        for(Map.Entry<String, QCSession> sessionEntry : sessionMap.entrySet()){
            if(sessionEntry.getValue().getValue("SESSION_USER_ID", "-1").equals(userID)){
                sessionsToRemove.add(sessionEntry.getKey());
            }
            for(String sessionID : sessionsToRemove){
                sessionMap.remove(sessionID);
            }
        }

        String remoteAddr = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        if(userAgent == null) userAgent = "UNKNOWN_AGENT";

        String newSessionID = createSessionID(remoteAddr + "_" + userAgent);

        QCSession session = new QCSession(newSessionID, remoteAddr, userAgent);
        session.setValue("SESSION_USER_ID", userID);

        sessionMap.put(newSessionID, session);

        QCUtil.setCookieValue(SESSION_COOKIE_IDENTIFIER, newSessionID, "/", true, false, response);

        return session;
    }
    public QCSession getSession(HttpServletRequest request) throws SecurityException{
        QCSession session = null;

        String sessionID = QCUtil.getCookieValue(SESSION_COOKIE_IDENTIFIER, request);
        if(sessionID != null && !sessionID.isEmpty()){
            session = sessionMap.get(sessionID);
        }

        String remoteAdds = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        if(userAgent == null) userAgent = "UNKNOWN_AGENT";
        if(session != null && !session.verifyOrigin(remoteAdds, userAgent)){
            throw new SecurityException("Origin mismatch");
        }

        return session;
    }

    public void removeSession(QCSession session){
        sessionMap.remove(session.getSessionID());
    }
    public void destroySession(HttpServletRequest request, HttpServletResponse response){
        String sessionID = QCUtil.getCookieValue(SESSION_COOKIE_IDENTIFIER, request);
        sessionMap.remove(sessionID);

        QCUtil.setCookieValue(SESSION_COOKIE_IDENTIFIER, "", "/", true, false, response);
    }

    public void updateSessionID(HttpServletRequest request, HttpServletResponse response){
        updateSessionID(getSession(request), response);
    }
    public void updateSessionID(QCSession session, HttpServletResponse response){
        if(session != null){
            sessionMap.remove(session.getSessionID());

            String newSessionID = createSessionID(session.getRemoteHost() + "_" + session.getUserAgent());
            sessionMap.put(newSessionID, session);

            session.updateSessionID(newSessionID);

            QCUtil.setCookieValue(SESSION_COOKIE_IDENTIFIER, newSessionID, "/", true, false, response);
        }
    }

}
