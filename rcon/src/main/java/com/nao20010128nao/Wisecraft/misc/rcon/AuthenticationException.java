package com.nao20010128nao.Wisecraft.misc.rcon;

public class AuthenticationException extends Exception {
	private static final long serialVersionUID = 1L;
	public AuthenticationException() {
		super();
	}
	public AuthenticationException(final String message) {
		super(message);
	}
	public AuthenticationException(final String message, final Throwable cause) {
		super(message, cause);
	}
	public AuthenticationException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause);
	}
	public AuthenticationException(final Throwable cause) {
		super(cause);
	}

}
