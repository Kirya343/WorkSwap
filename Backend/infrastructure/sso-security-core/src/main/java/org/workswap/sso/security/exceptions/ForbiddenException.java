package org.workswap.sso.security.exceptions;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("Authentication required");
    }
}

