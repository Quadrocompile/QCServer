package com.quadrocompile.qcserver.security.credentials;

import org.apache.log4j.Logger;

public class QCLoginCredentials implements QCCredentials {
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

    public void setUserNameDisplay(String userName){
        this.user = userName;
    }

    public boolean check(QCCredentials credentials){
        if(credentials instanceof QCLoginCredentials){
            log.error("Cannot compare two QCLoginCredentials!");
            return false;
        }
        else{
            return credentials.check(this);
        }
    }
}
