package com.logout.testcases;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.base.BaseSuite;
import com.base.pojos.WebDriverEnum;
import com.base.reports.ReportLogger;
import com.login.pages.HomeActions;
import com.login.pages.LoginActions;

public class LogOutTestCases extends BaseSuite  {
	
	@Test (groups = {"Logout"},priority=1,description = "TC015 :Functionality under test- Verify Logout Functionality from Home Page")
	public void verifyLogOutFromHomePage() {
		ReportLogger.logInfo(Status.INFO, "Starting the test case : TC015 : Verify Logout Functionality from Home Page");
		ReportLogger.logInfo(Status.INFO, "Navigating to the Login page");
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		//Login to application
		loginAsAdmin.loginAsGA();
		
		HomeActions homeActionsObj = new HomeActions(WebDriverEnum.custApp);
		homeActionsObj.verifyLogOutFunctionality();
		ReportLogger.logInfo(Status.INFO, "Ending the test case");
		
     }

	@Test (groups = {"Logout"},priority=2,description = "TC016 : Functionality under test- Verify Logout Functionality from Add Customer Page")
	public void verifyLogOutFromAddCustomerPage() {
		ReportLogger.logInfo(Status.INFO, "Starting the test case : TC016 : Verify Logout Functionality from Add Customer Page");
		ReportLogger.logInfo(Status.INFO, "Navigating to the Login page");
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		//Login to application
		loginAsAdmin.loginAsGA();
		
		HomeActions homeActionsObj = new HomeActions(WebDriverEnum.custApp);
		homeActionsObj.NavigateToAddCustomerLink();
		homeActionsObj.verifyLogOutFunctionality();
		ReportLogger.logInfo(Status.INFO, "Ending the test case");
		
     }
	
	@Test (groups = {"Logout"},priority=3,description = "TC017 : Functionality under test- Verify Logout Functionality from Show Customers Page")
	public void verifyLogOutFromShowCustomerPage() {
		ReportLogger.logInfo(Status.INFO, "Starting the test case : TC017 : Verify Logout Functionality from Show Customers Page");
		ReportLogger.logInfo(Status.INFO, "Navigating to the Login page");
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		//Login to application
		loginAsAdmin.loginAsGA();
		
		HomeActions homeActionsObj = new HomeActions(WebDriverEnum.custApp);
		homeActionsObj.NavigateToShowCustomersLink();
		homeActionsObj.verifyLogOutFunctionality();
		ReportLogger.logInfo(Status.INFO, "Ending the test case");
		
     }
}
