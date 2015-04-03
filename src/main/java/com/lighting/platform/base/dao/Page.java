/**
 * Copyright (c) 2005-2011 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 
 * $Id: Page.java 1453 2011-01-02 14:21:45Z calvinxiu $
 */
package com.lighting.platform.base.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 与具体ORM实现无关的分页参数及查询结果封装.
 * 本类只封装输入输出参数, 具体的分页逻辑全部封装在Paginator类.
 * 
 * @param <T> Page中记录的类型.
 * 
 * @author changhao
 */
public class Page<T> implements Serializable, Iterable<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	//-- 分页查询参数 --//
	protected int pageNo = 1;
	protected int pageSize = 10;
	protected String orderBy = null;
	protected String order = null;

	//-- 返回结果 --//
	protected List<T> rows = new ArrayList<T>();
	protected long total = 0;
	
	protected List<T> footer = new ArrayList<T>();
	

	public List<T> getFooter() {
		return footer;
	}

	public void setFooter(List<T> footer) {
		this.footer = footer;
	}

	//-- 构造函数 --//
	public Page() {
	}

	public Page(PageConfig config){
		this.pageNo = config.pageNo;
		this.pageSize = config.pageSize;
		this.order = config.order;
		this.orderBy = config.orderBy;
	}

	//-- 分页参数访问函数 --//
	/**
	 * 获得当前页的页号,序号从1开始,默认为1.
	 */
	public Integer getPageNo() {
		return pageNo;
	}


	/**
	 * 获得每页的记录数量, 默认为0.
	 */
	public int getPageSize() {
		return pageSize;
	}

	

	/**
	 * 获得排序字段,无默认值. 多个排序字段时用','分隔.
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * 获得排序方向, 无默认值.
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * 设置排序方式向.
	 * 
	 * @param order 可选值为desc或asc,多个排序字段时用','分隔.
	 */
	public void setOrder(final String order) {
		String lowcaseOrder = StringUtils.lowerCase(order);

		//检查order字符串的合法值
		String[] orders = StringUtils.split(lowcaseOrder, ',');
		for (String orderStr : orders) {
			if (!StringUtils.equals(PageConfig.DESC, orderStr) && !StringUtils.equals(PageConfig.ASC, orderStr)) {
				throw new IllegalArgumentException("排序方向" + orderStr + "不是合法值");
			}
		}

		this.order = lowcaseOrder;
	}

	/**
	 * 是否已设置排序字段,无默认值.
	 */
	public boolean isOrderBySetted() {
		return (StringUtils.isNotBlank(orderBy) && StringUtils.isNotBlank(order));
	}

	/**
	 * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从0开始.
	 * 用于Mysql,Hibernate.
	 */
	public int getOffset() {
		return ((pageNo - 1) * pageSize);
	}

	/**
	 * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从1开始.
	 * 用于Oracle.
	 */
	public int getStartRow() {
		return getOffset() + 1;
	}

	/**
	 * 根据pageNo和pageSize计算当前页最后一条记录在总结果集中的位置, 序号从1开始.
	 * 用于Oracle.
	 */
	public int getEndRow() {
		return pageSize * pageNo;
	}

	//-- 访问查询结果函数 --//

	/**
	 * 获得页内的记录列表.
	 */
	public List<T> getRows() {
		return rows;
	}

	/**
	 * 设置页内的记录列表.
	 */
	public void setRows(final List<T> rows) {
		this.rows = rows;
	}

	/** 
	 * 实现Iterable接口,可以for(Object item : page)遍历使用
	 */
	@SuppressWarnings("unchecked")
	public Iterator<T> iterator() {
		return rows == null ? IteratorUtils.EMPTY_ITERATOR : rows.iterator();
	}

	/**
	 * 获得总记录数, 默认值为-1.
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * 设置总记录数.
	 */
	public void setTotal(final long total) {
		this.total = total;

		// 如果每页条数过多, 设置为一共的条数
		/*int pageSize = getPageSize();
		if (pageSize > total) {
			this.pageSize = ((int) total);
		}*/

		// 如果页数太大, 设置为最后一页
		int pageNo = getPageNo();
		long totalPage = getTotalPages();
		if (pageNo > totalPage) {
			this.pageNo = ((int) totalPage);
		}

	}

	/**
	 * 是否最后一页.
	 */
	public boolean isLastPage() {
		return pageNo == getTotalPages();
	}

	/**
	 * 是否还有下一页.
	 */
	public boolean isHasNextPage() {
		return (pageNo + 1 <= getTotalPages());
	}

	/**
	 * 取得下页的页号, 序号从1开始.
	 * 当前页为尾页时仍返回尾页序号.
	 */
	public int getNextPage() {
		if (isHasNextPage()) {
			return pageNo + 1;
		} else {
			return pageNo;
		}
	}

	/**
	 * 是否第一页.
	 */
	public boolean isFirstPage() {
		return pageNo == 1;
	}

	/**
	 * 是否还有上一页.
	 */
	public boolean isHasPrePage() {
		return (pageNo - 1 >= 1);
	}

	/**
	 * 取得上页的页号, 序号从1开始.
	 * 当前页为首页时返回首页序号.
	 */
	public int getPrePage() {
		if (isHasPrePage()) {
			return pageNo - 1;
		} else {
			return pageNo;
		}
	}

	/**
	 * 根据pageSize与totalItems计算总页数, 默认值为-1.
	 */
	public long getTotalPages() {
		if (total == 0) {
			return 0;
		}

		long count = total / pageSize;
		if (total % pageSize > 0) {
			count++;
		}
		return count;
	}

	/**
	 * 计算以当前页为中心的页面列表,如"首页,23,24,25,26,27,末页"
	 * @param count 需要计算的列表大小
	 * @return pageNo列表 
	 */
	public List<Long> getSlider(int count) {
		int halfSize = count / 2;
		long totalPage = getTotalPages();

		long startPageNumber = Math.max(pageNo - halfSize, 1);
		long endPageNumber = Math.min(startPageNumber + count - 1, totalPage);

		if (endPageNumber - startPageNumber < count) {
			startPageNumber = Math.max(endPageNumber - count, 1);
		}

		List<Long> result = new ArrayList<Long>();
		for (long i = startPageNumber; i <= endPageNumber; i++) {
			result.add(new Long(i));
		}
		return result;
	}
}
