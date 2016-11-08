package com.shane.powersaver.bean.base;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class UidInfoDto implements Serializable {
    @JsonProperty("uid")
    public int m_uid;

    @JsonProperty("name")
    public String m_uidName = "";

    @JsonProperty("package")
    public String m_uidNamePackage = "";

    @JsonProperty("unique")
    public boolean m_uidUniqueName = false;
}
