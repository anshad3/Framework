package com.flip.testcases;

import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.base.BaseSuite;
import com.base.pojos.WebDriverEnum;
import com.base.reports.ReportLogger;
import com.ca.util.CommonUtil;
import com.flip.pages.AddCustomerActions;
import com.login.pages.HomePage;
import com.login.pages.LoginActions;

public class AddCustomer extends BaseSuite{

	
	@Test(dataProvider = "addCustomer", description = "Functionality under test- Add Customer")
	public void performAddCustomer(Map<String, String> testData) {
		
		ReportLogger.logInfo(Status.INFO, "Starting the test case"+ testData.get("TestCaseId"));
		ReportLogger.logInfo(Status.INFO, "Navigating to the 'Userlogin' page");
		
		//Login with username and password
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		loginAsAdmin.loginAsGA();
		
		ReportLogger.logInfo(Status.INFO, "Clicking on 'Add Customer' link in the header");
		//navigating from home page by clicking on the Add Customer Link
		HomePage homePageObj = new HomePage(WebDriverEnum.custApp);
		homePageObj.clickaddCustomerLink();
		
		//Creating an object for AddCustomerActions
		AddCustomerActions addCustomerActions=new AddCustomerActions((WebDriverEnum.custApp));
		//calling the method to add a new customer with the given customerId and customerName in excel
		addCustomerActions.addANewCustomerAction(testData.get("CustomerId"),testData.get("CustomerName"));
		
		ReportLogger.logInfo(Status.INFO, "Ending the test case");
	}
	
	

	@DataProvider(name = "addCustomer")
	public Object[][] readUserTestCaseData(ITestContext testContext) throws Exception {
		return new CommonUtil().getInputData(testContext, "addCustomerExcel", "addCustomerSheet");
	}
}
