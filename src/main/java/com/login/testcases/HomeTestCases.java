package com.login.testcases;

import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.base.BaseSuite;
import com.base.DriverFactory;
import com.base.pojos.WebDriverEnum;
import com.ca.util.CommonUtil;
import com.login.pages.HomeActions;
import com.login.pages.LoginActions;
import com.login.pages.LoginPage;

public class HomeTestCases extends BaseSuite {
	
	
	@Test    
	//verify navigation to home page after clicking on add customer and show cusomer
	public void verifyHomeNavigation() {
		LoginActions loginAsAdmin = new LoginActions(WebDriverEnum.custApp);
		loginAsAdmin.loginAsGA();
		HomeActions homeActionsObj = new HomeActions(WebDriverEnum.custApp);
		homeActionsObj.clickHomeLinkAndVerifyNavigation();
		
}

	
	
}
