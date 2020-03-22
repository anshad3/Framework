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
import org.testng.annotations.Test;
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
	
	
	public void PerformVerifyCustName(String cust)
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
	
	
	public void VerifyLoginEmptyCredentials()
	{
		LoginPage gaLogin = new LoginPage(WebDriverEnum.custApp);
		writeLogAs.info("Logging in as customerDashboard");
		
			//driver.get(Utility.getValueFromProperty(System.getProperty("user.dir") + File.separator + "am.properties", "gaurl"));
			driver.get(BaseSuite.caPropMap.get("gaurl"));
		//	ReportLogger.logScreenShot(Status.PASS, "LoginPageScreenshot", driverEnum);
			gaLogin.clickLoginBtn();
			writeLogAs.info("Clicked Login Button");
			//String dashview=driver.findElement(By.xpath("//B[text()='Hello Customer!!!']")).
			ReportLogger.logScreenShot(Status.PASS, "LoginPageScreenshot", driverEnum);
					///	boolean isPresent = false;
					try {
						//System.out.println("getsize" + driver.findElement(By.xpath("//H2[@class='form-signin-heading'][text()='Please sign in']")).getSize());
						if(driver.findElement(By.xpath("//B[text()='Hello Customer!!!']")).getSize() != null) {
							 
									System.out.println("element present");
									boolean elementPresent= false;
									Assert.assertTrue(elementPresent, "Able to Login with empty Credentials");
								}
									else {
										System.out.println("element absent");
										boolean elementPresent= true;
									}
						
						//isPresent = true;
					} catch (Exception e) {
						ReportLogger.logInfo(Status.PASS," Unable to login with Empty Credentials");
						//boolean elementPresent= true;
						System.out.println(e.toString());
						//Assert.assertTrue(elementPresent, "UnAble to Login with empty Credentials");
					}
					//ReportLogger.logInfo(Status.PASS," Unable to login with Empty Credentials");
					
					/*if(isPresent=true) {
						System.out.println("element present");
					}
						else {
							System.out.println("element absent");
						}
					*/
					
					//System.out.println("isPresent" + isPresent);
					//Assert.assertFalse(true, "Able to Login with empty Credentials");
													
	}
	
	
	
	public void verifyLoginTitle()
	{
	
		//LoginPage gaLogin = new LoginPage(WebDriverEnum.custApp);
		writeLogAs.info("Logging in as customerDashboard");
		
			//driver.get(Utility.getValueFromProperty(System.getProperty("user.dir") + File.separator + "am.properties", "gaurl"));
			driver.get(BaseSuite.caPropMap.get("gaurl"));
			String actualTitle = driver.getTitle();
			System.out.println("actualTitle" + actualTitle);
			String expectedTitle = "Please sign in";
			if(actualTitle.equalsIgnoreCase(expectedTitle)) {
				System.out.println("Title Matched");
			ReportLogger.logInfo(Status.PASS,actualTitle +" --Title Matching");
			ReportLogger.logScreenShot(Status.PASS, "Title Matching", driverEnum);
			}
			else
			{
				System.out.println("Title didn't match");
			ReportLogger.logScreenShot(Status.FAIL, "Title didn't match", driverEnum);
			}
		
	}
	
	
	public void loginCustNameWithoutPWD() {
		LoginPage gaLogin = new LoginPage(WebDriverEnum.custApp);
		writeLogAs.info("Logging in as customerDashboard");
		
			//driver.get(Utility.getValueFromProperty(System.getProperty("user.dir") + File.separator + "am.properties", "gaurl"));
			driver.get(BaseSuite.caPropMap.get("gaurl"));
			//String loginOrg = BaseSuite.caPropMap.get("gaOrg");
			String username = BaseSuite.caPropMap.get("gaName");
			gaLogin.setUserName(username);
			//gaLogin.setPassword(password);
			//ReportLogger.logScreenShot(Status.PASS, "Screenshot", driverEnum);
			gaLogin.clickLoginBtn();
			writeLogAs.info("Clicked Login Button");
			//String dashview=driver.findElement(By.xpath("//B[text()='Hello Customer!!!']")).
			ReportLogger.logScreenShot(Status.PASS, "LoginPageScreenshot", driverEnum);
					///	boolean isPresent = false;
					try {
						//System.out.println("getsize" + driver.findElement(By.xpath("//H2[@class='form-signin-heading'][text()='Please sign in']")).getSize());
						if(driver.findElement(By.xpath("//B[text()='Hello Customer!!!']")).getSize() != null) {
							 
									System.out.println("element present");
									boolean elementPresent= false;
									Assert.assertTrue(elementPresent, "Able to Login with empty password");
								}
									else {
										System.out.println("element absent");
										boolean elementPresent= true;
									}
						
						//isPresent = true;
					} catch (Exception e) {
						ReportLogger.logInfo(Status.PASS," Unable to login with Empty password");
						//boolean elementPresent= true;
						System.out.println(e.toString());
						//Assert.assertTrue(elementPresent, "UnAble to Login with empty Credentials");
					}
			
			
	}
	
	
	public void loginPWDWithoutCustName() {
		LoginPage gaLogin = new LoginPage(WebDriverEnum.custApp);
		writeLogAs.info("Logging in as customerDashboard");
		
			//driver.get(Utility.getValueFromProperty(System.getProperty("user.dir") + File.separator + "am.properties", "gaurl"));
			driver.get(BaseSuite.caPropMap.get("gaurl"));
			//String loginOrg = BaseSuite.caPropMap.get("gaOrg");
			//String username = BaseSuite.caPropMap.get("gaName");
			String password = BaseSuite.caPropMap.get("gaPwd");
			//gaLogin.setUserName(username);
			gaLogin.setPassword(password);
			//ReportLogger.logScreenShot(Status.PASS, "Screenshot", driverEnum);
			gaLogin.clickLoginBtn();
			writeLogAs.info("Clicked Login Button");
			//String dashview=driver.findElement(By.xpath("//B[text()='Hello Customer!!!']")).
			ReportLogger.logScreenShot(Status.PASS, "LoginPageScreenshot", driverEnum);
					///	boolean isPresent = false;
					try {
						//System.out.println("getsize" + driver.findElement(By.xpath("//H2[@class='form-signin-heading'][text()='Please sign in']")).getSize());
						if(driver.findElement(By.xpath("//B[text()='Hello Customer!!!']")).getSize() != null) {
							 
									System.out.println("element present");
									boolean elementPresent= false;
									Assert.assertTrue(elementPresent, "Able to Login with empty username");
								}
									else {
										System.out.println("element absent");
										boolean elementPresent= true;
									}
						
						//isPresent = true;
					} catch (Exception e) {
						ReportLogger.logInfo(Status.PASS," Unable to login with Empty username");
						//boolean elementPresent= true;
						System.out.println(e.toString());
						//Assert.assertTrue(elementPresent, "UnAble to Login with empty Credentials");
					}
			
			
	}
	/*public void VerifyPlaceHolder() {
		
		//LoginPage gaLogin = new LoginPage(WebDriverEnum.custApp);
	//	writeLogAs.info("Logging in as customerDashboard");
		
			//driver.get(Utility.getValueFromProperty(System.getProperty("user.dir") + File.separator + "am.properties", "gaurl"));
			driver.get(BaseSuite.caPropMap.get("gaurl"));
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//String custplaceholder=driver.findElement(By.xpath("//INPUT[@placeholder='Username']")).
			//System.out.println("cust" + custplaceholder);
			
			String pwdplaceholder=driver.findElement(By.xpath("//INPUT[@placeholder='Password']")).getTagName();
			System.out.println("cust" + custplaceholder);
			System.out.println(pwdplaceholder);
			String expectedcust= "Username";
			String expectedpwd= "Password";
			Assert.assertEquals(custplaceholder,expectedcust);
			Assert.assertEquals(pwdplaceholder,expectedpwd);
			ReportLogger.logInfo(Status.PASS, custplaceholder + " -- placeholder of username");
			ReportLogger.logInfo(Status.PASS, expectedpwd + " -- placeholder of Password");
			
			ReportLogger.logScreenShot(Status.PASS, "placeholderScreenshot", driverEnum);
		
	}*/
	
	
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
