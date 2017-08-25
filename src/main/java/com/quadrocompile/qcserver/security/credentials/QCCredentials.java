package com.quadrocompile.qcserver.security.credentials;

public interface QCCredentials {

    String getUser();
    String getPassword();
    boolean check(QCCredentials credentials);

}
