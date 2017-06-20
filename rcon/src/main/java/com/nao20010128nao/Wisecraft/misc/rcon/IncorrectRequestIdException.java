package com.nao20010128nao.Wisecraft.misc.rcon;

public class IncorrectRequestIdException extends AuthenticationException {
    private static final long serialVersionUID = 1L;

    public IncorrectRequestIdException(final int requestId) {
        super("Request id:" + requestId);
    }

    public IncorrectRequestIdException(final int requestId, final Throwable cause) {
        super("Request id:" + requestId, cause);
    }
}
