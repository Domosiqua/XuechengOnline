package com.cwb.base.exception;

import java.io.Serializable;

/**
 * @author CWB
 * @version 1.0
 * 抛出异常返回响应
 */
@SuppressWarnings({"all"})
public class RestErrorResponse implements Serializable {
    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
