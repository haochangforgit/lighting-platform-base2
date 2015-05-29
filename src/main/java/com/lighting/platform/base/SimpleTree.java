package com.lighting.platform.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/***
 * 简单树实现
 * @author changhao
 *
 * @param <K>
 * @param <V>
 */
public class SimpleTree<K extends Serializable, V extends Serializable> implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private V attributes;
	
	private K parentId;
	
	private K id;
	
	private String text;
	
	protected String state = "";

	public String getState()
	{
		return state;
	}
	public void setStateClosed()
	{
		this.state = "closed";
	}
	public void setStateOpen()
	{
		this.state = "";
	}
	
	private String iconSkin;

	private List<SimpleTree<K, V>> children;
	
	transient private Comparator<SimpleTree<K, V>> comparetor;
	
	public SimpleTree(V attributes, K parentId, Comparator<SimpleTree<K, V>> comparetor)
	{
		this(attributes, parentId);
		this.comparetor = comparetor;
	}
	
	public SimpleTree(V attributes, K parentId)
	{
		this.attributes = attributes;
		this.parentId = parentId;
		children = new ArrayList<SimpleTree<K, V>>();
	}
	
	public void setInfo(K id, String text, String iconSkin)
	{
		this.setId(id);
		this.setText(text);
		this.setIconSkin(iconSkin);
	}
	
	public void setInfo(K id, String name)
	{
		this.setInfo(id, name, null);
	}
	
	public K getId()
	{
		return id;
	}

	public void setId(K id)
	{
		this.id = id;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}
	
	public String geticonSkin()
	{
		return iconSkin;
	}

	public void setIconSkin(String iconSkin)
	{
		this.iconSkin = iconSkin;
	}
	
	public V getAttributes()
	{
		return attributes;
	}
	
	public K getParentId()
	{
		return parentId;
	}
	
	public List<SimpleTree<K, V>> getChildren()
	{
		return children;
	}
	
	public void addChild(SimpleTree<K, V> child)
	{
		this.addChild(child, true);
	}
	
	public void addChild(SimpleTree<K, V> child, boolean sortFlag)
	{
		this.children.add(child);
		if(sortFlag)
		{
			sort();
		}
	}
	
	public void addChildren(List<SimpleTree<K, V>> children)
	{
		this.children.addAll(children);
		sort();
	}
	
	private void sort()
	{
		if(comparetor != null)
		{
			Collections.sort(children, comparetor);
		}
	}
}
