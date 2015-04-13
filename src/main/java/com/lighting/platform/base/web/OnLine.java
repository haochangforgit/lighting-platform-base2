package com.lighting.platform.base.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.lighting.platform.base.ContextHolder;
import com.lighting.platform.base.entity.IUserDetails;




public class OnLine
{
	
	private static final String USER_IN_SESSION_KEY = IUserDetails.class.getName();
	
	public static IUserDetails getCurrentUserDetails()
	{
		HttpServletRequest request = ContextHolder.getCurrent().getRequest();
		return getUserDetails(request);
	}
	
	public static IUserDetails getUserDetails(HttpServletRequest request)
	{
		HttpSession session = request.getSession();
		if(session != null){
			return (IUserDetails)session.getAttribute(USER_IN_SESSION_KEY);
		}
		return null;
	}
	
	public static void login(IUserDetails userDetails)
	{
		HttpServletRequest request = ContextHolder.getCurrent().getRequest();
		HttpSession session = request.getSession();
		session.setAttribute(USER_IN_SESSION_KEY, userDetails);
	}
	
	public static void logout()
	{
		HttpServletRequest request = ContextHolder.getCurrent().getRequest();
		HttpSession session = request.getSession();
		session.invalidate();
	}
	
	
}
