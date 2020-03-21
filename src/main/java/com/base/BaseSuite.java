package com.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.xml.XmlSuite;

import com.base.pojos.BrowserEnum;
import com.base.pojos.WebDriverEnum;
//import com.ca.db_connection.util.DBConnections;
import com.ca.util.CAUtil;

public class BaseSuite {

	public static Map<String, String> caPropMap = null;
	public static Map<Long, Map<WebDriverEnum, WebDriver>> threadToDriverMap = null;
	public static Map<Long, Boolean> threadItestResultMap = new HashMap<Long, Boolean>();
	public static Map<Long, BrowserEnum> threadBrowserMap = new HashMap<Long, BrowserEnum>();
	private static String propFilePath = null;
	public static Map<Long, String> threadTestCaseIdMap = new HashMap<Long, String>();
	public static Integer totalNumOfTestCases = new Integer(0);
	public static Integer numOfPASSEDTestCases = new Integer(0);
	public static Integer numOfFAILEDTestCases = new Integer(0);
	public static Integer numOfSKIPPEDTestCases = new Integer(0);
	public static String tdsAcsTransId;
	/*public static Map<String, String> areqPropMap = null;
	public static Map<String, String> creq1PropMap = null;
	public static Map<String, String> creq2PropMap = null;
	public static Map<String, String> creq3PropMap = null;*/
	
	public static String parallelType = "none";
		
	
	@BeforeSuite
	public void beforeSuite(ITestContext testContext) {
		
		System.out.println("Inside Before Suite***********************************************************************");
		XmlSuite suite = testContext.getSuite().getXmlSuite();
		String suiteName = suite.getName();
		System.out.println("**************************** SUITE NAME : "+suiteName+" ****************************");
		int threads = suite.getThreadCount();
		
		
		String strPropertyfileName = testContext.getCurrentXmlTest().getParameter(
				"PropertyFileName");
		initialiseProperties(strPropertyfileName);
		
		loadBrowserStackProperties(testContext);			
		
		System.out.println("Before suite of BaseSuite Ended ");
		
	}
	
	private void loadBrowserStackProperties(ITestContext testContext) {
		
		String strBrowserStackEnabled = testContext.getCurrentXmlTest().getParameter("BrowserStack_Enabled");
		String strBrowser = testContext.getCurrentXmlTest().getParameter("BrowserStack_browser");
		String strBrowserVersion = testContext.getCurrentXmlTest().getParameter("BrowserStack_browser_version");
		String strOS = testContext.getCurrentXmlTest().getParameter("BrowserStack_os");
		String strResolution = testContext.getCurrentXmlTest().getParameter("BrowserStack_resolution");
		String strOSVersion = testContext.getCurrentXmlTest().getParameter("BrowserStack_os_version");
		String strBrowserName = testContext.getCurrentXmlTest().getParameter("BrowserStack_browserName");
		String strDevice = testContext.getCurrentXmlTest().getParameter("BrowserStack_device");
		if(strBrowserStackEnabled!=null)
			caPropMap.put("BrowserStack_Enabled", strBrowserStackEnabled);
		if(strBrowser!=null)
			caPropMap.put("BrowserStack_browser", strBrowser);
		if(strBrowserVersion!=null)
			caPropMap.put("BrowserStack_browser_version", strBrowserVersion);
		if(strOS!=null)
			caPropMap.put("BrowserStack_os", strOS);
		if(strResolution!=null)
			caPropMap.put("BrowserStack_resolution", strResolution);
		if(strOSVersion!=null)
			caPropMap.put("BrowserStack_os_version", strOSVersion);
		if(strBrowserName!=null)
			caPropMap.put("BrowserStack_browserName", strBrowserName);
		if(strDevice!=null)
			caPropMap.put("BrowserStack_device", strDevice);
		
		
	}

	@BeforeMethod
	public void beforeMethod(ITestContext testContext,ITestResult testResult) {
		
		
		String strBrowser = null;
		
		strBrowser = testContext.getCurrentXmlTest().getParameter(
				"Browser");
		if(strBrowser==null||strBrowser.length()==0){
			strBrowser = getCAPropertyValue("Browser");
		}
		if(strBrowser==null||strBrowser.length()==0){
			strBrowser="Chrome";
		}
		
		Long threadId = Long.valueOf(Thread.currentThread().getId());
		threadBrowserMap.put(threadId, BrowserEnum.getBrowserEnumForString(strBrowser));
		
		
	}
	
	
	/**
	 * This method reads all the keys and values defined in the Property file mention in the XML or CA.properties
	 * under config folder of the project and will store in to the caPropMap
	 * map.
	 */
	private void initialiseProperties(String strPropertyFileName) {
		System.out.println("inside properties");
		try {

			if(strPropertyFileName==null||strPropertyFileName.length()==0){
				//propFilePath = "config"+File.separator+"sit"+File.separator+"local"+File.separator+"config.properties";
				propFilePath = "config"+File.separator+"config.properties";
			}
			else{
				propFilePath = "config"+File.separator+strPropertyFileName;
			}
			
			System.out.println("CA properties file used for initialising is : "
					+ propFilePath);
			Properties properties = new Properties();

			FileInputStream fis = new FileInputStream(propFilePath);
			properties.load(fis);
			fis.close();
			System.out.println("Initialising the CA properties from file - "
					+ propFilePath);
			caPropMap = new HashMap<String,String>();
			for (String name : properties.stringPropertyNames())
				caPropMap.put(name, properties.getProperty(name));

			CAUtil util = new CAUtil();
			util.replaceWithSystemVariables(caPropMap);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	///PUT CODE for closing the drivers in after method, test , class and suite 
	
	@AfterMethod
	public void afterMethodProcessing(Method method, ITestContext testContext,ITestResult testResult){
		
		
		System.out.println("Quitting driver from After Methods");
		Long threadId = Long.valueOf(Thread.currentThread().getId());
		System.out.println("Thread ID = " + threadId+" closing all Dirvers for the Thread");
		threadBrowserMap.remove(threadId);
		DriverFactory.closeDrivers(threadId);

		
		
	}
	
	public synchronized static String getCAPropertyValue(String paramKey){
		
		if(caPropMap!=null){
			return caPropMap.get(paramKey);
		}
		return null;
	}
	
	@AfterSuite
	public void afterSuite(ITestContext testContext) {
		System.out.println("Closing the appium server");
		//AppiumFactory.closeAppium();
		//DBConnections.closeConnection();
	}
	
	
	
}
