package com.shane.powersaver.bean.base;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;


/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class MiscDto implements Serializable {

    // from StatElement
    @JsonProperty("uid")
    public int m_uid = -1;

    @JsonProperty("total")
    public long m_total;

    // from Misc
    @JsonProperty("name")
    public String m_name;

    @JsonProperty("time_on_ms")
    public long m_timeOn;

    @JsonProperty("time_running_ms")
    public long m_timeRunning;
}
