package com.quadrocompile.qcserver.sessions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface QCSessionHandler {

    String SESSION_COOKIE_IDENTIFIER = "j_qcSessionID";

    QCSession createSession(HttpServletRequest request, HttpServletResponse response);
    QCSession createSession(String userID, HttpServletRequest request, HttpServletResponse response);
    QCSession createSession(String userID, int maxAge, HttpServletRequest request, HttpServletResponse response);
    QCSession getSession(HttpServletRequest request);
    void removeSession(QCSession request);
    void destroySession(HttpServletRequest request, HttpServletResponse response);

    void updateSessionID(HttpServletRequest request, HttpServletResponse response);
    void updateSessionID(QCSession session, HttpServletResponse response);
}
