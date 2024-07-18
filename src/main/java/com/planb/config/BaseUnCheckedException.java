package com.planb.config;

import lombok.Getter;

@Getter
public class BaseUnCheckedException extends RuntimeException {

    private static final long serialVersionUID = -2755886906520742785L;

    protected int errCode = 200;

    public BaseUnCheckedException(String msg) {
        //标识
        super(msg);
    }

    public BaseUnCheckedException(String msg, int errCode) {
        super(msg);
        this.errCode = errCode;
    }

    public BaseUnCheckedException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
