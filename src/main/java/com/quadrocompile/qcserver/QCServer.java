package com.quadrocompile.qcserver;

import com.quadrocompile.qcserver.htmltemplates.QCTemplateEngine;
import com.quadrocompile.qcserver.sessions.QCSessionHandler;
import com.quadrocompile.qcserver.websocket.EchoServer;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.eclipse.jetty.util.ArrayUtil;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class QCServer {
    private static final Logger log = Logger.getLogger(QCServer.class);

    public static void main(String[] args) throws Exception{
        // Setup default console logger
        Logger.getRootLogger().getLoggerRepository().resetConfiguration();
        ConsoleAppender console = new ConsoleAppender(); //create appender
        String PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%p|%c{1}:%L] %m%n";
        console.setLayout(new PatternLayout(PATTERN));
        console.setThreshold(Level.INFO);
        console.activateOptions();
        Logger.getRootLogger().addAppender(console);

        // For debugging
        QCTemplateEngine.enableTemplateReloading();

        initializeInstanceDefault();

        initializeWebSockets(EchoServer.class);

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

    public static void initializeWebSockets(Class WebSocketClass){
        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(WebSocketClass);
            }
        };
        instance.SERVER.setHandler(wsHandler);
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

    public void replaceServletWithMapping(Class<? extends Servlet> servlet, String pathSpec){
        ServletMapping[] mappingArray = SERVLET_HANDLER.getServletMappings();
        for (ServletMapping mapping : mappingArray) {
            if (mapping.containsPathSpec(pathSpec)) {
                ServletMapping[] updatedMappings = ArrayUtil.removeFromArray(mappingArray, mapping);
                SERVLET_HANDLER.setServletMappings(updatedMappings);
                break;
            }
        }
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
