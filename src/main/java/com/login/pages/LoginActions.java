package com.login.pages;

//package com.ca.am.actions;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;
import com.base.BasePage;
import com.base.BaseSuite;
import com.base.DriverFactory;
import com.base.pojos.WebDriverEnum;
import com.base.reports.ReportLogger;
import org.testng.Assert;
public class LoginActions extends BasePage{
	
	private WebDriver driver;
	private static Logger writeLogAs = LogManager.getLogger(LoginActions.class.getSimpleName());
	Long threadId = Long.valueOf(Thread.currentThread().getId());
	WebDriverEnum driverEnum = DriverFactory.threadToCurrentDriverMap.get(threadId);
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
		//LoginPage gaLogin = new LoginPage(WebDriverEnum.custApp);
		//writeLogAs.info("Logging in as GA");
		
			//driver.get(Utility.getValueFromProperty(System.getProperty("user.dir") + File.separator + "am.properties", "gaurl"));
			driver.get(BaseSuite.caPropMap.get("sampleappurl"));
		
		
		
	}
	public void loginAsGA() {
		LoginPage gaLogin = new LoginPage(WebDriverEnum.custApp);
		writeLogAs.info("Logging in as customerDashboard");
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
			//gaLogin.clickOrgName();
			
			ReportLogger.logScreenShot(Status.PASS, "LoginPageScreenshot", driverEnum);
			//writeLogAs.info("Navigating to the 'Userlogin' page");
			ReportLogger.logInfo(Status.PASS, "Navigating to the 'Dashboard' page");
			gaLogin.setUserName(username);
			gaLogin.setPassword(password);
			//ReportLogger.logScreenShot(Status.PASS, "Screenshot", driverEnum);
			gaLogin.clickLoginBtn();
			
			Thread.sleep(2000);
			writeLogAs.info("Clicked Login Button");
			ReportLogger.logScreenShot(Status.PASS, "DashboardScreenshot", driverEnum);
		  // System.out.println("clicked Login Button");
		
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void logoutAsAdmin() {
		LoginPage gaLogin = new LoginPage(WebDriverEnum.custApp);
		gaLogin.clickLogoutLink();
	}
	
	
	public void InvalidLogin( String Email, String pwd, String invalidmsg) {
		System.out.println("inside loginExcelmethod");
		LoginPage gaLogin = new LoginPage(WebDriverEnum.custApp);
		driver.get(BaseSuite.caPropMap.get("gaurl"));
		//gaLogin.clickOrgName();
		gaLogin.setUserName(Email);
		gaLogin.setPassword(pwd);
		gaLogin.clickLoginBtn();
		String a=driver.findElement(By.xpath("//DIV[@class='alert alert-danger'][text()='Bad credentials']")).getText();
		//String invmsg="Bad credentials";
		//System.out.println("Displayed Text : "+ a);
		ReportLogger.logScreenShot(Status.PASS, "LoginScreenScreenshot", driverEnum);
		Assert.assertEquals(a, invalidmsg);
		ReportLogger.logInfo(Status.PASS, "Bad Credentials Displayed");
	  
		
	}
	
	
	public void verifyCustName(String cust)
	{
		
		LoginPage gaLogin = new LoginPage(WebDriverEnum.custApp);
		writeLogAs.info("Logging in as customerDashboard");
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
			//gaLogin.clickOrgName();
			
			ReportLogger.logScreenShot(Status.PASS, "LoginPageScreenshot", driverEnum);
			//writeLogAs.info("Navigating to the 'Userlogin' page");
			ReportLogger.logInfo(Status.PASS, "Navigating to the 'Dashboard' page");
			gaLogin.setUserName(username);
			gaLogin.setPassword(password);
			//ReportLogger.logScreenShot(Status.PASS, "Screenshot", driverEnum);
			gaLogin.clickLoginBtn();
			
			Thread.sleep(2000);
			writeLogAs.info("Clicked Login Button");
			ReportLogger.logScreenShot(Status.PASS, "DashboardScreenshot", driverEnum);
		  // System.out.println("clicked Login Button");
			String dashview=driver.findElement(By.xpath("//B[text()='Hello Customer!!!']")).getText();
			//ReportLogger.logScreenShot(Status.PASS, "DashboardScreenshot", driverEnum);
			Assert.assertEquals(dashview, cust);
			ReportLogger.logInfo(Status.PASS, cust + "displayed");
			
			
		
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	
	
	public void verifyLoginHeader(String Header) {
		
		LoginPage gaLogin = new LoginPage(WebDriverEnum.custApp);
		writeLogAs.info("Logging in as customerDashboard");
		
			//driver.get(Utility.getValueFromProperty(System.getProperty("user.dir") + File.separator + "am.properties", "gaurl"));
			driver.get(BaseSuite.caPropMap.get("gaurl"));
			String headername=driver.findElement(By.xpath("//H2[@class='form-signin-heading'][text()='Please sign in']")).getText();
			Assert.assertEquals(headername, Header);
			ReportLogger.logInfo(Status.PASS, Header + " displayed");
			ReportLogger.logScreenShot(Status.PASS, "Screenshot", driverEnum);
	}
	
	
	/*public void loginAsMA(String password) {
		MasterLoginPage maLogin = new MasterLoginPage(WebDriverEnum.custApp);
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
