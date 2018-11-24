package com.quadrocompile.qcserver.security.credentials;

import com.quadrocompile.qcserver.security.QCLoginCredentials;
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
    public boolean check(QCLoginCredentials credentials) {
        boolean authenticated = true;
        if(this.getUser().equals(credentials.getUser())){
            if(this.getPassword().length() != credentials.getPassword().length()){
                authenticated = false;
            }

            for (int i = 0; i < this.getPassword().length(); i++) {
                if(this.getPassword().charAt(i) != credentials.getPassword().charAt(i)){
                    authenticated = false;
                }
            }
        }
        else{
            authenticated = false;
        }
        return authenticated;
    }

    @Override
    public boolean checkIgnoreCase(QCLoginCredentials credentials) {
        boolean authenticated = true;
        if(this.getUser().equalsIgnoreCase(credentials.getUser())){
            if(this.getPassword().length() != credentials.getPassword().length()){
                authenticated = false;
            }

            for (int i = 0; i < this.getPassword().length(); i++) {
                if(this.getPassword().charAt(i) != credentials.getPassword().charAt(i)){
                    authenticated = false;
                }
            }
        }
        else{
            authenticated = false;
        }
        return authenticated;
    }

}
