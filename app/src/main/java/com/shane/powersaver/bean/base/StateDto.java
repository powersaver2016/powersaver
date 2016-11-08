package com.shane.powersaver.bean.base;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class StateDto implements Serializable {

    // from StatElement
    @JsonProperty("uid")
    public int m_uid = -1;

    @JsonProperty("total")
    public long m_total;

    // from State
    @JsonProperty("freq")
    public int m_freq = 0;

    @JsonProperty("duration_ms")
    public long m_duration = 0;

}
