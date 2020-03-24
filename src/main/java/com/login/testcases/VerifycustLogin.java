package com.login.testcases;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

//import java.util.Map;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.base.BaseSuite;
import com.base.DriverFactory;
import com.base.pojos.WebDriverEnum;
import com.base.reports.ReportLogger;
import com.ca.util.CommonUtil;
import com.login.pages.LoginActions;
import com.login.pages.LoginPage;

public class VerifycustLogin extends BaseSuite {
	
	@Test(groups = {"Login"},priority=1,description = "Functionality under test- TC001:Valid Login-Verify Login with valid credentials")     //(dataProvider = "createUser", description = "Functionality under test- Create Users")
	public void performLogin() {
		ReportLogger.logInfo(Status.INFO, "Starting the test case : TC001: Login with valid credentials");
		
	//	LoginPage loginAsAdmin = new LoginPage(WebDriverEnum.custApp);
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		
		loginAsAdmin.loginAsGA();
		ReportLogger.logInfo(Status.INFO, "Ending the test case");
}
	@Test(dataProvider = "register1",groups = {"Login"},priority=2,description = "Functionality under test- InValid Login-Verify Login with Invalid credentials and verify the message")
	public void performInvalidLogin(Map<String, String> testData) {
		ReportLogger.logInfo(Status.INFO, "Starting the test case : TC002: Login with invalid Credentials and verify the message");
		
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);

     String email = testData.get("Email");
String pwd = testData.get("password");
String invalidmsg=testData.get("invmsg");

		loginAsAdmin.InvalidLogin(email, pwd,invalidmsg);
		ReportLogger.logInfo(Status.INFO, "Ending the test case");
	}
	

	@DataProvider(name = "register1")
	public Object[][] readUserTestCaseData(ITestContext testContext) throws Exception {
		return new CommonUtil().getInputData(testContext, "register", "testexcelSheet");
	}
	
	
	@Test(dataProvider = "register2",groups = {"Login"},priority=3,description = "Functionality under test- Navigate to Login page with valid credentials and verify the customer name displayed in dashboard page")
	public void verifyCustName(Map<String, String> testData) {
		ReportLogger.logInfo(Status.INFO, "Starting the test case : TC003: Verify Customer name displayed in dashboard");
		
LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);


String custname=testData.get("dashboard");

		loginAsAdmin.PerformVerifyCustName(custname);
		ReportLogger.logInfo(Status.INFO, "Ending the test case");
		
	}
	

	@DataProvider(name = "register2")
	public Object[][] readUserTestCaseData2(ITestContext testContext) throws Exception {
		return new CommonUtil().getInputData(testContext, "register", "testexcelSheet2");
	}
	
	@Test(dataProvider = "register3", groups = {"Login"},priority=4,description = "Functionality under test- Verify the login header name")
	public void PerformToverifyLoginHeader(Map<String, String> testData)
	{
		ReportLogger.logInfo(Status.INFO, "Starting the test case : TC004: Verify Login Header");
		
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);


		String headertext=testData.get("header");

				loginAsAdmin.verifyLoginHeader(headertext);
				ReportLogger.logInfo(Status.INFO, "Ending the test case");
				
			}
	
	@DataProvider(name = "register3")
	public Object[][] readUserTestCaseData3(ITestContext testContext) throws Exception {
		return new CommonUtil().getInputData(testContext, "register", "testexcelSheet3");
	}
		
	@Test(groups = {"Login"},priority=5,description = "Functionality under test- Empty Login-Verify the user can Login with empty credentials")
	public void LoginEmptyCredentials()
	{
		ReportLogger.logInfo(Status.INFO, "Starting the test case : TC005: Login with Empty credentials");
		
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		loginAsAdmin.VerifyLoginEmptyCredentials();
		ReportLogger.logInfo(Status.INFO, "Ending the test case");
		//ReportLogger.logInfo(Status.PASS," Unable to login with Empty Credentials");
	}
	@Test(groups = {"Login"},priority=6,description = "Functionality under test-TC006: Verify login title")
	public void PerformVerifyLoginTitle()
	{
		ReportLogger.logInfo(Status.INFO, "Starting the test case : TC006: Verify Login Title");
		
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		loginAsAdmin.verifyLoginTitle();	
		ReportLogger.logInfo(Status.INFO, "Ending the test case");
	}
	
	
	@Test(groups = {"Login"},priority=7,description = "Functionality under test-TC007: Verify user can login with customer name with empty password")
	public void PerformloginCustNameWithoutPWD()
	{
		ReportLogger.logInfo(Status.INFO, "Starting the test case : TC007: Login with valid customer name and empty password");
		
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		loginAsAdmin.loginCustNameWithoutPWD();
		ReportLogger.logInfo(Status.INFO, "Ending the test case");
		//ReportLogger.logInfo(Status.PASS," Unable to login with Empty Credentials");
	}
	
	@Test(groups = {"Login"},priority=8,description = "Functionality under test-TC008: Verify user can login with empty customer name and valid password")
	public void PerformloginPWDWithoutCustName()
	{
		ReportLogger.logInfo(Status.INFO, "Starting the test case : TC008: Login with valid password and empty customername");
		
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		loginAsAdmin.loginPWDWithoutCustName();
		ReportLogger.logInfo(Status.INFO, "Ending the test case");
		//ReportLogger.logInfo(Status.PASS," Unable to login with Empty Credentials");
	}
	
	
	
	/*@Test
	public void PerformVerifyPlaceHolder()
	{
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		loginAsAdmin.VerifyPlaceHolder();
		//ReportLogger.logInfo(Status.PASS," Unable to login with Empty Credentials");
	}*/
	}
	
	

