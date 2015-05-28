package com.lighting.platform.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

/***
 * 用于在spring 自动装配bean前获取bean
 * @author changhao
 *
 */
public class SpringContext
{
	private static Log logger = LogFactory.getLog(SpringContext.class);
	
	private static ApplicationContext applicationContext = null;
	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name)
	{
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(Class<T> requiredType)
	{
		return applicationContext.getBean(requiredType);
	}
	
	public static void setApplicationContext(ApplicationContext applicationContext)
	{
		logger.debug("注入applicationContext");
		SpringContext.applicationContext = applicationContext;
	}
	
}