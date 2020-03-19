package com.ca.base.reports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticator;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.MediaEntityModelProvider;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.KlovReporter;
import com.ca.base.BaseSuite;
import com.ca.base.DriverFactory;
import com.ca.base.pojos.WebDriverEnum;
import com.ca.util.CommonUtil;
import com.ca.util.LogMode;

public class KlovReportManager {

	public static ExtentReports extentreporter;
	public static KlovReporter klovReporter;
	
	public static Map<Long, String> threadToTestNameMap = new HashMap<Long, String>();
	//public static Map<String, ExtentTest> nameToTestMap = new HashMap<String, ExtentTest>();
	public static Map<Long, ExtentTest> threadToExtentTestMap = new HashMap<Long, ExtentTest>();
	public static String extentReportName = null;

	public static Map<String, Integer> methodCountMap = new HashMap<String, Integer>();
	
	public static String strMongoDbIp;
	public static int intMongoDbPort;
	public static String strKlovUrl;
	public static String strSuiteName;
	public static String projectName;
	
	
	public synchronized static void logInfo(Status testStatus, String stepName){
		if (testStatus.compareTo(Status.WARNING) == 0)
			testStatus = Status.INFO;

		Long threadId = new Long(Thread.currentThread().getId());
		/*String testCaseName1 = Thread.currentThread().getStackTrace()[1]
				.getMethodName();
		//System.out.println(" method name as per Thread stack " + testCaseName1);

		String testCaseName = threadToTesNametMap.get(new Long(threadId));
		//System.out.println(" method name as per Thread Map " + testCaseName);
*/
		ExtentTest extenttest = threadToExtentTestMap.get(threadId);
		
		if(stepName!=null && stepName.startsWith("<a")){
			
			Map<String,String> htmlMap = new HashMap<String, String>();
			
			try{
			htmlMap = checkForhtmlFileAndRead(stepName);
		    
		    stepName = getCollapseHtml(htmlMap.get("htmlTopic"),htmlMap.get("htmlFileContent"));
			}catch(Exception e){
				
			}
			
		}

		if(stepName!=null)
		extenttest.log(testStatus, stepName);
		
		extentreporter.flush();
		
		if(testStatus.equals(Status.FAIL)||testStatus.equals(Status.ERROR)){
			
			BaseSuite.threadItestResultMap.put(threadId,true);
		}
	}
	
	
	private static String getCollapseHtml(String htmlTopic, String htmlContent) {
		// TODO Auto-generated method stub
		
		String htmlTag="<details><summary>"+htmlTopic+"</summary><p>"+htmlContent+"</p></details>";
		
		
		return htmlTag;
	}


	public synchronized static void logInfo(Status testStatus, String stepName, LogMode logMode){
		try {
		    if (isScreenshotRequired(logMode)) {
		    	logInfo(testStatus, stepName);
		    } 
		} catch (Exception e) {
		    System.out.println("Exception occured in Info Logging: " +e.toString());
		}
	}
	
	public synchronized static void logScreenShot(Status testStatus, String stepName, WebDriverEnum driverEnum, LogMode logMode) {
		
		try {
		    if (isScreenshotRequired(logMode)) {
		    	logScreenShot(testStatus, stepName,driverEnum);
		    } 
		} catch (Exception e) {
		    System.out.println("Exception occured in Taking Screen Shot: " +e.toString());
		}
	}
	
	
	/**
	 * This method takes the screenshot by using given input and places in the
	 * desired location.
	 * 
	 * 
	 */
	public synchronized static void logScreenShot(Status testStatus, String stepName, WebDriverEnum driverEnum) {

		if (testStatus.compareTo(Status.WARNING) == 0) {
			testStatus = Status.INFO;
		}
		Long threadId = new Long(Thread.currentThread().getId());
		
		if(testStatus.equals(Status.FAIL)||testStatus.equals(Status.ERROR)){
			
			BaseSuite.threadItestResultMap.put(threadId,true);
		}


		
		String reporting = BaseSuite.caPropMap.get("reporting");
		WebDriver driver = DriverFactory.getDriver(threadId, driverEnum);
		/*String testCaseName1 = Thread.currentThread().getStackTrace()[1]
				.getMethodName();*/
		//System.out.println(" method name as per Thread stack " + testCaseName1);

		String testCaseName = threadToTestNameMap.get(threadId);
		ExtentTest extenttest = threadToExtentTestMap.get(threadId);

		if (reporting.equalsIgnoreCase("OFF")) {
			logInfo(testStatus, stepName);
			return;
		}

		String destFile = null;
		try {
			destFile = getScreenshotFilePath(driver,
					testCaseName, stepName);
		} catch (NullPointerException e) {
			e.printStackTrace();
			if (extenttest != null)
				extenttest.log(Status.WARNING,
						"Exception while capturing image " + e.getMessage()
								+ "\nstackTrace:"
								+ e.getStackTrace().toString());
			return;
		} catch (IOException e) {
			e.printStackTrace();
			if (extenttest != null)
				extenttest.log(Status.WARNING,
						"Exception while capturing image " + e.getMessage()
								+ "\nstackTrace:"
								+ e.getStackTrace().toString());
			return;
		} catch (WebDriverException e) {
			e.printStackTrace();
			if (extenttest != null)
				extenttest.log(Status.WARNING,
						"Exception while capturing image " + e.getMessage()
								+ "\nstackTrace:"
								+ e.getStackTrace().toString());
			return;
		}

		try {
			if (extenttest == null) {
				System.out
						.println("ERROR: Extent TEST IS NULL. IMAGE IS CAPTURED AT "
								+ destFile
								+ " BUT IMAGE WILL NOT BE ATTACHED TO EXTENT REPORT");
				System.out.println("ERROR: PLEASE DEBUG PLEASE LOOK INTO THIS");
				return;
			}
			extenttest.log(testStatus, stepName + " image attached below");
			/*String image = extenttest.addScreenCapture(destFile);
			extenttest.log(testStatus, stepName, image);*/
			
			MediaEntityModelProvider mediaModel;
			
				mediaModel = MediaEntityBuilder.createScreenCaptureFromBase64String(destFile).build();
				extenttest.log(testStatus, stepName, mediaModel);

			extentreporter.flush();
		} catch (Exception e) {
			e.printStackTrace();
			extenttest.log(Status.WARNING,
					"Exception while capturing image " + e.getMessage()
							+ "\nstackTrace:" + e.getStackTrace().toString());
		}
	}
	
	public synchronized static void initialiseReport(String mongoDbIp,int mongoDbPort,String klovUrl,String suiteName,String strprojectName) {
		
		strMongoDbIp=mongoDbIp;
		intMongoDbPort=mongoDbPort;
		strKlovUrl=klovUrl;
		strSuiteName=suiteName;
		projectName=strprojectName;
		
		if (extentreporter == null) {

			extentreporter = new ExtentReports();
			
			klovReporter = new KlovReporter();
			
			klovReporter.initMongoDbConnection(strMongoDbIp, intMongoDbPort);	
			klovReporter.setProjectName(projectName);
			klovReporter.setReportName(strSuiteName);
			klovReporter.setKlovUrl(strKlovUrl);
			
			extentreporter.attachReporter(klovReporter);
		}
		
	}

	public synchronized static ExtentReports getExtentReport() {
		
		if(extentreporter==null){
			
			initialiseReport(strMongoDbIp, intMongoDbPort, strKlovUrl, strKlovUrl,projectName);
		}
		return extentreporter;
		
	}

	public synchronized static ExtentTest getTest(String methodName, String testDescription, String strTestName,
			int... counts) {

		Long threadID = Thread.currentThread().getId();
		if(!threadToExtentTestMap.containsKey(threadID)){
			ExtentTest test = getExtentReport().createTest(methodName, testDescription);
			threadToExtentTestMap.put(threadID, test);
			threadToTestNameMap.put(threadID, methodName);
		}
		
		return threadToExtentTestMap.get(threadID);
		
		
		/*int count = 1;
		for (int intCount : counts) {
			count = intCount;
		}

		// if this test has already been created return
		if (!nameToTestMap.containsKey(methodName)) {
			Long threadID = Thread.currentThread().getId();
			System.out.println("Extent report test case name = " + methodName + " " + count);
			//ExtentTest test = getExtentReport().startTest(methodName, testDescription);
			
			ExtentTest test = getExtentReport().createTest(methodName, testDescription);
			nameToTestMap.put(methodName, test);
			threadToTestNametMap.put(threadID, methodName);

		}
		return nameToTestMap.get(methodName);*/
	}

	public synchronized static ExtentTest getTest(String suiteName, String methodName, String testDescription,
			String strTestName, int... counts) {

		Long threadID = Thread.currentThread().getId();
		if(!threadToExtentTestMap.containsKey(threadID)){
			ExtentTest test = getExtentReport().createTest(methodName, testDescription);
			threadToExtentTestMap.put(threadID, test);
			threadToTestNameMap.put(threadID, methodName);
		}
		
		return threadToExtentTestMap.get(threadID);
		
		/*int count = 1;
		for (int intCount : counts) {
			count = intCount;
		}

		// if this test has already been created return
		if (!nameToTestMap.containsKey(methodName)) {
			Long threadID = Thread.currentThread().getId();
			System.out.println("Extent report test case name = " + methodName + " " + count);
			//ExtentTest test = getExtentReport().startTest(methodName, testDescription);
			
			ExtentTest test = getExtentReport().createTest(methodName, testDescription);
			test.assignCategory(suiteName);
			nameToTestMap.put(methodName, test);
			threadToTestNameMap.put(threadID, methodName);

		}
		return nameToTestMap.get(methodName);*/
	}

	public synchronized static ExtentTest getTest(String methodName, String strTestName, int... count) {
		return getTest(methodName, "", strTestName, count);
	}

	public synchronized static ExtentTest getTest() {
		Long threadID = Thread.currentThread().getId();
		return threadToExtentTestMap.get(threadID);

		/*if (threadToTestNameMap.containsKey(threadID)) {
			String testName = threadToTestNameMap.get(threadID);
			return nameToTestMap.get(testName);
		}
		// In actual scenario null should never happen
		return null;*/
	}

	public synchronized static void endTest() {
		
		Long threadID = Thread.currentThread().getId();
		ExtentTest test = null;
		if (threadToExtentTestMap.containsKey(threadID)) {		
			test = threadToExtentTestMap.get(threadID);
			threadToExtentTestMap.remove(threadID);
			threadToTestNameMap.remove(threadID);
		}
		
		/*if (threadToTestNameMap.containsKey(threadID)) {
			String testName = threadToTestNameMap.get(threadID);
			test = nameToTestMap.get(testName);
			nameToTestMap.remove(testName);
		}*/

		if (test != null) {
			//extentreporter.endTest(test);
			extentreporter.flush();
		}

	}

	public synchronized static void endTest(String testName) {

		Long threadID = Thread.currentThread().getId();
		ExtentTest test = null;
		if (threadToExtentTestMap.containsKey(threadID)) {		
			test = threadToExtentTestMap.get(threadID);
			threadToExtentTestMap.remove(threadID);
			threadToTestNameMap.remove(threadID);
		}
		
		/*ExtentTest test = null;
		if (nameToTestMap.containsKey(testName)) {

			test = nameToTestMap.get(testName);
			nameToTestMap.remove(testName);
		}*/

		if (test != null) {
			//extentreporter.endTest(test);
			extentreporter.flush();
		}
	}

	public synchronized static void closeTest(ExtentTest test) {
		if (test != null) {
			//getExtentReport().endTest(test);
		}
	}

	public synchronized static void closeTest() {
		ExtentTest test = getTest();
		closeTest(test);
	}

	public synchronized static void closeReport() {
		if (extentreporter != null) {
			extentreporter.flush();
			//extentreporter.close();
		}
	}

	public synchronized static void flushReport() {
		if (extentreporter != null) {
			extentreporter.flush();
		}
	}
	
	/**
	 * Checks whether the screenshot should be taken or not based on the log mode
	 * FATAL => Only Fatal logs will be displayed
	 * ERROR => ERROR and FATAL logs will be displayed
	 * INFO => INFO, ERROR and FATAL logs will be displayed
	 * WARN => WARN, INFO, ERROR and FATAL logs will be displayed
	 * DEBUG => All logs ie. DEBUG, WARN, INFO, ERROR, FATAL logs will be displayed
	 *
	 * @param LogMode log mode (FATAL/ERROR/INFO/WARN/DEBUG)
	 * @return Boolean value to tell whether the log should be displayed or not
	 * @throws Exception
	 */
	private synchronized static boolean isScreenshotRequired(LogMode logMode) throws Exception {
		boolean isLoggingRequired = false;
		String propertyLogMode = BaseSuite.caPropMap.get("LogMode");

		if (propertyLogMode != null && !propertyLogMode.equalsIgnoreCase("none")) {
        		int propertyLogModeValue = 0;
        		int requestedLogModeValue = logMode.getValue();
        		propertyLogModeValue = LogMode.valueOf(propertyLogMode.toUpperCase()).getValue();
        		isLoggingRequired = ((propertyLogModeValue & requestedLogModeValue) == requestedLogModeValue);
		}

		return isLoggingRequired;
	}
	
	public synchronized static String getScreenshotFilePath(WebDriver driver,
			String strTestCaseName, String strTestStep) throws IOException,
			WebDriverException, NullPointerException {

		
		String scrFile = null;
		try {
			scrFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.BASE64);
			System.out.println("screen shot Taken");
		} catch (WebDriverException ex) {
			System.out
					.println("ERROR while taking screen shot please debug:\nmessage:"
							+ ex.getMessage()
							+ "\nURL"
							+ ex.getSupportUrl()
							+ "\nSystem Info:"
							+ ex.getSystemInformation()
							+ "\n BuildInfo:"
							+ ex.getBuildInformation()
							+ "\n additional Info:"
							+ ex.getAdditionalInformation());
			ex.printStackTrace();
			throw ex;
		}
		
		//TODO VINOD write a code to change the report location to /Report/ExtentReport/ also write a code to flush the folder

		
		return scrFile;
	}
	
	

	public synchronized static void initialiseReportForAtTest(ITestResult result) {

		String suiteName = result.getTestContext().getSuite().getName();
		String xmlTestName = result.getTestContext().getName();
		String methodName = result.getMethod().getConstructorOrMethod().getMethod().getName();
		Integer count = new Integer(1);
		if (methodCountMap.containsKey(methodName)) {
			count = methodCountMap.get(methodName);
			count++;
			methodCountMap.put(methodName, count);
		} else {
			methodCountMap.put(methodName, count);
		}
		System.out.println("Inside Extent Test creation for Method : " + methodName);
		System.out.println("Thread ID = " + Thread.currentThread().getId());
		System.out.println(" Sending value to extent report " + methodName
				+ " count =" + count.intValue());
		
		
		String strTestName = result.getMethod().getConstructorOrMethod().getMethod().getName();
		String strTestCaseId = null;
		String strTestCaseTitle = null;
		String strTestCaseDesc = null;
		Map<String, String> testCaseData = null;

		Object[] parameterValues = result.getParameters();
		if (parameterValues == null || parameterValues.length < 1) {
			System.out.println("method parameter values are null or empty");
		} else
			System.out.println("No of parameter values are:" + parameterValues.length);

		for (int i = 0; i < parameterValues.length; i++) {
			System.out.println("Parameter " + (i + 1) + "=" + parameterValues[i].toString());
			if (parameterValues[i] instanceof Map) {				
				Map<String, Object> map = (Map<String, Object>) parameterValues[i];
				if(map.containsKey("TestCaseID")){
					strTestCaseId= (String) map.get("TestCaseID");
					strTestCaseTitle= (String) map.get("TestCaseTitle");
					strTestCaseDesc = (String) map.get("TestCaseDescription");
					testCaseData = (Map<String, String>) parameterValues[i] ;
				}
				
			}				
		}
		
		if(strTestCaseTitle!=null){
			final StringBuilder ret = new StringBuilder(strTestCaseTitle.length());
	
			//Code to convert Test Case Title to Camel Case
		    for (final String word : strTestCaseTitle.split(" ")) {
		        if (!word.isEmpty()) {
		            ret.append(word.substring(0, 1).toUpperCase());
		            ret.append(word.substring(1));
		        }
		        if (!(ret.length()==strTestCaseTitle.length()))
		            ret.append(" ");
		    }
	    
		    strTestCaseTitle= ret.toString();
		}
		if(strTestCaseId!=null){
			strTestName = strTestCaseId+"_"+strTestName;
		}
		if(strTestCaseTitle!=null){
			strTestName = strTestName+"_"+strTestCaseTitle;
		}
	 
		ExtentTest test = getTest(suiteName,strTestName, "", xmlTestName,
				count.intValue());
		
		Long threadId = new Long(Thread.currentThread().getId());
		BaseSuite.threadItestResultMap.put(threadId, false);
		System.out.println("Thread id is " + threadId);
		
		System.out.println("Extent Test created for Method : " + methodName);
		
	}
	
	public synchronized static void closeReportForAtTest(ITestResult testResult) {
				
		String methodName = testResult.getMethod().getConstructorOrMethod().getMethod().getName();
		Long threadId = new Long(Thread.currentThread().getId());
		System.out.println("Thread ID = " + threadId);
		String message = "Test Passed";
		Status status = Status.PASS;
		try {
			if (testResult.getStatus() == ITestResult.FAILURE) {
				/*System.out
						.println("calling take screen shot in CaptureScreen method in failure case");			*/	
				
				status = Status.FAIL;
				if(!BaseSuite.threadItestResultMap.get(threadId)){
					/*BaseSuiteDES
					.takeScreenShot(Status.FAIL, "Error Captured");*/
					Throwable objThrow = testResult.getThrowable();
					if (objThrow != null) {
						message = testResult.getThrowable().getMessage();
						logInfo(Status.FAIL, message);
					} else
						message = null;
				}

			}

		} catch (Exception e) {
			System.out.println(" error while getting screen shots ");
		} finally {

			ExtentTest test = getTest();					
			endTest(methodName);
			
		}
		
	}


	public synchronized static void logTestCaseIDandDesc(ITestResult result) {
		
		Method method = result.getMethod().getConstructorOrMethod().getMethod();
		System.out.println("Method name:" + method.getName());
		
		String strTestName = method.getName();
		String strTestCaseId = null;
		String strTestCaseTitle = null;
		String strTestCaseDesc = null;
		Map<String, String> testCaseData = null;

		Object[] parameterValues = result.getParameters();
		if (parameterValues == null || parameterValues.length < 1) {
			System.out.println("method parameter values are null or empty");
		} else
			System.out.println("No of parameter values are:" + parameterValues.length);

		for (int i = 0; i < parameterValues.length; i++) {
			System.out.println("Parameter " + (i + 1) + "=" + parameterValues[i].toString());
			if (parameterValues[i] instanceof Map) {				
				Map<String, Object> map = (Map<String, Object>) parameterValues[i];
				if(map.containsKey("TestCaseID")){
					strTestCaseId= (String) map.get("TestCaseID");
					strTestCaseTitle= (String) map.get("TestCaseTitle");
					strTestCaseDesc = (String) map.get("TestCaseDescription");
					testCaseData = (Map<String, String>) parameterValues[i] ;
				}
				
			}				
		}
		
		String testDesc = "";
		if(strTestCaseDesc!=null){
			String[] lstTestDesc = strTestCaseDesc.split("\n");
			for(String desc : lstTestDesc){
				testDesc+=desc+"<br>";
			}
		}
			
		if(strTestCaseTitle!=null){
			final StringBuilder ret = new StringBuilder(strTestCaseTitle.length());
	
			//Code to convert Test Case Title to Camel Case
		    for (final String word : strTestCaseTitle.split(" ")) {
		        if (!word.isEmpty()) {
		            ret.append(word.substring(0, 1).toUpperCase());
		            ret.append(word.substring(1));
		        }
		        if (!(ret.length()==strTestCaseTitle.length()))
		            ret.append(" ");
		    }
	    
		    strTestCaseTitle= ret.toString();
		}
		if(strTestCaseId!=null){
			strTestName = strTestCaseId+"_"+strTestName;
		}
		if(strTestCaseTitle!=null){
			strTestName = strTestName+"_"+strTestCaseTitle;
		}
	    ExtentTest extentTest = getTest();
		//extentTest.getTest().setName(strTestName);
		
	    extentTest.getModel().setName(strTestName);
		
		if(strTestCaseTitle!=null&&strTestCaseTitle.length()>0)
			logInfo(Status.INFO, "<b>Test Case Title: "
					+ strTestCaseTitle + "</b>");
		
		if(testDesc!=null&&testDesc.length()>0)
			logInfo(Status.INFO, "<b>Test Case Description: "
				+ "<br>"+ "</b>"+testDesc );
		
		CommonUtil cu = new CommonUtil();
		// Test Data from Excel.
		String htmlfile = cu.generateHTMLReportForInputTestData(testCaseData,
				strTestName, "Input XL Sheet Data for " + strTestName,
				"Input XL Sheet Data");
		logInfo(Status.INFO, htmlfile);
		
	}


	public synchronized static void displayServerLog(String testCaseID,String logName,String logDisplayName, String logData, String  outputFileName) {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
		Date date = new Date();
		String strDate = dateFormat.format(date);
		String htmlFileName = testCaseID+"_"+logName+"_"+strDate+".log";
		
		String destDirFileFullPath = ExtentReportUtil.getDestDirFileFullPath(htmlFileName);
		String htmlData = ExtentReportUtil.createHTMLFileDataForLogs(testCaseID,logData, logDisplayName);
		String extentLogOutput = ExtentReportUtil.createHTMLFileForLogs(destDirFileFullPath, htmlData, htmlFileName, logDisplayName);
		logInfo(Status.INFO, extentLogOutput);
		
	}
	
	private static Map<String, String> checkForhtmlFileAndRead(String strContent){
		
		String content = "";
			String regExp= Pattern.quote("=..") + "(.*?)" + Pattern.quote("target");
			
			Pattern pattern = Pattern.compile(regExp);
			
			Matcher matcher = pattern.matcher(strContent);
			String textInBetween = null;
			
			while (matcher.find()) {
				  textInBetween = matcher.group(1); 
				}
			
			String htmlFilePath = "/Extent"+textInBetween.trim();
			
			regExp= Pattern.quote(">") + "(.*?)" + Pattern.quote("<");
			
			pattern = Pattern.compile(regExp);
			
			matcher = pattern.matcher(strContent);
			textInBetween = null;
			
			while (matcher.find()) {
				  textInBetween = matcher.group(1); 
				}
			
			String htmlTopic = textInBetween;
			
		    try {
		        BufferedReader in = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/TestResult"+htmlFilePath));
		        String str;
		        while ((str = in.readLine()) != null) {
		            content +=str+"\n";
		        }
		        in.close();
		    } catch (IOException e) {
		    }
		    
		    if(htmlFilePath.contains(".log")){
		    	content=content.replaceAll("<", "&lt;");
		    	content=content.replaceAll(">", "&gt;");
		    	content = "<div>"+content;
		    	content=content.replaceAll("\n","</div><div>");
		    	content = content+"</div>";
		    }
		    Map<String,String> htmlMap = new HashMap<String, String>();
		    htmlMap.put("htmlTopic", htmlTopic);
		    htmlMap.put("htmlFileContent", content);
		    
		    return htmlMap;
		    
	}
	

}
