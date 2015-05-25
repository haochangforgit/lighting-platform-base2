package com.lighting.platform.base;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.NamedThreadLocal;

/**
 * 当前线程绑定的资源<p>
 * 可以设置一些与当前线程绑定的属性. 在生命周期结束后会自动清除<p>
 * 注意事项:
 * <li> 绑定开始于请求到来时 </li>
 * <li> 绑定自动结束于视图渲染之后(或异常处理之后)</li>
 * <li> 对于非请求触发的线程(如内部定时器产生的线程), 会抛出 IllegalStateException 异常</li>
 * 
 * @author zhaoyk10@gmail.com
 *
 */
public class ContextHolder {

	private static final ThreadLocal<ContextHolder> threadLocal = new NamedThreadLocal<ContextHolder>("current TheadBindHolder");

	private HttpServletRequest request;

	private HttpServletResponse response;
	
	public ContextHolder(HttpServletRequest request, HttpServletResponse response)
	{
		this.request = request;
		this.response = response;
	}

	public static void setCurrent(ContextHolder tbh)
	{
		threadLocal.set(tbh);
	}

	public static void removeCurrent()
	{
		threadLocal.remove();
	}

	public static ContextHolder getCurrent()
	{
		ContextHolder holder = threadLocal.get();
		return holder;
	}
	
	public HttpServletRequest getRequest()
	{
		return request;
	}

	public HttpServletResponse getResponse()
	{
		return response;
	}

}
