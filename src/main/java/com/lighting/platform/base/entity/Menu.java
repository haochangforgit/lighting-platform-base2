package com.lighting.platform.base.entity;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "sys_menu")
public class Menu extends CreateAndModify implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	private String id;	  //菜单Id
	
	
	private String menuName;	//菜单名称
	
	
	private String supMenuId;   //父级菜单Id
	
	
	private String supMenuName; //父级菜单名
	
	
	private String menuUrl;	 //url
	
	
	private String menuFlag;	//是否可用
	
	
	private String orderNo;	 //排序
	
	
	private String isLeaf;	  //是否叶子节点
	
	
    /***
     * 多对多关联 用户方
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "menus", fetch = FetchType.LAZY)
    private Set<Auth> users = new LinkedHashSet<Auth>();


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getMenuName() {
		return menuName;
	}


	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}


	public String getSupMenuId() {
		return supMenuId;
	}


	public void setSupMenuId(String supMenuId) {
		this.supMenuId = supMenuId;
	}


	public String getSupMenuName() {
		return supMenuName;
	}


	public void setSupMenuName(String supMenuName) {
		this.supMenuName = supMenuName;
	}


	public String getMenuUrl() {
		return menuUrl;
	}


	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}


	public String getMenuFlag() {
		return menuFlag;
	}


	public void setMenuFlag(String menuFlag) {
		this.menuFlag = menuFlag;
	}


	public String getOrderNo() {
		return orderNo;
	}


	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}


	public String getIsLeaf() {
		return isLeaf;
	}


	public void setIsLeaf(String isLeaf) {
		this.isLeaf = isLeaf;
	}
	
	
	

}
