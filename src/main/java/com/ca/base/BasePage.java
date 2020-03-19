package com.ca.base;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ca.util.LogMode;
import com.ca.util.Result;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

import com.ca.base.pojos.WebDriverEnum;
import com.ca.base.reports.ReportLogger;
import com.aventstack.extentreports.Status;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.util.StringUtils;

public class BasePage {

	// URL Related String

	public static String desAdminConsole_URL = null;
	public static String strDESAdminLogin = null;
	public static String strDESAdminPassword = null;

	public static String desMasterAdminConsole_URL = null;
	public static String strDESMasterAdminLogin = null;
	public static String strDESMasterAdminPassword = null;

	// wait time constants
	public static final long WAIT_FIVE = 5;
	public static final long WAIT_TEN = 10;
	public static final long WAIT_TWENTY = 20;
	public static final long WAIT_THIRTY = 30;
	public static final long WAIT_SIXTY = 60;
	public static final long WAIT_ONETWENTY = 120;

	// DES Admin Page login Related

	protected WebDriverWait wait = null;

	@FindBy(xpath = "//*[@class='_39M2dM JB4AMj'][1]")
	private WebElement username;


	@FindBy(name = "//*[@class='_39M2dM JB4AMj'][2]")
	private WebElement password;

	@FindBy(xpath = "//button[@type='submit'][1]")
	private WebElement Login;
	public BasePage() {

		/*
		 * BaseSuite.caPropMap.get("GolfStore3DSUrl").trim() =
		 * BaseSuite.BaseSuite.caPropMap.get("GolfStore3DSUrl").trim();
		 * BaseSuite.caPropMap.get("GolfStoreTMUrl").trim() =
		 * BaseSuite.BaseSuite.caPropMap.get("GolfStoreTMUrl").trim();
		 * 
		 * tmAdminConsole_URL = BaseSuite.tmAdminConsole_URL; strTMAdminLogin =
		 * BaseSuite.tmAdminLogin; strTMAdminPassword =
		 * BaseSuite.tmAdminPassword;
		 * 
		 * tmMasterAdminConsole_URL = BaseSuite.tmMasterAdminConsole_URL;
		 * strTM_MasterPwdFisrst = BaseSuite.tmMasterMasterPwdFisrst;
		 * strTM_MasterPwdSecond = BaseSuite.tmMasterMasterPwdSecond;
		 * 
		 * 
		 * raAdminConsole_URL = BaseSuite.raAdminConsole_URL; strRAAdminLogin =
		 * BaseSuite.raAdminLogin; strRAAdminPassword =
		 * BaseSuite.raAdminPassword; strRAOrgName = BaseSuite.raOrgName;
		 */

	}

	public BasePage(WebDriverEnum driverEnum) {
		this();
		System.out.println("Inside BasePage");
		Long threadId = Long.valueOf(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId, driverEnum);
		PageFactory.initElements(driver, this);

	}

	public void waitForLoad(WebDriver driver) {
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(pageLoadCondition);
	}

	/**
	 * This method is common for wait and clicking the web element wait until
	 * element to be clickable with time limit
	 * 
	 * @param element
	 */
	public void waitForElementClickable(WebElement element, WebDriverEnum driverEnum) {
		Long threadId = new Long(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId, driverEnum);
		WebDriverWait wait = new WebDriverWait(driver, WAIT_SIXTY);
		wait.until(ExpectedConditions.elementToBeClickable(element));

	}

	public void waitForElementPresence(WebElement element, WebDriverEnum driverEnum) {
		Long threadId = new Long(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId, driverEnum);
		WebDriverWait wait = new WebDriverWait(driver, WAIT_SIXTY);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	public void waitForElementTextPresence(WebElement element, String text, WebDriverEnum driverEnum) {
		Long threadId = new Long(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId, driverEnum);
		WebDriverWait wait = new WebDriverWait(driver, WAIT_SIXTY);
		wait.until(ExpectedConditions.textToBePresentInElement(element, text));

	}

	public void waitforAjaxImgloader(WebDriverEnum driverEnum) {
		Long threadId = new Long(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId, driverEnum);
		By loadingImage = By.id("ajax_loader");
		WebDriverWait waitForImage = new WebDriverWait(driver, WAIT_SIXTY);
		waitForImage.until(ExpectedConditions.invisibilityOfElementLocated(loadingImage));
	}

	public void waitForElementClickable(WebElement element, long waitTime, WebDriverEnum driverEnum) {
		Long threadId = new Long(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId, driverEnum);
		WebDriverWait wait = new WebDriverWait(driver, waitTime);
		wait.until(ExpectedConditions.elementToBeClickable(element));

	}

	public void waitAndClick(WebElement element, WebDriverEnum driverEnum) {
		Long threadId = new Long(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId, driverEnum);
		WebDriverWait wait = new WebDriverWait(driver, WAIT_THIRTY);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		element.click();
	}

	public void waitAndSubmit(WebElement elementToSubmit, WebElement elementTobeAvailable, WebDriverEnum driverEnum) {
		Long threadId = new Long(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId, driverEnum);
		WebDriverWait wait = new WebDriverWait(driver, WAIT_THIRTY);
		wait.until(ExpectedConditions.visibilityOf(elementTobeAvailable));
		elementToSubmit.submit();
	}

	public void waitAndClick(WebElement element, long waitTime, WebDriverEnum driverEnum) {
		Long threadId = new Long(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId, driverEnum);
		WebDriverWait wait = new WebDriverWait(driver, waitTime);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		element.click();
	}

	public void clickAndWait(WebElement elementTobeClicked, WebElement elementTobeAvailable, WebDriverEnum driverEnum) {
		Long threadId = new Long(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId, driverEnum);
		elementTobeClicked.click();
		WebDriverWait wait = new WebDriverWait(driver, WAIT_THIRTY);
		wait.until(ExpectedConditions.visibilityOf(elementTobeAvailable));
	}

	public void clickAndWait(WebElement elementTobeClicked, WebElement elementTobeAvailable, long waitTime,
			WebDriverEnum driverEnum) {
		Long threadId = new Long(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId, driverEnum);
		elementTobeClicked.click();
		WebDriverWait wait = new WebDriverWait(driver, waitTime);
		wait.until(ExpectedConditions.visibilityOf(elementTobeAvailable));
	}

	public void waitForFrameAndSwitch(String frameid, WebDriverEnum driverEnum) {
		Long threadId = new Long(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId, driverEnum);
		WebDriverWait wait = new WebDriverWait(driver, 60);
		WebElement frame = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(frameid)));
		driver.switchTo().defaultContent();
		driver.switchTo().frame(frame);
		/*
		 * BaseSuiteNew.takeScreenShot(Status.INFO, "Frame to be switched",
		 * LogMode.DEBUG);
		 */

	}

	public static boolean isElementPresent(WebElement element) {

		boolean isPresent = false;
		try {
			element.getSize();
			isPresent = true;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return isPresent;
	}

	public boolean checkValueIsNullorNot(String value) {
		return StringUtils.hasText(value);
	}

	public void selectValueFromDropdown(WebElement ele, String value) {
		if (isElementPresent(ele)) {
			Select sel = new Select(ele);
			if (checkValueIsNullorNot(value)) {
				sel.selectByVisibleText(value);
			}
		}
	}

	public void clickOnCheckBoxOrRadioBtn(boolean flag, WebElement ele, WebDriverEnum driverEnum) {
		if (flag) {
			if (!ele.isSelected()) {
				waitAndClick(ele, driverEnum);
			}
		} else {
			if (ele.isSelected()) {
				waitAndClick(ele, driverEnum);
			}
		}
	}

	public void scrollToElement(WebElement element, WebDriverEnum driverEnum,String strDeviceName) {
		Long threadId = new Long(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId,strDeviceName,driverEnum);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}
	
	public void javaScriptBtnClick(WebElement element, WebDriverEnum driverEnum,String strDeviceName) {
		Long threadId = new Long(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId,strDeviceName,driverEnum);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
	}
	
	/**
	 * This method is common for wait and clicking the web element wait until
	 * element to be clickable with time limit
	 * 
	 * @param element
	 */
	public void waitForElementClickable(WebElement element, WebDriverEnum driverEnum,String strDeviceName) {
		Long threadId = new Long(Thread.currentThread().getId());
		WebDriver driver = DriverFactory.getDriver(threadId,strDeviceName,driverEnum);
		WebDriverWait wait = new WebDriverWait(driver, WAIT_TEN);
		wait.until(ExpectedConditions.elementToBeClickable(element));

	}

	public boolean isAlertPresent(WebDriverEnum driverEnum,String strDeviceName) {
		try {
			Long threadId = new Long(Thread.currentThread().getId());
			WebDriver driver = DriverFactory.getDriver(threadId,strDeviceName,driverEnum);
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException ex) {
			return false;
		}
	}
	
	public void enterText(WebElement ele,String text, WebDriverEnum driverEnum) {
		isElementPresent(ele);
		ele.clear();
		ele.sendKeys(text);
	}
	
	public void scrollDownInMobileBrowser() {
		Long threadId = new Long(Thread.currentThread().getId());
		AndroidDriver driver = (AndroidDriver) DriverFactory.getDriver(threadId, "Android", WebDriverEnum.GolfStore2);

		// The viewing size of the device
		Dimension size = driver.manage().window().getSize();
		// x position set to mid-screen horizontally
		int width = size.width / 2;
		// Starting y location set to 80% of the height (near bottom)
		int startPoint = (int) (size.getHeight() * 0.80);
		// Ending y location set to 20% of the height (near top)
		int endPoint = (int) (size.getHeight() * 0.20);

		new TouchAction(driver).press(PointOption.point(width, startPoint))
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000))).moveTo(PointOption.point(width, endPoint))
				.release().perform();

	}
	
		
	public void scrollRightInMobileBrowser() {
		Long threadId = new Long(Thread.currentThread().getId());
		AndroidDriver driver = (AndroidDriver) DriverFactory.getDriver(threadId, "Android", WebDriverEnum.GolfStore2);

		// The viewing size of the device
		Dimension size = driver.manage().window().getSize();
		// x position set to mid-screen horizontally
		int startY = size.height / 2;
		// Starting y location set to 80% of the height (near bottom)
		int endX = (int) (size.width * 0.30);
		// Ending y location set to 20% of the height (near top)
		int startX = (int) (size.width * 0.70);

		new TouchAction(driver).press(PointOption.point(startX, startY))
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000))).moveTo(PointOption.point(endX, startY))
				.release().perform();

	}
	
	public void scrollUpInMobileBrowser() {
		Long threadId = new Long(Thread.currentThread().getId());
		AndroidDriver driver = (AndroidDriver) DriverFactory.getDriver(threadId, "Android", WebDriverEnum.GolfStore2);

		// The viewing size of the device
		Dimension size = driver.manage().window().getSize();
		// x position set to mid-screen horizontally
		int width = size.width / 2;
		// Starting y location set to 80% of the height (near bottom)
		int startPoint = (int) (size.getHeight() * 0.20);
		// Ending y location set to 20% of the height (near top)
		int endPoint = (int) (size.getHeight() * 0.80);

		new TouchAction(driver).press(PointOption.point(width, startPoint))
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000))).moveTo(PointOption.point(width, endPoint))
				.release().perform();

	}
	
	public void scrollLeftInMobileBrowser() {
		Long threadId = new Long(Thread.currentThread().getId());
		AndroidDriver driver = (AndroidDriver) DriverFactory.getDriver(threadId, "Android", WebDriverEnum.GolfStore2);

		// The viewing size of the device
		Dimension size = driver.manage().window().getSize();
		// x position set to mid-screen horizontally
		int startY = size.height / 2;
		// Starting y location set to 80% of the height (near bottom)
		int endX = (int) (size.width * 0.80);
		// Ending y location set to 20% of the height (near top)
		int startX = (int) (size.width * 0.20);

		new TouchAction(driver).press(PointOption.point(startX, startY))
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000))).moveTo(PointOption.point(endX, startY))
				.release().perform();

	}
}
