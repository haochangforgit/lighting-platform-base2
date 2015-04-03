package com.lighting.platform.base.dao;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;;


/**
 * 分页条件
 * @author xieyulin
 * @author changhao
 */
public class PageConfig implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int pageNo = 1;
	public int pageSize = 10;
	public String orderBy = null;//排序字段,多个排序字段时用','分隔.
	public String order = null; //排序方向 ASC/DESC
	 
	//-- 公共变量 --//
	public static final String ASC = "asc";
	public static final String DESC = "desc";
	
	/**
	 * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
	 * @param pageNo
	 * @return
	 */
	public PageConfig setPageNo(int pageNo) {
		if (pageNo < 1) {
			this.pageNo = 1;
		} else {
			this.pageNo = pageNo;
		}
		
		return this;
	}
	/**
	 * 设置每页的记录数量.
	 * @param pageSize
	 */
	public PageConfig setPageSize(int pageSize) {
		if (pageSize <= 0) {
			this.pageSize = 10;
		}else{
			this.pageSize = pageSize;
		}
		return this;
	}
	
	public PageConfig setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public PageConfig setOrder(String order) {
		this.order = order;
		return this;
	}
	
	private PageConfig(){
		
	}
	private PageConfig(int pageNo, int pageSize){
		this.setPageNo(pageNo).setPageSize(pageSize);
	}
	
	public static PageConfig newInstance(){
		return new PageConfig();
	}
	
	public static PageConfig newInstance(int pageNo, int pageSize){
		return new PageConfig(pageNo, pageSize);
	}
	
	public static PageConfig createPageConfig(HttpServletRequest request){
		String pageSizeStr=request.getParameter("rows");
		String pageNoStr=request.getParameter("page");
		String sort = request.getParameter("sort");
		String order = request.getParameter("order");
		int pageSize=Integer.parseInt(pageSizeStr==null?"20":pageSizeStr);
		int pageNo=Integer.parseInt(pageSizeStr==null?"1":pageNoStr);
		PageConfig pageConfig = PageConfig.newInstance(pageNo, pageSize);
		if(!StringUtils.isBlank(sort) && !StringUtils.isBlank(order)){
			pageConfig.setOrder(order);
			pageConfig.setOrderBy(sort);
		}
		return pageConfig;
	}
	
}
