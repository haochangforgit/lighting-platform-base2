package com.lighting.platform.base.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.lighting.platform.base.ContextHolder;
import com.lighting.platform.base.entity.User;
import com.lighting.platform.base.entity.in.IUserDetails;



/**
 * 用户在线功能类
 * @author changhao
 *
 */
public class OnLine
{
	
	/*** 当前登录用户session key **/
	private static final String CURRENT_USER_KEY = IUserDetails.class.getName();
	
	
	/**
	 * 获取当前用户 from request's session
	 * @return
	 */
	public static User getCurrentUser()
	{
		HttpServletRequest request = ContextHolder.getCurrent().getRequest();
		return getCurrentUser(request);
	}
	
	/**
	 * 获取当前用户 from request's session
	 * @param request
	 * @return
	 */
	public static User getCurrentUser(HttpServletRequest request)
	{
		HttpSession session = request.getSession();
		if(session != null)
			return (User)session.getAttribute(CURRENT_USER_KEY);
		
		return null;
	}
	
	/**
	 * 登录当前用户，使当前用户为在线状态
	 * @param user
	 */
	public static void login(User user)
	{
		HttpServletRequest request = ContextHolder.getCurrent().getRequest();
		
		HttpSession session = request.getSession();
		
		session.setAttribute(CURRENT_USER_KEY, user);
	}
	
	
	/***
	 * 杀死session 登出
	 */
	public static void logout()
	{
		HttpServletRequest request = ContextHolder.getCurrent().getRequest();
		
		HttpSession session = request.getSession();
		
		session.invalidate();
	}
	
	
}
