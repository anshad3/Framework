package com.base.pojos;

import java.util.Map;

public class ElementReposPojo {
	
	public String reposId = null;
	public LocatorEnum locator = null;
	public String value = null;
	public String desc = null;

	public ElementReposPojo(Map<String, String> reposMap) {
		reposId = reposMap.get("ID");
		String strLocator = reposMap.get("Locator");
		locator = LocatorEnum.getLocatorEnumForString(strLocator);
		value = reposMap.get("Value");
		desc = reposMap.get("Description");
	}

	public String getReposId() {
		return reposId;
	}

	public void setReposId(String reposId) {
		this.reposId = reposId;
	}

	public LocatorEnum getLocator() {
		return locator;
	}

	public void setLocator(LocatorEnum locator) {
		this.locator = locator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	

}
