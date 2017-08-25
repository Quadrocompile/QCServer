package com.quadrocompile.qcserver.security;

import com.quadrocompile.qcserver.security.credentials.QCCredentials;

import java.util.Set;

public interface QCAuthenticationService {
    boolean authenticate(QCCredentials credentials);
    Set<String> getRolesForUser(QCCredentials credentials);
}
