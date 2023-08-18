package com.cwb.base.exception;

/**
 * @author CWB
 * @version 1.0
 * 项目异常类
 */
@SuppressWarnings({"all"})
public class XcException extends RuntimeException{
    private String errMessage;

    public XcException() {
        super();
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public XcException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public static void cast(CommonError commonError){
        throw new XcException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new XcException(errMessage);
    }
}
