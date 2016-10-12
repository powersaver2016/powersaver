package com.shane.powersaver.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
@SuppressWarnings("serial")
@XStreamAlias("result")
public class Result implements Serializable {

    @XStreamAlias("errorCode")
    private int errorCode;

    @XStreamAlias("errorMessage")
    private String errorMessage;

    public boolean OK() {
	return errorCode == 1;
    }

    public int getErrorCode() {
	return errorCode;
    }

    public void setErrorCode(int errorCode) {
	this.errorCode = errorCode;
    }

    public String getErrorMessage() {
	return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }
}
