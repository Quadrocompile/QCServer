package com.quadrocompile.qcserver.security.credentials;

import org.apache.log4j.Logger;

public class QCInitialCredentials implements QCCredentials {
    private static final Logger log = Logger.getLogger(QCInitialCredentials.class);

    private final String user;
    private final String password;

    public QCInitialCredentials(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getUser(){
        return user;
    }

    public String getPassword(){
        return password;
    }

    @Override
    public boolean check(QCCredentials credentials) {
        if(!(credentials instanceof QCLoginCredentials)){
            log.error("Cannot compare " + credentials.getClass() + " to QCInitialCredentials!");
            return false;
        }

        if(this.getUser().equalsIgnoreCase(credentials.getUser()) &&
                this.getPassword().equals(credentials.getPassword())){
            ((QCLoginCredentials)credentials).setUserNameDisplay(this.user);
            return true;
        }
        else{
            return false;
        }
    }

}
