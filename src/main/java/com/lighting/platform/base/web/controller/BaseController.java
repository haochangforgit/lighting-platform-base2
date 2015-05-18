package com.lighting.platform.base.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.lighting.platform.base.entity.CreateAndModify;
import com.lighting.platform.base.web.OnLine;
import com.lighting.platform.base.dao.PageConfig;

/***
 * 基础Controller类
 * @author changhao
 *
 */
public abstract class BaseController
{
	
	private Log _logger = LogFactory.getLog(this.getClass());
	
	protected DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
	
	protected final static String SUCCESS = "/success/index";
	
	protected final static String ERROR = "/error/index";
	
	protected void setError(ModelMap model, Exception e){
		setError(model, e.getMessage());
	}
	
	/**
	 * 设置返回值,适用于form的ajax提交
	 * @param model
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	protected void setResponse(ModelMap model, String key, Object value){
		Map<String, Object> resp = (Map<String, Object>)model.get("SYS_RESP");
		if(resp == null){
			resp = new HashMap<String, Object>();
			model.put("SYS_RESP", resp);
		}
		resp.put(key, value);
	}
	
	protected void setError(ModelMap model, String error){
		if(error == null){
			model.put("SYS_ERROR", "提交出错");
		}else{
			model.put("SYS_ERROR", error);
		}
		_logger.error(error);
	}
	
	protected void setCreater(CreateAndModify entity)
	{
		entity.setUserId(OnLine.getCurrentUserDetails().getUserId());
	}
	
	protected void setLastModify(CreateAndModify entity){
		entity.setLastModifyTime(new Date());
		entity.setLastModifyUserId(OnLine.getCurrentUserDetails().getUserId());
	}

	protected PageConfig createPageConfig(HttpServletRequest request)
	{
		String pageSizeStr=request.getParameter("rows");
		String pageNoStr=request.getParameter("page");
		String sort = request.getParameter("sort");
		String order = request.getParameter("order");
		int pageSize=Integer.parseInt(pageSizeStr==null?"20":pageSizeStr);
		int pageNo=Integer.parseInt(pageNoStr==null?"1":pageNoStr);
		PageConfig pageConfig = PageConfig.newInstance(pageNo, pageSize);
		if(!StringUtils.isBlank(sort) && !StringUtils.isBlank(order)){
			pageConfig.setOrder(order);
			pageConfig.setOrderBy(sort);
		}
		return pageConfig;
	}
	
	@InitBinder 
	public void initBinder(WebDataBinder binder)
	{ 
		dateFormat.setLenient(true); 
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true)); 
	}
	
}
