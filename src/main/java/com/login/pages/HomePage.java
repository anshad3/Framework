package com.login.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.base.BasePage;
import com.base.DriverFactory;
import com.base.pojos.WebDriverEnum;

public class HomePage extends BasePage{
	  private WebDriver driver = null;
		
			public HomePage(WebDriverEnum driverEnum) {
			       
		        Long threadId = new Long(Thread.currentThread().getId());
		        driver = DriverFactory.getDriver(threadId, driverEnum);
		        PageFactory.initElements(driver, this);
		        wait = new WebDriverWait(driver, 10);
			}
	
	
		@FindBy(xpath="//a[text()='Home']") private WebElement homeLink;

		@FindBy(xpath="//a[contains(text(),'Add')]") private WebElement addCustomerLink;
		
		@FindBy(xpath="//a[contains(text(),'Show')]") private WebElement showCustomersLink;
		
		@FindBy(xpath="//b[text()='Hello Customer!!!']") private WebElement helloCustomerText;
		
		@FindBy(linkText="Logout") private WebElement logOutLink;
		
		public void clickHomeLink() {
			homeLink.click();
		}
		
		public void clickaddCustomerLink() {
			addCustomerLink.click();
		}
		
		public void clickshowCustomersLink() {
			showCustomersLink.click();
		}
		
		public boolean checkHelloCustomerTextDisplayed() {
			boolean flag=false;
			if(helloCustomerText.isDisplayed()) {
				flag=true;
			}
			return flag;
		}
		

		public void clicklogOutLink() {
			logOutLink.click();
		}
		
}
