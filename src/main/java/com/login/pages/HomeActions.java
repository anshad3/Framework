package com.login.pages;


import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;
import com.base.BasePage;
import com.base.DriverFactory;

import com.base.pojos.WebDriverEnum;
import com.base.reports.ReportLogger;
import com.flip.pages.AddCustomerPage;
import com.flip.pages.ShowCustomersPage;

public class HomeActions  extends BasePage{

	private WebDriver driver;
	private static Logger writeLogAs = LogManager.getLogger(LoginActions.class.getSimpleName());
	
	public HomeActions(WebDriver driver) {
		this.driver = driver;
	}
	public HomeActions(WebDriverEnum driverEnum) {
	       
        Long threadId = new Long(Thread.currentThread().getId());
        driver = DriverFactory.getDriver(threadId, driverEnum);
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, 10);
	}
	
	
	public void NavigateToAddCustomerLink() {
		HomePage homePageObj = new HomePage(WebDriverEnum.custApp);
		homePageObj.clickaddCustomerLink();
		//Navigation to add customer link
		ReportLogger.logScreenShot(Status.PASS, "Verify navigation to Add Customer link", WebDriverEnum.custApp);
	}
	
	public void VerifyNavigationFromAddCustomerPageToHomePage()  {
		
		NavigateToAddCustomerLink();
		//validating if customer Id textbox is present in Add Customer Page
		
		AddCustomerPage addCustObj = new AddCustomerPage(WebDriverEnum.custApp);
		boolean customerIdTextBoxPresence= addCustObj.checkCustomerIdTxtBoxDisplayed();
		//boolean customerIdTextBoxPresence= driver.findElement(By.xpath("//*[@id='custId']")).isDisplayed();
		if(customerIdTextBoxPresence) {
			ReportLogger.logInfo(Status.PASS, "Successfully navigated to Add Customer Page");
		}
		else {ReportLogger.logInfo(Status.FAIL, "Failed to navigate to add customer page");}
		
		VerifyNavigationToHomePage();
	}
	
	    public void VerifyNavigationToHomePage() {
	    	HomePage homePageObj = new HomePage(WebDriverEnum.custApp);
	    	homePageObj.clickHomeLink();
			
	    	//validate navigation to home is successful
			ReportLogger.logScreenShot(Status.PASS, "Verify navigation to Home", WebDriverEnum.custApp);
			
			//validating if hello customer text is displayed in home page
			boolean helloCustTextPresence= homePageObj.checkHelloCustomerTextDisplayed();
			if(helloCustTextPresence) {
				ReportLogger.logInfo(Status.PASS, "Successfully navigated to Home page");
			}
			else {ReportLogger.logInfo(Status.FAIL, "Failed to navigate to Home page");}
	    }
	    
	    public void NavigateToShowCustomersLink() {
	    	HomePage homePageObj = new HomePage(WebDriverEnum.custApp);
			homePageObj.clickshowCustomersLink();
			//Navigation to show customer link
			ReportLogger.logScreenShot(Status.PASS, "Verify navigation to Show Customers link", WebDriverEnum.custApp);
	    }
	    
        public void VerifyNavigationFromShowCustomerPageToHomePage() {
		
        NavigateToShowCustomersLink();
		
		ShowCustomersPage showCustObj = new ShowCustomersPage(WebDriverEnum.custApp);
		
		//validating if show all customer header is present in show customers page
		boolean showAllCustomersHeaderPresence= showCustObj.showAllCustomersHeaderValidation();
		
		if(showAllCustomersHeaderPresence) {
			ReportLogger.logInfo(Status.PASS, "Successfully navigated to Show Customers Page");
		}
		else {ReportLogger.logInfo(Status.FAIL, "Failed to Navigate to Show Customers Page");}
		
		VerifyNavigationToHomePage();
	}
        
       public void verifyLogOutFunctionality() {

   		HomePage homePageObj = new HomePage(WebDriverEnum.custApp);
   		homePageObj.clicklogOutLink();
   		ReportLogger.logScreenShot(Status.PASS, "Verify navigation to login screen after clicking on LogOut", WebDriverEnum.custApp);
   		LoginPage loginPageObj = new LoginPage(WebDriverEnum.custApp);
   		boolean loginButtonPresence= loginPageObj.verifySignInButtonDisplayed();
   		if(loginButtonPresence) {
			ReportLogger.logInfo(Status.PASS, "Successfully navigated to Login Page");
		}
		else {ReportLogger.logInfo(Status.FAIL, "Failed to Navigate to Login Page");}
   
       } 
       
       public void verifyLogOutFunctionalityFromAddCustomerPage() {
    	   
       }
       
      
	
}
