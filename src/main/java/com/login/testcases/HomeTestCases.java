package com.login.testcases;

import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.base.BaseSuite;
import com.base.DriverFactory;
import com.base.pojos.WebDriverEnum;
import com.base.reports.ReportLogger;
import com.ca.util.CommonUtil;
import com.login.pages.HomeActions;
import com.login.pages.LoginActions;
import com.login.pages.LoginPage;

public class HomeTestCases extends BaseSuite {
	
	
	@Test (groups = {"Home"},priority=1,description = "Functionality under test- Home Navigations- Verify navigation to home page from Add Customer Page")
	//verify navigation to home page after clicking on add customer
	public void verifyHomeNavigationFromAddCustomer() {
		ReportLogger.logInfo(Status.INFO, "Starting the test case : Verify navigation to Home Page from Add Customer Page");
		ReportLogger.logInfo(Status.INFO, "Navigating to the Login page");
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		//Login to application
		loginAsAdmin.loginAsGA();
		
		HomeActions homeActionsObj = new HomeActions(WebDriverEnum.custApp);
		//verifying navigation to home page after clicking on add customer link
		homeActionsObj.VerifyNavigationFromAddCustomerPageToHomePage();
		ReportLogger.logInfo(Status.INFO, "Ending the test case");
		
     }

	
	@Test (groups = {"Home"},priority=2,description = "Functionality under test- Home Navigations- Verify navigation to Home page from Show Customers Page")
	//verify navigation to home page after clicking on add customer
	public void verifyHomeNavigationFromShowCustomer() {
		ReportLogger.logInfo(Status.INFO, "Starting the test case : Verify navigation to Home page from Show Customers Page");
		ReportLogger.logInfo(Status.INFO, "Navigating to the Login page");
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		//Login to application
		loginAsAdmin.loginAsGA();
		
		HomeActions homeActionsObj = new HomeActions(WebDriverEnum.custApp);
		//verifying navigation to home page after clicking show customer link
		homeActionsObj.VerifyNavigationFromShowCustomerPageToHomePage();
		ReportLogger.logInfo(Status.INFO, "Ending the test case");
      }
	
}
