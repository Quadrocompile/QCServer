package com.quadrocompile.qcserver.servlets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class QCLoginHttpServlet extends HttpServlet {

    @Override
    public abstract void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    @Override
    public abstract void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException;

}
