package com.flip.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;
import com.base.BasePage;
import com.base.pojos.WebDriverEnum;
import com.base.reports.ReportLogger;
import com.login.pages.LoginActions;

public class AddCustomerActions extends BasePage{
	private WebDriver driver;
	private static Logger writeLogAs = LogManager.getLogger(AddCustomerActions.class.getSimpleName());
	
	public AddCustomerActions() {
		this.driver = driver;
	}
	
	
	public void addANewCustomerAction(String customerId, String customerName) {
		
		//Add Customer
		ReportLogger.logInfo(Status.INFO, "Inside Add customer page");
		//Creating an object for AddCustomerPage
		AddCustomerPage addCustomer = new AddCustomerPage();
		//calling the method to add a customer with given id and name
		addCustomer.addANewCustomer(customerId, customerName);
		ReportLogger.logInfo(Status.INFO, "Customer Added Successfully");
	}

}