package com.lighting.platform.base.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * 用户实体bean
 * @author changhao
 *
 */
@Entity
@Table(name = "sys_user")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "com.lighting.platform.base.entity.User")
public class User extends CreateAndModify implements Serializable
{
	/**
	 * 账号使用状态
	 */
	public static enum UseStatu
	{
		/**
		 *  启用
		 */
		ENABLE,
		/**
		 *  停用
		 */
		DISABLE
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	private String id;
	
	//登录名
	private String loginName;
	
	
	//密码
	private String password;
	
	
	//真实姓名
	private String realName;
	
	
	//使用状态
	@Enumerated(EnumType.STRING)
	private UseStatu useStatu = UseStatu.ENABLE;
	
	
	//密码找回邮箱
	private String email;
	
	
	//停止原因
	private String endReason;
	
	
	//停止使用时间
	private Date endTime;
	
	
	//多对多关联 角色方
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(
			name = "sys_user_role", 
			joinColumns = { @JoinColumn(name = "user_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "role_id") }
			)
	//@OrderBy("id")
	private Set<Role> roles = new LinkedHashSet<Role>();
	
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	public UseStatu getUseStatu() {
		return useStatu;
	}

	public void setUseStatu(UseStatu useStatu) {
		this.useStatu = useStatu;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEndReason() {
		return endReason;
	}

	public void setEndReason(String endReason) {
		this.endReason = endReason;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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
