package com.shane.powersaver.bean.base;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;


/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class NetworkUsageDto implements Serializable {

    // from StatElement
    @JsonProperty("uid")
    public int m_uid = -1;

    @JsonProperty("total")
    public long m_total;

    // from NetworkUsage
    @JsonProperty("bytes_received")
    public long m_bytesReceived = 0;

    @JsonProperty("bytes_sent")
    public long m_bytesSent = 0;

    @JsonProperty("iface")
    public String m_iface = "";
}
