package com.login.testcases;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

//import java.util.Map;

import org.testng.annotations.Test;

import com.base.BaseSuite;
import com.base.DriverFactory;
import com.base.pojos.WebDriverEnum;
import com.ca.util.CommonUtil;
import com.login.pages.LoginActions;
import com.login.pages.LoginPage;

public class VerifycustLogin extends BaseSuite {
	
	@Test     //(dataProvider = "createUser", description = "Functionality under test- Create Users")
	public void performLogin() {
	//	LoginPage loginAsAdmin = new LoginPage(WebDriverEnum.custApp);
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		
		loginAsAdmin.loginAsGA();
}
	/*@Test(dataProvider = "register1", description = "Functionality under test- register")
	public void performLoginExcel(Map<String, String> testData) {
LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);

     String email = testData.get("Email");
String pwd = testData.get("password");

		loginAsAdmin.LoginExcel(email, pwd);
		
	}
	

	@DataProvider(name = "register1")
	public Object[][] readUserTestCaseData(ITestContext testContext) throws Exception {
		return new CommonUtil().getInputData(testContext, "register", "testexcelSheet");
	}
	*/
	
}