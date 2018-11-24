package com.quadrocompile.qcserver.security.credentials;

import com.quadrocompile.qcserver.security.QCLoginCredentials;
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
    public boolean check(QCLoginCredentials credentials) {
        if(this.getUser().equals(credentials.getUser()) &&
                BCrypt.checkpw(credentials.getPassword(), pwHash)){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public boolean checkIgnoreCase(QCLoginCredentials credentials) {
        if(this.getUser().equalsIgnoreCase(credentials.getUser()) &&
                BCrypt.checkpw(credentials.getPassword(), pwHash)){
            return true;
        }
        else{
            return false;
        }
    }

}
