package com.lighting.platform.base.entity;


public interface IUserDetails {
	
	//public Set<String> getAuthorities();
	
	//public Set<String> getOptCodes(String uri);
	
	public Long getUserId();
	
	public String getToken();
	
	public String getPersonId();
	
	public String getName();
	
}
