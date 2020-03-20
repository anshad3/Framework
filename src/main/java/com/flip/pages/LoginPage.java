package com.flip.pages;


	//package com.ca.am.pages.logins;

	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
	import org.openqa.selenium.support.FindBy;
	import org.openqa.selenium.support.PageFactory;
	import org.openqa.selenium.support.ui.WebDriverWait;

	import com.ca.base.BasePage;
	import com.ca.base.DriverFactory;
	import com.ca.base.pojos.WebDriverEnum;

	public class LoginPage  extends BasePage{
		
	    private WebDriver driver = null;
		
		public LoginPage(WebDriverEnum driverEnum) {
		       
	        Long threadId = new Long(Thread.currentThread().getId());
	        driver = DriverFactory.getDriver(threadId, driverEnum);
	        PageFactory.initElements(driver, this);
	        wait = new WebDriverWait(driver, 10);
		}

		
		//@FindBy(xpath="//A[@class='account-header-menu-item__signin-register-link']") private WebElement orgNameText;
		@FindBy(xpath="//BUTTON[@class='btn btn-lg btn-primary btn-block'][text()='Sign in']") private WebElement logInBtn;
		@FindBy(xpath="//INPUT[@id='username']") private WebElement userNameText;
		@FindBy(xpath="//INPUT[@id='password']") private WebElement passwordText;
		
		@FindBy(xpath="(//A[text()='Logout'])[1]") private WebElement logOutLink;
		
	/*	public void clickOrgName() {
			//orgNameText.clear();
			orgNameText.click();
		}*/
		public void clickLoginBtn() {
			logInBtn.click();
		}
		public void setUserName(String userName) {
			userNameText.clear();
			userNameText.sendKeys(userName);
		}
		public void setPassword(String password) {
			passwordText.clear();
			passwordText.sendKeys(password);
		}
		
		public void clickLogoutLink() {
			logOutLink.click();
		}
	}


