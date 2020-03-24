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
	
	@Test(groups = {"Login"},priority=1,description = "Functionality under test- Valid Login-Verify Login with valid credentials")     //(dataProvider = "createUser", description = "Functionality under test- Create Users")
	public void performLogin() {
	//	LoginPage loginAsAdmin = new LoginPage(WebDriverEnum.custApp);
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		
		loginAsAdmin.loginAsGA();
}
	@Test(dataProvider = "register1",groups = {"Login"},priority=2,description = "Functionality under test- InValid Login-Verify Login with Invalid credentials and verify the message")
	public void performInvalidLogin(Map<String, String> testData) {
LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);

     String email = testData.get("Email");
String pwd = testData.get("password");
String invalidmsg=testData.get("invmsg");

		loginAsAdmin.InvalidLogin(email, pwd,invalidmsg);
		
	}
	

	@DataProvider(name = "register1")
	public Object[][] readUserTestCaseData(ITestContext testContext) throws Exception {
		return new CommonUtil().getInputData(testContext, "register", "testexcelSheet");
	}
	
	
	@Test(dataProvider = "register2",groups = {"Login"},priority=3,description = "Functionality under test- Navigate to Login page with valid credentials and verify the customer name displayed in dashboard page")
	public void verifyCustName(Map<String, String> testData) {
LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);


String custname=testData.get("dashboard");

		loginAsAdmin.PerformVerifyCustName(custname);
		
	}
	

	@DataProvider(name = "register2")
	public Object[][] readUserTestCaseData2(ITestContext testContext) throws Exception {
		return new CommonUtil().getInputData(testContext, "register", "testexcelSheet2");
	}
	
	@Test(dataProvider = "register3", groups = {"Login"},priority=4,description = "Functionality under test- Verify the login header name")
	public void PerformToverifyLoginHeader(Map<String, String> testData)
	{
		
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);


		String headertext=testData.get("header");

				loginAsAdmin.verifyLoginHeader(headertext);
				
			}
	
	@DataProvider(name = "register3")
	public Object[][] readUserTestCaseData3(ITestContext testContext) throws Exception {
		return new CommonUtil().getInputData(testContext, "register", "testexcelSheet3");
	}
		
	@Test(groups = {"Login"},priority=5,description = "Functionality under test- Empty Login-Verify the user can Login with empty credentials")
	public void LoginEmptyCredentials()
	{
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		loginAsAdmin.VerifyLoginEmptyCredentials();
		//ReportLogger.logInfo(Status.PASS," Unable to login with Empty Credentials");
	}
	@Test(groups = {"Login"},priority=6,description = "Functionality under test- Verify login title")
	public void PerformVerifyLoginTitle()
	{
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		loginAsAdmin.verifyLoginTitle();	
	}
	
	
	@Test(groups = {"Login"},priority=7,description = "Functionality under test- Verify user can login with customer name with empty password")
	public void PerformloginCustNameWithoutPWD()
	{
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		loginAsAdmin.loginCustNameWithoutPWD();
		//ReportLogger.logInfo(Status.PASS," Unable to login with Empty Credentials");
	}
	
	@Test(groups = {"Login"},priority=8,description = "Functionality under test- Verify user can login with empty customer name and valid password")
	public void PerformloginPWDWithoutCustName()
	{
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		loginAsAdmin.loginPWDWithoutCustName();
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
	
	

