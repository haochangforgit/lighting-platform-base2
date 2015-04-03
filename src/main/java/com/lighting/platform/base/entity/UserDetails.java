package com.lighting.platform.base.entity;


/***
 * 当前在线用户实例
 * @author changhao
 *
 */
public class UserDetails implements IUserDetails {
	
	private User user;
	private String token;
	/*private Set<String> authorities;
	private Map<String, Set<String>> optCodes;

	public UserDetails(User user, Set<String> authorities, Map<String, Set<String>> opts){
		this.user = user;
		this.authorities = authorities;
		optCodes = opts;
	}
	
	public Set<String> getAuthorities() {
		return authorities;
	}*/

	public UserDetails(User user, String token)
	{
		this.user = user;
		this.token = token;
	}
	public Long getUserId()
	{
		return user.getId();
	}
	public String getPersonId()
	{
		return user.getPersonId();
	}
	
	public String getToken()
	{
		return token;
	}
	
	public String getName()
	{
		return user.getName();
	}


	/*public Set<String> getOptCodes(String uri) {
		Set<String> set = optCodes.get(uri);
		return set;
	}*/
}
