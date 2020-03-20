package com.flip.testcases;

import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.ca.base.pojos.WebDriverEnum;
import com.ca.base.reports.ReportLogger;
import com.ca.util.CommonUtil;
import com.flip.pages.AddCustomerActions;

public class AddCustomer {

	
	@Test(dataProvider = "addCustomer", description = "Functionality under test- Add Customer")
	public void performAddCustomer(Map<String, String> testData) {
		ReportLogger.logInfo(Status.INFO, "Starting the test case"+ testData.get("TestCaseId"));
		//Creating an object for AddCustomerActions
		AddCustomerActions addCustomerActions=new AddCustomerActions();
		//calling the method to add a new customer with the given customerId and customerName in excel
		addCustomerActions.addANewCustomerAction(testData.get("CustomerId"),testData.get("CustomerName"));
		ReportLogger.logInfo(Status.INFO, "Ending the test case");
	}
	
	

	@DataProvider(name = "addCustomer")
	public Object[][] readUserTestCaseData(ITestContext testContext) throws Exception {
		return new CommonUtil().getInputData(testContext, "addCustomerExcel", "addCustomerSheet");
	}
}
