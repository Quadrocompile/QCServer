package com.quadrocompile.qcserver.security;

import org.apache.log4j.Logger;

public class QCLoginCredentials{
    private static final Logger log = Logger.getLogger(QCLoginCredentials.class);

    private String user;
    private final String password;
    public QCLoginCredentials(String user, String password){
        this.user = user;
        this.password = password;
    }

    public String getUser(){
        return user;
    }

    public String getPassword(){
        return password;
    }
}
