package com.ca.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;

import com.ca.base.BaseSuite;
import com.ca.base.reports.ReportLogger;
import com.aventstack.extentreports.Status;;

public class CommonUtil {

	
	private String tableStartTag = "<table>";
	private String tableEndTag = "</table>";

	private static Boolean isAllTrue = null;

	public Object[][] getInputData(ITestContext testContext, String strRingBufferFile, String strRingBufferSheet) {
		String fileName = null, sheetName = null, testCaseIdKey = null;
		ReadExcel re = new ReadExcel();
		if (testContext == null)
			System.out.println("test context is null");
		else {

			String strfileName = testContext.getCurrentXmlTest().getParameter(strRingBufferFile);
			sheetName = testContext.getCurrentXmlTest().getParameter(strRingBufferSheet);
			testCaseIdKey = testContext.getCurrentXmlTest().getParameter("TestCaseID");
			String dataProviderPath = testContext.getCurrentXmlTest().getParameter("DataProviderPath");
			if (dataProviderPath != null) {
				fileName = dataProviderPath + strfileName;
			} else {
				fileName = strfileName;
			}
			//System.out.println("Excel Path =" + dataProviderPath);
			System.out.println("File Name =" + fileName);

		}
		if (testCaseIdKey == null)
			testCaseIdKey = "TestCaseID";

		Map<String, Map<String, String>> mom = null;
		try {
			System.out.println("Current File Name in Common Util->" + fileName);
			System.out.println("Current Sheet Name in Common Util->" + sheetName);
			mom = re.getTestAllData(fileName, sheetName, testCaseIdKey);
		} catch (ExcelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int sizeOfMap = mom.size();
		Iterator<String> keyIterator = mom.keySet().iterator();

		Object m[][] = new Object[sizeOfMap][];
		for (int i = 0; i < sizeOfMap; i++) {
			m[i] = new Object[1];
			m[i][0] = mom.get(keyIterator.next());
		}
		return m;
	}

	public String generateHTMLReportExpectedAndActualResult(Map<String, String> expectedReslMap,
			Map<String, String> actualReslMap, String testCaseName, String tableDisplayName) {
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
				tableDisplayName = "Expected And Actual Result";
			
			Long threadID = Thread.currentThread().getId();
			
			String testCaseId = BaseSuite.threadTestCaseIdMap.get(threadID);
			
			testCaseName = testCaseId + "_" + ranInt + "_" + timeStamp + ".html";
			String destDirFile = createDestDirFile(testCaseName);
			StringBuilder tableData = createTableDataForExpAulResult(expectedReslMap, actualReslMap);
			String htmlData = createHTMLFileData(tableData, tableDisplayName);
			outputFile = createHTMLFile(destDirFile, htmlData, testCaseName, tableDisplayName);
			// childtest.log(Status.INFO, "Expected_And_Actual_Result",
			// outputFile);
		}
		System.out.println("out file for comapare name:" + outputFile);
		return outputFile;
	}

	public String generateHTMLReportForInputTestData(Map<String, String> inputTestDataMap, String testCaseName,
			String tableDisplayName) {
		return generateHTMLReportForInputTestData(inputTestDataMap, testCaseName, tableDisplayName, tableDisplayName);
	}

	public String generateHTMLReportForInputTestData(Map<String, String> inputTestDataMap, String testCaseName,
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
			
			Long threadID = Thread.currentThread().getId();
			
			String testCaseId = BaseSuite.threadTestCaseIdMap.get(threadID);
			
			testCaseName = testCaseId + "_" + ranInt + "_" + timeStamp + ".html";
			String destDirFile = createDestDirFile(testCaseName);
			StringBuilder tableData = createTableData(inputTestDataMap);
			String htmlData = createHTMLFileData(tableData, tableDisplayName);
			outputFile = createHTMLFile(destDirFile, htmlData, testCaseName, detailDesc);
			// childtest.log(Status.INFO, "Test_Data", outputFile);
		}
		System.out.println(" out file name:" + outputFile);
		return outputFile;
	}

	private String createDestDirFile(String testCaseName) {
		String destdir1 = System.getProperty("user.dir");
		
		String destDirFile = destdir1 + File.separator + "TestResult"+ File.separator +"Extent" + File.separator + "HTML" + File.separator
				+ testCaseName;
		System.out.println("dest dir File===========>" + destDirFile);
		return destDirFile;
	}

	private String createHTMLFile(String destDirFile, String htmlData, String testCaseName, String tableDisplayName) {
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
		// String windowOpen = "<a
		// href=\"javascript:window.open("+reldirFile+",'Satya','width=600,height=400')\">"+tableDisplayName+"</a>";
		String outputFile = null;
		if (isAllTrue != null && isAllTrue == false)
			outputFile = "<a href=" + reldirFile + " target=\"_blank\" style=\"color: #FF0000\">" + tableDisplayName
					+ "</a>";
		else if (isAllTrue != null && isAllTrue == true)
			outputFile = "<a href=" + reldirFile + " target=\"_blank\" style=\"color: #00FF00\">" + tableDisplayName
					+ "</a>";
		else
			outputFile = "<a href=" + reldirFile + " target=\"_blank\" >" + tableDisplayName + "</a>";
		isAllTrue = null;
		return outputFile;
	}

	private String createHTMLFile(String destDirFile, String htmlData, String testCaseName, String tableDisplayName,
			boolean status) {
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
		// String windowOpen = "<a
		// href=\"javascript:window.open("+reldirFile+",'Satya','width=600,height=400')\">"+tableDisplayName+"</a>";
		String outputFile = null;
		if (!status)
			outputFile = "<a href=" + reldirFile + " target=\"_blank\" style=\"color: #FF0000\">" + tableDisplayName
					+ "</a>";
		else
			outputFile = "<a href=" + reldirFile + " target=\"_blank\" style=\"color: #00FF00\">" + tableDisplayName
					+ "</a>";

		return outputFile;
	}

	private String createHTMLFileData(StringBuilder tableData, String tableDisplayName) {
		StringBuilder sb = new StringBuilder();
		String header = "<!DOCTYPE html><html><head><style>table, th, td {    border: 1px solid black;    border-collapse: collapse;}th, td {    padding: 5px;}</style></head><body> ";
		String title = "<h2><center>" + tableDisplayName + "</center></h2>";
		String closingTags = "</body></html>";
		sb.append(header);
		sb.append(title);
		sb.append(tableData);
		sb.append(closingTags);
		return sb.toString();
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

	private StringBuilder createTableDataForExpAulResult(Map<String, String> expReslMap,
			Map<String, String> actReslMap) {
		StringBuilder tableData = new StringBuilder();
		String tableStartTag = "<table>";
		String tableEndTag = "</table>";
		String thStartTag = "<th>";
		String thEndTag = "</th>";
		

		tableData.append(tableStartTag);
		// StringBuilder firstCellData
		StringBuilder firstCellData = createSingleDivisionData(thStartTag, "Key Name", thEndTag);
		StringBuilder secCellData = createSingleDivisionData(thStartTag, "Expected Value", thEndTag);
		StringBuilder thrdCellData = createSingleDivisionData(thStartTag, "Actual Value", thEndTag);
		tableData.append(firstCellData);
		tableData.append(secCellData);
		tableData.append(thrdCellData);

		StringBuffer sb = comapreExpectedWithActualValue(expReslMap, actReslMap);
		tableData.append(sb);
		tableData.append(tableEndTag);
		return tableData;
	}

	private StringBuilder createSingleDivisionData(String thStartTag, String actualValue, String thEndTag) {
		StringBuilder cellData = new StringBuilder();
		cellData.append(thStartTag);
		cellData.append(actualValue);
		cellData.append(thEndTag);
		return cellData;
	}

	public StringBuffer comapreExpectedWithActualValue(Map<String, String> expectedMap, Map<String, String> actualMap) {
		StringBuffer tableData = new StringBuffer();
		// String tdStartREDTag = "<td style=\"background-color:red\">";
		String tdStartREDTag = "<td style=\"background-color:#FF7050\">"; // light
																			// red
		String tdStartGREENTag = "<td style=\"background-color:#90EE90\">";// lightgreen
		String tdStartTag = "<td>";
		String tdEndTag = "</td>";
		String trStartTag = "<tr>";
		String trEndTag = "</tr>";
		HashSet<String> allKeys = new HashSet<String>();
		Set<String> expKeys = expectedMap.keySet();
		Set<String> actKeys = actualMap.keySet();
		allKeys.addAll(expKeys);
		allKeys.addAll(actKeys);
		tableData.append(trStartTag);
		
		isAllTrue = true;
		for (String key : allKeys) {
			if (expectedMap.containsKey(key)) {
				if (actualMap != null && actualMap.containsKey(key)) {
					String expMapValue = expectedMap.get(key);
					String actMapValue = actualMap.get(key);
					if ((expMapValue != null && expMapValue.equalsIgnoreCase(actMapValue))
							|| (expMapValue == null && actMapValue == null)) { // In
																				// few
																				// cases
																				// both
																				// the
																				// values
																				// would
																				// expect
																				// null
						StringBuilder firstCellData = createSingleDivisionData(tdStartTag, key, tdEndTag);
						// Expected value
						StringBuilder secCellData = createSingleDivisionData(tdStartTag, expMapValue, tdEndTag);
						// Actual value
						StringBuilder thrdCellData = createSingleDivisionData(tdStartGREENTag, actMapValue, tdEndTag);
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
						StringBuilder firstCellData = createSingleDivisionData(tdStartTag, key, tdEndTag);
						// Expected value
						StringBuilder secCellData = createSingleDivisionData(tdStartTag, expectedMap.get(key),
								tdEndTag);
						// Actual value
						StringBuilder thrdCellData = createSingleDivisionData(tdStartREDTag, actualMap.get(key),
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
					tableData.append(tdStartREDTag);
					tableData.append(key);
					tableData.append(tdEndTag);
					// Expected value
					tableData.append(tdStartREDTag);
					tableData.append(expectedMap.get(key));
					tableData.append(tdEndTag);

					tableData.append(trEndTag);
					/*
					 * test.log(Status.FAIL, "expected=" +
					 * expectedMap.get(key) + "|actual=KEYNOTPRESENT");
					 */
				}
			} else {
				tableData.append(tdStartREDTag);
				tableData.append(key);
				tableData.append(tdEndTag);
				// Actual value
				tableData.append(tdStartREDTag);
				tableData.append(actualMap.get(key));
				tableData.append(tdEndTag);

				tableData.append(trEndTag);
				/*
				 * test.log(Status.FAIL, "expected=KEYNOTPRESENT" +
				 * "|actual=" + actualMap.get(key));
				 */
			}
		}
		return tableData;

	}

	public String generateHTMLReportForSuiteLogs(String filePath, String logFileName, String logFileDisplayName) {
		String outputFile = null;

		if (logFileDisplayName == null)
			logFileDisplayName = "All Logs";

		String relSuiteLogFileName = "../../" + filePath + logFileName;

		outputFile = "<a href=" + relSuiteLogFileName + " download=" + logFileDisplayName
				+ "><p style=\"text-decoration:underline;\">Click here to download " + logFileDisplayName + "</p></a>";

		System.out.println(" out file name:" + outputFile);
		return outputFile;
	}

	// method to add logs attachment to HTML report
	public synchronized String generateHTMLReportForLogs(String filePath, String logFileName,
			String logFileDisplayName) {
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
		String logHtmlFileName = logFileName + "_Logs_" + ranInt + "_" + timeStamp + ".html";
		// String filePathNew=filePath+logFileName;

		// Test Result html folder
		String destDirFile = createDestDirFile(logHtmlFileName);

		// Reading the data from the logs file.
		String logfileData = createFileData(filePath, logFileName);

		// Writing the data to html
		String htmlData = createHTMLFileDataForLogs(logfileData, logFileDisplayName);

		outputFile = createHTMLFileForLogs(destDirFile, htmlData, logHtmlFileName, logFileDisplayName,
				filePath + logFileName);

		System.out.println(" out file name:" + outputFile);
		return outputFile;
	}

	// method to read from log file to String
	private synchronized String createFileData(String filePath, String logFileName) {
		StringBuilder sb = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath + logFileName));
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
	private synchronized String createHTMLFileDataForLogs(String fileData, String logFileDisplayName) {
		StringBuilder sb = new StringBuilder();
		String header = "<!DOCTYPE html><html><head><title>" + logFileDisplayName + "</title></head><body> ";
		String closingTags = "</body></html>";
		sb.append(header);
		sb.append(fileData);
		sb.append(System.getProperty("line.separator"));
		sb.append(closingTags);
		return sb.toString();
	}

	// method to return the logs data in HTML logs file
	private synchronized String createHTMLFileForLogs(String destDirFile, String htmlData, String logHtmlFileName,
			String logFileDisplayName, String logFileName) {
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
			outputFile = "<a href=" + reldirFile + " target=\"_blank\" style=\"color: #FF0000\">" + logFileDisplayName
					+ "</a>" + "&nbsp;&nbsp;&nbsp; <a href=" + relFileName + " download=" + logFileDisplayName
					+ "><p style=\"text-decoration:underline;\">Click here to download " + logFileDisplayName
					+ "</p></a>";

		else if (isAllTrue != null && isAllTrue == true)
			outputFile = "<a href=" + reldirFile + " target=\"_blank\" style=\"color: #00FF00\">" + logFileDisplayName
					+ "</a>" + "&nbsp;&nbsp;&nbsp; <a href=" + relFileName + " download=" + logFileDisplayName
					+ "><p style=\"text-decoration:underline;\">Click here to download " + logFileDisplayName
					+ "</p></a>";
		else
			outputFile = "<a href=" + reldirFile + " target=\"_blank\" >" + logFileDisplayName + "</a>"
					+ "&nbsp;&nbsp;&nbsp; <a href=" + relFileName + " download=" + logFileDisplayName
					+ "><p style=\"text-decoration:underline;\">Click here to download " + logFileDisplayName
					+ "</p></a>";
		System.out.println("Output file:" + outputFile);
		isAllTrue = null;
		return outputFile;
	}

	public static String expectedAndActualResult(String expectedMessage, String actualMessage, boolean isTrue) {
		String s = null;
		if (isTrue)
			s = "Expected = <font face=\"verdana\" color=\"green\"> " + expectedMessage
					+ "</font> <br> Actual = <font face=\"verdana\" color=\"green\">" + actualMessage + "</font>";
		else
			s = "Expected = <font face=\"verdana\" color=\"Red\"> " + expectedMessage
					+ "</font> <br> Actual = <font face=\"verdana\" color=\"Red\">" + actualMessage + "</font>";
		return s;
	}

	/**
	 * 
	 * @param testContext
	 * @param strRingBufferFile
	 * @param strRingBufferSheet
	 * @return
	 */
	public Object[][] getInputDataDontResolveSymbols(ITestContext testContext, String strRingBufferFile,
			String strRingBufferSheet) {
		String fileName = null, sheetName = null, testCaseIdKey = null;
		ReadExcel re = new ReadExcel();
		if (testContext == null)
			System.out.println("test context is null");
		else {
			String strfileName = testContext.getCurrentXmlTest().getParameter(strRingBufferFile);
			sheetName = testContext.getCurrentXmlTest().getParameter(strRingBufferSheet);
			testCaseIdKey = testContext.getCurrentXmlTest().getParameter("TestCaseID");
			String dataProviderPath = testContext.getCurrentXmlTest().getParameter("DataProviderPath");
			if (dataProviderPath != null) {
				fileName = dataProviderPath + strfileName;
			} else {
				fileName = strfileName;
			}
			//System.out.println("Excel Path =" + dataProviderPath);
			System.out.println("File Name =" + fileName);

		}
		if (testCaseIdKey == null)
			testCaseIdKey = "TestCaseID";

		Map<String, Map<String, String>> mom = null;
		try {
			System.out.println("Current File Name in Common Util" + fileName);
			System.out.println("Current Sheet Name in Common Util" + sheetName);
			mom = re.getTestAllDataDontResolveSymbols(fileName, sheetName, testCaseIdKey);
		} catch (ExcelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int sizeOfMap = mom.size();
		Iterator<String> keyIterator = mom.keySet().iterator();

		Object m[][] = new Object[sizeOfMap][];
		for (int i = 0; i < sizeOfMap; i++) {
			m[i] = new Object[1];
			m[i][0] = mom.get(keyIterator.next());
		}
		return m;
	}

	

	/**
	 * To verify the downloaded file is in directory or not
	 * 
	 * @param filename
	 * @param filepath
	 */
	

	

	public static Map<String, Map<String, String>> getReportData(String strRingBufferFile, String strRingBufferSheet,
			String key) {
		String fileName = null, sheetName = null, reportName = null;
		ReadExcel re = new ReadExcel();

		fileName = System.getProperty("user.dir") + File.separator + strRingBufferFile;
		sheetName = strRingBufferSheet;
		System.out.println("Excel Path =" + strRingBufferFile);
		System.out.println("File Name =" + strRingBufferSheet);

		if (key != null)
			reportName = key;
		else
			reportName = "ReportName";

		Map<String, Map<String, String>> mom = null;
		try {
			System.out.println("Current File Name in Common Util" + fileName);
			System.out.println("Current Sheet Name in Common Util" + sheetName);
			mom = re.getTestAllData(fileName, sheetName, reportName);
		} catch (ExcelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (mom == null) {
			System.out.println("Report data is empty");
			return mom;
		}
		int sizeOfMap = mom.size();
		Iterator<String> keyIterator = mom.keySet().iterator();

		Object m[][] = new Object[sizeOfMap][];
		for (int i = 0; i < sizeOfMap; i++) {
			m[i] = new Object[1];
			m[i][0] = mom.get(keyIterator.next());
		}
		// return m;
		return mom;
	}

	public Result createHTMLStringData(LinkedHashMap<String, LinkedHashMap<String, String>> uiMapofMap,
			LinkedHashMap<String, LinkedHashMap<String, String>> csvMapofMap, String htmlDistplayName,
			String strTestCase) {

		Result resMessage = new Result();
		StringBuilder sb = new StringBuilder();
		boolean status = true;

		String header = "<!DOCTYPE html><html><head><style>table, th, td {    border: 1px solid black;    border-collapse: collapse;}th, td {    padding: 5px;}</style></head><body> ";
		String title = "<h2><left>" + htmlDistplayName + "</left></h2>";
		String closingTags = "</body></html>";
		String tdGreenStartTag = "<td><font face=\"verdana\" color=\"green\">";
		String tdRedStartTag = "<td><font face=\"verdana\" color=\"Red\">";
		String tdNormalStartTag = "<td><font face=\"verdana\" color=\"Black\">";
		String tdEndTag = "<//font><//td>";
		sb.append(header);
		sb.append(title);
		sb.append(tableStartTag);

		HashSet<String> allKeys = new HashSet<String>();
		Set<String> expKeys = uiMapofMap.keySet();
		Set<String> actKeys = csvMapofMap.keySet();
		allKeys.addAll(expKeys);
		allKeys.addAll(actKeys);

		Set momKeys = uiMapofMap.keySet();
		String strKey = null;

		for (String key : allKeys) {
			if (expKeys.contains(key) && actKeys.contains(key)) {
				strKey = key;
				break;
			}
		}

		LinkedHashMap<String, String> colHeadMap = csvMapofMap.get(strKey);
		Set<String> colHeaderList = colHeadMap.keySet();
		List<String> colHeadLst = new ArrayList<String>(colHeaderList);
		sb.append("<tr>");
		for (String colHead : colHeadLst) {
			sb.append(tdNormalStartTag);
			sb.append(colHead);
			sb.append(tdEndTag);
		}
		sb.append("</tr>");

		for (String key : allKeys) {

			LinkedHashMap<String, String> uiMap = uiMapofMap.get(key);
			LinkedHashMap<String, String> csvMap = csvMapofMap.get(key);
			if (uiMap != null && csvMap != null) {
				sb.append("<tr>");
				for (int i = 0; i < colHeadLst.size(); i++) {
					String colHeadValue = colHeadLst.get(i);
					// System.out.println("Column Header : "+colHeadValue);
					String uiValue = uiMap.get(colHeadValue);
					String csvValue = csvMap.get(colHeadValue);
					if (uiValue != null && csvValue != null) {
						if (uiValue.equalsIgnoreCase(csvValue)) {
							sb.append(tdGreenStartTag);
							sb.append(uiValue);
						} else {
							status = false;
							sb.append(tdRedStartTag);
							sb.append("Expected Value : " + uiValue + "<br>" + "Actual Value : " + csvValue);
						}
						sb.append(tdEndTag);
					} else if (uiValue != null) {
						status = false;
						sb.append(tdRedStartTag);
						sb.append("Expected Value : " + uiValue + "<br>" + "Actual Value : ");
					} else if (csvValue != null) {
						status = false;
						sb.append(tdRedStartTag);
						sb.append("Expected Value : <br>" + "Actual Value : " + csvValue);
					}

				}
				sb.append("</tr>");
			} else if (uiMap == null) {
				sb.append("<tr>");
				for (int i = 0; i < colHeadLst.size(); i++) {
					String colHeadValue = colHeadLst.get(i);
					String csvValue = csvMap.get(colHeadValue);

					status = false;
					sb.append(tdRedStartTag);
					sb.append("Expected Value : <br>" + "Actual Value : " + csvValue);

					sb.append(tdEndTag);

				}
				sb.append("</tr>");
			} else if (csvMap == null) {

				sb.append("<tr>");
				for (int i = 0; i < colHeadLst.size(); i++) {
					String colHeadValue = colHeadLst.get(i);
					String uiValue = uiMap.get(colHeadValue);

					status = false;
					sb.append(tdRedStartTag);
					sb.append("Expected Value : " + uiValue + "<br>" + "Actual Value : ");

					sb.append(tdEndTag);

				}
				sb.append("</tr>");

			}

		}
		sb.append(tableEndTag);
		sb.append("<br/>");
		sb.append("<br/>");
		sb.append(closingTags);
		String strHtmlExtentRep = generateHTMLReportForText(strTestCase, htmlDistplayName, sb.toString(), status);
		resMessage.setOutputMessage(strHtmlExtentRep);
		resMessage.setSuccess(status);
		return resMessage;
	}

	

	public String generateHTMLReportForText(String testCaseName, String tableDisplayName, String htmlData,
			boolean status) {
		String outputFile = null;
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		try {
			ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String timeStamp = getCurrentTimeStamp();
		if (testCaseName == null)
			testCaseName = "Html";
		if (tableDisplayName == null)
			tableDisplayName = "Expected And Actual Result";
		
		Long threadID = Thread.currentThread().getId();
		
		String testCaseId = BaseSuite.threadTestCaseIdMap.get(threadID);
		
		testCaseName = testCaseId + "_" + ranInt + "_" + timeStamp + ".html";
		testCaseName = testCaseName.replaceAll(" ", "_");
		String destDirFile = createDestDirFile(testCaseName);

		outputFile = createHTMLFile(destDirFile, htmlData, testCaseName, tableDisplayName, status);
		// childtest.log(Status.INFO, "Expected_And_Actual_Result",
		// outputFile);

		return outputFile;
	}

	public String generateHTMLReportForText(String testCaseName, String tableDisplayName, String htmlData) {
		String outputFile = null;
		RandomNumberAndString ranNum = new RandomNumberAndString();
		Long ranInt = null;
		try {
			ranInt = ranNum.generateRandInt(1, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String timeStamp = getCurrentTimeStamp();
		if (testCaseName == null)
			testCaseName = "Html";
		if (tableDisplayName == null)
			tableDisplayName = "Expected And Actual Result";
		
		Long threadID = Thread.currentThread().getId();
		
		String testCaseId = BaseSuite.threadTestCaseIdMap.get(threadID);
		
		testCaseName = testCaseId + "_" + ranInt + "_" + timeStamp + ".html";
		String destDirFile = createDestDirFile(testCaseName);

		outputFile = createHTMLFile(destDirFile, htmlData, testCaseName, tableDisplayName);
		// childtest.log(Status.INFO, "Expected_And_Actual_Result",
		// outputFile);

		return outputFile;
	}

	

	public boolean checkDirExist(String strDirPath) {

		File f = new File(strDirPath);
		if (f.exists() && f.isDirectory()) {
			return true;
		}
		return false;
	}

	public String getFilenameForDirList(String strDirPath, String filePattern) {

		int i = 1;
		while (i <= 10) {

			File[] files = new File(strDirPath).listFiles();
			// If this pathname does not denote a directory, then listFiles()
			// returns null.

			for (File file : files) {
				if (file.isFile()) {
					if (file.getName().contains(filePattern)) {
						return file.getName();
					}
				}
			}
			i++;
			// Code to wait till the file gets downloaded
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	public void deleteFilesInDir(String strDirPath, String filePattern) {

		File file = new File(strDirPath);
		if (!checkDirExist(strDirPath)) {
			return;
		}
		String[] myFiles;
		if (file.isDirectory()) {
			myFiles = file.list();
			System.out.println("Files with pattern " + filePattern + " will be deleted from Folder : " + strDirPath);
			for (int i = 0; i < myFiles.length; i++) {

				File myFile = new File(file, myFiles[i]);
				if (myFile.getName().contains(filePattern)) {
					System.out.println("Deleting the File : " + myFile.getName());
					myFile.delete();
				}
			}

		}

	}

	public static String acceptAlertandGetText(WebDriver driver) {
		String alertText = null;
		try {
			Alert alert = driver.switchTo().alert();
			alertText = alert.getText();
			alert.accept();

		} catch (Exception e) {

		}
		return alertText;
	}

	public static String dismissAlertandGetText(WebDriver driver) {
		String alertText = null;
		try {
			Alert alert = driver.switchTo().alert();
			alertText = alert.getText();
			alert.dismiss();

		} catch (Exception e) {

		}
		return alertText;
	}

	public static void waitForPageLoaded(WebDriver driver) {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
						.equals("complete");
			}
		};
		try {
			Thread.sleep(1000);
			System.out.println("document waiting.........");
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(expectation);
		} catch (Throwable error) {
			Assert.fail("Timeout waiting for Page Load Request to complete.");
		}
	}

	public static void selectDropDownValue(WebElement element, String value) {
		if (value != null) {
			if (!value.isEmpty()) {
				Select dropdown = new Select(element);
				dropdown.selectByVisibleText(value);
			}
		}
	}

	public Result createHTMLStringData(LinkedHashMap<String, LinkedHashMap<String, String>> uiMapofMap,
			LinkedHashMap<String, LinkedHashMap<String, String>> csvMapofMap, String htmlDistplayName,
			String strTestCase, String ignoreFields) {
		System.out.println("************Inside createHTML*******");
		String[] ignoreFieldsArray = null;
		if (ignoreFields != null || !ignoreFields.equals("")) {
			ignoreFieldsArray = ignoreFields.split(",");
		}

		Result resMessage = new Result();
		StringBuilder sb = new StringBuilder();
		boolean status = true;

		String passMsg = "Expected and acutal matched records are shown as ";
		String ignoredMsg = "Internally Custom transformed records by RA Plugin are shown as ";
		String failMsg = "Expected and actual not matched records are shown as ";

		String header = "<!DOCTYPE html><html><head><style>table, th, td {    border: 1px solid black;    border-collapse: collapse;}th, td {    padding: 5px;}</style></head><body> ";
		String title = "<h2><left>" + htmlDistplayName + "</left></h2>";
		String closingTags = "</body></html>";
		String tdGreenStartTag = "<td><font face=\"verdana\" color=\"green\">";
		String tdRedStartTag = "<td><font face=\"verdana\" color=\"Red\">";
		String tdNormalStartTag = "<td><font face=\"verdana\" color=\"Black\">";
		String tdOrangeStartTag = "<td><font face=\"verdana\" color=\"#FFA500\">";
		String tdEndTag = "<//font><//td>";
		String ignoredTag = "<font face=\"verdana\" color=\"#FFA500\">" + "IGNORED" + "</font><br>";
		String failTag = "<font face=\"verdana\" color=\"red\">" + "FAIL" + "</font><br>";
		String passTag = "<font face=\"verdana\" color=\"green\">" + "PASS" + "</font><br>";
		// String colorInformation= "<p><font face=\"verdana\"
		// color=\"#FFA500\">" + "IGNORED" + "</font>"+"<font face=\"verdana\"
		// color=\"green\">" + "PASS" + "</font>"+"<font face=\"verdana\"
		// color=\"red\">" + "FAIL" + "</font>"+"</p>";
		sb.append(header);
		sb.append(title);
		sb.append(passMsg);
		sb.append(passTag);

		sb.append(failMsg);
		sb.append(failTag);

		sb.append(ignoredMsg);
		sb.append(ignoredTag);

		// sb.append(colorInformation);
		sb.append(tableStartTag);

		HashSet<String> allKeys = new HashSet<String>();
		Set<String> expKeys = uiMapofMap.keySet();
		Set<String> actKeys = csvMapofMap.keySet();
		allKeys.addAll(expKeys);
		allKeys.addAll(actKeys);

		Set momKeys = uiMapofMap.keySet();
		String strKey = null;

		for (String key : allKeys) {
			if (expKeys.contains(key) && actKeys.contains(key)) {
				strKey = key;
				break;
			}
		}
		System.out.println(strKey);

		LinkedHashMap<String, String> colHeadMap = csvMapofMap.get(strKey);
		Set<String> colHeaderList = colHeadMap.keySet();
		List<String> colHeadLst = new ArrayList<String>(colHeaderList);
		sb.append("<tr>");
		for (String colHead : colHeadLst) {
			sb.append(tdNormalStartTag);
			sb.append(colHead);
			sb.append(tdEndTag);
		}
		sb.append("</tr>");

		for (String key : allKeys) {
			LinkedHashMap<String, String> uiMap = uiMapofMap.get(key);
			LinkedHashMap<String, String> csvMap = csvMapofMap.get(key);
			if (uiMap != null && csvMap != null) {
				sb.append("<tr>");
				for (int i = 0; i < colHeadLst.size(); i++) {
					String colHeadValue = colHeadLst.get(i);
					// System.out.println("Column Header : "+colHeadValue);
					String uiValue = uiMap.get(colHeadValue);
					String csvValue = csvMap.get(colHeadValue);
					boolean ignoreFlag = false;
					for (int j = 0; j < ignoreFieldsArray.length; j++) {
						if (ignoreFieldsArray[j].equalsIgnoreCase(colHeadValue)) {
							ignoreFlag = true;
						}
					}
					if (uiValue != null && csvValue != null) {
						System.out.println("uiValue is " + uiValue);
						System.out.println("csvValue " + csvValue);
						if (ignoreFlag) {
							sb.append(tdOrangeStartTag);
							sb.append("Expected Value : " + uiValue + "<br>" + "Actual Value : " + csvValue);
						} else if (uiValue.equalsIgnoreCase(csvValue)) {
							sb.append(tdGreenStartTag);
							sb.append(uiValue);
						} else {
							status = false;
							sb.append(tdRedStartTag);
							sb.append("Expected Value : " + uiValue + "<br>" + "Actual Value : " + csvValue);
						}
						sb.append(tdEndTag);
					} else if (uiValue != null) {
						status = false;
						sb.append(tdRedStartTag);
						sb.append("Expected Value : " + uiValue + "<br>" + "Actual Value : ");
					} else if (csvValue != null) {
						status = false;
						sb.append(tdRedStartTag);
						sb.append("Expected Value : <br>" + "Actual Value : " + csvValue);
					}

				}
				sb.append("</tr>");
			} else if (uiMap == null) {
				sb.append("<tr>");
				for (int i = 0; i < colHeadLst.size(); i++) {
					String colHeadValue = colHeadLst.get(i);
					String csvValue = csvMap.get(colHeadValue);

					status = false;
					sb.append(tdRedStartTag);
					sb.append("Expected Value : <br>" + "Actual Value : " + csvValue);

					sb.append(tdEndTag);

				}
				sb.append("</tr>");
			} else if (csvMap == null) {

				sb.append("<tr>");
				for (int i = 0; i < colHeadLst.size(); i++) {
					String colHeadValue = colHeadLst.get(i);
					String uiValue = uiMap.get(colHeadValue);

					status = false;
					sb.append(tdRedStartTag);
					sb.append("Expected Value : " + uiValue + "<br>" + "Actual Value : ");

					sb.append(tdEndTag);

				}
				sb.append("</tr>");

			}

		}
		sb.append(tableEndTag);
		sb.append("<br/>");
		sb.append("<br/>");
		sb.append(closingTags);
		String strHtmlExtentRep = generateHTMLReportForText(strTestCase, htmlDistplayName, sb.toString(), status);
		resMessage.setOutputMessage(strHtmlExtentRep);
		resMessage.setSuccess(status);
		return resMessage;
	}

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
		String destDirFile = createDestDirFile(strHtmlFileName);
		StringBuilder mapCompareString = createTableDataForExpAulResult(
				expectedMap, actualMap);
		String htmlData = createHTMLFileData(mapCompareString, htmlDisplayText);
		htmlfile = createHTMLFile(destDirFile, htmlData, strHtmlFileName,
				hyperLinkText);
		// childtest.log(Status.INFO, "Test_Data", outputFile);

		// return outputFile;

		if (mapCompareString.toString().contains("color:#FF7050")) {
			ReportLogger.logInfo(Status.FAIL, htmlfile);
			return false;
		} else
			ReportLogger.logInfo(Status.PASS, htmlfile);

		return true;
	}
	
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
}
