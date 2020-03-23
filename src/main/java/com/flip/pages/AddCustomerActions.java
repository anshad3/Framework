package com.flip.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;
import com.base.BasePage;
import com.base.DriverFactory;
import com.base.pojos.WebDriverEnum;
import com.base.reports.ReportLogger;
import com.login.pages.LoginActions;

public class AddCustomerActions extends BasePage{
	
	private WebDriver driver;
	private static Logger writeLogAs = LogManager.getLogger(AddCustomerActions.class.getSimpleName());
	Long threadId = Long.valueOf(Thread.currentThread().getId());
	WebDriverEnum driverEnum = DriverFactory.threadToCurrentDriverMap.get(threadId);
	
	public AddCustomerActions(WebDriver driver) {
		this.driver = driver;
	}
	
	public AddCustomerActions(WebDriverEnum driverEnum) {
	       
        Long threadId = new Long(Thread.currentThread().getId());
        driver = DriverFactory.getDriver(threadId, driverEnum);
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, 10);
	}
	
	
	public void addANewCustomerAction(String customerId, String customerName) {
		
		//Add Customer
		ReportLogger.logInfo(Status.INFO, "Inside Add customer page");
		//Creating an object for AddCustomerPage
		AddCustomerPage addCustomer = new AddCustomerPage(WebDriverEnum.custApp);
		//calling the method to add a customer with given id and name
		addCustomer.addANewCustomer(customerId, customerName);
		ReportLogger.logScreenShot(Status.PASS, "Customer Added Successfully", WebDriverEnum.custApp);
	
	}

}
