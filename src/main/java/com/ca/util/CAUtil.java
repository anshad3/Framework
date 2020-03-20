package com.ca.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;
import com.aventstack.extentreports.Status;
import com.base.reports.ReportLogger;;

public class CAUtil {

	public int selectDropdownOption(WebDriver driver, WebElement dropDown, String strOptionItem) {

		/*WebElement ddWebElement;
		try {
			WebDriverWait wait = new WebDriverWait(driver, 30);
			ddWebElement = wait.until(ExpectedConditions.elementToBeClickable(dropDown));
		} catch (TimeoutException e) {
			return -3;
		}

		// Checking whether DropDown was located
		if (ddWebElement == null) {

			// -3 indicates presence of Dropdown was not located
			return -3;
		}

		// Wait till Options of dropdown are loaded
		final Select droplist;
		try {
			droplist = new Select(dropDown);
			new FluentWait<WebDriver>(driver).withTimeout(60, TimeUnit.SECONDS).pollingEvery(100, TimeUnit.MILLISECONDS)
					.until(new Predicate<WebDriver>() {

						public boolean apply(WebDriver d) {
							return (!droplist.getOptions().isEmpty());
						}
					});
		} catch (TimeoutException e) {
			return -2;
		}

		// Checking whether DropDown was loaded with items
		if (droplist.getOptions().size() == 0) {

			// -2 indicates Dropdown items were not loaded.
			return -2;
		}

		// Find the index of Option item in dropdown
		int index = -1;
		int count = 0;
		List<WebElement> lstOptions = droplist.getOptions();
		for (WebElement option : lstOptions) {
			String text = option.getText();
			if (text.trim().equalsIgnoreCase(strOptionItem.trim())) {
				return count;

			}
			count++;
		}*/

		// -1 will returned if Option item was not in dropdown
		return -1;
	}

	public Result generateReturnMessage(int index, String dropDownName, String optionName) {
		Result resultMsg = new Result();

		if (index == -3) {
			resultMsg.setUiSuccess(false);
			resultMsg.setUiOutputMsg("Unable to locate DropDown '" + dropDownName + "'");
			return resultMsg;
		}

		if (index == -2) {
			resultMsg.setUiSuccess(false);
			resultMsg.setUiOutputMsg("Option Items were not loaded in the DropDown '" + dropDownName + "'");
			return resultMsg;
		}

		if (index == -1) {
			resultMsg.setUiSuccess(false);
			resultMsg
					.setUiOutputMsg("Option Item '" + optionName + "' was not found in DropDown'" + dropDownName + "'");
			return resultMsg;
		}
		return resultMsg;

	}

	public String getFileContents(String filePath) {

		String destdir = System.getProperty("user.dir");
		FileReader file = null;
		BufferedReader reader = null;
		StringBuilder strBuilder = new StringBuilder();
		String headerFullPath = destdir + "/" + filePath;
		System.out.println("Reading from the Header file : " + headerFullPath);

		try {
			file = new FileReader(headerFullPath);
			reader = new BufferedReader(file);
			int count = 1;
			String line = null;
			line = reader.readLine();

			while (line != null) {
				if (line.trim().length() > 0) {
					strBuilder.append(line);
					strBuilder.append("\n");
				} else {
					strBuilder.append("\n");
				}
				line = reader.readLine();
			}
		} catch (Exception e) {
			System.out.println("Error in Csv File Reading  " + filePath);
			System.out.println(e.getMessage());

		}
		return strBuilder.toString().trim();
	}

	public void showComparedMessagesInExtentReport(String expectedMessage, String actualMessage) {
		if (expectedMessage.trim().equals(actualMessage.trim())) {
			ReportLogger.logInfo(Status.PASS,
					"Expected = <font face=\"verdana\" color=\"green\">" + expectedMessage + "</font>"
							+ "Actual = <font face=\"verdana\" color=\"green\"> " + actualMessage + "</font>");
		} else {
			ReportLogger.logInfo(Status.FAIL, "Expected = <font face=\"verdana\" color=\"red\">" + expectedMessage
					+ "</font>" + "Actual = <font face=\"verdana\" color=\"red\"> " + actualMessage + "</font>");
		}
	}

	public void showComparedMessagesInExtentReport(String descritpion, String expectedMessage, String actualMessage) {
		if (expectedMessage != null && actualMessage != null) {
			if (expectedMessage.trim().equals(actualMessage.trim())) {
				ReportLogger.logInfo(Status.PASS,
						descritpion + "<br>" + "Expected = <font face=\"verdana\" color=\"green\">" + expectedMessage
								+ "</font>" + "<br>Actual = <font face=\"verdana\" color=\"green\"> " + actualMessage
								+ "</font>");
			} else {
				ReportLogger.logInfo(Status.FAIL,
						descritpion + "<br>" + "Expected = <font face=\"verdana\" color=\"red\">" + expectedMessage
								+ "</font>" + "<br>Actual = <font face=\"verdana\" color=\"red\"> " + actualMessage
								+ "</font>");
			}
		} else {
			ReportLogger.logInfo(Status.FAIL,
					descritpion + "<br>" + "Expected = <font face=\"verdana\" color=\"red\">" + expectedMessage
							+ "</font>" + "<br>Actual = <font face=\"verdana\" color=\"red\"> " + actualMessage
							+ "</font>");
		}
	}

	public void showStatusInExtentReport(Status status, String desc) {
		if (status.equals(Status.PASS)) {
			ReportLogger.logInfo(status, "<font face=\"verdana\" color=\"green\"> " + desc + "</font>");
		} else if (status.equals(Status.FAIL)) {
			ReportLogger.logInfo(status, "<font face=\"verdana\" color=\"red\"> " + desc + "</font>");
		}
	}

	public Map<String, String> split(String input, String seperator) {
		Map map = new LinkedHashMap<String, String>();
		String[] str = input.split(seperator);
		for (int i = 0; i <= str.length - 1; i++) {
			map.put("COLUMN:" + i, str[i]);
		}
		return map;
	}
	
	
	public void replaceWithSystemVariables(Map<String, String> caPropMap){
		
		for(String propKey:caPropMap.keySet()){
			
			if(System.getenv(propKey)!=null){
			
				caPropMap.put(propKey, System.getenv(propKey));
				
			}
		}
	}

}
