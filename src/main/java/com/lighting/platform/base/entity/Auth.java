package com.lighting.platform.base.entity;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "sys_auth")
public class Auth extends CreateAndModify implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	private String id;
	
	
	private String authName;
	
    /***
     * 多对多关联 用户方
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "auths", fetch = FetchType.LAZY)
    private Set<Role> roles = new LinkedHashSet<Role>();
    
    /***
     * 多对多关联 菜单方
     */
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(
			name = "sys_auth_menu", 
			joinColumns = { @JoinColumn(name = "auth_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "menu_id") }
			)
    private Set<Menu> menus = new LinkedHashSet<Menu>(); 
	

	public Set<Menu> getMenus() {
		return menus;
	}


	public void setMenus(Set<Menu> menus) {
		this.menus = menus;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getAuthName() {
		return authName;
	}


	public void setAuthName(String authName) {
		this.authName = authName;
	}
	
}
