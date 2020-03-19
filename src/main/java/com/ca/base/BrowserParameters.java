package com.ca.base;

public class BrowserParameters
{
	private String browserName;
	private String profileName;
	private String URL;
	private Long pageLoadTimeOut;
	private Long implicitTimeWait;
	private String driverLocation;
	
	
	public String getDriverLocation()
	{
		return driverLocation;
	}
	public void setDriverLocation(String driverLocation)
	{
		this.driverLocation = driverLocation;
	}
	public String getBrowserName()
	{
		return browserName;
	}
	public void setBrowserName(String browserName)
	{
		this.browserName = browserName;
	}
	public String getProfileName()
	{
		return profileName;
	}
	public void setProfileName(String profileName)
	{
		this.profileName = profileName;
	}
	public String getURL()
	{
		return URL;
	}
	public void setURL(String uRL)
	{
		URL = uRL;
	}
	public Long getPageLoadTimeOut()
	{
		return pageLoadTimeOut;
	}
	public void setPageLoadTimeOut(Long pageLoadTimeOut)
	{
		this.pageLoadTimeOut = pageLoadTimeOut;
	}
	public Long getImplicitTimeWait()
	{
		return implicitTimeWait;
	}
	public void setImplicitTimeWait(Long implicitTimeWait)
	{
		this.implicitTimeWait = implicitTimeWait;
	}

}
