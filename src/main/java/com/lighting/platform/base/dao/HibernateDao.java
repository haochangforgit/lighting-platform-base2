package com.lighting.platform.base.dao;

/**
 * Copyright (c) 2005-2011 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 
 * $Id: HibernateDao.java 1486 2011-02-12 15:50:51Z calvinxiu $
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
//import org.hihbernate.impl.CriteriaImpl;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

import com.lighting.platform.base.utils.AssertUtils;
import com.lighting.platform.base.utils.ReflectionUtils;

/**
 * 封装SpringSide扩展功能的Hibernat DAO泛型基类.
 * 
 * 扩展功能包括分页查询,按属性过滤条件列表查询.
 * 
 * @param <T> DAO操作的对象类型
 * @param <ID> 主键类型
 * 
 * @author changhao
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class HibernateDao<T, ID extends Serializable> extends SimpleHibernateDao<T, ID> {
	/**
	 * 通过子类的泛型定义取得对象类型Class.
	 * eg.
	 * public class UserDao extends HibernateDao<User, Long>{
	 * }
	 */
	public HibernateDao() {
		super();
	}

	public HibernateDao(Class<T> entityClass) {
		super(entityClass);
	}

	// -- 分页查询函数 --//

	/**
	 * 分页获取全部对象.
	 */
	public Page<T> getAll(PageConfig pageConfig) {
		return findPage(pageConfig);
	}

	/**
	 * 按HQL分页查询.
	 * 
	 * @param page 分页参数. 注意不支持其中的orderBy参数.
	 * @param hql hql语句.
	 * @param values 数量可变的查询参数,按顺序绑定.
	 * 
	 * @return 分页查询结果, 附带结果列表及所有查询输入参数.
	 */
	public Page<T> findPage(PageConfig pageConfig, final String hql, final Object... values) {
		Page<T> page = new Page<T>(pageConfig);
		
		Query q = createQuery(hql, values);

		long totalCount = countHqlResult(hql, values);
		page.setTotal(totalCount);
		
		setPageParameterToQuery(q, page);

		List result = q.list();
		page.setRows(result);
		return page;
	}
	
	public <K> Page<K> findPage(PageConfig pageConfig, SqlEso eso, Class<K> clazz){
		Page<K> page = new Page<K>(pageConfig);
		Query q = createQuery(eso.getSql(), eso.getParam());
		long totalCount = countHqlResult(eso.getSql(), eso.getParam());
		page.setTotal(totalCount);
		setPageParameterToQuery(q, page);
		q.setResultTransformer(Transformers.aliasToBean(clazz));
		List result = q.list();
		page.setRows(result);
		return page;
	}
	
	public Page<T> findPage(PageConfig pageConfig, SqlEso eso){
		Page<T> page = new Page<T>(pageConfig);
		
		Query q = createQuery(eso);

		long totalCount = countHqlResult(eso);
		page.setTotal(totalCount);
		
		setPageParameterToQuery(q, page);

		List result = q.list();
		page.setRows(result);
		return page;
	}
	/**
	 * 按HQL分页查询.
	 * 
	 * @param page 返回全部结果.
	 * @param hql hql语句.
	 * @param values 数量可变的查询参数,按顺序绑定.
	 * 
	 * @return 分页查询结果, 附带结果列表及所有查询输入参数.
	 */
	public Page<T> findAllPage(PageConfig pageConfig, final String hql, final Object... values) {
		Page<T> page = new Page<T>(pageConfig);
		
		Query q = createQuery(hql, values);
		
		long totalCount = countHqlResult(hql, values);
		page.setTotal(totalCount);
		setPageParameterToQuery(q, page);
		
		List result = q.list();
		page.setRows(result);
		return page;
	}
	
	/**
	 * 按HQL分页查询.
	 * 
	 * @param page 分页参数. 注意不支持其中的orderBy参数.
	 * @param hql hql语句.
	 * @param values 命名参数,按名称绑定.
	 * 
	 * @return 分页查询结果, 附带结果列表及所有查询输入参数.
	 */
	public Page<T> findPage(PageConfig pageConfig, final String hql, final Map<String, ?> values) {
		Page<T> page = new Page<T>(pageConfig);

		Query q = createQuery(hql, values);

		long totalCount = countHqlResult(hql, values);
		page.setTotal(totalCount);

		setPageParameterToQuery(q, page);

		List result = q.list();
		page.setRows(result);
		return page;
	}
	
	private void setParams(Query query, Object[] values){
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
	}
	
	public SQLQuery createSQLQuery(String sql){
		return this.getSession().createSQLQuery(sql);
	}
	
	public <K> Page<K> pageSQLQuery(PageConfig pageConfig, SQLQuery sqlQuery, Object ... values){
		Page<K> page = new Page<K>(pageConfig);
		String totalSql="select count(*) as total from("+sqlQuery.getQueryString()+")";
		Query countQuery = this.getSession().createSQLQuery(totalSql);
		setParams(countQuery, values);
		Number total = (Number)countQuery.uniqueResult();
		if(total != null){
			page.setTotal(total.longValue());
			sqlQuery.setFirstResult(page.getOffset())
					.setMaxResults(page.getPageSize());
			setParams(sqlQuery, values);
			List<K> list = sqlQuery.list();
			page.setRows(list);
		}else{
			page.setTotal(0);
			page.setRows(new ArrayList<K>());
		}
		return page;
	}
	
	
	
	public Page<Map<String, Object>> pageSQLQuery(PageConfig pageConfig, String sql, Object ... values){
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(pageConfig);
		String totalSql="select count(*) as total from("+sql+")";
		Query countQuery = this.getSession().createSQLQuery(totalSql);
		setParams(countQuery, values);
		Number total = (Number)countQuery.uniqueResult();
		if(total != null){
			page.setTotal(total.longValue());
			Query query = this.getSession().createSQLQuery(sql)
			.setFirstResult(page.getOffset())
			.setMaxResults(page.getPageSize())
			.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			setParams(query, values);
			List<Map<String, Object>> list = query.list();
			page.setRows(list);
		}else{
			page.setTotal(0);
			page.setRows(new ArrayList<Map<String, Object>>());
		}
		return page;
	}
	
	/**
	 * 分页查询,将返回的结果组装成map,适用于关联查询
	 * @param pageConfig
	 * @param hsql
	 * @param values
	 * @return
	 */
	
	public Page<Map<String, Object>> PageMapQuery(PageConfig pageConfig, String hsql, Map<String, Object> values){
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(pageConfig);
		long total = countHqlResult(hsql, values);
		page.setTotal(total);
		Query query = this.createQuery(hsql, values)
						  .setFirstResult(page.getOffset())
						  .setMaxResults(page.getPageSize())
						  .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		page.setRows(query.list());
		return page;
	}

	/**
	 * 按Criteria分页查询.
	 * 
	 * @param page 分页参数.
	 * @param criterions 数量可变的Criterion.
	 * 
	 * @return 分页查询结果.附带结果列表及所有查询输入参数.
	 */
	public Page<T> findPage(PageConfig pageConfig, final Criterion... criterions) {

		Page<T> page = new Page<T>(pageConfig);
		
		Criteria c = createCriteria(criterions);

		long totalCount = countCriteriaResult(c);
		page.setTotal(totalCount);

		setPageParameterToCriteria(c, page);

		List result = c.list();
		page.setRows(result);
		return page;
	}

	/**
	 * 设置分页参数到Query对象,辅助函数.
	 */
	protected Query setPageParameterToQuery(final Query q, final Page<?> page) {
		AssertUtils.isTrue(page.getPageSize() > 0, "Page Size must larger than zero");

		q.setFirstResult(page.getOffset());
		q.setMaxResults(page.getPageSize());

		return q;
	}

	/**
	 * 设置分页参数到Criteria对象,辅助函数.
	 */
	protected Criteria setPageParameterToCriteria(final Criteria c, final Page<T> page) {
		AssertUtils.isTrue(page.getPageSize() > 0, "Page Size must larger than zero");

		c.setFirstResult(page.getOffset());
		c.setMaxResults(page.getPageSize());

		if (page.isOrderBySetted()) {
			String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
			String[] orderArray = StringUtils.split(page.getOrder(), ',');

			AssertUtils.isTrue(orderByArray.length == orderArray.length, "分页多重排序参数中,排序字段与排序方向的个数不相等");

			for (int i = 0; i < orderByArray.length; i++) {
				if (PageConfig.ASC.equals(orderArray[i])) {
					c.addOrder(Order.asc(orderByArray[i]));
				} else {
					c.addOrder(Order.desc(orderByArray[i]));
				}
			}
		}
		return c;
	}

	/**
	 * 执行count查询获得本次Hql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 */
	protected long countHqlResult(final String hql, final Object... values) {
		String countHql = prepareCountHql(hql);

		try {
			Long count = findUnique(countHql, values);
			return count;
		} catch (Exception e) {
			throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
		}
	}

	protected long countHqlResult(final SqlEso eso){
		String countHql = prepareCountHql(eso.getSql());
		try {
			Long count = findUnique(countHql, eso.getParam());
			return count;
		} catch (Exception e) {
			throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
		}
	}
	
	/**
	 * 执行count查询获得本次Hql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 */
	protected long countHqlResult(final String hql, final Map<String, ?> values) {
		String countHql = prepareCountHql(hql);

		try {
			Long count = findUnique(countHql, values);
			return count;
		} catch (Exception e) {
			throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
		}
	}

	private String prepareCountHql(String orgHql) {
		String fromHql = orgHql;
		// select子句与order by子句会影响count查询,进行简单的排除.
		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");

		String countHql = "select count(*) " + fromHql;
		return countHql;
	}

	/**
	 * 执行count查询获得本次Criteria查询所能获得的对象总数.
	 */
	protected long countCriteriaResult(final Criteria c) {
		org.hibernate.internal.CriteriaImpl impl = (CriteriaImpl) c;

		// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();

		List<CriteriaImpl.OrderEntry> orderEntries = null;
		try {
			orderEntries = (List) ReflectionUtils.getFieldValue(impl, "orderEntries");
			ReflectionUtils.setFieldValue(impl, "orderEntries", new ArrayList());
		} catch (Exception e) {
			logger.error("不可能抛出的异常:" + e.getMessage());
		}

		// 执行Count查询
		Long totalCountObject = (Long) c.setProjection(Projections.rowCount()).uniqueResult();
		long totalCount = (totalCountObject != null) ? totalCountObject : 0;

		// 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
		c.setProjection(projection);

		if (projection == null) {
			c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (transformer != null) {
			c.setResultTransformer(transformer);
		}
		try {
			ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
		} catch (Exception e) {
			logger.error("不可能抛出的异常:" + e.getMessage());
		}

		return totalCount;
	}

	// ------------------------------
	/**
	 * 页数是从1开始的, 记录是从0开始的<p>
	 * 使用例子:<p>
	 * 1. mydao.findByHQuery(sql, arg1, arg2).fetch(); 取出所有记录，与 mydao.find(sql, arg1, arg2) 功能相同
	 * 2. mydao.findByHQuery(sql, arg1, arg2).fetch(100); 取出前100条记录, 即: [0-99]
	 * 3. mydao.findByHQuery(sql, arg1, arg2).from(10).fetch(5); 取出从第10条开始的共5条记录. 即: [10-14]
	 * 4. mydao.findByHQuery(sql, arg1, arg2).fetch(3, 15); 取出 第三页，每页15个 的记录. 即: [30-44]
	 * 5. mydao.findByHQuery(sql, arg1, arg2).from(10).fetch(3, 15); 取出从第10条开始的 第三页，每页15个 的记录, 即: [40-54]
	 */
	public HQuery findByHQuery(String query, Object... params) {
		Query q = createQuery(query, params);
		return new HQuery(query, q);
	}

	public HQuery findByHQuery(Query q) {
		return new HQuery(q);
	}

	/**
	 * 一个方便的中间类
	 */
	public static class HQuery {
		public Query query;
		public String sq;

		private int from = 0;

		public HQuery(String sq, Query query) {
			this.query = query;
			this.sq = sq;
		}

		public HQuery(Query query) {
			this.query = query;
			this.sq = null;
		}

		public <T> T first() {
			return (T) query.uniqueResult();
		}

		public HQuery bind(String name, Object param) {
			if (param.getClass().isArray()) {
				param = Arrays.asList((Object[]) param);
			}
			query.setParameter(name, param);
			return this;
		}

		public HQuery bind(int position, Object param) {
			if (param.getClass().isArray()) {
				param = Arrays.asList((Object[]) param);
			}
			query.setParameter(position, param);
			return this;
		}

		/**
		 * Retrieve all results of the query
		 * @return A list of entities
		 */
		public <T> List<T> fetch() {
			return query.list();
		}

		public <T> List<T> fetch(int max) {
			return query.setMaxResults(max).list();
		}

		public <T> HQuery from(int position) {
			query.setFirstResult(position);
			this.from = position;
			return this;
		}

		public <T> List<T> fetch(int page, int length) {
			if (page < 1) {
				page = 1;
			}
			this.from += (page - 1) * length;
			query.setFirstResult(this.from);
			query.setMaxResults(length);
			return query.list();
		}
	}
}
