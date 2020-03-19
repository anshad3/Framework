package com.ca.base.reports;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.ITestResult;

import com.aventstack.extentreports.Status;
import com.ca.util.LogMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;

public class Log4jLogger {
	
	private static Logger lof4jLog = Logger.getLogger(Log4jLogger.class);
	public static String log4jFileName = null;
	public static String log4jLocation = null;

	public static void initialiseLog4jReport() {
		
		String logFullPath = System.getProperty("user.dir")+log4jFileName;
		String logLocationPath = System.getProperty("user.dir")+log4jLocation;
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(logFullPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		props.setProperty("log4j.appender.file.File", logLocationPath);
		PropertyConfigurator.configure(props);;
		
		BasicConfigurator.configure();
		
		
	}

	public static void initialiseReportForAtTest(ITestResult result) {
		String methodName = result.getMethod().getConstructorOrMethod().getMethod().getName();
		lof4jLog.info("*************************************************************************************************************");
		lof4jLog.info("*************************************************************************************************************");
		lof4jLog.info("************      Starting Method : "+methodName+"                           ***********");
		
	}

	public static void closeReportForAtTest(ITestResult result) {

		String methodName = result.getMethod().getConstructorOrMethod().getMethod().getName();
		lof4jLog.info("************      Ending Method : "+methodName+"                           ***********");
		lof4jLog.info("*************************************************************************************************************");
		lof4jLog.info("");
		lof4jLog.info("");
		
		
	}

	public static void logTestCaseIDandDesc(ITestResult result) {
		
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
				testDesc+=desc+"\n";
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
	    strTestName = strTestCaseId+"_"+strTestName+"_"+strTestCaseTitle;
	    
		
		if(strTestCaseTitle!=null&&strTestCaseTitle.length()>0)
			lof4jLog.info("Test Case Title: "+ strTestCaseTitle);
			
		
		if(testDesc!=null&&testDesc.length()>0)
			lof4jLog.info("Test Case Description: "	+testDesc );
			
		
		lof4jLog.info("************Test Case Data is : ");
		if(testCaseData!=null){
			for (Map.Entry<String,String> entry : testCaseData.entrySet()) {
				if(!entry.getKey().equals("TestCaseDescription"))
					lof4jLog.info(entry.getKey() + " : "+ entry.getValue()); 
			}
			lof4jLog.info("************Test Case Data Details Over");
		}
     
		
	}

	public static void logScreenShot(Status testStatus, String stepName, LogMode logMode) {
		doLoggin(testStatus, stepName);
		
	}

	public static void logScreenShot(Status testStatus, String stepName) {
		doLoggin(testStatus, stepName);
		
	}

	public static void logInfo(Status testStatus, String stepName, LogMode logMode) {
		doLoggin(testStatus, stepName);
		
	}

	public static void logInfo(Status testStatus, String stepName) {
		doLoggin(testStatus, stepName);
		
	}
	
	public static void doLoggin(Status testStatus,String stepName){
		
		if(testStatus.equals(Status.ERROR))
			lof4jLog.error(stepName);
		else if(testStatus.equals(Status.FAIL))
			lof4jLog.fatal(stepName);
		else if(testStatus.equals(Status.FATAL))
			lof4jLog.fatal(stepName);
		else if(testStatus.equals(Status.INFO))
			lof4jLog.info(stepName);
		else if(testStatus.equals(Status.PASS))
			lof4jLog.info(stepName);
		else 
			lof4jLog.info(stepName);
	}
	

}
