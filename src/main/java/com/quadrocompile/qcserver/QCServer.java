package com.quadrocompile.qcserver;

import com.quadrocompile.qcserver.htmltemplates.QCTemplateEngine;
import com.quadrocompile.qcserver.sessions.QCSessionHandler;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class QCServer {
    private static final Logger log = Logger.getLogger(QCServer.class);

    public static void main(String[] args) throws Exception{
        // For debugging
        QCTemplateEngine.enableTemplateReloading();

        initializeInstanceDefault();
        getInstance().startServer();
    }

    private static QCServer instance;

    public static synchronized void initializeInstanceDefault(){
        try {
            if(instance != null){
                throw new Exception("QCServer is already initialized!");
            }

            instance = new QCServer(9900, 20, 4, 60000, -1, -1, new LinkedBlockingQueue<>(5000));
        }
        catch (Exception ex){
            log.error("Cannot initialize new QCServer instance", ex);
        }
    }
    public static synchronized void initializeInstance(int port, int maxthreads, int minthreads, int timeout,
                                                       int capacity, int acceptors, int selectors){
        try {
            if(instance != null){
                throw new Exception("QCServer is already initialized!");
            }

            instance = new QCServer(port, maxthreads, minthreads, timeout, acceptors, selectors, new LinkedBlockingQueue<>(capacity));
        }
        catch (Exception ex){
            log.error("Cannot initialize new QCServer instance", ex);
        }
    }
    public static synchronized void initializeInstance(int port, int maxthreads, int minthreads, int timeout,
                                                       int acceptors, int selectors, LinkedBlockingQueue<Runnable> queue){
        try {
            if(instance != null){
                throw new Exception("QCServer is already initialized!");
            }

            instance = new QCServer(port, maxthreads, minthreads, timeout, acceptors, selectors, queue);
        }
        catch (Exception ex){
            log.error("Cannot initialize new QCServer instance", ex);
        }
    }
    public static QCServer getInstance(){
        return instance;
    }

    private QCSessionHandler sessionHandler;
    private String URL_HANDLE_LOGIN;
    private String URL_HANDLE_FORBIDDEN;

    private final Server SERVER;
    private final ServletHandler SERVLET_HANDLER;

    private QCServer(int port, int maxthreads, int minthreads, int timeout,
                     int acceptors, int selectors, LinkedBlockingQueue<Runnable> queue) throws IOException{

        SERVER = new Server(new QueuedThreadPool(maxthreads, minthreads, timeout, queue));

        ServerConnector serverConnector = new ServerConnector(SERVER, acceptors, selectors);
        serverConnector.setPort(port);
        SERVER.setConnectors(new Connector[]{serverConnector});

        SERVLET_HANDLER = new ServletHandler();
        SERVER.setHandler(SERVLET_HANDLER);
    }

    public void startServer() throws Exception{
        SERVER.start();
        SERVER.join();
    }

    public void shutdownServer() throws Exception{
        SERVER.stop();
    }

    public void addServletWithMapping(Class<? extends Servlet> servlet, String pathSpec){
        SERVLET_HANDLER.addServletWithMapping(servlet, pathSpec);
    }

    public QCSessionHandler getSessionHandler(){
        return sessionHandler;
    }
    public void setSessionHandler(QCSessionHandler handler){
        sessionHandler = handler;
    }

    public String getHandleLoginURL() { return URL_HANDLE_LOGIN; }
    public void setHandleLoginURL(String url) { URL_HANDLE_LOGIN = url; }
    public String getHandleForbiddenURL() { return URL_HANDLE_FORBIDDEN; }
    public void setHandleForbiddenURL(String url) { URL_HANDLE_FORBIDDEN = url; }
}
