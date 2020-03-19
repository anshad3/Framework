package com.ca.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import java.util.Map;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ca.base.BaseSuite;
import com.ca.base.reports.ReportLogger;
import com.aventstack.extentreports.Status;;

public class reportUtilDOntUse {

	private static Boolean isAllTrue = null;
	
	public String generateHTMLReportForXML(String testCaseName, String hyperLinkName, String htmlData) {
		String outputFile = null;
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		try {
			ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String timeStamp = getCurrentTimeStamp();
		if(testCaseName == null)
			testCaseName = "Html";
		if(hyperLinkName == null)
			hyperLinkName = "Expected And Actual Result";
		testCaseName = testCaseName+"_"+ ranInt + "_" + timeStamp+ ".html";
		String destDirFile = createDestDirFile(testCaseName);
		
		outputFile = createHTMLFileForXMl(destDirFile, htmlData, testCaseName, hyperLinkName);
		
		return outputFile;
	}
	
	
	
	public int selectDropdownOption(WebDriver driver,WebElement dropDown, String strOptionItem){
		
		
		/*WebElement ddWebElement;
		try {
			WebDriverWait  wait = new WebDriverWait(driver, 30);
			ddWebElement = wait.until(ExpectedConditions.elementToBeClickable(dropDown));
		} catch (TimeoutException e) {
			return -3;
		}
		
		
		//Checking whether DropDown was located
		if(ddWebElement==null){
			
			//-3 indicates presence of Dropdown was not located
			return -3;
		}
		
		//Wait till Options of dropdown are loaded
		Select droplist;
		try {
			droplist = new Select(dropDown);
			new FluentWait<WebDriver>(driver)
			        .withTimeout(60, TimeUnit.SECONDS)
			        .pollingEvery(100, TimeUnit.MILLISECONDS)
			        .until(new Predicate<WebDriver>() {

			            public boolean apply(WebDriver d) {
			                return (!droplist.getOptions().isEmpty());
			            }
			        });
		} catch (TimeoutException e) {
			return -2;
		}
		
		//Checking whether DropDown was loaded with items
		if(droplist.getOptions().size()==0){

			//-2 indicates Dropdown items were not loaded.
			return -2;
		}
		
		//Find the index of Option item in dropdown		
		int index = -1;
		int count = 0;
		List<WebElement> lstOptions = droplist.getOptions();
		for(WebElement option : lstOptions){
			String text = option.getText();
			if(text.trim().equalsIgnoreCase(strOptionItem.trim())){
				return count;
				
			}
			count++;
		}
		
		//-1 will returned if Option item was not in dropdown*/
		return -1;
	}
	
	public Result generateReturnMessage(int index, String dropDownName, String optionName){
		Result resultMsg = new Result();
		
		if(index==-3){
			resultMsg.setUiSuccess(false);
			resultMsg.setUiOutputMsg("Unable to locate DropDown '"+dropDownName+"'");
			return resultMsg;
		}
		
		if(index==-2){
			resultMsg.setUiSuccess(false);
			resultMsg.setUiOutputMsg("Option Items were not loaded in the DropDown '"+dropDownName+"'");
			return resultMsg;
		}
		
		if(index==-1){
			resultMsg.setUiSuccess(false);
			resultMsg.setUiOutputMsg("Option Item '"+ optionName +"' was not found in DropDown'"+ dropDownName +"'");
			return resultMsg;
		}
		return resultMsg;
		
	}

	public String generateHTMLReportForInputHtml(String inputHtml,
			String testCaseName, String tableDisplayName, String detailDesc) {
		String outputFile = null;
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		try {
			ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (inputHtml != null) {
			String timeStamp = getCurrentTimeStamp();
			if (testCaseName == null)
				testCaseName = "Html";
			if (tableDisplayName == null)
				tableDisplayName = "Input XL Sheet Data";
			testCaseName = testCaseName.replace(' ', '_');
			testCaseName = testCaseName + "_" + ranInt + "_" + timeStamp
					+ ".html";
			String destDirFile = createDestDirFileName(testCaseName);
			StringBuilder tableData = new StringBuilder(inputHtml);
			String htmlData = createHTMLFileData(tableData, tableDisplayName);
			outputFile = createHTMLFile(destDirFile, htmlData, testCaseName,
					detailDesc);
			// childtest.log(Status.INFO, "Test_Data", outputFile);
		}
		System.out.println(" out file name:" + outputFile);
		return outputFile;
	}

	public Map<String, Map<String, String>> getInputData(String fileName,
			String sheetName) {
		String testCaseIdKey = null;
		ReadExcel re = new ReadExcel();

		testCaseIdKey = "TestCaseID";
		System.out.println("File Name =" + fileName);

		Map<String, Map<String, String>> mom = null;
		try {
			System.out.println("Accessing file Name " + fileName);
			System.out.println("Accessing Sheet Name in Common Util"
					+ sheetName);
			mom = re.getTestAllData(fileName, sheetName, testCaseIdKey);
		} catch (ExcelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mom;
	}
	
	
	
	/** 
	 * 
	 * @param resultInfo : three key has to be pass in this map
	 * 					   * actualResult
	 * 					   * expectedResult
	 * 					   * screenShotPath	 
	 * 					   
	 * @param strTestCaseName
	 * @param displayName : display link string
	 */
	
	public boolean generateHTMLreportForCardSearchResult(LinkedHashMap<String, HashMap<String, String>> resultInfo, String strTestCaseName,String displayName) {
		
		RandomNumberAndString ranNum = new RandomNumberAndString();
		boolean status = true;
		Long ranInt = null;
		try {
			ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String timeStamp = getCurrentTimeStamp();
		strTestCaseName = strTestCaseName.replace(' ', '_');
		strTestCaseName = strTestCaseName + "_" + ranInt + "_" + timeStamp
				+ ".html";
	
		String destDirFile = createDestDirFileName(strTestCaseName);
		
		StringBuilder displayContent = createDisplayData(resultInfo);
		
		String htmlData = createHTMLFileData(displayContent,displayName);
		
		String htmlFile = createHTMLFile(destDirFile,htmlData,strTestCaseName,displayName);
		
		if (htmlData.toString().contains("color=\"red\"")) {
			ReportLogger.logInfo(Status.FAIL, htmlFile);
			status = false;
			
		} else{
			ReportLogger.logInfo(Status.PASS, htmlFile);
		}
		return status;	
			
		
	}

	
	private StringBuilder createDisplayData(LinkedHashMap<String, HashMap<String, String>> resultInfo) {
	
		StringBuilder tableData = new StringBuilder();
		String tableStartTag = "<table><style>table, th, td {    border: 1px solid black;    border-collapse: collapse;}</style><col width=\"150\"><col width=\"\"><col width=\"\"><col width=\"200\">";
		String tableEndTag = "</table>";
		String thStartTag = "<th>";
		String thEndTag = "</th>";
		String trStartTag = "<tr>";
		String trEndTag = "</tr>";
		String tdStartTag = "<td>";
		String tdEndTag = "</td>";
	
		tableData.append(tableStartTag);
	
		tableData.append(thStartTag);
		tableData.append("Card Number");
		tableData.append(thEndTag);
		
		tableData.append(thStartTag);
		tableData.append("Expected result");
		tableData.append(thEndTag);
	
		tableData.append(thStartTag);
		tableData.append("Actual result");
		tableData.append(thEndTag);
		
		tableData.append(thStartTag);
		tableData.append("Screen Shots");
		tableData.append(thEndTag);
		
		for (String key : resultInfo.keySet()) {
			
			tableData.append(trStartTag);
			// <td>key</td>
			tableData.append(tdStartTag);
			tableData.append(key);
			tableData.append(tdEndTag);
			
			if(resultInfo.get(key).get("expectedResult").equals(resultInfo.get(key).get("actualResult"))){
				
				// <td>key</td>
				tableData.append(tdStartTag);
				tableData.append("<font color=\"green\">"+resultInfo.get(key).get("expectedResult")+"</font>");
				tableData.append(tdEndTag);
				
				// <td>key</td>
				tableData.append(tdStartTag);
				tableData.append("<font color=\"green\">"+resultInfo.get(key).get("actualResult")+"</font>");
				tableData.append(tdEndTag);
				
			}else{
				
				// <td>key</td>
							tableData.append(tdStartTag);
							tableData.append("<font color=\"red\">"+resultInfo.get(key).get("expectedResult")+"</font>");
							tableData.append(tdEndTag);
							
							// <td>key</td>
							tableData.append(tdStartTag);
							tableData.append("<font color=\"red\">"+resultInfo.get(key).get("actualResult")+"</font>");
							tableData.append(tdEndTag);
				
			}
			
			// <td>value</td>
					tableData.append("<td align=\"center\">");
					tableData.append("<img src="+resultInfo.get(key).get("screenShotPath")+" width=\"80%\" height=\"15%\">");
					tableData.append(tdEndTag);
			
			
			tableData.append(trEndTag);
		}
		tableData.append(tableEndTag);
		return tableData;
		
	}

	private String getCurrentTimeStamp() {
		Date d = new Date();
		Timestamp t = new Timestamp(d.getTime());
		System.out.println(t);
		String timeStamp = t.toString();
		timeStamp = timeStamp.replace(' ', '_');
		timeStamp = timeStamp.replace(':', '_');
		return timeStamp;
	}

	private String createDestDirFileName(String strHtmlFileName) {
		String destdir1 = System.getProperty("user.dir");
		String destDirFile = destdir1 + File.separator + "TestResult"
				+ File.separator + "HTML" + File.separator + strHtmlFileName;
		System.out.println("dest dir File===========>" + destDirFile);
		return destDirFile;
	}

	private StringBuilder createTableData(Map<String, String> inputMap) {
		StringBuilder tableData = new StringBuilder();
		String tableStartTag = "<table>";
		String tableEndTag = "</table>";
		String thStartTag = "<th>";
		String thEndTag = "</th>";
		String trStartTag = "<tr>";
		String trEndTag = "</tr>";
		String tdStartTag = "<td>";
		String tdEndTag = "</td>";

		tableData.append(tableStartTag);

		tableData.append(thStartTag);
		tableData.append("Key Name");
		tableData.append(thEndTag);

		tableData.append(thStartTag);
		tableData.append("Value Details");
		tableData.append(thEndTag);

		tableData.append(trStartTag);
		for (String key : inputMap.keySet()) {
			// <td>key</td>
			tableData.append(tdStartTag);
			tableData.append(key);
			tableData.append(tdEndTag);

			// <td>value</td>
			tableData.append(tdStartTag);
			tableData.append(inputMap.get(key));
			tableData.append(tdEndTag);

			tableData.append(trEndTag);
		}
		tableData.append(tableEndTag);
		return tableData;
	}
	
	private StringBuilder createTableDataWithListCompare(Map<String, String> inputMap,ArrayList<String> lstTagNames) {
		StringBuilder tableData = new StringBuilder();
		String tableStartTag = "<table>";
		String tableEndTag = "</table>";
		String thStartTag = "<th>";
		String thEndTag = "</th>";
		String trStartTag = "<tr>";
		String trEndTag = "</tr>";
		String tdStartTag = "<td>";
		String tdStartGreenTag = "<td style=\"background-color:green\">";
		String tdEndTag = "</td>";
		HashMap<String, String> tagNameMap = new HashMap<String, String>();
		int count=1;
		tableData.append(tableStartTag);

		tableData.append(thStartTag);
		tableData.append("Key Name");
		tableData.append(thEndTag);

		tableData.append(thStartTag);
		tableData.append("Value Details");
		tableData.append(thEndTag);

		tableData.append(trStartTag);
		for (String keytrim : inputMap.keySet()) {
			// <td>key</td>
			if(keytrim.contains("Tag.")){
				tagNameMap.put(
                              keytrim.substring(4, keytrim.length()), 
                              inputMap.get(keytrim));  
          }else if(count>5){
            tagNameMap.put(keytrim, inputMap.get(keytrim));
			}
			count++;
		}

		for (String key : tagNameMap.keySet()) {
			// <td>key</td>
			tableData.append(tdStartTag);	
			if(lstTagNames.contains(key)){
				
				tableData.append("<font face=\"verdana\" color=\"green\">");
			}
			
			tableData.append(key);
			
			if(lstTagNames.contains(key)){
				
				tableData.append("</font>");
			}
			tableData.append(tdEndTag);

			// <td>value</td>
			tableData.append(tdStartTag);
			if(lstTagNames.contains(key)){
				
				tableData.append("<font face=\"verdana\" color=\"green\">");
			}
			tableData.append(tagNameMap.get(key));
			tableData.append(tdEndTag);

			if(lstTagNames.contains(key)){
				
				tableData.append("</font>");
			}
			tableData.append(trEndTag);
			
		}
		tableData.append(tableEndTag);
		return tableData;
	}

	
	public HashMap<String, String> getInputFromDatasheet(Map<String, String> testCaseData){
		HashMap<String, String> tagTrimmedMap = new HashMap<String, String>();
		for (String keytrim : testCaseData.keySet()) {
			// <td>key</td>
			if(keytrim.contains("Tag.")){
				tagTrimmedMap.put(
                              keytrim.substring(4, keytrim.length()), 
                              testCaseData.get(keytrim));  
          }
				
		}
		return tagTrimmedMap;
	}
	
	
	
	
	private String createHTMLFileData(StringBuilder tableData,
			String htmlDisplayName) {
		StringBuilder sb = new StringBuilder();
		String header = "<!DOCTYPE html><html><head><style>table, th, td {    border: 1px solid black;    border-collapse: collapse;}th, td {    padding: 5px;}</style> <meta charset=\"UTF-8\"></head><body> ";
		String title = "<h2><left>" + htmlDisplayName + "</left></h2>";
		String closingTags = "</body></html>";
		sb.append(header);
		sb.append(title);
		sb.append(tableData);
		sb.append("<br/>");
		sb.append("<br/>");
		sb.append(closingTags);
		return sb.toString();
	}
	
	private String createHTMLFileForXMl(String destDirFile, String htmlData,
			String testCaseName, String hyperLinkName) {
		String relativedir = ".." + File.separator + "HTML" + File.separator;
		String strOutputHtml = htmlData;
		File fss = new File(destDirFile);
		if (!htmlData.startsWith("<HTML>\r\n<HEAD")) {
		String strBeginHtmlPart1 = "<HTML><Body><Center><H2>";
		String strBeginHtmlPart2 = "</H2></Center><pre class='brush: xml;'><h3>";
		String strEndHtmlPart = "</h3></pre></Body></Html>";
			htmlData = htmlData.replace("<", "&lt;");
			htmlData = htmlData.replace(">", "&gt;");
		
		strOutputHtml = strBeginHtmlPart1+hyperLinkName+strBeginHtmlPart2+htmlData+strEndHtmlPart;
	}
		fss.getParentFile().mkdirs();
		FileWriter fw = null;
		try {
			fss.createNewFile();
			fw = new FileWriter(fss.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(strOutputHtml);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String reldirFile = relativedir + testCaseName;
		String outputFile = null;
		
		outputFile = "<b><a href=" + reldirFile + " target=\"_blank\" >"
					+ hyperLinkName + "</a></b>";
		return outputFile;
	}


	private String createHTMLFile(String destDirFile, String htmlData,
			String testCaseName, String hyperLinkName) {
		String relativedir = ".." + File.separator + "HTML" + File.separator;
		File fss = new File(destDirFile);
		fss.getParentFile().mkdirs();
		FileWriter fw = null;
		try {
			fss.createNewFile();
			fw = new FileWriter(fss.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(htmlData);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String reldirFile = relativedir + testCaseName;
		String outputFile = null;
		
		if (htmlData.toString().contains("red")){
			outputFile = "<b><a  href=" + reldirFile
					+ " target=\"_blank\" style=\"color: #CC0000\">"
					+ hyperLinkName + "</a></b>";
			//ReportLogger.logInfo(Status.FAIL, "Test_Data", outputFile);
		}
		else if (htmlData.toString().contains("green")){
			outputFile = "<b><a  href=" + reldirFile
					+ " target=\"_blank\" style=\"color: #00cc00\">"
					+ hyperLinkName + "</a></b>";
			//ReportLogger.logInfo(Status.PASS, "Test_Data", outputFile);
		}
		else{
			outputFile = "<b><a href=" + reldirFile + " target=\"_blank\" >"
					+ hyperLinkName + "</a></b>";
			//ReportLogger.logInfo(Status.INFO, "Test_Data", outputFile);
		}
		return outputFile;
	}

	private String createHTMLFile(String destDirFile, String htmlData,
			String testCaseName, String tableDisplayName,
			boolean isNegativeTestScenario) {
		String relativedir = ".." + File.separator + "HTML" + File.separator;
		File fss = new File(destDirFile);
		fss.getParentFile().mkdirs();
		FileWriter fw = null;
		try {
			fss.createNewFile();
			fw = new FileWriter(fss.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(htmlData);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String reldirFile = relativedir + testCaseName;

		String outputFile = null;
		if (isAllTrue != null && isAllTrue == false)
			outputFile = "<b><a href=" + reldirFile
					+ " target=\"_blank\" style=\"color: #FF0000\">"
					+ tableDisplayName + " Passed" + "</a></b>";
		else if (isAllTrue != null && isAllTrue == true)
			outputFile = "<b><a href=" + reldirFile
					+ " target=\"_blank\" style=\"color: #008000\">"
					+ tableDisplayName + " Failed" + "</a></b>";
		else
			outputFile = "<b><a href=" + reldirFile + " target=\"_blank\" >"
					+ tableDisplayName + "</a></b>";
		isAllTrue = null;
		return outputFile;
	}

	public String generateHTMLReportForInputTestData(
			Map<String, String> inputTestDataMap, String testCaseName,
			String tableDisplayName, String detailDesc) {
		String outputFile = null;
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		try {
			ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (inputTestDataMap != null) {
			String timeStamp = getCurrentTimeStamp();
			if (testCaseName == null)
				testCaseName = "Html";
			if (tableDisplayName == null)
				tableDisplayName = "Input XL Sheet Data";
			testCaseName = testCaseName.replace(' ', '_');
			testCaseName = testCaseName + "_" + ranInt + "_" + timeStamp
					+ ".html";
			String destDirFile = createDestDirFileName(testCaseName);
			StringBuilder tableData = createTableData(inputTestDataMap);
			String htmlData = createHTMLFileData(tableData, tableDisplayName);
			outputFile = createHTMLFile(destDirFile, htmlData, testCaseName,
					detailDesc);
			// childtest.log(Status.INFO, "Test_Data", outputFile);
		}
		System.out.println(" out file name:" + outputFile);
		return outputFile;
	}
	
	public String generateHTMLReportForMapWithListCompare(
			Map<String, String> inputTestDataMap, ArrayList<String> lstTagNames,String testCaseName,
			String tableDisplayName, String detailDesc) {
		String outputFile = null;
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		try {
			ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (inputTestDataMap != null) {
			String timeStamp = getCurrentTimeStamp();
			if (testCaseName == null)
				testCaseName = "Html";
			if (tableDisplayName == null)
				tableDisplayName = "Input XL Sheet Data";
			testCaseName = testCaseName.replace(' ', '_');
			testCaseName = testCaseName + "_" + ranInt + "_" + timeStamp
					+ ".html";
			String destDirFile = createDestDirFileName(testCaseName);
			StringBuilder tableData = createTableDataWithListCompare(inputTestDataMap,lstTagNames);
			String htmlData = createHTMLFileData(tableData, tableDisplayName);
			outputFile = createHTMLFile(destDirFile, htmlData, testCaseName,
					detailDesc);
			
		}
		System.out.println(" out file name:" + outputFile);
		return outputFile;
	}

	
	
	
	
	//**************************************************************************************************************************************************
	//**************************************************************************************************************************************************
	//**************************************************************************************************************************************************
	//**************************************************************************************************************************************************
	//**************************************************************************************************************************************************
	//**************************************************************************************************************************************************
	public boolean generateHTMLReportForTwoMapData(
			Map<String, String> expectedMap, Map<String, String> actualMap,
			String hyperLinkText, String htmlDisplayText) {
		String htmlfile = null;
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		String strHtmlFileName = hyperLinkText;
		try {
			ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String timeStamp = getCurrentTimeStamp();

		strHtmlFileName = strHtmlFileName.replace(' ', '_');
		strHtmlFileName = strHtmlFileName + "_" + ranInt + "_" + timeStamp
				+ ".html";
		String destDirFile = createDestDirFileName(strHtmlFileName);
		StringBuilder mapCompareString = createTableDataForExpActualMapResult(
				expectedMap, actualMap);

		String htmlData = createHTMLFileData(mapCompareString, htmlDisplayText);
		htmlfile = createHTMLFile(destDirFile, htmlData, strHtmlFileName,
				hyperLinkText);

		if (mapCompareString.toString().contains("red")) {
			ReportLogger.logInfo(Status.FAIL, htmlfile);
			return false;
		} else
			ReportLogger.logInfo(Status.PASS, htmlfile);

		return true;
	}
	
	public boolean generateHTMLReportForTwoMapDataWithCustomizedHeaders(
			Map<String, String> expectedMap, Map<String, String> actualMap,String expectedHeading, String actualHeading,
			String hyperLinkText, String htmlDisplayText) {
		String htmlfile = null;
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		String strHtmlFileName = hyperLinkText;
		try {
			ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String timeStamp = getCurrentTimeStamp();

		strHtmlFileName = strHtmlFileName.replace(' ', '_');
		strHtmlFileName = strHtmlFileName + "_" + ranInt + "_" + timeStamp
				+ ".html";
		String destDirFile = createDestDirFileName(strHtmlFileName);
		StringBuilder mapCompareString = createTableDataForExpActualMapResultWithCustomisedHeaders(expectedHeading, actualHeading,
				expectedMap, actualMap);

		String htmlData = createHTMLFileData(mapCompareString, htmlDisplayText);
		htmlfile = createHTMLFile(destDirFile, htmlData, strHtmlFileName,
				hyperLinkText);

		if (mapCompareString.toString().contains("color=\"red\"")) {
			ReportLogger.logInfo(Status.FAIL, htmlfile);
			return false;
		} else
			ReportLogger.logInfo(Status.PASS, htmlfile);

		return true;
	}
	
	
	public boolean generateHTMLRepForTwoMapDataWithExceptionList(
															Map<String, String> expectedMap, Map<String, String> actualMap,
															String strExpectedHeading,String strActualHeading,
															String hyperLinkText, String htmlDisplayText,ArrayList<String> lstExceptions) {
		String htmlfile = null;
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		String strHtmlFileName = hyperLinkText;
		try {
			ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String timeStamp = getCurrentTimeStamp();

		strHtmlFileName = strHtmlFileName.replace(' ', '_');
		strHtmlFileName = strHtmlFileName + "_" + ranInt + "_" + timeStamp
				+ ".html";
		String destDirFile = createDestDirFileName(strHtmlFileName);
		StringBuilder mapCompareString = createTableDataForExpActualMapResultWithExceptionList(
				expectedMap,actualMap,strExpectedHeading,strActualHeading, lstExceptions);

		String htmlData = createHTMLFileData(mapCompareString, htmlDisplayText);
		htmlfile = createHTMLFile(destDirFile, htmlData, strHtmlFileName,
				hyperLinkText);

		if (mapCompareString.toString().contains("red")) {
			ReportLogger.logInfo(Status.FAIL, htmlfile);
			return false;
		} else
			ReportLogger.logInfo(Status.PASS, htmlfile);

		return true;
	}

	private String createDestDirFile(String testCaseName) {
		String destdir1 = System.getProperty("user.dir");
		String destDirFile = destdir1 + File.separator + "TestResult"
				+ File.separator + "HTML" + File.separator + testCaseName;
		System.out.println("dest dir File===========>" + destDirFile);
		return destDirFile;
	}

	private String createHTMLFile(String destDirFile, String htmlData,
			String testCaseName, boolean isSuccess) {
		String relativedir = ".." + File.separator + "HTML" + File.separator;
		File fss = new File(destDirFile);
		fss.getParentFile().mkdirs();
		FileWriter fw = null;
		try {
			fss.createNewFile();
			fw = new FileWriter(fss.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(htmlData);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String reldirFile = relativedir + testCaseName;
		// String windowOpen =
		// "<a href=\"javascript:window.open("+reldirFile+",'Satya','width=600,height=400')\">"+tableDisplayName+"</a>";
		String outputFile = null;
		if (isSuccess == false)
			outputFile = "<b><a href=" + reldirFile
					+ " target=\"_blank\" style=\"color: #FF0000\">"
					+ "Test Results - Failed </a></b>";
		else
			outputFile = "<b><a href=" + reldirFile
					+ " target=\"_blank\" style=\"color: #008000\">"
					+ "Test Results - Passed </a></b>";
		isAllTrue = null;
		return outputFile;
	}


	public String generateHTMLReportExpectedAndActualResult(
			Map<String, String> expectedReslMap,
			Map<String, String> actualReslMap, String testCaseName,
			String tableDisplayName) {
		String outputFile = null;
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		try {
			ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (expectedReslMap != null && actualReslMap != null) {
			String timeStamp = getCurrentTimeStamp();
			if (testCaseName == null)
				testCaseName = "Html";
			if (tableDisplayName == null)
				tableDisplayName = "Test Result";
			testCaseName = testCaseName + "_" + ranInt + "_" + timeStamp
					+ ".html";
			String destDirFile = createDestDirFileName(testCaseName);
			StringBuilder tableData = createTableDataForExpActualMapResult(
					expectedReslMap, actualReslMap);
			String htmlData = createHTMLFileData(tableData, tableDisplayName);
			outputFile = createHTMLFile(destDirFile, htmlData, testCaseName,
					tableDisplayName);
			// childtest.log(Status.INFO, "Expected_And_Actual_Result",
			// outputFile);
		}
		System.out.println("out file for comapare name:" + outputFile);
		return outputFile;
	}

	public StringBuilder createTableDataForExpActualMapResult(
			Map<String, String> expReslMap, Map<String, String> actReslMap) {
		StringBuilder tableData = new StringBuilder();
		
		String tableStartTag = "<table><style>table, th, td {    border: 1px solid black;    border-collapse: collapse;}th, td {    padding: 5px;}</style>";
		String tableEndTag = "</table>";
		String thStartTag = "<th>";
		String thEndTag = "</th>";
		String trStartTag = "<tr>";
		String trEndTag = "</tr>";
		String tdStartRedTag = "<td style=\"background-color:red\"";
		String tdStartGreenTag = "<td style=\"background-color:green\"";
		String tdEndTag = "</td>";

		tableData.append(tableStartTag);
		// StringBuilder firstCellData
		StringBuilder firstCellData = createSingleDivisionData(thStartTag, "",
				thEndTag);
		StringBuilder secCellData = createSingleDivisionData(thStartTag,
				"Expected", thEndTag);
		StringBuilder thrdCellData = createSingleDivisionData(thStartTag,
				"Actual", thEndTag);
		tableData.append(firstCellData);
		tableData.append(secCellData);
		tableData.append(thrdCellData);
		StringBuffer sb = compareExpectedWithActualValue(expReslMap, actReslMap);
		tableData.append(sb);
		tableData.append(tableEndTag);
		return tableData;
	}
	
	public StringBuilder createTableDataForExpActualMapResultWithCustomisedHeaders(String expectedHeading, String actualHeading,
			Map<String, String> expReslMap, Map<String, String> actReslMap) {
		StringBuilder tableData = new StringBuilder();
		
		String tableStartTag = "<table><style>table, th, td {    border: 1px solid black;    border-collapse: collapse;}th, td {    padding: 5px;}</style>";
		String tableEndTag = "</table>";
		String thStartTag = "<th>";
		String thEndTag = "</th>";
		String trStartTag = "<tr>";
		String trEndTag = "</tr>";
		String tdStartRedTag = "<td style=\"background-color:red\"";
		String tdStartGreenTag = "<td style=\"background-color:green\"";
		String tdEndTag = "</td>";

		tableData.append(tableStartTag);
		// StringBuilder firstCellData
		StringBuilder firstCellData = createSingleDivisionData(thStartTag, "",
				thEndTag);
		StringBuilder secCellData = createSingleDivisionData(thStartTag,
				expectedHeading, thEndTag);
		StringBuilder thrdCellData = createSingleDivisionData(thStartTag,
				actualHeading, thEndTag);
		tableData.append(firstCellData);
		tableData.append(secCellData);
		tableData.append(thrdCellData);
		StringBuffer sb = compareExpectedWithActualValue(expReslMap, actReslMap);
		tableData.append(sb);
		tableData.append(tableEndTag);
		return tableData;
	}
	
	public StringBuilder createTableDataForExpActualMapResultWithExceptionList(
			Map<String, String> expReslMap, Map<String, String> actReslMap, String strExpectedHeading,String strActualHeading,ArrayList<String> lstException) {
		StringBuilder tableData = new StringBuilder();
		
		String tableStartTag = "<table><style>table, th, td {    border: 1px solid black;    border-collapse: collapse;}th, td {    padding: 5px;}</style>";
		String tableEndTag = "</table>";
		String thStartTag = "<th>";
		String thEndTag = "</th>";
		String trStartTag = "<tr>";
		String trEndTag = "</tr>";
		String tdStartTag = "<td>";
		String tdEndTag = "</td>";

		tableData.append(tableStartTag);
		// StringBuilder firstCellData
		StringBuilder firstHeading = createSingleDivisionData(thStartTag, "",
				thEndTag);
		StringBuilder secHeading = createSingleDivisionData(thStartTag,
				strExpectedHeading, thEndTag);
		StringBuilder thrdHeading = createSingleDivisionData(thStartTag,
				strActualHeading, thEndTag);
		tableData.append(firstHeading);
		tableData.append(secHeading);
		tableData.append(thrdHeading);
		
		LinkedHashSet<String> allKeys = new LinkedHashSet<String>();
		Set<String> expKeys = expReslMap.keySet();
		Set<String> actKeys = actReslMap.keySet();
		allKeys.addAll(expKeys);
		allKeys.addAll(actKeys);
		tableData.append(trStartTag);
		if (expReslMap == null)
			System.out.println("expected map is null");
		if (actReslMap == null)
			System.out.println("actual map is null");
		isAllTrue = true;
		for (String key : allKeys) {
			if (expReslMap.containsKey(key) && !lstException.contains(key)) {
				
				if (actReslMap != null && actReslMap.containsKey(key)) {
					String expMapValue = expReslMap.get(key);
					//Vinod code Begins
					if(expMapValue!=null&&expMapValue.trim().length()==0){
						StringBuilder firstCellData = createSingleDivisionData(
								tdStartTag, key, tdEndTag);
						// Expected value
						StringBuilder secCellData = createSingleDivisionData(
								tdStartTag,
								"<font face=\"verdana\" color=\"green\">"
										+ "</font>", tdEndTag);
						// Actual value
						StringBuilder thrdCellData = createSingleDivisionData(
								tdStartTag,
								"<font face=\"verdana\" color=\"green\">"
										+"</font>", tdEndTag);
						tableData.append(firstCellData);
						tableData.append(secCellData);
						tableData.append(thrdCellData);

						tableData.append(trEndTag);
						continue;
						
					}
					//Vinod Code Ends
					String actMapValue = actReslMap.get(key);
					
					
					if(expMapValue !=null && expMapValue.contains("NA") && !lstException.contains(key)){
						StringBuilder firstCellData = createSingleDivisionData(tdStartTag, key, tdEndTag);
						StringBuilder secCellData = createSingleDivisionData(
								tdStartTag,expMapValue, tdEndTag);
						// Actual value
						StringBuilder thrdCellData = createSingleDivisionData(
								tdStartTag,actMapValue , tdEndTag);
						tableData.append(firstCellData);
						tableData.append(secCellData);
						tableData.append(thrdCellData);

						tableData.append(trEndTag);
					}
					else if ((expMapValue != null && (expMapValue.trim()
							.equalsIgnoreCase(actMapValue)|| lstException.contains(key)))
							|| (expMapValue == null && actMapValue == null)) { // In
						// few cases both the values would expect null
						if(!lstException.contains(key))
						{
							StringBuilder firstCellData = createSingleDivisionData(
									tdStartTag, key, tdEndTag);
							// Expected value
							StringBuilder secCellData = createSingleDivisionData(
									tdStartTag,
									"<font face=\"verdana\" color=\"green\">"
											+ expMapValue + "</font>", tdEndTag);
							// Actual value
							StringBuilder thrdCellData = createSingleDivisionData(
									tdStartTag,
									"<font face=\"verdana\" color=\"green\">"
											+ actMapValue + "</font>", tdEndTag);
							tableData.append(firstCellData);
							tableData.append(secCellData);
							tableData.append(thrdCellData);

							tableData.append(trEndTag);
						}
					} else {
						StringBuilder firstCellData = createSingleDivisionData(
								tdStartTag, key, tdEndTag);
						// Expected value
						StringBuilder secCellData = createSingleDivisionData(
								tdStartTag,
								"<font face=\"verdana\" color=\"red\">"
										+ expReslMap.get(key) + "</font>",
								tdEndTag);
						// Actual value
						StringBuilder thrdCellData = createSingleDivisionData(
								tdStartTag,
								"<font face=\"verdana\" color=\"red\">"
										+ actReslMap.get(key) + "</font>",
								tdEndTag);
						tableData.append(firstCellData);
						tableData.append(secCellData);
						tableData.append(thrdCellData);

						tableData.append(trEndTag);
						isAllTrue = false;
					}
				} else {
					
					if(!lstException.contains(key))
					{
						tableData.append(tdStartTag);
						tableData.append(key);

						// Expected value
						StringBuilder secCellData = new StringBuilder();
						if (expReslMap.get(key) != null) {
							secCellData = createSingleDivisionData(tdStartTag,
									"<font face=\"verdana\" color=\"red\">"
											+ expReslMap.get(key) + "</font>",
									tdEndTag);
						}
						// Actual value
						StringBuilder thrdCellData = new StringBuilder();
						if (actReslMap.get(key) != null) {
							thrdCellData = createSingleDivisionData(tdStartTag,
									"<font face=\"verdana\" color=\"red\">"
											+ actReslMap.get(key) + "</font>",
									tdEndTag);
						}
						tableData.append(secCellData);
						tableData.append(thrdCellData);
						tableData.append(tdEndTag);
						tableData.append(tdEndTag);

						tableData.append(trEndTag);
					}
				}
			} else {
				
				if(!lstException.contains(key))
				{
					tableData.append(tdStartTag);
					tableData.append(key);
					tableData.append(tdEndTag);

					tableData.append(tdStartTag);
					StringBuilder secCellData = new StringBuilder();
					tableData.append(secCellData);
					StringBuilder thrdCellData = new StringBuilder();
					if (actReslMap.get(key) != null) {
						thrdCellData = createSingleDivisionData(
								tdStartTag,
								"<font face=\"verdana\" color=\"red\">"
										+ actReslMap.get(key) + "</font>", tdEndTag);
					}
					tableData.append(thrdCellData);
					tableData.append(trEndTag);
				}
			}
		}
		
		/*StringBuffer sb = compareExpectedWithActualValueWithExceptionList(expReslMap, actReslMap,lstException);
		tableData.append(sb);*/
		
		tableData.append(tableEndTag);
		
		tableData.append("<br><br><br>");
		
		if(actReslMap.containsKey("JpegPath"))
		{
			String screenshotPath = actReslMap.get("JpegPath");
			String screenshot = "<img src="+screenshotPath+" width=\"50%\" height=\"50%\">";
			tableData.append(screenshot);
		}
		return tableData;
	}

	
	private StringBuilder createSingleDivisionData(String thStartTag,
			String actualValue, String thEndTag) {
		StringBuilder cellData = new StringBuilder();
		cellData.append(thStartTag);
		cellData.append(actualValue);
		cellData.append(thEndTag);
		return cellData;
	}

	public StringBuffer compareExpectedWithActualValue(
			Map<String, String> expectedMap, Map<String, String> actualMap) {
		StringBuffer tableData = new StringBuffer();
		// String tdStartREDTag = "<td style=\"background-color:red\">";
		String tdStartREDTag = "<td style=\"background-color:#FF7050\">"; // light
		// red
		String tdStartGREENTag = "<td style=\"background-color:#90EE90\">";// lightgreen
		String tdStartTag = "<td width=\"40%\">";
		String tdEndTag = "</td>";
		String trStartTag = "<tr>";
		String trEndTag = "</tr>";
		LinkedHashSet<String> allKeys = new LinkedHashSet<String>();
		Set<String> expKeys = expectedMap.keySet();
		Set<String> actKeys = actualMap.keySet();
		allKeys.addAll(expKeys);
		allKeys.addAll(actKeys);
		tableData.append(trStartTag);
		if (expectedMap == null) {
			System.out.println("expected map is null");
		}
		if (actualMap == null) {
			System.out.println("actual map is null");
		}
		isAllTrue = true;
		for (String key : allKeys) {
			if (expectedMap.containsKey(key)) {
				
				if (actualMap != null && actualMap.containsKey(key)) {
					String expMapValue = expectedMap.get(key);
					String actMapValue = actualMap.get(key);
					if(expMapValue !=null && expMapValue.equals("NA")){
						StringBuilder firstCellData = createSingleDivisionData(tdStartTag, key, tdEndTag);
						StringBuilder secCellData = createSingleDivisionData(
								tdStartTag,expMapValue, tdEndTag);
						// Actual value
						StringBuilder thrdCellData = createSingleDivisionData(
								tdStartTag,actMapValue , tdEndTag);
						tableData.append(firstCellData);
						tableData.append(secCellData);
						tableData.append(thrdCellData);

						tableData.append(trEndTag);
					}
					else if ((expMapValue != null && expMapValue
							.equalsIgnoreCase(actMapValue))
							|| (expMapValue == null && actMapValue == null)) { // In
						// few
						// cases
						// both
						// the
						// values
						// would
						// expect
						// null
						StringBuilder firstCellData = createSingleDivisionData(
								tdStartTag, key, tdEndTag);
						// Expected value
						StringBuilder secCellData = createSingleDivisionData(
								tdStartTag,
								"<font face=\"verdana\" color=\"green\">"
										+ expMapValue + "</font>", tdEndTag);
						// Actual value
						StringBuilder thrdCellData = createSingleDivisionData(
								tdStartTag,
								"<font face=\"verdana\" color=\"green\">"
										+ actMapValue + "</font>", tdEndTag);
						tableData.append(firstCellData);
						tableData.append(secCellData);
						tableData.append(thrdCellData);

						tableData.append(trEndTag);
						/*
						 * test.log(Status.PASS, "expected=" +
						 * expectedMap.get(key) + "|actual=" +
						 * actualMap.get(key));
						 */
					} else {
						StringBuilder firstCellData = createSingleDivisionData(
								tdStartTag, key, tdEndTag);
						// Expected value
						StringBuilder secCellData = createSingleDivisionData(
								tdStartTag,
								"<font face=\"verdana\" color=\"red\">"
										+ expectedMap.get(key) + "</font>",
								tdEndTag);
						// Actual value
						StringBuilder thrdCellData = createSingleDivisionData(
								tdStartTag,
								"<font face=\"verdana\" color=\"red\">"
										+ actualMap.get(key) + "</font>",
								tdEndTag);
						tableData.append(firstCellData);
						tableData.append(secCellData);
						tableData.append(thrdCellData);

						tableData.append(trEndTag);
						isAllTrue = false;
						/*
						 * test.log(Status.FAIL, "expected=" +
						 * expectedMap.get(key) + "|actual=" +
						 * actualMap.get(key));
						 */

					}
				} else {

					tableData.append(tdStartTag);
					tableData.append(key);

					// Expected value
					StringBuilder secCellData = new StringBuilder();
					if (expectedMap.get(key) != null) {
						secCellData = createSingleDivisionData(tdStartTag,
								"<font face=\"verdana\" color=\"red\">"
										+ expectedMap.get(key) + "</font>",
								tdEndTag);

					}
					// Actual value
					StringBuilder thrdCellData = new StringBuilder();
					if (actualMap.get(key) != null) {
						thrdCellData = createSingleDivisionData(tdStartTag,
								"<font face=\"verdana\" color=\"red\">"
										+ actualMap.get(key) + "</font>",
								tdEndTag);

					}

					tableData.append(secCellData);
					tableData.append(thrdCellData);
					tableData.append(tdEndTag);
					// Expected value
					/*
					 * tableData.append(tdStartTag);
					 * tableData.append(expectedMap.get(key));
					 */
					tableData.append(tdEndTag);

					tableData.append(trEndTag);

					// changed by bhavana

					/*
					 * test.log(Status.FAIL, "expected=" +
					 * expectedMap.get(key) + "|actual=KEYNOTPRESENT");
					 */

				}
			} else {

				tableData.append(tdStartTag);
				tableData.append(key);
				tableData.append(tdEndTag);

				tableData.append(tdStartTag);

				StringBuilder secCellData = new StringBuilder();
				tableData.append(secCellData);
				/*
				 * tableData.append(tdEndTag); tableData.append(tdStartTag);
				 */
				StringBuilder thrdCellData = new StringBuilder();
				if (actualMap.get(key) != null) {
					thrdCellData = createSingleDivisionData(
							tdStartTag,
							"<font face=\"verdana\" color=\"red\">"
									+ actualMap.get(key) + "</font>", tdEndTag);

				}

				tableData.append(thrdCellData);

				// tableData.append(tdEndTag);
				// Actual value
				// tableData.append(tdStartTag);
				/* tableData.append(actualMap.get(key)); */
				// tableData.append(tdEndTag);

				tableData.append(trEndTag);
				/*
				 * test.log(Status.FAIL, "expected=KEYNOTPRESENT" +
				 * "|actual=" + actualMap.get(key));
				 */
			}
		}
		return tableData;

	}
	
	public StringBuffer compareExpectedWithActualValueWithExceptionList(
			Map<String, String> expectedMap, Map<String, String> actualMap,ArrayList<String> lstException) {
		StringBuffer tableData = new StringBuffer();
		String tdStartTag = "<td>";
		String tdEndTag = "</td>";
		String trStartTag = "<tr>";
		String trEndTag = "</tr>";
		LinkedHashSet<String> allKeys = new LinkedHashSet<String>();
		Set<String> expKeys = expectedMap.keySet();
		Set<String> actKeys = actualMap.keySet();
		allKeys.addAll(expKeys);
		allKeys.addAll(actKeys);
		tableData.append(trStartTag);
		if (expectedMap == null)
			System.out.println("expected map is null");
		if (actualMap == null)
			System.out.println("actual map is null");
		isAllTrue = true;
		for (String key : allKeys) {
			if (expectedMap.containsKey(key) && !lstException.contains(key)) {
				
				if (actualMap != null && actualMap.containsKey(key)) {
					String expMapValue = expectedMap.get(key);
					String actMapValue = actualMap.get(key);
					
					
					if(expMapValue !=null && expMapValue.contains("NA") && !lstException.contains(key)){
						StringBuilder firstCellData = createSingleDivisionData(tdStartTag, key, tdEndTag);
						StringBuilder secCellData = createSingleDivisionData(
								tdStartTag,expMapValue, tdEndTag);
						// Actual value
						StringBuilder thrdCellData = createSingleDivisionData(
								tdStartTag,actMapValue , tdEndTag);
						tableData.append(firstCellData);
						tableData.append(secCellData);
						tableData.append(thrdCellData);

						tableData.append(trEndTag);
					}
					else if ((expMapValue != null && (expMapValue
							.equalsIgnoreCase(actMapValue)|| lstException.contains(key)))
							|| (expMapValue == null && actMapValue == null)) { // In
						// few cases both the values would expect null
						if(!lstException.contains(key))
						{
							StringBuilder firstCellData = createSingleDivisionData(
									tdStartTag, key, tdEndTag);
							// Expected value
							StringBuilder secCellData = createSingleDivisionData(
									tdStartTag,
									"<font face=\"verdana\" color=\"green\">"
											+ expMapValue + "</font>", tdEndTag);
							// Actual value
							StringBuilder thrdCellData = createSingleDivisionData(
									tdStartTag,
									"<font face=\"verdana\" color=\"green\">"
											+ actMapValue + "</font>", tdEndTag);
							tableData.append(firstCellData);
							tableData.append(secCellData);
							tableData.append(thrdCellData);

							tableData.append(trEndTag);
						}
					} else {
						StringBuilder firstCellData = createSingleDivisionData(
								tdStartTag, key, tdEndTag);
						// Expected value
						StringBuilder secCellData = createSingleDivisionData(
								tdStartTag,
								"<font face=\"verdana\" color=\"red\">"
										+ expectedMap.get(key) + "</font>",
								tdEndTag);
						// Actual value
						StringBuilder thrdCellData = createSingleDivisionData(
								tdStartTag,
								"<font face=\"verdana\" color=\"red\">"
										+ actualMap.get(key) + "</font>",
								tdEndTag);
						tableData.append(firstCellData);
						tableData.append(secCellData);
						tableData.append(thrdCellData);

						tableData.append(trEndTag);
						isAllTrue = false;
					}
				} else {
					
					if(!lstException.contains(key))
					{
						tableData.append(tdStartTag);
						tableData.append(key);

						// Expected value
						StringBuilder secCellData = new StringBuilder();
						if (expectedMap.get(key) != null) {
							secCellData = createSingleDivisionData(tdStartTag,
									"<font face=\"verdana\" color=\"red\">"
											+ expectedMap.get(key) + "</font>",
									tdEndTag);
						}
						// Actual value
						StringBuilder thrdCellData = new StringBuilder();
						if (actualMap.get(key) != null) {
							thrdCellData = createSingleDivisionData(tdStartTag,
									"<font face=\"verdana\" color=\"red\">"
											+ actualMap.get(key) + "</font>",
									tdEndTag);
						}
						tableData.append(secCellData);
						tableData.append(thrdCellData);
						tableData.append(tdEndTag);
						tableData.append(tdEndTag);

						tableData.append(trEndTag);
					}
				}
			} else {
				
				if(!lstException.contains(key))
				{
					tableData.append(tdStartTag);
					tableData.append(key);
					tableData.append(tdEndTag);

					tableData.append(tdStartTag);
					StringBuilder secCellData = new StringBuilder();
					tableData.append(secCellData);
					StringBuilder thrdCellData = new StringBuilder();
					if (actualMap.get(key) != null) {
						thrdCellData = createSingleDivisionData(
								tdStartTag,
								"<font face=\"verdana\" color=\"red\">"
										+ actualMap.get(key) + "</font>", tdEndTag);
					}
					tableData.append(thrdCellData);
					tableData.append(trEndTag);
				}
			}
		}
		
		if(actualMap.containsKey("JpegPath"))
		{
			String screenshotPath = actualMap.get("JpegPath");
			String screenshot = "<p><img src="+screenshotPath+"></p>";
			tableData.append(screenshot);
		}
		return tableData;

	}

	public static boolean isBigDecimalEquals(BigDecimal value1,
			BigDecimal value2) {
		if (value1 == value2) {
			return true;
		}

		return (value1 != null && value2 != null && value1.compareTo(value2) == 0);
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

	public String generateHTMLReportForSuiteLogs(String filePath,
			String logFileName, String logFileDisplayName) {
		String outputFile = null;

		if (logFileDisplayName == null)
			logFileDisplayName = "All Logs";

		String relSuiteLogFileName = "../../" + filePath + logFileName;

		outputFile = "<a href="
				+ relSuiteLogFileName
				+ " download="
				+ logFileDisplayName
				+ "><p style=\"text-decoration:underline;\">Click here to download "
				+ logFileDisplayName + "</p></a>";

		System.out.println(" out file name:" + outputFile);
		return outputFile;
	}

	// method to add logs attachment to HTML report
	public String generateHTMLReportForLogs(String filePath,
			String logFileName, String logFileDisplayName) {
		String outputFile = null;
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		try {
			ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String timeStamp = getCurrentTimeStamp();
		if (logFileDisplayName == null)
			logFileDisplayName = "Log Files";
		if (logFileName == null)
			logFileName = "Logs";
		logFileName = logFileName.replace(' ', '_');
		String logHtmlFileName = logFileName + "_Logs_" + ranInt + "_"
				+ timeStamp + ".html";
		// String filePathNew=filePath+logFileName;

		// Test Result html folder
		String destDirFile = createDestDirFileName(logHtmlFileName);
		//
		// //Reading the data from the logs file.
		String logfileData = createFileData(filePath, logFileName);
		//
		// //Writing the data to html
		String htmlData = createHTMLFileDataForLogs(logfileData,
				logFileDisplayName);
		//
		// outputFile =
		// "<a href="+filePath+logFileName+" download="+logFileDisplayName+"><p style=\"text-decoration:underline;\">Click here to download "+logFileDisplayName+"</p></a>";
		outputFile = createHTMLFileForLogs(destDirFile, htmlData,
				logHtmlFileName, logFileDisplayName, filePath + logFileName);

		System.out.println(" out file name:" + outputFile);
		return outputFile;
	}

	// method to read from log file to String
	private String createFileData(String filePath, String logFileName) {
		StringBuilder sb = null;

		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath
					+ logFileName));
			sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("<br>");
				String newLine = System.getProperty("line.separator");
				// sb.append(System.getProperty("line.separator"));

				sb = sb.append(line).append(newLine);
				line = br.readLine();
			}
			br.close();
		}

		catch (Exception e) {
			System.out.println("After reading from file:" + e);
		}
		return sb.toString();

	}

	// method to add logs data to HTML
	private String createHTMLFileDataForLogs(String fileData,
			String logFileDisplayName) {

		StringBuilder sb = new StringBuilder();
		String header = "<!DOCTYPE html><html><head><title>"
				+ logFileDisplayName + "</title></head><body> ";
		String closingTags = "</body></html>";
		sb.append(header);
		sb.append(fileData);
		sb.append(System.getProperty("line.separator"));
		sb.append(closingTags);
		return sb.toString();
	}

	// method to return the logs data in HTML logs file
	private String createHTMLFileForLogs(String destDirFile, String htmlData,
			String logHtmlFileName, String logFileDisplayName,
			String logFileName) {

		String relativedir = ".." + File.separator + "HTML" + File.separator;
		File fss = new File(destDirFile);
		fss.getParentFile().mkdirs();
		FileWriter fw = null;
		try {
			fss.createNewFile();
			fw = new FileWriter(fss.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(htmlData);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String reldirFile = relativedir + logHtmlFileName;
		String relFileName = "../../" + logFileName;

		String outputFile = null;

		if (isAllTrue != null && isAllTrue == false)
			outputFile = "<a href="
					+ reldirFile
					+ " target=\"_blank\" style=\"color: #FF0000\">"
					+ logFileDisplayName
					+ "</a>"
					+ "&nbsp;&nbsp;&nbsp; <a href="
					+ relFileName
					+ " download="
					+ logFileDisplayName
					+ "><p style=\"text-decoration:underline;\">Click here to download "
					+ logFileDisplayName + "</p></a>";

		else if (isAllTrue != null && isAllTrue == true)
			outputFile = "<a href="
					+ reldirFile
					+ " target=\"_blank\" style=\"color: #00FF00\">"
					+ logFileDisplayName
					+ "</a>"
					+ "&nbsp;&nbsp;&nbsp; <a href="
					+ relFileName
					+ " download="
					+ logFileDisplayName
					+ "><p style=\"text-decoration:underline;\">Click here to download "
					+ logFileDisplayName + "</p></a>";
		else
			outputFile = "<a href="
					+ reldirFile
					+ " target=\"_blank\" >"
					+ logFileDisplayName
					+ "</a>"
					+ "&nbsp;&nbsp;&nbsp; <a href="
					+ relFileName
					+ " download="
					+ logFileDisplayName
					+ "><p style=\"text-decoration:underline;\">Click here to download "
					+ logFileDisplayName + "</p></a>";

		isAllTrue = null;
		return outputFile;
	}
	
	public boolean compareExpectedAndActualDBUIMap(String strHyperLinkName,LinkedHashMap<String, LinkedHashMap<String, String>> expectedMapOfMap,LinkedHashMap<String, LinkedHashMap<String, String>> dbMapOfMap, 
			LinkedHashMap<String, LinkedHashMap<String, String>> uiMapOfMap, Set<String> lstCardNumNonDeleteAction, 
			boolean dBMapExists, boolean uIMapExists, String db, String ui, String keyName, ArrayList<String> lstException,String displayStringWhenNoUIDataFound )
	{
		
		boolean status = true; 
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		try {
			ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String header = "<!DOCTYPE html><html><head><style>table, th, td {    border: 1px solid black;    border-collapse: collapse;}th, td {    padding: 5px;}</style></head><body> ";
		String title = "<h2><left>" + "Comparision Result" + "</left></h2>";
		String closingTags = "</body></html>";
		
		String tableStartTag = "<table>";
		String tableEndTag = "</table>";
		
		String trStartTag = "<tr>";
		String trEndTag = "</tr>";
		
		String thStartTag = "<th>";
		String thEndTag = "</th>";
		
		String tdStartTag = "<td>";
		String tdEndTag = "</td>";
		
		if(keyName==null)
			keyName = "Key Column";
		
		if(ui==null)
		{
			ui="UI Validation";
		}
		
		if(db==null)
		{
			db="DB Validation";
		}
		StringBuilder sb = new StringBuilder();
		
		sb.append(header);
		sb.append(title);
		sb.append(tableStartTag);		
		sb.append(trStartTag);
		
		StringBuilder dbKeyColumn = createSingleDivisionData(thStartTag, keyName , thEndTag);
		sb.append(dbKeyColumn);
		
		if(dBMapExists)
		{
			StringBuilder dbColumnHeading = createSingleDivisionData(thStartTag, db , thEndTag);
			sb.append(dbColumnHeading);
		}
		
		if(uIMapExists)
		{
			StringBuilder uiColumnHeading = createSingleDivisionData(thStartTag, ui , thEndTag);
			sb.append(uiColumnHeading);
		}
		
		sb.append(trEndTag);
		
		reportUtilDOntUse utilDes = new reportUtilDOntUse();
		
		for (String key: lstCardNumNonDeleteAction)
		{
			sb.append(trStartTag);
			
			sb.append(tdStartTag);
			sb.append(key);
			sb.append(tdEndTag);
			
			isAllTrue = true;
			if (expectedMapOfMap.containsKey(key)) 
			{
				if (expectedMapOfMap != null && expectedMapOfMap.containsKey(key)) 
				{
					HashMap<String, String> expectedMap = expectedMapOfMap.get(key);
					
					if(dBMapExists){	
						if(dbMapOfMap!=null)
						{
							HashMap<String, String> actualDBMap = dbMapOfMap.get(key);
							if(actualDBMap!=null){
								String dbHrefLink=utilDes.generateHTMLRepForTwoMapDataWithExceptionListString(expectedMap, actualDBMap,
										"Expected","DB",
												db,"Comparison of Expected Map and DB Map",lstException);
								if(dbHrefLink.contains("green"))
								{
									StringBuilder dbCompareCellData = createSingleDivisionData(
											tdStartTag, dbHrefLink +
											"<font face=\"verdana\" color=\"green\">"
													  + ": Passed" +  "</font>", tdEndTag);
									sb.append(dbCompareCellData);
								}
								else
								{
									StringBuilder dbCompareCellData = createSingleDivisionData(
											tdStartTag, dbHrefLink +
											"<font face=\"verdana\" color=\"red\">"
													 + ": Failed" + "</font>", tdEndTag);
									sb.append(dbCompareCellData);
								}
							}
							else{
								StringBuilder uiCompareCellData = createSingleDivisionData(
										tdStartTag, "<font face=\"verdana\" color=\"red\">"
												+ "No DB Details: Failed" + "</font>", tdEndTag);
								sb.append(uiCompareCellData);
							}
						}
						else
						{
							ReportLogger.logInfo(Status.FAIL, "Failed to extract Details from DataBase");
						}
					}
					
					if(uIMapExists){	
						if(uiMapOfMap!=null)
						{
							HashMap<String, String> actualUIMap = uiMapOfMap.get(key);
							if(actualUIMap!=null){
								String uiHreflink=utilDes.generateHTMLRepForTwoMapDataWithExceptionListString(expectedMap, actualUIMap,
										"Expected","UI",
												ui,"Comparison of Expected and UI Map",lstException);
								if(uiHreflink.contains("green"))
								{
									StringBuilder uiCompareCellData = createSingleDivisionData(
											tdStartTag, uiHreflink +
											"<font face=\"verdana\" color=\"green\">"
											+ ": Passed" +"</font>", tdEndTag);
								
									sb.append(uiCompareCellData);
								}
								else
								{
									StringBuilder uiCompareCellData = createSingleDivisionData(
											tdStartTag, uiHreflink +
											"<font face=\"verdana\" color=\"red\">"
													+ ": Failed" + "</font>", tdEndTag);
									sb.append(uiCompareCellData);
								}
							}
							else{
								StringBuilder uiCompareCellData = createSingleDivisionData(
										tdStartTag, "<font face=\"verdana\" color=\"red\">"
												+ displayStringWhenNoUIDataFound+" : Failed" + "</font>", tdEndTag);
								sb.append(uiCompareCellData);
							}
						}
						else
						{
							ReportLogger.logInfo(Status.FAIL, "Failed to extract Details from UI");
							
						}
					}
				}
				else
				{
					ReportLogger.logInfo(Status.FAIL, "Expected input data is either null or not exists");
					
				}
			}
			
			sb.append(trEndTag);			
		}
		
		String timeStamp = getCurrentTimeStamp();
		
		String hyperlinkName = null;
		
		String strHtmlFileName = "TestCase";
		
		if(dBMapExists && uIMapExists)
		{
			hyperlinkName = strHyperLinkName;
		}
		else if(dBMapExists)
		{
			hyperlinkName = "DB Comparison results";
		} else if(uIMapExists)
		{
			hyperlinkName = "UI comparison results";
		}
		else
		{
			hyperlinkName = "No results to compare";
		}
		strHtmlFileName = strHtmlFileName.replace(' ', '_');
		strHtmlFileName = strHtmlFileName + "_" + ranInt + "_" + timeStamp
				+ ".html";
		String destDirFile = createDestDirFileName(strHtmlFileName);
		//sb.append(trEndTag);
		//String destDirFile = createDestDirFileName("Test");
		String htmlData = createHTMLFileData(sb, "FinalCompareList");
		String htmlfile = createHTMLFile(destDirFile, htmlData, strHtmlFileName,
				hyperlinkName);
		
		sb.append(tableEndTag);
		sb.append(closingTags);
		
		if (htmlData.toString().contains("color=\"red\"")) {
			ReportLogger.logInfo(Status.FAIL, htmlfile);
			status = false;
			
		} else{
			ReportLogger.logInfo(Status.PASS, htmlfile);
		}
		return status;
	
	}
	
	public String generateHTMLRepForTwoMapDataWithExceptionListString(
			Map<String, String> expectedMap, Map<String, String> actualMap,
			String strExpectedHeading,String strActualHeading,
			String hyperLinkText, String htmlDisplayText,ArrayList<String> lstExceptions) {
		String htmlfile = null;
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		String strHtmlFileName = hyperLinkText;
		try {
		ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
		e.printStackTrace();
		}
		
		if(strExpectedHeading == null)
			strExpectedHeading = "Expected Result";
		
		if(strActualHeading == null)
			strActualHeading = "Actual Result";
		
		if(hyperLinkText == null)
			hyperLinkText = "Test Result";
		
		if(htmlDisplayText == null)
			htmlDisplayText = "Comparison Result";
		
		String timeStamp = getCurrentTimeStamp();
		
		strHtmlFileName = strHtmlFileName.replace(' ', '_');
		strHtmlFileName = strHtmlFileName + "_" + ranInt + "_" + timeStamp
		+ ".html";
		String destDirFile = createDestDirFileName(strHtmlFileName);
		StringBuilder mapCompareString = createTableDataForExpActualMapResultWithExceptionList(
		expectedMap,actualMap,strExpectedHeading,strActualHeading, lstExceptions);
		
		String htmlData = createHTMLFileData(mapCompareString, htmlDisplayText);
		htmlfile = createHTMLFile(destDirFile, htmlData, strHtmlFileName,
		hyperLinkText);
		
		String relativedir = ".." + File.separator + "HTML" + File.separator;
		String reldirFile = relativedir + strHtmlFileName;
		String outputFile = null;
		
		if (htmlData.toString().contains("color=\"red\""))
		outputFile = "<b><a  href=" + reldirFile
		+ " target=\"_blank\" style=\"color: red\">"
		+ hyperLinkText + "</a></b>";
		else if (htmlData.toString().contains("color=\"green\""))
		outputFile = "<b><a  href=" + reldirFile
		+ " target=\"_blank\" style=\"color: green\">"
		+ hyperLinkText + "</a></b>";
		else
		outputFile = "<b><a href=" + reldirFile + " target=\"_blank\" >"
		+ hyperLinkText + "</a></b>";
		
		
		
		return outputFile;
	}

	public StringBuilder createTableDataForDataExistinDBOrNot(String columnHeaderText , String dBValidation, LinkedHashMap<String,Boolean> isExistsPropertyMap, String passText, String FailText, String tableName)
	{
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		try {
		ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
		e.printStackTrace();
		}
		String header = "<!DOCTYPE html><html><head><style>table, th, td {    border: 1px solid black;    border-collapse: collapse;}th, td {    padding: 5px;}</style></head><body> ";
		String title = "<h2><left>" + "Comparision Result" + "</left></h2>";
		String closingTags = "</body></html>";
		
		String tableStartTag = "<table>";
		String tableEndTag = "</table>";
		
		String trStartTag = "<tr>";
		String trEndTag = "</tr>";
		
		String thStartTag = "<th>";
		String thEndTag = "</th>";
		
		String tdStartTag = "<td>";
		String tdEndTag = "</td>";
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(header);
		sb.append(title);
		sb.append(tableStartTag);		
		sb.append(trStartTag);
		
		StringBuilder dbKeyColumn = createSingleDivisionData(thStartTag, columnHeaderText , thEndTag);
		sb.append(dbKeyColumn);
		
		
		StringBuilder dbColumnHeading = createSingleDivisionData(thStartTag, dBValidation , thEndTag);
		sb.append(dbColumnHeading);
		
		
		sb.append(trEndTag);
		
		Set<String> keys = isExistsPropertyMap.keySet();
		
		for (String key: keys)
		{
			sb.append(trStartTag);
			
			sb.append(tdStartTag);
			sb.append(key);
			sb.append(tdEndTag);
			
			boolean isPropExists = isExistsPropertyMap.get(key);
			
			if(isPropExists)
			{
				StringBuilder cellData = createSingleDivisionData(
						tdStartTag,
						"<font face=\"verdana\" color=\"red\">"
								+columnHeaderText+" "+ key + " " + FailText+ " in "+tableName+ "</font>", tdEndTag);
				sb.append(cellData);
			}
			else
			{
				StringBuilder cellData = createSingleDivisionData(
						tdStartTag,
						"<font face=\"verdana\" color=\"green\">"
								+columnHeaderText+" "+ key + " " + passText+ " in "+tableName+ "</font>", tdEndTag);
				sb.append(cellData);
			}
			
			sb.append(trEndTag);
		
		}
		
		sb.append(tableEndTag);
		sb.append(closingTags);
		
		return sb;
	}
	
	public boolean generateHTMLReportForDataExistinDBOrNot(String testCaseName,
			String tableDisplayName, String columnHeaderText , String dBValidation, LinkedHashMap<String,Boolean> isExistsPropertyMap, String passText, String FailText, String tableName) {
		String outputFile = null;
		boolean status = true;
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		try {
			ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isExistsPropertyMap != null) {
			String timeStamp = getCurrentTimeStamp();
			
			if(columnHeaderText==null)
				columnHeaderText = "Key Column";
			
			if(dBValidation==null)
				dBValidation="DB Validation";
			if (testCaseName == null)
				testCaseName = "Html";
			if (tableDisplayName == null)
				tableDisplayName = "Test Result";
			testCaseName = testCaseName + "_" + ranInt + "_" + timeStamp
					+ ".html";
			String destDirFile = createDestDirFileName(testCaseName);
			StringBuilder tableData = createTableDataForDataExistinDBOrNot(columnHeaderText, dBValidation, isExistsPropertyMap, passText, FailText, tableName);
			String htmlData = createHTMLFileData(tableData, tableDisplayName);
			outputFile = createHTMLFile(destDirFile, htmlData, testCaseName,
					tableDisplayName);
			// childtest.log(Status.INFO, "Expected_And_Actual_Result",
			// outputFile);
			
			if (htmlData.toString().contains("color=\"red\"")) {
				ReportLogger.logInfo(Status.FAIL, outputFile);
				status = false;
				
			} else
				ReportLogger.logInfo(Status.PASS, outputFile);
		}
		System.out.println("out file for comapare name:" + outputFile);
		
		
		
		return status;
	}
}