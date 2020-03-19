package com.flip.pages;

//package com.ca.am.actions;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;
//import com.ca.am.pages.logins.LoginPage;
//import com.ca.am.pages.logins.MasterLoginPage;
//import com.ca.am.utilities.Utility;
import com.ca.base.BasePage;
import com.ca.base.BaseSuite;
import com.ca.base.DriverFactory;
import com.ca.base.pojos.WebDriverEnum;
import com.ca.base.reports.ReportLogger;

public class LoginActions extends BasePage{
	
	private WebDriver driver;
	private static Logger writeLogAs = LogManager.getLogger(LoginActions.class.getSimpleName());
	
	public LoginActions(WebDriver driver) {
		this.driver = driver;
	}
	public LoginActions(WebDriverEnum driverEnum) {
	       
        Long threadId = new Long(Thread.currentThread().getId());
        driver = DriverFactory.getDriver(threadId, driverEnum);
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, 10);
	}

	public void loginToSampleApp() {
		//LoginPage gaLogin = new LoginPage(WebDriverEnum.flipkart);
		//writeLogAs.info("Logging in as GA");
		
			//driver.get(Utility.getValueFromProperty(System.getProperty("user.dir") + File.separator + "am.properties", "gaurl"));
			driver.get(BaseSuite.caPropMap.get("sampleappurl"));
		
		
		
	}
	public void loginAsGA() {
		LoginPage gaLogin = new LoginPage(WebDriverEnum.flipkart);
		writeLogAs.info("Logging in as GA");
		try {
			//driver.get(Utility.getValueFromProperty(System.getProperty("user.dir") + File.separator + "am.properties", "gaurl"));
			driver.get(BaseSuite.caPropMap.get("gaurl"));
			//String loginOrg = BaseSuite.caPropMap.get("gaOrg");
			String username = BaseSuite.caPropMap.get("gaName");
			String password = BaseSuite.caPropMap.get("gaPwd");
			//printScreenshot
			Long threadId = Long.valueOf(Thread.currentThread().getId());
			WebDriverEnum driverEnum = DriverFactory.threadToCurrentDriverMap.get(threadId);
		//	Thread.sleep(5000);
			gaLogin.clickOrgName();
			
			ReportLogger.logScreenShot(Status.PASS, "Screenshot", driverEnum);
			//writeLogAs.info("Navigating to the 'Userlogin' page");
			ReportLogger.logInfo(Status.PASS, "Navigating to the 'Userlogin' page");
			gaLogin.setUserName(username);
			gaLogin.setPassword(password);
			ReportLogger.logScreenShot(Status.PASS, "Screenshot", driverEnum);
			gaLogin.clickLoginBtn();
			
			Thread.sleep(2000);
			writeLogAs.info("Clicked Login Button");
			ReportLogger.logScreenShot(Status.PASS, "Screenshot", driverEnum);
		  // System.out.println("clicked Login Button");
		
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void logoutAsAdmin() {
		LoginPage gaLogin = new LoginPage(WebDriverEnum.flipkart);
		gaLogin.clickLogoutLink();
	}
	
	
	public void LoginExcel( String Email, String pwd) {
		System.out.println("inside loginExcelmethod");
		LoginPage gaLogin = new LoginPage(WebDriverEnum.flipkart);
		driver.get(BaseSuite.caPropMap.get("gaurl"));
		gaLogin.clickOrgName();
		gaLogin.setUserName(Email);
		gaLogin.setPassword(pwd);
		gaLogin.clickLoginBtn();
		
		
	}
	
	/*public void loginAsMA(String password) {
		MasterLoginPage maLogin = new MasterLoginPage(WebDriverEnum.flipkart);
		writeLogAs.info("Logging in as MA");
		
		try {
			driver.get(Utility.getValueFromProperty(System.getProperty("user.dir") + File.separator + "am.properties", "maurl"));
			Thread.sleep(1000);
			if(maLogin.getUserName().equalsIgnoreCase(Utility.getValueFromProperty(System.getProperty("user.dir") + File.separator + "am.properties", "maName"))) {
				maLogin.setPassword(Utility.getValueFromProperty(System.getProperty("user.dir") + File.separator + "am.properties", "maPwd"));
				maLogin.clickLoginBtn();
			} else {
				System.out.println("Invalid user!! Please verify your test inputs!");
			}
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}*/
}
