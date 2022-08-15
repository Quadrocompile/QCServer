package com.quadrocompile.qcserver.servlets;

import com.quadrocompile.qcserver.QCServer;
import com.quadrocompile.qcserver.sessions.QCSession;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ResourceBundle;

public abstract class QCProtectedHttpServlet extends HttpServlet {
    private static final String LSTRING_FILE =
            "javax.servlet.http.LocalStrings";
    private static ResourceBundle lStrings =
            ResourceBundle.getBundle(LSTRING_FILE);

    private static final Logger log = Logger.getLogger(QCProtectedHttpServlet.class);

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        QCServer instance = QCServer.getInstance();
        QCSession session = instance.getSessionHandler()!=null ? instance.getSessionHandler().getSession(req) : null;

        if(session != null){
            doPostProtected(req, resp, session);
        }
        else{
            String handleLoginURL = QCServer.getInstance().getHandleLoginURL();
            if(handleLoginURL == null){
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "403 - Forbidden");
            }
            else{
                resp.sendRedirect(handleLoginURL);
            }
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

    @Override
    public long getLastModified(HttpServletRequest req){
        QCServer instance = QCServer.getInstance();
        QCSession session = instance.getSessionHandler()!=null ? instance.getSessionHandler().getSession(req) : null;

        if(session != null){
            return getLastModifiedProtected(req, session);
        }
        else{
            return -1;
        }
    }
    protected long getLastModifiedProtected(HttpServletRequest req, QCSession session){
        return -1;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        QCServer instance = QCServer.getInstance();
        QCSession session = instance.getSessionHandler()!=null ? instance.getSessionHandler().getSession(req) : null;

        if(session != null){
            doGetProtected(req, resp, session);
        }
        else{
            String handleLoginURL = QCServer.getInstance().getHandleLoginURL();
            if(handleLoginURL == null){
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "403 - Forbidden");
            }
            else{
                resp.sendRedirect(handleLoginURL);
            }
        }
    }
    protected  void doGetProtected(HttpServletRequest req, HttpServletResponse resp, QCSession session) throws IOException{
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_get_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

}
