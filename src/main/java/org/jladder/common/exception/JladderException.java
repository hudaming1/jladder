package org.jladder.common.exception;

public class JladderException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JladderException(String msg) {
		super(msg);
	}

	public JladderException(String msg, Throwable error) {
		super(msg, error);
	}
}
