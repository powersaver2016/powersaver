package com.shane.powersaver.bean.base;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;


/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class WakelockDto implements Serializable {

    // from StatElement
    @JsonProperty("uid")
    public int m_uid;

    @JsonProperty("total")
    public long m_total;

    // from Wakelock
    @JsonProperty("wake_type")
    public int m_wakeType;

    @JsonProperty("name")
    public String m_name;

    @JsonProperty("duration_ms")
    public long m_duration;

    @JsonProperty("count")
    public int m_count;

}
