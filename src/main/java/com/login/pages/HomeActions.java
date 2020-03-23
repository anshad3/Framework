package com.login.pages;


import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;
import com.base.BasePage;
import com.base.DriverFactory;

import com.base.pojos.WebDriverEnum;
import com.base.reports.ReportLogger;
import com.flip.pages.AddCustomerPage;
import com.flip.pages.ShowCustomersPage;

public class HomeActions  extends BasePage{

	private WebDriver driver;
	private static Logger writeLogAs = LogManager.getLogger(LoginActions.class.getSimpleName());
	
	public HomeActions(WebDriver driver) {
		this.driver = driver;
	}
	public HomeActions(WebDriverEnum driverEnum) {
	       
        Long threadId = new Long(Thread.currentThread().getId());
        driver = DriverFactory.getDriver(threadId, driverEnum);
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, 10);
	}
	
	
	public void VerifyNavigationFromAddCustomerPageToHomePage()  {
		
		HomePage homePageObj = new HomePage(WebDriverEnum.custApp);
	
		homePageObj.clickaddCustomerLink();

		//verify navigation to add customer link
		ReportLogger.logScreenShot(Status.PASS, "Verify navigation to Add Customer link", WebDriverEnum.custApp);
		//AddCustomerPage addCustObj = new AddCustomerPage();
		//boolean customerIdTextBoxPresence= addCustObj.checkCustomerIdTxtBoxDisplayed();
		boolean customerIdTextBoxPresence= driver.findElement(By.xpath("//*[@id='custId']")).isDisplayed();
		if(customerIdTextBoxPresence) {
			ReportLogger.logInfo(Status.PASS, "Successfully navigated to Add Customer Page");
		}
		else {ReportLogger.logInfo(Status.FAIL, "Failed to navigate to add customer page");}
		
		VerifyNavigationToHomePage();
	}
	
	    public void VerifyNavigationToHomePage() {
	    	HomePage homePageObj = new HomePage(WebDriverEnum.custApp);
	    	homePageObj.clickHomeLink();
			
	    	//validate navigation to home is successful
			ReportLogger.logScreenShot(Status.PASS, "Verify navigation to Home", WebDriverEnum.custApp);
			
			boolean helloCustTextPresence= homePageObj.checkHelloCustomerTextDisplayed();
			if(helloCustTextPresence) {
				ReportLogger.logInfo(Status.PASS, "Successfully navigated to Home page");
			}
			else {ReportLogger.logInfo(Status.FAIL, "Failed to navigate to Home page");}
	    }
	    
	    
        public void VerifyNavigationFromShowCustomerPageToHomePage() {
		
		HomePage homePageObj = new HomePage(WebDriverEnum.custApp);
		homePageObj.clickshowCustomersLink();
		
		//verify navigation to show customer link
		ReportLogger.logScreenShot(Status.PASS, "Verify navigation to Show Customers link", WebDriverEnum.custApp);
		
		ShowCustomersPage showCustObj = new ShowCustomersPage(WebDriverEnum.custApp);
		boolean customerIdTextBoxPresence= showCustObj.showAllCustomersHeaderValidation();
		
		if(customerIdTextBoxPresence) {
			ReportLogger.logInfo(Status.PASS, "Successfully navigated to Show Customers Page");
		}
		else {ReportLogger.logInfo(Status.FAIL, "Failed to Navigate to Show Customers Page");}
		
		VerifyNavigationToHomePage();
	}
	
}
