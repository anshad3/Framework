package com.base.reports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.testng.ISuite;
import org.testng.ITestResult;

import redis.clients.jedis.Jedis;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.KlovReporter;
import com.base.kafka.KafkaProducerManager;
import com.base.kafka.MessageContentPojo;
import com.base.pojos.WebDriverEnum;
import com.base.redis.RedisPublisher;
import com.ca.util.CAUtil;
import com.ca.util.LogMode;

public class ReportLogger {
	
	public static boolean loggerInitialised;
	public static boolean extentReportEnabled = false;
	public static boolean log4jEnabled = false;
	public static boolean allureReportEnabled = false;
	public static boolean kafkaReportEnabled = false;
	public static boolean klovReportEnabled = false;
	public static boolean redisReportEnabled = false;
	
	public static ExtentReports extentreporter = null;
	public static String strExtenReportFileName = null;
	public static String strExtenReportTitle = null;
	public static KlovReporter klovReport=null;
	
	static KafkaProducer<String, MessageContentPojo> kafkaProducer;
	static Jedis jedis;
	
	public static Map<String, String> caPropMap = null;
	private static String propFilePath = null;
	
	private static String testCaseNameForKafka;
	private static String testCaseNameForRedis;
	
	public static void initialiseLogger(ISuite suite){
		
		if(loggerInitialised){
			return;
		}
		
		String strPropertyfileName = suite.getParameter("PropertyFileName");
				
		initialiseProperties(strPropertyfileName);
		
		String dockerEnabled = caPropMap.get("DockerEnabled");
		String dockerMode = caPropMap.get("DockerMode");
		
		if(dockerEnabled.equalsIgnoreCase("true") && dockerMode.equalsIgnoreCase("StandAloneWithoutReports"))
		{
			
			if(caPropMap.get("KafkaReportEnabled").equalsIgnoreCase("Yes")){
				
				initialiseReportForKafkaProducer();
				kafkaReportEnabled = true;
			}
			if(caPropMap.get("RedisReportEnabled").equalsIgnoreCase("Yes")){
				
				initialiseReportForJedisPublisher();
				redisReportEnabled = true;
				
			}
		}
		else{	
		
			String strReportsToBeGenerated = caPropMap.get("ReportsToBeGenerated");
			
			if(strReportsToBeGenerated!=null){
			String[] reportsEnabed = strReportsToBeGenerated.split(";");
			for(String report : reportsEnabed){
				if(report.equals("Extent")){
					extentReportEnabled = true;
					
					for(String SearchForKlov:reportsEnabed){
						if(SearchForKlov.equals("Klov")){
							klovReportEnabled=true;
						}
					}
				}else if(report.equals("Allure"))
					allureReportEnabled = true;
				 else if(report.equals("Log4j"))
					log4jEnabled = true;
			}
		}
		else{
			System.out.println("********************NO reports Selected for generation************");
			System.exit(0);
			}
		}
			
		if(extentReportEnabled)
			initialiseExtentReport();
		if(log4jEnabled)
			initialiseLog4j();
		if(allureReportEnabled)
			initialiseAllureReport();
		if(klovReportEnabled)
			initialiseKloveReport(suite);
		
		loggerInitialised = true;
		
	}
	
	

	private synchronized static void initialiseExtentReport() {
		
		if(extentreporter==null){
			strExtenReportFileName = caPropMap.get("ExtentReportFile");
			strExtenReportTitle = caPropMap.get("ExtentReportTitle");
			ExtentReportManager.initialiseReport(strExtenReportFileName, strExtenReportTitle);
			extentreporter=ExtentReportManager.extentreporter;
		}
		
	}

	private synchronized static void initialiseAllureReport() {
		// TODO Auto-generated method stub
		
	}

	private synchronized static void initialiseLog4j() {
		Log4jLogger.log4jFileName = caPropMap.get("Log4jPropFileName");
		Log4jLogger.log4jLocation = caPropMap.get("LogFileLocation");
		Log4jLogger.initialiseLog4jReport();
		
	}
	
	private synchronized static void initialiseReportForKafkaProducer(){
		
		if(kafkaProducer==null){
			KafkaProducerManager.inistialiseProducer();
		
			kafkaProducer = KafkaProducerManager.kafkaProducer;
		}
		
	}
	
	private synchronized static void initialiseReportForJedisPublisher(){
		
		if(jedis==null){
			RedisPublisher.inistialisePublisher();
		
			jedis = RedisPublisher.jedis;
		}
		
	}
	
	
	private static void initialiseKloveReport(ISuite suite) {
		
		if(klovReport==null){
		String mongoDbIp = caPropMap.get("MongoDbIp");
		int mongoDbPort = Integer.parseInt(caPropMap.get("MongoDbPort"));
		String klovUrl = caPropMap.get("KlovUrl");
		String projectName = caPropMap.get("KlovProjectName");
		
		KlovReportManager.initialiseReport(mongoDbIp, mongoDbPort, klovUrl, suite.getName(),projectName);
		
		klovReport = KlovReportManager.klovReporter;
		}
	}
	
	public static void logInfo(Status testStatus, String stepName){
		
		if(extentReportEnabled){
			ExtentReportManager.logInfo(testStatus, stepName);
		}
		if(log4jEnabled){
			Log4jLogger.logInfo(testStatus, stepName);
		}
		if(allureReportEnabled){
			
		}
		if(kafkaReportEnabled){
			KafkaProducerManager.logInfo(testStatus, stepName);
		}
		if(klovReportEnabled){
			
			KlovReportManager.logInfo(testStatus, stepName);
		}
		if(redisReportEnabled){
			
			RedisPublisher.logInfo(testStatus, stepName);
		}
		
	}
	
	public static void logInfo(Status testStatus, String stepName, LogMode logMode){
		
		if(extentReportEnabled){
			ExtentReportManager.logInfo(testStatus, stepName,logMode);
		}
		if(log4jEnabled){
			Log4jLogger.logInfo(testStatus, stepName,logMode);
		}
		if(allureReportEnabled){
			
		}
		if(kafkaReportEnabled){
			KafkaProducerManager.logInfo(testStatus, stepName,logMode);
		}
		if(klovReportEnabled){
			KlovReportManager.logInfo(testStatus, stepName, logMode);
		}
		if(redisReportEnabled){
			RedisPublisher.logInfo(testStatus, stepName,logMode);
		}
		
	}
	
	public static void logScreenShot(Status testStatus, String stepName, WebDriverEnum driverEnum) {
		
		if(extentReportEnabled){
			ExtentReportManager.logScreenShot(testStatus, stepName, driverEnum);
		}
		if(log4jEnabled){
			Log4jLogger.logScreenShot(testStatus, stepName);
		}
		if(allureReportEnabled){
			
		}
		if(kafkaReportEnabled){
			KafkaProducerManager.logScreenShot(testStatus, stepName, driverEnum);
		}
		if(klovReportEnabled){
			KlovReportManager.logScreenShot(testStatus, stepName, driverEnum);
		}
		if(redisReportEnabled){
			RedisPublisher.logScreenShot(testStatus, stepName, driverEnum);
			
		}
		
	}
	
	public static void logScreenShot(Status testStatus, String stepName, WebDriverEnum driverEnum, LogMode logMode) {
		
		if(extentReportEnabled){
			ExtentReportManager.logScreenShot(testStatus, stepName, driverEnum, logMode);
		}
		if(log4jEnabled){
			Log4jLogger.logScreenShot(testStatus, stepName, logMode);
			
		}
		if(allureReportEnabled){
			
		}
		if(kafkaReportEnabled){
			KafkaProducerManager.logScreenShot(testStatus, stepName, driverEnum, logMode);
		}
		if(klovReportEnabled){
			KlovReportManager.logScreenShot(testStatus, stepName, driverEnum, logMode);
		}
		if(redisReportEnabled){
			
			RedisPublisher.logScreenShot(testStatus, stepName, driverEnum, logMode);
			
		}
		
	}
		

	

	public synchronized static void initialiseReportForAtTest(ITestResult result) {
		
		if(extentReportEnabled){
			ExtentReportManager.initialiseReportForAtTest(result);
		}
		if(log4jEnabled){
			Log4jLogger.initialiseReportForAtTest(result);
			
		}
		if(allureReportEnabled){
			
		}
		if(kafkaReportEnabled){
			KafkaProducerManager.initialiseReportForAtTest(result);
		}
		if(klovReportEnabled){
			KlovReportManager.initialiseReportForAtTest(result);
		}
		if(redisReportEnabled){
			RedisPublisher.initialiseReportForAtTest(result);
		}
		
	}
	
	public synchronized static void closeReportForAtTest(ITestResult result) {
		
		if(extentReportEnabled){
			ExtentReportManager.closeReportForAtTest(result);
		}
		if(log4jEnabled){
			Log4jLogger.closeReportForAtTest(result);
		}
		if(allureReportEnabled){
			
		}
		if(kafkaReportEnabled){
			KafkaProducerManager.closeReportForAtTest(result,testCaseNameForKafka);
		}
		if(klovReportEnabled){
			KlovReportManager.closeReportForAtTest(result);
			
		}if(redisReportEnabled){
			RedisPublisher.closeReportForAtTest(result, testCaseNameForRedis);
		}
		
	}
	
	private static void initialiseProperties(String strPropertyFileName) {
		System.out.println("insideReportLogger/com.ca.reports");
		try {

			if(strPropertyFileName==null||strPropertyFileName.length()==0){
				System.out.println("inside if");
				propFilePath = "config"+File.separator+"config.properties";
				System.out.println("After path"+   propFilePath);
				
			}
			else{
				System.out.println("inside else");
				propFilePath = "config"+File.separator+strPropertyFileName;
			}
			
			
			Properties properties = new Properties();

			FileInputStream fis = new FileInputStream(propFilePath);
			System.out.println("before load method");
			properties.load(fis);
			fis.close();
			System.out.println("caPropMap");
			caPropMap = new HashMap<String,String>();
			for (String name : properties.stringPropertyNames())
				caPropMap.put(name, properties.getProperty(name));
			
			CAUtil util = new CAUtil();
			util.replaceWithSystemVariables(caPropMap);
			System.out.println("After for loop");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void logTestCaseIDandDesc(ITestResult result){
		
		if(extentReportEnabled){
			ExtentReportManager.logTestCaseIDandDesc(result);
		}
		if(log4jEnabled){
			Log4jLogger.logTestCaseIDandDesc(result);
		}
		if(allureReportEnabled){
			
		}
		
		if(kafkaReportEnabled){
			testCaseNameForKafka = KafkaProducerManager.logTestCaseIDandDesc(result);
		}
		if(klovReportEnabled){
			KlovReportManager.logTestCaseIDandDesc(result);
		}
		if(redisReportEnabled){
			testCaseNameForRedis = RedisPublisher.logTestCaseIDandDesc(result);
		}
		
	}
	
	public static void displayServerLog(String testCaseID,String logName,String logDisplayName, String logData,String  outputFileName){
		
		if(extentReportEnabled){
			ExtentReportManager.displayServerLog(testCaseID,logName,logDisplayName,logData,outputFileName);
		}
		if(log4jEnabled){
		}
		if(allureReportEnabled){
			
		}
		if(kafkaReportEnabled){
			KafkaProducerManager.displayServerLog(testCaseID,logName,logDisplayName,logData,outputFileName);
		}
		if(klovReportEnabled){
			KlovReportManager.displayServerLog(testCaseID, logName, logDisplayName, logData, outputFileName);
		}
		if(redisReportEnabled){
			RedisPublisher.displayServerLog(testCaseID, logName, logDisplayName, logData, outputFileName);
			
		}
		
	}
	
	public synchronized static void closeReport(){
		
		
		if(extentReportEnabled){
			
		}
		if(log4jEnabled){
			
		}
		if(allureReportEnabled){
			
		}
		if(kafkaReportEnabled){
			KafkaProducerManager.closeReport();
		}
		if(redisReportEnabled){
			RedisPublisher.closeReport();
		}
		
	}

	
}
