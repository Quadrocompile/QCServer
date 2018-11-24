package com.quadrocompile.qcserver.security.credentials;

import com.quadrocompile.qcserver.security.QCLoginCredentials;

public interface QCCredentials {

    String getUser();
    String getPassword();
    boolean check(QCLoginCredentials credentials);
    boolean checkIgnoreCase(QCLoginCredentials credentials);

}
