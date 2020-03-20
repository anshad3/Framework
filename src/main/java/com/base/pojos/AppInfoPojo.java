package com.base.pojos;

import org.json.simple.JSONObject;

public class AppInfoPojo {
	
	public String name = null;
	public String appPackage = null;
	public String appActivity = null;
	public String appPath = null;
	
	
	public AppInfoPojo(JSONObject appInfoObj){
		
		name = (String) appInfoObj.get("name");
		appPackage = (String) appInfoObj.get("APP_PACKAGE");
		appActivity = (String) appInfoObj.get("APP_ACTIVITY");
		appPath = (String) appInfoObj.get("path");
		
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAppPackage() {
		return appPackage;
	}

	public void setAppPackage(String appPackage) {
		this.appPackage = appPackage;
	}

	public String getAppActivity() {
		return appActivity;
	}

	public void setAppActivity(String appActivity) {
		this.appActivity = appActivity;
	}

	public String getAppPath() {
		return appPath;
	}

	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}

}
