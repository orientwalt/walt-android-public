package com.weiyu.baselib.net.exception;

public class BackErrorException extends Exception {
    public String message;
    public int code;
    public String errCode;
    public String errMsg;

    public BackErrorException(String message) {
        this.message = message;
        this.errMsg = message;
    }
}
