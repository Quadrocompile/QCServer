package com.quadrocompile.qcserver.util;

import com.quadrocompile.qcserver.htmltemplates.QCHTMLTemplate;
import com.quadrocompile.qcserver.htmltemplates.QCTemplateEngine;
import com.quadrocompile.qcserver.htmltemplates.paramdata.QCTemplateParam;
import org.apache.log4j.Logger;
import org.eclipse.jetty.io.EofException;
import org.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

public class QCUtil {
    public static final String SESSION_COOKIE_IDENTIFIER = "j_qcSessionID";

    private static final Logger log = Logger.getLogger(QCUtil.class);

    public static void setCookieValue(String key, String value, String path, boolean httpOnly, boolean secure, HttpServletResponse resp){
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        if(httpOnly) cookie.setHttpOnly(true);
        if(secure) cookie.setSecure(true);

        resp.addCookie(cookie);
    }
    public static void setCookieValue(String key, String value, String path, int maxAge, boolean httpOnly, boolean secure, HttpServletResponse resp){
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        if(httpOnly) cookie.setHttpOnly(true);
        if(secure) cookie.setSecure(true);
        cookie.setMaxAge(maxAge);

        resp.addCookie(cookie);
    }

    public static void setCookieValue(String key, String value, String path, String domain, boolean httpOnly, boolean secure, HttpServletResponse resp){
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        cookie.setDomain(domain);
        if(httpOnly) cookie.setHttpOnly(true);
        if(secure) cookie.setSecure(true);

        resp.addCookie(cookie);
    }
    public static void setCookieValue(String key, String value, String path, String domain, int maxAge, boolean httpOnly, boolean secure, HttpServletResponse resp){
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        cookie.setDomain(domain);
        if(httpOnly) cookie.setHttpOnly(true);
        if(secure) cookie.setSecure(true);
        cookie.setMaxAge(maxAge);

        resp.addCookie(cookie);
    }

    public static String getCookieValue(String cookieName, HttpServletRequest req){
        if(req.getCookies() != null){
            for(Cookie cookie : req.getCookies()){
                if(cookie.getName().equals(cookieName)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static String getRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder requestString = new StringBuilder(req.getContentLength());
        BufferedReader userRequest = new BufferedReader(new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8));
        String line = null;
        while ((line = userRequest.readLine()) != null) {
            requestString.append(line);
            requestString.append("\n");
        }
        userRequest.close();
        return requestString.toString();
    }

    public static JSONObject getJSONFromRequest(HttpServletRequest req) throws IOException{
        String request = getRequestBody(req);
        return new JSONObject(request);
    }

    public static long writeJSON(HttpServletRequest req, HttpServletResponse resp, JSONObject json) {
        long payload;
        try {
            resp.setStatus(200);
            resp.setContentType("application/json; charset=utf-8");
            OutputStreamWriter outputStream = new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8);
            String jsonString = json.toString();
            outputStream.write(jsonString);
            payload = jsonString.getBytes(StandardCharsets.UTF_8).length;
            resp.setContentLength((int)payload);
            outputStream.flush();

            return payload;
        }
        catch (EofException ignored){
            // Ignore Eof Exceptions. There is nothing that we can do about a reset connection anyways
            return 0L;
        }
        catch (Exception ex){
            log.error("Cannot stream json: " + json.toString(), ex);

            try {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            }
            catch (IOException ignored){}

            return -1;
        }
    }
    public static void streamJSON(HttpServletRequest req, HttpServletResponse resp, JSONObject json) {
        try {
            resp.setStatus(200);
            resp.setContentType("application/json; charset=utf-8");
            OutputStreamWriter outputStream = new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8);

            json.write(outputStream);
            outputStream.flush();
        }
        catch (EofException ignored){
            // Ignore Eof Exceptions. There is nothing that we can do about a reset connection anyways
        }
        catch (Exception ex){
            log.error("Cannot stream json: " + json.toString(), ex);
            try {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            }
            catch (IOException ignored){}
        }
    }

    public static long streamTemplate(HttpServletRequest req, HttpServletResponse resp, String templateName, Map<String, QCTemplateParam> params){
        QCHTMLTemplate template = QCTemplateEngine.getTemplate(templateName);

        if(template == null){
            try {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "404 - Not found");
            }
            catch (IOException ignored){}
            return 0L;
        }
        else{
            // Stream HTML
            long payload;
            try{
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("text/html; charset=utf-8");
                OutputStreamWriter outputStream = new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8);
                payload = template.writeToStream(outputStream, params);
                resp.setContentLength((int)payload);
                outputStream.flush();

                return payload;
            }
            catch (EofException ignored){
                return 0L;
            }
            catch (Exception ex){
                log.error("Cannot stream html template: " + templateName, ex);
                return 0L;
            }
        }
    }
    public static long streamTemplate(HttpServletRequest req, HttpServletResponse resp, String templateName, Map<String, QCTemplateParam> params, Locale locale){
        QCHTMLTemplate template = QCTemplateEngine.getTemplate(templateName);

        if(template == null){
            try {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "404 - Not found");
            }
            catch (IOException ignored){}
            return 0L;
        }
        else{
            // Stream HTML
            long payload;
            try{
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("text/html; charset=utf-8");
                OutputStreamWriter outputStream = new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8);
                payload = template.writeToStream(outputStream, params, locale);
                resp.setContentLength((int)payload);
                outputStream.flush();

                return payload;
            }
            catch (EofException ignored){
                return 0L;
            }
            catch (Exception ex){
                log.error("Cannot stream html template: " + templateName, ex);
                return 0L;
            }
        }
    }

}
