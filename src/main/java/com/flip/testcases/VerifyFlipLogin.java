package com.flip.testcases;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

//import java.util.Map;

import org.testng.annotations.Test;

//import com.ca._3ds.backlog.util.GolfStoreInput;
import com.ca.base.BaseSuite;
import com.ca.base.DriverFactory;
import com.ca.base.pojos.WebDriverEnum;
import com.ca.util.CommonUtil;
import com.flip.pages.LoginActions;
import com.flip.pages.LoginPage;
//import com.ca.base.WebDriverEnum;
//import com.ca.base.WebDriverEnum;
//import com.flip.util.FlipkartInput;

public class VerifyFlipLogin extends BaseSuite {
	
	@Test     //(dataProvider = "createUser", description = "Functionality under test- Create Users")
	public void performLogin() {
	//	LoginPage loginAsAdmin = new LoginPage(WebDriverEnum.flipkart);
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.flipkart);
		
		loginAsAdmin.loginAsGA();
	
}
	/*@Test(dataProvider = "register1", description = "Functionality under test- register")
	public void performLoginExcel(Map<String, String> testData) {
LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.flipkart);

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
