package com.ridetogether.server.global.apiPayload.exception;

public abstract class BaseException extends RuntimeException{
	public abstract BaseExceptionType getExceptionType();
}
