package com.quadrocompile.qcserver.security.credentials;

import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

public class QCBCryptCredentials implements QCCredentials {
    private static final Logger log = Logger.getLogger(QCBCryptCredentials.class);

    private final String user;
    private final String pwHash;

    public QCBCryptCredentials(String user, String hash) {
        this.user = user;
        this.pwHash = hash.startsWith("CRYPT:")?hash.substring("CRYPT:".length()):hash;;
    }

    public String getUser(){
        return user;
    }

    public String getPassword(){
        return pwHash;
    }

    @Override
    public boolean check(QCCredentials credentials) {
        if(!(credentials instanceof QCLoginCredentials)){
            log.error("Cannot compare " + credentials.getClass() + " to QCBCryptCredentials!");
            return false;
        }

        if(this.getUser().equalsIgnoreCase(credentials.getUser()) &&
                BCrypt.checkpw(credentials.getPassword(), pwHash)){
            ((QCLoginCredentials)credentials).setUserNameDisplay(this.user);
            return true;
        }
        else{
            return false;
        }
    }

}
