package com.lighting.platform.base.dao;

/***
 * sql字符串与参数构造类
 * @author changhao
 *
 */
public class SqlEso {
	private String sql;
	private Object[] param;
	protected SqlEso(String sql,Object[] param){
		this.sql=sql;
		this.param=param;
	}
	public String getSql() {
		return sql;
	}
	public Object[] getParam() {
		return param;
	}
	
}
