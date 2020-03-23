package com.flip.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.base.BasePage;
import com.base.DriverFactory;
import com.base.pojos.WebDriverEnum;

import junit.framework.Assert;

public class ShowCustomersPage extends BasePage{
	  private WebDriver driver = null;
	public ShowCustomersPage(WebDriverEnum driverEnum) {
	       
        Long threadId = new Long(Thread.currentThread().getId());
        driver = DriverFactory.getDriver(threadId, driverEnum);
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, 10);
	}

	
	
	
	@FindBy(xpath="//h3[text()='Show All Customers']") 
	private WebElement allCustomersTxt;
	
	public boolean showAllCustomersHeaderValidation() {
		boolean flag=false;
		if(allCustomersTxt.isDisplayed()) {
			flag=true;
		}
		return flag;
	}
	
}
