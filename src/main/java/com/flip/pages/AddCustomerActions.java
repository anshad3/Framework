package com.flip.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;
import com.ca.base.BasePage;
import com.ca.base.BaseSuite;
import com.ca.base.DriverFactory;
import com.ca.base.pojos.WebDriverEnum;
import com.ca.base.reports.ReportLogger;

public class AddCustomerActions extends BasePage{
	private WebDriver driver;
	private static Logger writeLogAs = LogManager.getLogger(LoginActions.class.getSimpleName());
	
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
		ReportLogger.logInfo(Status.INFO, "Navigating to the 'Userlogin' page");
		//Login
		ReportLogger.logInfo(Status.INFO, "Clicking on 'Add Customer' link in the header");
		//Add Customer
		ReportLogger.logInfo(Status.INFO, "Inside Add customer page");
		//Creating an object for AddCustomerPage
		AddCustomerPage addCustomer = new AddCustomerPage(WebDriverEnum.AddCustomer);
		//calling the method to add a customer with given id and name
		addCustomer.addANewCustomer(customerId, customerName);
		ReportLogger.logInfo(Status.INFO, "Customer Added Successfully");
	}

}
