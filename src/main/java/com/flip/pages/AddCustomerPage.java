package com.flip.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ca.base.BasePage;
import com.ca.base.DriverFactory;
import com.ca.base.pojos.WebDriverEnum;

import junit.framework.Assert;

public class AddCustomerPage extends BasePage{
	
	@FindBy(xpath="//*[@id='custId']") 
	private WebElement customerIdTxt;
	
	@FindBy(xpath="//*[@id='custName']") 
	private WebElement customerNameTxt;
	
	@FindBy(xpath="//*[@id='cust']/input") 
	private WebElement submitBtn;
	
	@FindBy(xpath="//h3") 
	private WebElement SuccessMessageTxt;
	
	
	private void setCustomerId(String customerId) {
		customerIdTxt.clear();
		customerIdTxt.sendKeys(customerId);	
	}
	
	private void setCustomerName(String customerName) {
		customerNameTxt.clear();
		customerNameTxt.sendKeys(customerName);
	}
	
	private void clickSubmitBtn() {
		submitBtn.click();
	}
	
	public void addANewCustomer(String customerId, String customerName) {
		String expectedSuccesssMsg = "Successfully Added!!!";
		
		//Enter CustomerId
		setCustomerId(customerId);
		//Enter CustomerName
		setCustomerName(customerName);
		//Click Submit button
		clickSubmitBtn();
		//Validate the success message
		String actualSuccessMsg = SuccessMessageTxt.getText();
		Assert.assertEquals(expectedSuccesssMsg, actualSuccessMsg);
		
	}

}
