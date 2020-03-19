package com.ca.base.reports;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
//import com.relevantcodes.extentreports.*;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
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
import com.ca.base.BaseSuite;
import com.ca.base.DriverFactory;
import com.ca.base.pojos.WebDriverEnum;
import com.ca.util.CommonUtil;
import com.ca.util.LogMode;



public class ExtentReportManager {
	
	

	// Extent Reporting Part
	public static ExtentReports extentreporter;
	
	public static Map<Long, String> threadToTestNameMap = new HashMap<Long, String>();
	//public static Map<String, ExtentTest> nameToTestMap = new HashMap<String, ExtentTest>();
	public static Map<Long, ExtentTest> threadToExtentTestMap = new HashMap<Long, ExtentTest>();
	public static String extentReportName = null;

	public static Map<String, Integer> methodCountMap = new HashMap<String, Integer>();

	//public static ExtentReports extent;

	public static String strExtenReportFileName = null;

	public static String strExtenReportTitle = null;
	
	public static ExtentHtmlReporter htmlReport;
	//private static ExtentHtmlReporter extentHtmlReporter;
	
	public static void logInfo(Status testStatus, String stepName){
		if (testStatus.compareTo(Status.WARNING) == 0)
			testStatus = Status.INFO;

		Long threadId = new Long(Thread.currentThread().getId());
		/*String testCaseName1 = Thread.currentThread().getStackTrace()[1]
				.getMethodName();
		//System.out.println(" method name as per Thread stack " + testCaseName1);

		//String testCaseName = threadToTesNametMap.get(new Long(threadId));
		//System.out.println(" method name as per Thread Map " + testCaseName);
*/
		ExtentTest extenttest = threadToExtentTestMap.get(threadId);

		extenttest.log(testStatus, stepName);
		extentreporter.flush();
		
		if(testStatus.equals(Status.FAIL)||testStatus.equals(Status.ERROR)){
			
			BaseSuite.threadItestResultMap.put(threadId,true);
		}
	}
	
	
	public static void logInfo(Status testStatus, String stepName, LogMode logMode){
		try {
		    if (isScreenshotRequired(logMode)) {
		    	logInfo(testStatus, stepName);
		    } 
		} catch (Exception e) {
		    System.out.println("Exception occured in Info Logging: " +e.toString());
		}
	}
	
	public static void logScreenShot(Status testStatus, String stepName, WebDriverEnum driverEnum, LogMode logMode) {
		
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
	public static void logScreenShot(Status testStatus, String stepName, WebDriverEnum driverEnum) {

		if(testStatus.compareTo(Status.DEBUG)== 0){
			
			logScreenShot(Status.INFO, stepName, driverEnum, LogMode.DEBUG);
		}
		
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

		/*String testCaseName = threadToTesNametMap.get(new Long(threadID));
		//System.out.println(" method name as per Thread Map " + testCaseName);
*/
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
			
				mediaModel = MediaEntityBuilder.createScreenCaptureFromPath(destFile).build();
				extenttest.log(testStatus, stepName, mediaModel);

			extentreporter.flush();
		} catch (Exception e) {
			e.printStackTrace();
			extenttest.log(Status.WARNING,
					"Exception while capturing image " + e.getMessage()
							+ "\nstackTrace:" + e.getStackTrace().toString());
		}
	}
	
	public synchronized static void initialiseReport(String extenReportFileName, String extenReportTitle) {
		/**
		 * This method reads the report file name and creates it at given path
		 * after initialization the extent reports.
		 * 
		 * @param testContext		 */		
		strExtenReportFileName=extenReportFileName;
		strExtenReportTitle=extenReportTitle;
		
		if (extentreporter == null) {

			String dest = System.getProperty("user.dir") + strExtenReportFileName;
			System.out.println(" Extent Report path is " + dest);
			String destDir = System.getProperty("user.dir")+"/TestResult/Extent/TestReport/";
			
			if (!new File(destDir).exists())
				new File(destDir).mkdirs();
			
			//extentreporter = new ExtentReports(dest, true, DisplayOrder.OLDEST_FIRST);
			extentreporter = new ExtentReports();
			htmlReport = new ExtentHtmlReporter(dest);
			htmlReport.config().setDocumentTitle(strExtenReportTitle);
			
			extentreporter.attachReporter(htmlReport);
		}
		
	}

	public synchronized static ExtentReports getExtentReport() {
		
		if(extentreporter==null){
		
			if(strExtenReportFileName==null){
				strExtenReportFileName = ""+File.separator+"TestResult"+File.separator+"TestReport"+File.separator+"SITAutomation.html";
			}
			
			if(strExtenReportFileName==null){
				strExtenReportTitle = "Automation Report";
			}		
			initialiseReport(strExtenReportFileName,strExtenReportTitle);
		}
		return extentreporter;
		
	}

	public static ExtentTest getTest(String methodName, String testDescription, String strTestName,
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
		}*/
		
		

		// if this test has already been created return
		/*if (!nameToTestMap.containsKey(methodName)) {
			Long threadID = Thread.currentThread().getId();
			System.out.println("Extent report test case name = " + methodName + " " + count);
			//ExtentTest test = getExtentReport().startTest(methodName, testDescription);
			
			ExtentTest test = getExtentReport().createTest(methodName, testDescription);
			nameToTestMap.put(methodName, test);
			threadToTestNameMap.put(threadID, methodName);

		}
		return nameToTestMap.get(methodName);*/
		
		
	}

	public static ExtentTest getTest(String suiteName, String methodName, String testDescription,
			String strTestName, int... counts) {

		Long threadID = Thread.currentThread().getId();
		if(!threadToExtentTestMap.containsKey(threadID)){
			ExtentTest test = getExtentReport().createTest(methodName, testDescription);
			test.assignCategory(suiteName);
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

	public static ExtentTest getTest() {
		
		Long threadID = Thread.currentThread().getId();
		return threadToExtentTestMap.get(threadID);

		/*if (threadToTestNameMap.containsKey(threadID)) {
			String testName = threadToTestNameMap.get(threadID);
			return nameToTestMap.get(testName);
		}
		// In actual scenario null should never happen
		return null;*/
	}

	public static void endTest() {
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

	public static void endTest(String testName) {
		
		Long threadID = Thread.currentThread().getId();
		ExtentTest test = null;
		if (threadToExtentTestMap.containsKey(threadID)) {		
			test = threadToExtentTestMap.get(threadID);
			threadToExtentTestMap.remove(threadID);
			threadToTestNameMap.remove(threadID);
		}

/*		ExtentTest test = null;
		if (nameToTestMap.containsKey(testName)) {

			test = nameToTestMap.get(testName);
			nameToTestMap.remove(testName);
		}*/

		if (test != null) {
			//extentreporter.endTest(test);
			extentreporter.flush();
		}
	}

	public static void closeTest(ExtentTest test) {
		if (test != null) {
			//getExtentReport().endTest(test);
		}
	}

	public static void closeTest() {
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
	private static boolean isScreenshotRequired(LogMode logMode) throws Exception {
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
	
	public static String getScreenshotFilePath(WebDriver driver,
			String strTestCaseName, String strTestStep) throws IOException,
			WebDriverException, NullPointerException {

		String testCaseName = strTestCaseName.replaceAll(" ", "");
		String testStep = strTestStep.replaceAll(" ", "");
		
		Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
		Matcher match= pt.matcher(strTestCaseName);
        while(match.find())
        {
            String s= match.group();
            strTestCaseName=strTestCaseName.replaceAll("\\"+s, "");
        }
		
		
		match= pt.matcher(testStep);
		while(match.find()){
		
			String s= match.group();
			testStep=testStep.replaceAll("\\"+s, "");
		}
		
		if(testCaseName!=null&&testCaseName.length()>30)
			testCaseName = testCaseName.substring(0,30);
		      
		//String strimageName = testCaseName + "-" + testStep;
		
		/*System.out.println("Taking screen shot for " + strimageName
				+ " ....>>>>");*/
		if (driver == null) {
			System.out
					.println("ERROR: DRIVER IS NULL PLEASE DEBUG PLEASE CHECK");
			throw new NullPointerException(
					"Driver passed to take screen shot is null");
		}

		Date d = new Date();
		Timestamp t = new Timestamp(d.getTime());
		// System.out.println(t);
		String timeStamp = t.toString();
		timeStamp = timeStamp.replace(' ', '_');
		timeStamp = timeStamp.replace(':', '_');
		
		Random rand = new Random(); 
		
		long rand_int1 = rand.nextLong(); 
		
		Long threadID = Thread.currentThread().getId();
		
		String testCaseId = BaseSuite.threadTestCaseIdMap.get(threadID);
		
		
		String strimageName = testCaseId+"_"+rand_int1+"_"+timeStamp;
		
		File scrFile = null;
		try {
			scrFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);
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

		String destdir = System.getProperty("user.dir") + File.separator
				+ "TestResult"+File.separator+"Extent"+File.separator+"Screenshot"+File.separator+testCaseName;
		System.out.println("destination dir:--> " + destdir);
		String relativedir = ".." + File.separator + "Screenshot" + File.separator + testCaseName;
		System.out.println("Relative dir:--> " + relativedir);
		if (!new File(destdir).exists())
			new File(destdir).mkdirs();
		String destFile = destdir + File.separator + strimageName +".jpg";
		String reldirFile = relativedir + File.separator + strimageName + ".jpg";
		try {
			FileUtils.copyFile(scrFile, new File(destFile));
		} catch (IOException e) {
			System.out.println("ERROR: IOException while copying");
			e.printStackTrace();
			throw e;
		}
		return reldirFile;
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
		ExtentTest test = getTest(suiteName,methodName, "", xmlTestName,
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


	public static void logTestCaseIDandDesc(ITestResult result) {
		
		Method method = result.getMethod().getConstructorOrMethod().getMethod();
		System.out.println("Method name:" + method.getName());
		System.out.println("");
		
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
				else{
					System.out.println("ERROR: Your Test data should have TestCaseID field.");
					System.err.println("ERROR: Your Test data should have TestCaseID field.");
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


	public static void displayServerLog(String testCaseID,String logName,String logDisplayName, String logData, String  outputFileName) {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
		Date date = new Date();
		String strDate = dateFormat.format(date);
		String htmlFileName = testCaseID+"_"+logName+"_"+strDate+".log";
		
		String destDirFileFullPath = ExtentReportUtil.getDestDirFileFullPath(htmlFileName);
		String htmlData = ExtentReportUtil.createHTMLFileDataForLogs(testCaseID,logData, logDisplayName);
		String extentLogOutput = ExtentReportUtil.createHTMLFileForLogs(destDirFileFullPath, htmlData, htmlFileName, logDisplayName);
		logInfo(Status.INFO, extentLogOutput);
		
	}


	

}
