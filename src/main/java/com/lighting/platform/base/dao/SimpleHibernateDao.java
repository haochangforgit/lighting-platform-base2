package com.lighting.platform.base.dao;

/**
 * Copyright (c) 2005-20101 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 
 * $Id: SimpleHibernateDao.java 1486 2011-02-12 15:50:51Z calvinxiu $
 */

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import com.lighting.platform.base.utils.AssertUtils;
import com.lighting.platform.base.utils.ReflectionUtils;


/**
 * 封装Hibernate原生API的DAO泛型基类.
 * 
 * 参考Spring2.5自带的Petlinc例子, 取消了HibernateTemplate, 直接使用Hibernate原生API.
 * 
 * @param <T> DAO操作的对象类型
 * @param <ID> 主键类型
 * 
 * @author changhao
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SimpleHibernateDao<T, ID extends Serializable> {

	protected Log logger = LogFactory.getLog(getClass());

	protected SessionFactory sessionFactory;

	protected Class<T> entityClass;

	/**
	 * 通过子类的泛型定义取得对象类型Class.
	 * eg.
	 * public class UserDao extends SimpleHibernateDao<User, Long>
	 */
	public SimpleHibernateDao() {
		this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
	}

	public SimpleHibernateDao(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * 取得sessionFactory.
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * 新建对象，可以无视对象本身的ID生成策略,手动设置ID,存入数据库
	 * 注意此方法仅仅是新建对象
	 * @param t
	 */
	public void insertDIYID(final T t) {
		getSession().replicate(t, ReplicationMode.EXCEPTION);
	}

	/**
	 * 采用@Autowired按类型注入SessionFactory, 当有多个SesionFactory的时候在子类重载本函数.
	 */
	@Autowired
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * 取得当前Session.
	 */
	public Session getSession() {
		return sessionFactory.openSession();
	}

	/**
	 * 使用数据库的悲观锁, 通常是 select ... for update
	 * 锁是完全排他的, 在事务提交后自动释放. 请谨慎使用
	 */
	public void pessimisticLock(T entity) {
		LockOptions option = new LockOptions(LockMode.PESSIMISTIC_WRITE);
		getSession().buildLockRequest(option).lock(entity);
	}

	/**
	 * 在当前事务完成后执行的动作 (very useful)
	 * @param action
	 */
	/*public void doAfterTransactionCompletion(final Action action) {
		SessionImpl sessionImpl = (SessionImpl) getSession();
		sessionImpl.getActionQueue().registerProcess(new AfterTransactionCompletionProcess() {
			@Override
			public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
				try {
					action.doAction(success);
				} catch (Exception e) {
					logger.error("Error in doAfterTransactionCompletion action", e);
				}
			}
		});
	}*/

	/**
	 * 保存新增或修改的对象.
	 */
	public T save(final T entity) {
		AssertUtils.notNull(entity, "entity不能为空");
		check(entity);
		getSession().saveOrUpdate(entity);
		return entity;
	}
	
	public void save(final List<T> entities){
		for(int i=0; i<entities.size(); i++){
			save(entities.get(i));
		}
	}

	
	public T merge(final T entity) {
		AssertUtils.notNull(entity, "entity不能为空");
		check(entity);
		return (T) getSession().merge(entity);
	}
	/**
	 * 入库前的校验
	 * @param entity
	 */
	protected void check(final T entity){
		
	}

	/**
	 * 删除对象.
	 * 
	 * @param entity 对象必须是session中的对象或含id属性的transient对象.
	 */
	public void delete(final T entity) {
		AssertUtils.notNull(entity, "entity不能为空");
		getSession().delete(entity);
	}

	public void delete(List<T> entitys){
		if(entitys != null && !entitys.isEmpty()){
			for(T entity : entitys){
				delete(entity);
			}
		}
	}
	
	/**
	 * 按id删除对象.
	 */
	public void delete(final ID id) {
		AssertUtils.notNull(id, "id不能为空");
		delete(get(id));
	}

	/**
	 * 不使用load, 改为get
	 */
	
	public T get(final ID id) {
		AssertUtils.notNull(id, "id不能为空");
		return (T) getSession().get(entityClass, id);
	}

	/**
	 * 按id列表获取对象列表.
	 */
	public List<T> get(final Collection<ID> ids) {
		return find(Restrictions.in(getIdName(), ids));
	}

	/**
	 *	获取全部对象.
	 */
	public List<T> getAll() {
		return find();
	}

	/**
	 *	获取全部对象, 支持按属性行序.
	 */
	
	public List<T> getAll(String orderByProperty, boolean isAsc) {
		Criteria c = createCriteria();
		if (isAsc) {
			c.addOrder(Order.asc(orderByProperty));
		} else {
			c.addOrder(Order.desc(orderByProperty));
		}
		return c.list();
	}

	/**
	 * 按属性查找对象列表, 匹配方式为相等.
	 */
	public List<T> findBy(final String propertyName, final Object value) {
		AssertUtils.hasText(propertyName, "propertyName不能为空");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return find(criterion);
	}

	/**
	 * 按属性查找唯一对象, 匹配方式为相等.
	 */
	
	public T findUniqueBy(final String propertyName, final Object value) {
		AssertUtils.hasText(propertyName, "propertyName不能为空");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return (T) createCriteria(criterion).uniqueResult();
	}

	/**
	 * 按HQL查询对象列表.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 */
	
	public <X> List<X> find(final String hql, final Object... values) {
		return createQuery(hql, values).list();
	}
	/**
	 * 按HQL查询对象列表.
	 * @param <X>
	 * @param hql 
	 * @param clazz 转换对象
	 * @param values
	 * @return
	 */
	
	public <X> List<X> find(final String hql, Class<X> clazz, final Object... values) {
		return createQuery(hql, values)
		.setResultTransformer(Transformers.aliasToBean(clazz)).list();
	}

	/**
	 * 按HQL查询对象列表.
	 * 
	 * @param values 命名参数,按名称绑定.
	 */
	
	public <X> List<X> find(final String hql, final Map<String, ?> values) {
		return createQuery(hql, values).list();
	}
	
	public <X> List<X> find(final SqlEso eso) {
		return createQuery(eso.getSql(), eso.getParam()).list();
	}
	public <X> List<X> find(final SqlEso eso, Class<X> clazz) {
		return find(eso.getSql(), eso.getParam(), clazz);
	}
	
	/**
	 * 按HQL查询唯一对象.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 */
	
	public <X> X findUnique(final String hql, final Object... values) {
		return (X) createQuery(hql, values).uniqueResult();
	}

	/**
	 * 按HQL查询唯一对象.
	 * 
	 * @param values 命名参数,按名称绑定.
	 */
	
	public <X> X findUnique(final String hql, final Map<String, ?> values) {
		return (X) createQuery(hql, values).uniqueResult();
	}

	/**
	 * 执行HQL进行批量修改/删除操作.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 * @return 更新记录数.
	 */
	public int batchExecute(final String hql, final Object... values) {
		return createQuery(hql, values).executeUpdate();
	}

	/**
	 * 执行HQL进行批量修改/删除操作.
	 * 
	 * @param values 命名参数,按名称绑定.
	 * @return 更新记录数.
	 */
	public int batchExecute(final String hql, final Map<String, ?> values) {
		return createQuery(hql, values).executeUpdate();
	}


	/**
	 * 按Criteria查询对象列表.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	
	public List<T> find(final Criterion... criterions) {
		return createCriteria(criterions).list();
	}

	/**
	 * 按Criteria查询唯一对象.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	
	public T findUnique(final Criterion... criterions) {
		return (T) createCriteria(criterions).uniqueResult();
	}

	/**
	 * 初始化对象.
	 * 使用load()方法得到的仅是对象Proxy, 在传到View层前需要进行初始化.
	 * 如果传入entity, 则只初始化entity的直接属性,但不会初始化延迟加载的关联集合和属性.
	 * 如需初始化关联属性,需执行:
	 * Hibernate.initialize(user.getRoles())，初始化User的直接属性和关联集合.
	 * Hibernate.initialize(user.getDescription())，初始化User的直接属性和延迟加载的Description属性.
	 */
	public void initProxyObject(Object proxy) {
		Hibernate.initialize(proxy);
	}

	/**
	 * Flush当前Session.
	 */
	public void flush() {
		getSession().flush();
	}

	/**
	 * 清除当前session中未保存的修改
	 */
	public void refresh(T obj) {
		getSession().refresh(obj);
	}


	/**
	 * 取得对象的主键名.
	 */
	public String getIdName() {
		ClassMetadata meta = getSessionFactory().getClassMetadata(entityClass);
		return meta.getIdentifierPropertyName();
	}

	/**
	 * 判断对象的属性值在数据库内是否唯一.
	 * 
	 * 在修改对象的情景下,如果属性新修改的值(value)等于属性原来的值(orgValue)则不作比较.
	 */
	
	public boolean isPropertyUnique(final String propertyName, final Object newValue, final Object oldValue) {
		// 对于数据库中 已经存在重复值 时的判断有问题
		if (newValue == null || newValue.equals(oldValue)) {
			return true;
		}
		List list = findBy(propertyName, newValue);
		return list.isEmpty();
	}
	
	public Number queryForNumber(final String hsql, Object ... params){
		Query query = this.createQuery(hsql, params);
		return (Number)query.uniqueResult();
	}

	/**
	 * 获得下一个序列值
	 * @param seqName 序列名
	 * @return
	 */
	public Number getNextSeqVal(String seqName) {
		AssertUtils.hasText(seqName, "序列名不能为空");
		return (Number)this.getSession().createSQLQuery("select " + seqName + ".nextval from dual").uniqueResult();
	}
	
	protected boolean cacheable(){
		return false;
	}
	
	/**
	 * 为Criteria添加distinct transformer.
	 * 预加载关联对象的HQL会引起主对象重复, 需要进行distinct处理.
	 */
	public Criteria distinct(Criteria criteria) {
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return criteria.setCacheable(cacheable());
	}
	/**
	 * 为Query添加distinct transformer.
	 * 预加载关联对象的HQL会引起主对象重复, 需要进行distinct处理.
	 */
	public Query distinct(Query query) {
		query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return query.setCacheable(cacheable());
	}
	/**
	 * 根据Criterion条件创建Criteria.
	 * 与find()函数可进行更加灵活的操作.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	public Criteria createCriteria(final Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria.setCacheable(cacheable());
	}
	/**
	 * 根据查询HQL与参数列表创建Query对象.
	 * 与find()函数可进行更加灵活的操作.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 */
	public Query createQuery(final String queryString, final Object... values) {
		AssertUtils.hasText(queryString, "queryString不能为空");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query.setCacheable(cacheable());
	}

	/**
	 * 根据查询HQL与参数列表创建Query对象.
	 * 与find()函数可进行更加灵活的操作.
	 * 
	 * @param values 命名参数,按名称绑定.
	 */
	public Query createQuery(final String queryString, final Map<String, ?> values) {
		AssertUtils.hasText(queryString, "queryString不能为空");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			query.setProperties(values);
		}
		return query.setCacheable(cacheable());
	}
	
	public Query createQuery(final SqlEso eso){
		return createQuery(eso.getSql(), eso.getParam());
	}

}