package com.shane.powersaver.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
@SuppressWarnings("serial")
@XStreamAlias("powersaver")
public class NewsList extends Entity implements ListEntity<News> {

	public final static String PREF_READED_NEWS_LIST = "readed_news_list.pref";

	public final static int CATALOG_ALL = 1;
	public final static int CATALOG_INTEGRATION = 2;
	public final static int CATALOG_SOFTWARE = 3;

	public final static int CATALOG_WEEK = 4;
	public final static int CATALOG_MONTH = 5;

	@XStreamAlias("catalog")
	private int catalog;

	@XStreamAlias("pagesize")
	private int pageSize;

	@XStreamAlias("newscount")
	private int newsCount;

	@XStreamAlias("newslist")
	private List<News> list = new ArrayList<News>();

	public int getCatalog() {
		return catalog;
	}

	public void setCatalog(int catalog) {
		this.catalog = catalog;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getNewsCount() {
		return newsCount;
	}

	public void setNewsCount(int newsCount) {
		this.newsCount = newsCount;
	}

	public List<News> getList() {
		return list;
	}

	public void setList(List<News> list) {
		this.list = list;
	}
}
