package com.lighting.platform.base.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * 用户实体bean
 * @author xieyulin
 * @author changhao
 *
 */
@Entity
@Table(name = "c_user")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "com.lighting.platform.base.entity.User")
public class User implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * 账号使用状态
	 */
	public static enum UseStatus {
		/**
		 *  启用
		 */
		USE,
		/**
		 *  停用
		 */
		STOP

	}
	
	@Id
	//@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "c_user_id_seq")
	//@SequenceGenerator(name = "c_user_id_seq", sequenceName = "c_user_id_seq", allocationSize = 1)
	private String id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "password", nullable = false)
	private String password;//密码
	
	@Enumerated(EnumType.STRING)
	@Column(name = "use_status", nullable = false)
	private UseStatus useStatus = UseStatus.USE;//使用状态
	
	@Column(name = "email")
	private String email;//密码找回邮箱
	
	@Column(name = "end_condition")
	private String endCondition;
	
	@Column(name = "end_time")
	private Date endTime;//停止使用时间
	
	@Column(name = "location", nullable = false, updatable = false)
	private String location;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UseStatus getUseStatus() {
		return useStatus;
	}

	public void setUseStatus(UseStatus useStatus) {
		this.useStatus = useStatus;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEndCondition() {
		return endCondition;
	}

	public void setEndCondition(String endCondition) {
		this.endCondition = endCondition;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.getId() != null)
				return false;
		} else if (!id.equals(other.getId()))
			return false;
		return true;
	}
}
