package com.quadrocompile.qcserver.security;

public interface QCAuthenticationService {
    boolean authenticate(QCLoginCredentials credentials);
}
