package com.ca.base;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.internal.TestResult;

import com.ca.base.pojos.BrowserEnum;
import com.ca.base.pojos.ServerLogOutputPojo;
import com.ca.base.pojos.WebDriverEnum;
import com.ca.base.reports.ReportLogger;
import com.aventstack.extentreports.Status;

public class SITListener implements ITestListener, ISuiteListener {
	
	
	public void onStart(ISuite suite) {
		//HybridFactory.initialiseRepos();
		ReportLogger.initialiseLogger(suite);
		ServerLogListener.initialiseLogger(suite);
		
	}

	/**
	 * This will be called when Suite is Finished
	 */
	public void onFinish(ISuite suite) {
		// TODO Auto-generated method stub
		
		ReportLogger.closeReport();
		Map<String, ISuiteResult> results = suite.getResults();
		
		for(String key:results.keySet()){
			System.out.println("Key is:"+key);
			ISuiteResult b = results.get(key);
			int passedTestCases = b.getTestContext().getPassedTests().size();
			int failedTests = b.getTestContext().getFailedTests().size();
			int skippedTests = b.getTestContext().getSkippedTests().size();
			
			int totalNumOfTest = passedTestCases+failedTests+skippedTests;
			
			System.out.println("*********************************************");
			System.out.println("Test cases details executed in Suite"+key);
			System.out.println("Number of Test executed in Suite \""+key+"\" : "+totalNumOfTest+" , Passed Tests : "+passedTestCases+" , Failed Tests : "+failedTests+
					" , Skipped Tests : "+skippedTests);
			System.out.println("Pass Percentage of Tests executed in Suite \""+key+"\" : "+(passedTestCases/totalNumOfTest)*100);
			System.out.println("*********************************************");
			
			System.out.println("==============================================");
			
			
			BaseSuite.numOfPASSEDTestCases = BaseSuite.numOfPASSEDTestCases + passedTestCases;
			BaseSuite.numOfFAILEDTestCases = BaseSuite.numOfFAILEDTestCases + failedTests;
			BaseSuite.numOfSKIPPEDTestCases = BaseSuite.numOfSKIPPEDTestCases + skippedTests;
			BaseSuite.totalNumOfTestCases = BaseSuite.totalNumOfTestCases + totalNumOfTest;
			
		}
		
		System.out.println("*********************************************");
		System.out.println("Overall Test cases details executed");
		System.out.println("Number of Test executed : "+BaseSuite.totalNumOfTestCases+" , Passed Tests : "+BaseSuite.numOfPASSEDTestCases+" , Failed Tests : "+BaseSuite.numOfFAILEDTestCases+
				" , Skipped Tests : "+BaseSuite.numOfSKIPPEDTestCases);
		System.out.println("Pass Percentage of Overall Tests executed : "+(BaseSuite.numOfPASSEDTestCases/BaseSuite.totalNumOfTestCases)*100);
		System.out.println("*********************************************");
		
		
	}

	public void onTestStart(ITestResult result) {
		
			
		//BaseSuite.num_Of_TestCases++;
		
		String suiteName = result.getTestContext().getSuite().getName();
		String methodName = result.getMethod().getConstructorOrMethod().getMethod().getName();
		System.out.println("On Test Start in the Listener for the Suite : "+suiteName+"  and for Method Name :"+methodName);
		
		String strBrowser = null;

		Object[] parameterValues = result.getParameters();
		if (parameterValues == null || parameterValues.length < 1) {
			System.out.println("method parameter values are null or empty");
		} else
			System.out.println("No of parameter values are:" + parameterValues.length);

		String strTestCaseId = null;
		
		
		for (int i = 0; i < parameterValues.length; i++) {
			System.out.println("Parameter " + (i + 1) + "=" + parameterValues[i].toString());
			if (parameterValues[i] instanceof Map) {				
				Map<String, Object> map = (Map<String, Object>) parameterValues[i];
				if(map.containsKey("TestCaseID")){
					strTestCaseId= (String) map.get("TestCaseID");
				}
				if(map.containsKey("Browser")){
					strBrowser= (String) map.get("Browser");
					break;
				}
				
			}				
		}
		Long threadId = Long.valueOf(Thread.currentThread().getId());
		BaseSuite.threadTestCaseIdMap.put(threadId, strTestCaseId);
		if(strBrowser!=null){
			
			BaseSuite.threadBrowserMap.put(threadId, BrowserEnum.getBrowserEnumForString(strBrowser));
		}
		
		ReportLogger.initialiseReportForAtTest(result);
		ReportLogger.logTestCaseIDandDesc(result);
		if(ServerLogListener.loggerInitialised){
			ServerLogListener.startServerLogging(result);
		}
		
		
		BaseSuite.threadItestResultMap.put(Thread.currentThread().getId(),Boolean.valueOf(false));
		
		
	}

	public void onTestSuccess(ITestResult result) {
		
		//BaseSuite.num_Of_PASS_TestCases++;
		
		String suiteName = result.getTestContext().getSuite().getName();
		String methodName = result.getMethod().getConstructorOrMethod().getMethod().getName();
		System.out.println("On Test Success in the Listener for the Suite : "+suiteName+"  and for Method Name :"+methodName);
		
		Long threadId = Long.valueOf(Thread.currentThread().getId());
		WebDriverEnum driverEnum = DriverFactory.threadToCurrentDriverMap.get(threadId);
		ReportLogger.logScreenShot(Status.PASS, "Screenshot", driverEnum);
	//	ReportLogger.logInfo(Status.FAIL,exceptionAsString);
		
		if(ServerLogListener.loggerInitialised){
			ServerLogListener.stopServerLogging(result);
		
			String strMode = ServerLogListener.serverLogPropMap.get("ServerLoggingMode");
			if(strMode!=null&& strMode.equalsIgnoreCase("Default")){
				String testCaseID = ServerLogListener.getTestCaseIDfromITestResult(result);
				displayServerLogsInReport(testCaseID);
			}
		
		}
		if(BaseSuite.threadItestResultMap.get(Thread.currentThread().getId()).equals(true)){
			ReportLogger.logInfo(Status.FAIL, "Test Case Failed");
			result.setStatus(TestResult.FAILURE);;
			
		}
		else{
		ReportLogger.logInfo(Status.PASS, "Test Case Passed");
		}
		
		ReportLogger.closeReportForAtTest(result);
		
	}

	

	public void onTestFailure(ITestResult result) {
		
		//BaseSuite.num_Of_FAIL_TestCases++;
		String suiteName = result.getTestContext().getSuite().getName();
		String methodName = result.getMethod().getConstructorOrMethod().getMethod().getName();
		System.out.println("On Test Failure in the Listener for the Suite : "+suiteName+"  and for Method Name :"+methodName);
		Throwable objThrow = result.getThrowable();
		if(ServerLogListener.loggerInitialised){
			ServerLogListener.stopServerLogging(result);
			
			String strMode = ServerLogListener.serverLogPropMap.get("ServerLoggingMode");
			if(strMode!=null&& (strMode.equalsIgnoreCase("Default")||strMode.equalsIgnoreCase("Failure"))){
				String testCaseID = ServerLogListener.getTestCaseIDfromITestResult(result);
				displayServerLogsInReport(testCaseID);
			}
		}
		Long threadId = Long.valueOf(Thread.currentThread().getId());
		WebDriverEnum driverEnum = DriverFactory.threadToCurrentDriverMap.get(threadId);

		if (objThrow != null) {
			String message = result.getThrowable().getMessage();
			if(message!=null && driverEnum!=null)
				ReportLogger.logInfo(Status.FAIL, message);
		}
		
		StringWriter sw = new StringWriter();
		objThrow.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();
		//System.out.println(exceptionAsString);
		exceptionAsString = exceptionAsString.replaceAll("\\R", "<br>");
		System.out.println();
				
		if(driverEnum!=null) 
		{
			ReportLogger.logScreenShot(Status.FAIL, "Exception Happened in the Page", driverEnum);
			ReportLogger.logInfo(Status.FAIL,exceptionAsString);
		}
		else {
			if(!objThrow.toString().contains("java.lang.AssertionError: null"))
			{
				if(objThrow.getLocalizedMessage()!=null && objThrow.getLocalizedMessage().length()!=0)
				{
					ReportLogger.logInfo(Status.FAIL,"Assertion Error due to : "+objThrow.getLocalizedMessage());
				}
			}
		}
		
		ReportLogger.logInfo(Status.FAIL, "Test Case Failed");
		ReportLogger.closeReportForAtTest(result);
		
	}

	public void onTestSkipped(ITestResult result) {
		
		//BaseSuite.num_Of_FAIL_TestCases++;
		String suiteName = result.getTestContext().getSuite().getName();
		String methodName = result.getMethod().getConstructorOrMethod().getMethod().getName();
		System.out.println("On Test Skipped in the Listener for the Suite : "+suiteName+"  and for Method Name :"+methodName);
		
		String strMode = ServerLogListener.serverLogPropMap.get("ServerLoggingMode");
		if(strMode!=null&& strMode.equalsIgnoreCase("Default")){
			String testCaseID = ServerLogListener.getTestCaseIDfromITestResult(result);
			displayServerLogsInReport(testCaseID);
		}
		
		ReportLogger.logInfo(Status.INFO, "Test Case Skipped");
		ReportLogger.closeReportForAtTest(result);
		
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	public void onStart(ITestContext context) {
		// TODO Auto-generated method stub
		
	}

	public void onFinish(ITestContext context) {
		// TODO Auto-generated method stub
		
	}
	
	private void displayServerLogsInReport(String testCaseID) {
		
		Map<String,ServerLogOutputPojo> serverOutputMap = ServerLogListener.serverLogOutputMap.get(testCaseID);
		for(String logName : serverOutputMap.keySet()){
			ServerLogOutputPojo outputpojo = serverOutputMap.get(logName);
			ReportLogger.displayServerLog(testCaseID,outputpojo.getLogName(),outputpojo.getLogDsiplayName(), outputpojo.getLogOutput(), outputpojo.getOutputFileName());
		}
		// TODO Auto-generated method stub
		
	}

	

}
