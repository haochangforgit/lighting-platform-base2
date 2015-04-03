package com.lighting.platform.base.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 基本创建修改信息记录类
 * @author xieyulin
 * @author changhao
 *
 */
@MappedSuperclass
public abstract class CreateAndModify implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "user_id",updatable = false)
	private Long userId;
	
	@Column(name = "create_time", updatable=false)
	private Date createTime = new Date();
	
	@Column(name = "last_modify_user_id")
	private Long lastModifyUserId;
	
	@Column(name = "last_modify_time")
	private Date lastModifyTime = new Date();

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getLastModifyUserId() {
		return lastModifyUserId;
	}

	public void setLastModifyUserId(Long lastModifyUserId) {
		this.lastModifyUserId = lastModifyUserId;
	}

	public Date getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	

}
