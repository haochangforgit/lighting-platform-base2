package com.lighting.platform.base.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.lighting.platform.base.ContextHolder;
import com.lighting.platform.base.entity.User;


public class RequestInterceptorAdapter extends HandlerInterceptorAdapter
{

	private static Log logger = LogFactory.getLog(RequestInterceptorAdapter.class);
	
	/**
	 * 获取请求的URI
	 * @param request
	 * @return
	 */
	private String getRequestURL(HttpServletRequest request) 
	{
		String ctx = request.getContextPath();
		String uri = request.getRequestURI();
		return uri.substring(ctx.length());
	}

	@Override
	public void postHandle(HttpServletRequest request,HttpServletResponse response, Object handler,	ModelAndView modelAndView) throws Exception
	{
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request,HttpServletResponse response, Object handler, Exception ex) throws Exception
	{
		ContextHolder.removeCurrent();
		if(ex != null)
		{
			ex.printStackTrace();
			logger.error(ex.getMessage());
		}
		super.afterCompletion(request, response, handler, ex);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		ContextHolder ch = new ContextHolder(request, response);
		ContextHolder.setCurrent(ch);
		
		User currentUser = OnLine.getCurrentUser();
		
		if(currentUser != null)
		{
			this.getRequestURL(request);
			//String uri = this.getRequestURL(request);
			//Set<String> optCodes = Context.getAuthService().getOptCodes(Context.getSystemId(), userDetails.getUserId(), uri);
			//ToolsUtil.setTools(optCodes, request);
		}
		return super.preHandle(request, response, handler);
	}

}
