package com.shane.powersaver.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
@SuppressWarnings("serial")
public abstract class Entity extends Base {

    @XStreamAlias("id")
    protected int id;

    protected String cacheKey;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getCacheKey() {
	return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
	this.cacheKey = cacheKey;
    }
}
