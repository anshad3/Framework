package com.base;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;

import com.base.pojos.BrowserEnum;

public class DockerFactory {
	
	public static boolean dockerEnabled;
	public static String dockerMode = null;
	
	public synchronized static RemoteWebDriver getDriver(Long threadId){
		
		String browser = null;
		
		BrowserEnum browserEnum = BaseSuite.threadBrowserMap.get(threadId);
		DesiredCapabilities cap=null;
		if(browser.equalsIgnoreCase("chrome")){
			cap = DesiredCapabilities.chrome();
		}
		else{
			cap = DesiredCapabilities.firefox();
		}
		
		URL u = null;
		String hubUrl =BaseSuite.getCAPropertyValue("GridHubUrl");
		try {
			u = new URL(hubUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Assert.fail("MalformedURLException related to selenium Grid Hub");
		}
		RemoteWebDriver driver=new RemoteWebDriver(u,cap);
		
		return driver;
	}

}
