package com.shane.powersaver.bean.base;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;


/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class NativeKernelWakelockDto implements Serializable {

    // from StatElement
    @JsonProperty("uid")
    public int m_uid = -1;

    @JsonProperty("total")
    public long m_total;

    // from NativeKernelWakelock
    @JsonProperty("name")
    public String m_name;

    @JsonProperty("details")
    public String m_details;

    @JsonProperty("count")
    public int m_count;

    @JsonProperty("expire_count")
    public int m_expireCount;

    @JsonProperty("wake_count")
    public int m_wakeCount;

    @JsonProperty("active_since")
    public long m_activeSince;

    @JsonProperty("total_time")
    public long m_ttlTime;

    @JsonProperty("sleep_time")
    public long m_sleepTime;

    @JsonProperty("max_time")
    public long m_maxTime;

    @JsonProperty("last_change")
    public long m_lastChange;

}
