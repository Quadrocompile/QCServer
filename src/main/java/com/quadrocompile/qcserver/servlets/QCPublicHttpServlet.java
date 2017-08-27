package com.quadrocompile.qcserver.servlets;

import com.quadrocompile.qcserver.QCServer;
import com.quadrocompile.qcserver.session.QCSession;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ResourceBundle;

public abstract class QCPublicHttpServlet extends HttpServlet {
    private static final String LSTRING_FILE =
            "javax.servlet.http.LocalStrings";
    private static ResourceBundle lStrings =
            ResourceBundle.getBundle(LSTRING_FILE);

    private static final Logger log = Logger.getLogger(QCPublicHttpServlet.class);

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        QCServer instance = QCServer.getInstance();
        QCSession session = instance.getSessionHandler().getSession(req);

        if (session != null && session.isAuthenticated()) {
            doPostProtected(req, resp, session);
        } else {
            doPostPublic(req, resp, session);
        }
    }

    protected void doPostProtected(HttpServletRequest req, HttpServletResponse resp, QCSession session) throws IOException{
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_post_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }
    protected void doPostPublic(HttpServletRequest req, HttpServletResponse resp, QCSession session) throws IOException{
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_post_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    @Override
    public long getLastModified(HttpServletRequest req) {
        QCServer instance = QCServer.getInstance();
        QCSession session = instance.getSessionHandler().getSession(req);

        if (session != null && session.isAuthenticated()) {
            return getLastModifiedPublic(req, session);
        } else {
            return getLastModifiedProtected(req, session);
        }
    }

    protected long getLastModifiedPublic(HttpServletRequest req, QCSession session) {
        return -1;
    }

    protected long getLastModifiedProtected(HttpServletRequest req, QCSession session) {
        return -1;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        QCServer instance = QCServer.getInstance();
        QCSession session = instance.getSessionHandler().getSession(req);

        if (session != null && session.isAuthenticated()) {
            doGetProtected(req, resp, session);
        } else {
            doGetPublic(req, resp, session);
        }
    }

    protected void doGetPublic(HttpServletRequest req, HttpServletResponse resp, QCSession session) throws IOException {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_post_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    protected void doGetProtected(HttpServletRequest req, HttpServletResponse resp, QCSession session) throws IOException {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_post_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }
}
