package com.quadrocompile.qcserver;

import com.quadrocompile.qcserver.servlets.QCLoginHttpServlet;
import com.quadrocompile.qcserver.session.QCSessionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class QCServer {

    protected static QCServer instance;
    public static void main(String[] args) throws Exception{
        instance = new QCServer();
    }
    public static QCServer getInstance(HttpServletRequest req){
        return instance;
    }


    private QCSessionHandler sessionHandler;
    private QCLoginHttpServlet loginServlet;

    public QCServer() throws IOException{

    }

    public QCSessionHandler getSessionHandler(){
        return sessionHandler;
    }
    public QCLoginHttpServlet getLoginServlet() { return loginServlet; }

}
