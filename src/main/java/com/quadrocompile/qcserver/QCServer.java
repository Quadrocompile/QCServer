package com.quadrocompile.qcserver;

import com.quadrocompile.qcserver.htmltemplates.QCTemplateEngine;
import com.quadrocompile.qcserver.servlets.QCPublicHttpServlet;
import com.quadrocompile.qcserver.sessions.QCSession;
import com.quadrocompile.qcserver.sessions.QCSessionHandler;
import com.quadrocompile.qcserver.websocket.EchoServer;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.eclipse.jetty.util.ArrayUtil;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class QCServer {
    private static final Logger log = Logger.getLogger(QCServer.class);

    public static class BrokenPage extends QCPublicHttpServlet {
        @Override
        protected void doGetPublic(HttpServletRequest req, HttpServletResponse resp, QCSession session) throws IOException {
            String a = null;
            System.out.println(a.length()); // Provoke NP exception
        }
    };
    private static void htmlRow(Writer writer, String tag, Object value) throws IOException {
        writer.write("<tr><th>");
        writer.write(tag);
        writer.write(":</th><td>");
        if (value == null) {
            writer.write("-");
        } else {
            writer.write(StringUtil.sanitizeXmlString(value.toString()));
        }

        writer.write("</td></tr>\n");
    }
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

        //initializeWebSockets(EchoServer.class);



        ErrorPageErrorHandler eh = new ErrorPageErrorHandler () {
            void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException{
            }
            protected void handleErrorPage(HttpServletRequest request, Writer writer, int code, String message) throws IOException{
                BufferedWriter bw = new BufferedWriter(writer);
                bw.write("<html><head></head><body><h1>Custom Error Page!</h1>");

                bw.write("<h2>HTTP ERROR ");
                String status = Integer.toString(code);
                bw.write(status);
                if (message != null && !message.equals(status)) {
                    bw.write(32);
                    bw.write(StringUtil.sanitizeXmlString(message));
                }

                bw.write("</h2>\n");
                bw.write("<table>\n");
                htmlRow(bw, "URI", request.getRequestURI());
                htmlRow(bw, "STATUS", status);
                htmlRow(bw, "MESSAGE", message);
                if (this.isShowServlet()) {
                    htmlRow(bw, "SERVLET", request.getAttribute("javax.servlet.error.servlet_name"));
                }

                for(Throwable cause = (Throwable)request.getAttribute("javax.servlet.error.exception"); cause != null; cause = cause.getCause()) {
                    htmlRow(bw, "CAUSED BY", cause);
                }

                bw.write("</table>\n");

                Throwable th = (Throwable)request.getAttribute("javax.servlet.error.exception");
                if (th != null) {
                    bw.write("<h3>Caused by:</h3><pre>");
                    StringWriter sw = new StringWriter();

                    try {
                        PrintWriter pw = new PrintWriter(sw);

                        try {
                            th.printStackTrace(pw);
                            pw.flush();
                            this.write(bw, sw.getBuffer().toString());
                        } catch (Throwable var10) {
                            try {
                                pw.close();
                            } catch (Throwable var9) {
                                var10.addSuppressed(var9);
                            }

                            throw var10;
                        }

                        pw.close();
                    } catch (Throwable var11) {
                        try {
                            sw.close();
                        } catch (Throwable var8) {
                            var11.addSuppressed(var8);
                        }

                        throw var11;
                    }

                    sw.close();
                    bw.write("</pre>\n");
                }

                bw.write("</body></html>\n");
                bw.flush();
                System.out.println("");
            }
            protected void writeErrorPage(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks) throws IOException{
            }
            protected void writeErrorPageHead(HttpServletRequest request, Writer writer, int code, String message) throws IOException{}
            protected void writeErrorPageBody(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks) throws IOException{}
            protected void writeErrorPageMessage(HttpServletRequest request, Writer writer, int code, String message, String uri) throws IOException{}
            protected void writeErrorPageStacks(HttpServletRequest request, Writer writer) throws IOException{}
        };
        getInstance().addErrorHandler(eh);

        getInstance().addServletWithMapping(BrokenPage.class, "/");

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
        instance.SERVER.insertHandler(wsHandler);
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

    public void addErrorHandler(ErrorPageErrorHandler errorHandler){
        SERVER.addBean(errorHandler);
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
