package com.shane.powersaver.bean.base;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class ProcessDto implements Serializable {

    // from StatElement
    @JsonProperty("uid")
    public int m_uid = -1;

    @JsonProperty("total")
    public long m_total;

    // from Process
    @JsonProperty("name")
    public String m_name;

    @JsonProperty("system_time")
    public long m_systemTime;

    @SerializedName("user_time")
    public long m_userTime;

    @JsonProperty("starts")
    public int m_starts;

}
