package com.base;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.base.pojos.BrowserEnum;
import com.base.pojos.WebDriverEnum;




public class DriverFactory {
	
	public static Map<Long, Map<WebDriverEnum, WebDriver>> threadToDriverMap = new HashMap<Long, Map<WebDriverEnum, WebDriver>>();
	public static Map<Long, WebDriverEnum> threadToCurrentDriverMap = new HashMap<Long, WebDriverEnum>();
	
	public synchronized static  WebDriver getDriver(Long threadId ,WebDriverEnum driverEnum){
		
		//String browserType =BaseSuite.getCAPropertyValue("Browser");
		Map<WebDriverEnum, WebDriver> enumDriverMap = threadToDriverMap.get(threadId);
		threadToCurrentDriverMap.put(threadId, driverEnum);
		if(enumDriverMap==null){
			loadEnumDriverMap(threadId);
			enumDriverMap = threadToDriverMap.get(threadId);
		}		
		WebDriver driver = enumDriverMap.get(driverEnum);
		if(driver==null){
			
			String dockerEnabled = BaseSuite.getCAPropertyValue("DockerEnabled");
			String dockerMode = BaseSuite.getCAPropertyValue("DockerMode");
			if(dockerEnabled!=null && dockerEnabled.equalsIgnoreCase("true")){
				
				if(dockerMode!=null){
					
					if(dockerMode.equalsIgnoreCase("GridMode")){
						driver = DockerFactory.getDriver(threadId);
						enumDriverMap.put(driverEnum, driver);
						return driver;
					}
					else{
						//Assert.fail("Wrong Parameter value for dockerMode Paramter in CA.properties file");
					}
				}
				else{
					Assert.fail("dockerMode : Paramter is missing in CA.properties file");
				}
				
			}
			
			driver = BrowserFactory.getBrowser(threadId);			
			enumDriverMap.put(driverEnum, driver);
			
		}
		return driver;
		
	}
	
	public synchronized static  WebDriver getDriver(Long threadId ,WebDriverEnum driverEnum, String deviceName, String appName){
		
		Map<WebDriverEnum, WebDriver> enumDriverMap = threadToDriverMap.get(threadId);
		if(enumDriverMap==null){
			loadEnumDriverMap(threadId);
			enumDriverMap = threadToDriverMap.get(threadId);
		}		
		WebDriver driver = enumDriverMap.get(driverEnum);
		if(driver==null){					
			
			driver = AppiumFactory.getAppiumDriver(deviceName, appName);		
			enumDriverMap.put(driverEnum, driver);
			
		}
		return driver;
		
	}
	
	public synchronized static  WebDriver getDriver(Long threadId, String deviceName, WebDriverEnum driverEnum){
		
		Map<WebDriverEnum, WebDriver> enumDriverMap = threadToDriverMap.get(threadId);
		if(enumDriverMap==null){
			loadEnumDriverMap(threadId);
			enumDriverMap = threadToDriverMap.get(threadId);
		}		
		WebDriver driver = enumDriverMap.get(driverEnum);
		if(driver==null){
			
			/*String dockerEnabled = BaseSuite.getCAPropertyValue("DockerEnabled");
			String dockerMode = BaseSuite.getCAPropertyValue("DockerMode");
			if(dockerEnabled!=null && dockerEnabled.equalsIgnoreCase("true")){
				
				if(dockerMode!=null){
					
					if(dockerMode.equalsIgnoreCase("GridMode")){
						driver = DockerFactory.getDriver(browserType);
						enumDriverMap.put(driverEnum, driver);
						return driver;
					}
					else{
						Assert.fail("Wrong Parameter value for dockerMode Paramter in CA.properties file");
					}
				}
				else{
					Assert.fail("dockerMode : Paramter is missing in CA.properties file");
				}
				
			}*/
			BrowserEnum browserEnum = BaseSuite.threadBrowserMap.get(threadId);
			driver = AppiumFactory.getAppiumBrowserDriver(deviceName, browserEnum);		
			enumDriverMap.put(driverEnum, driver);
			
		}
		return driver;
		
	}
	public synchronized static void loadEnumDriverMap(Long threadId){
		
		Map<WebDriverEnum, WebDriver> enumDriverMap = new HashMap<WebDriverEnum, WebDriver>();
		enumDriverMap.put(WebDriverEnum.GolfStore1, null);
		enumDriverMap.put(WebDriverEnum.GolfStore2, null);
		enumDriverMap.put(WebDriverEnum.RAGLobalAdmin, null);
		enumDriverMap.put(WebDriverEnum.RAMasterAdmin, null);
		enumDriverMap.put(WebDriverEnum.TMGLobalAdmin, null);
		enumDriverMap.put(WebDriverEnum.TMMasterAdmin, null);
		enumDriverMap.put(WebDriverEnum.GolfStoreApp, null);
		enumDriverMap.put(WebDriverEnum.Custom, null);
		threadToDriverMap.put(threadId, enumDriverMap);
	}
	
	

	public static void closeDrivers(Long threadId) {

		Map<WebDriverEnum, WebDriver> enumDriverMap = threadToDriverMap.get(threadId);
		if(enumDriverMap==null)	
			return;
		for ( Map.Entry<WebDriverEnum, WebDriver> entrySet : enumDriverMap.entrySet()) {
			WebDriverEnum driverEnum = entrySet.getKey();
			WebDriver driver = entrySet.getValue();
			if(driver!=null){
				driver.quit();
			}
		    // do something with key and/or tab
		}
		threadToDriverMap.remove(threadId);
		
	}
	

}
