package com.quadrocompile.qcserver.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class QCUtil {

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
}
