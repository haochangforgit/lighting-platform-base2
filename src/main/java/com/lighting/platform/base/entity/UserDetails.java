package com.lighting.platform.base.entity;


/***
 * 当前在线用户实例
 * @author changhao
 *
 */
public class UserDetails{
	
	private User user;

	public UserDetails(User user)
	{
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	

}
