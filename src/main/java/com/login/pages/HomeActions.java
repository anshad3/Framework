package com.login.pages;


import java.io.File;

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
	
	
	public void clickHomeLinkAndVerifyNavigation() {
		
		HomePage homePageObj = new HomePage(WebDriverEnum.custApp);
		homePageObj.clickaddCustomerLink();
		
		//verify navigation to add customer link
		ReportLogger.logScreenShot(Status.PASS, "AddCustomerScreenshot", WebDriverEnum.custApp);
		boolean customerIdTextBoxPresence=driver.findElement(By.xpath("//*[@id='custId']")).isDisplayed();
		if(customerIdTextBoxPresence) {
			ReportLogger.logInfo(Status.PASS, "Successfully navigated to add customer page");
		}
		else {
			ReportLogger.logInfo(Status.FAIL, "Failed to navigate to add customer page");
			
		}
		
		homePageObj.clickHomeLink();
		
		//validate navigation to home is successful
		
		
        homePageObj.clickshowCustomersLink();
		
		//verify navigation to show  customer link
		
		homePageObj.clickHomeLink();
		
		//validate navigation to home is successful
		
	}
	
}
