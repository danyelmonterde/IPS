package com.monterdev.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IqwdException extends Exception{

    private String code;

    public IqwdException(String code, String message){
        super(message);
        this.setCode(code);
    }

    public IqwdException(String code, String message, Throwable throwable){
        super(message,throwable);
        this.setCode(code);
    }

}
