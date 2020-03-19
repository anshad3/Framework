package com.ca.base.redis;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.webbitserver.helpers.Base64;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.aventstack.extentreports.Status;
import com.ca.base.BaseSuite;
import com.ca.base.DriverFactory;
import com.ca.base.kafka.MessageContentPojo;
import com.ca.base.pojos.WebDriverEnum;
import com.ca.base.reports.ExtentReportUtil;
import com.ca.base.reports.ReportLogger;
import com.ca.util.CommonUtil;
import com.ca.util.LogMode;

public class RedisPublisher {

public static Jedis jedis;

	
	public static void inistialisePublisher() {

		 String redisHost = ReportLogger.caPropMap.get("RedisIP");
		 Integer redisPort = Integer.parseInt(ReportLogger.caPropMap.get("RedisPort"));

		 JedisPool pool = new JedisPool(redisHost, redisPort);
		 
		 jedis = pool.getResource();
	}

	public synchronized static void logInfo(Status testStatus,String stepName) {
		
		if (testStatus.compareTo(Status.WARNING) == 0)
			testStatus = Status.INFO;

		Long threadId = new Long(Thread.currentThread().getId());
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String threadIdWithIp = threadId+"_"+ip;
		
		MessageContentPojo pojo = new MessageContentPojo();
		pojo.setThreadId(threadId);
		pojo.setStatus(testStatus);
		pojo.setStepName(stepName);
		pojo.setThreadIdWithIp(threadIdWithIp);
		
		Map<String,String> htmlMap=null;
		
		if(stepName!=null && stepName.startsWith("<a")){
		
			htmlMap=checkForhtmlFileAndRead(stepName);
			
			pojo.setFilePath(htmlMap.get("htmlPath"));
			pojo.setHtmlContent(htmlMap.get("htmlFileContent"));
			
		}
		
		writePojoObjectToPublisher("logInfo",pojo);
		
		if(testStatus.equals(Status.FAIL)||testStatus.equals(Status.ERROR)){
			
			BaseSuite.threadItestResultMap.put(threadId,true);
		}
		
	}

	public synchronized static void logInfo(Status testStatus,
			String stepName, LogMode logMode) {
		try {
			if (isScreenshotRequired(logMode)) {
				logInfo(testStatus, stepName);
			}
		} catch (Exception e) {
			System.out.println("Exception occured in Info Logging: "
					+ e.toString());
		}
	}

	public synchronized static void logScreenShot(Status testStatus,
			String stepName, WebDriverEnum driverEnum, LogMode logMode) {

		try {
			if (isScreenshotRequired(logMode)) {
				logScreenShot(testStatus, stepName, driverEnum);
			}
		} catch (Exception e) {
			System.out.println("Exception occured in Taking Screen Shot: "
					+ e.toString());
		}
	}

	/**
	 * This method takes the screenshot by using given input and places in the
	 * desired location.
	 * 
	 * 
	 */
	public synchronized static void logScreenShot(Status testStatus,
			String stepName, WebDriverEnum driverEnum) {

		if (testStatus.compareTo(Status.WARNING) == 0) {
			testStatus = Status.INFO;
		}

		if (testStatus.equals(Status.FAIL)
				|| testStatus.equals(Status.ERROR)) {

			Long threadId = new Long(Thread.currentThread().getId());
			BaseSuite.threadItestResultMap.put(threadId, true);
		}

		String reporting = BaseSuite.caPropMap.get("reporting");
		long threadID = Thread.currentThread().getId();
		WebDriver driver = DriverFactory.getDriver(threadID, driverEnum);

		if (reporting.equalsIgnoreCase("OFF")) {
			logInfo(testStatus, stepName);
			return;
		}

		String imgString=null;
		try {
			imgString = getScreenshotFile(driver);
		} catch (Exception e) {
			e.printStackTrace();
			logInfo(Status.WARNING,
						"Exception while capturing image " + e.getMessage()
								+ "\nstackTrace:"
								+ e.getStackTrace().toString());
			return;
		} 
		
		//byte [] imageByteArray = convertImageToByteArray(scrFile);
		
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String threadIdWithIp = threadID+"_"+ip;
		
		MessageContentPojo pojo = new MessageContentPojo();
		pojo.setStatus(testStatus);
		pojo.setStepName(stepName);
		//pojo.setScreenShotcontent(imageByteArray);
		pojo.setImgBase64string(imgString);
		pojo.setThreadId(threadID);
		pojo.setThreadIdWithIp(threadIdWithIp);
		
		writePojoObjectToPublisher("logScreenShot",pojo);
	}


	public synchronized static void closeReport() {
		
		
		writePojoObjectToPublisher("closeReport",null);
		
		jedis.close();
		
		
	}

	/**
	 * Checks whether the screenshot should be taken or not based on the log
	 * mode FATAL => Only Fatal logs will be displayed ERROR => ERROR and FATAL
	 * logs will be displayed INFO => INFO, ERROR and FATAL logs will be
	 * displayed WARN => WARN, INFO, ERROR and FATAL logs will be displayed
	 * DEBUG => All logs ie. DEBUG, WARN, INFO, ERROR, FATAL logs will be
	 * displayed
	 *
	 * @param LogMode
	 *            log mode (FATAL/ERROR/INFO/WARN/DEBUG)
	 * @return Boolean value to tell whether the log should be displayed or not
	 * @throws Exception
	 */
	private synchronized static boolean isScreenshotRequired(LogMode logMode)
			throws Exception {
		boolean isLoggingRequired = false;
		String propertyLogMode = BaseSuite.caPropMap.get("LogMode");

		if (propertyLogMode != null
				&& !propertyLogMode.equalsIgnoreCase("none")) {
			int propertyLogModeValue = 0;
			int requestedLogModeValue = logMode.getValue();
			propertyLogModeValue = LogMode.valueOf(
					propertyLogMode.toUpperCase()).getValue();
			isLoggingRequired = ((propertyLogModeValue & requestedLogModeValue) == requestedLogModeValue);
		}

		return isLoggingRequired;
	}

	public synchronized static String getScreenshotFile(WebDriver driver) throws IOException,
			WebDriverException, NullPointerException {

		
		if (driver == null) {
			System.out
					.println("ERROR: DRIVER IS NULL PLEASE DEBUG PLEASE CHECK");
			throw new NullPointerException(
					"Driver passed to take screen shot is null");
		}

		String imgString = null;
		try {
			imgString = ((TakesScreenshot) driver)
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

		
		return imgString;
	}

	public synchronized static void initialiseReportForAtTest(ITestResult result) {

		String suiteName = result.getTestContext().getSuite().getName();
		String xmlTestName = result.getTestContext().getName();
		String methodName = result.getMethod().getConstructorOrMethod()
				.getMethod().getName();
		
		MessageContentPojo producerPojo = new MessageContentPojo();
		
		producerPojo.setSuiteName(suiteName);
		producerPojo.setXmlTestName(xmlTestName);
		producerPojo.setMethodName(methodName);
		
		Long threadID = Thread.currentThread().getId();
		
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String threadIdWithIp = threadID+"_"+ip;
		
		producerPojo.setThreadId(threadID);
		producerPojo.setThreadIdWithIp(threadIdWithIp);
		
		writePojoObjectToPublisher("initialiseReportForAtTest",producerPojo);
		
		Long threadId = new Long(Thread.currentThread().getId());
		BaseSuite.threadItestResultMap.put(threadId, false);
		
	}

	public synchronized static void closeReportForAtTest(ITestResult testResult,String testCaseNameForKafka) {

		String methodName = testResult.getMethod().getConstructorOrMethod()
				.getMethod().getName();
		MessageContentPojo pojo = new MessageContentPojo();
		Long threadId = new Long(Thread.currentThread().getId());
		System.out.println("Thread ID = " + threadId);
		String message = "Test Passed";
		Status status = Status.PASS;
		try {
			if (testResult.getStatus() == ITestResult.FAILURE) {
				/*
				 * System.out .println(
				 * "calling take screen shot in CaptureScreen method in failure case"
				 * );
				 */

				status = Status.FAIL;
				if (!BaseSuite.threadItestResultMap.get(threadId)) {
					/*
					 * BaseSuiteDES .takeScreenShot(Status.FAIL,
					 * "Error Captured");
					 */
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

			pojo.setMethodName(methodName);
			pojo.setThreadId(threadId);
			
			String ip = null;
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String threadIdWithIp = threadId+"_"+ip;
			
			pojo.setThreadIdWithIp(threadIdWithIp);

			writePojoObjectToPublisher("closeReportForAtTest",pojo);
		}

	}

	public synchronized static String logTestCaseIDandDesc(ITestResult result) {

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
			System.out.println("No of parameter values are:"
					+ parameterValues.length);

		for (int i = 0; i < parameterValues.length; i++) {
			System.out.println("Parameter " + (i + 1) + "="
					+ parameterValues[i].toString());
			if (parameterValues[i] instanceof Map) {
				Map<String, Object> map = (Map<String, Object>) parameterValues[i];
				if (map.containsKey("TestCaseID")) {
					strTestCaseId = (String) map.get("TestCaseID");
					strTestCaseTitle = (String) map.get("TestCaseTitle");
					strTestCaseDesc = (String) map.get("TestCaseDescription");
					testCaseData = (Map<String, String>) parameterValues[i];
				}

			}
		}

		String testDesc = "";
		if (strTestCaseDesc != null) {
			String[] lstTestDesc = strTestCaseDesc.split("\n");
			for (String desc : lstTestDesc) {
				testDesc += desc + "<br>";
			}
		}

		if (strTestCaseTitle != null) {
			final StringBuilder ret = new StringBuilder(
					strTestCaseTitle.length());

			// Code to convert Test Case Title to Camel Case
			for (final String word : strTestCaseTitle.split(" ")) {
				if (!word.isEmpty()) {
					ret.append(word.substring(0, 1).toUpperCase());
					ret.append(word.substring(1));
				}
				if (!(ret.length() == strTestCaseTitle.length()))
					ret.append(" ");
			}

			strTestCaseTitle = ret.toString();
		}
		strTestName = strTestCaseId + "_" + strTestName + "_"
				+ strTestCaseTitle;
		
		MessageContentPojo pojo = new MessageContentPojo();
		pojo.setTestCaseName(strTestName);
		
		Long threadID = Thread.currentThread().getId();
		pojo.setThreadId(threadID);
		
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String threadIdWithIp = threadID+"_"+ip;
		
		pojo.setThreadIdWithIp(threadIdWithIp);
		
		writePojoObjectToPublisher("logTestCaseName",pojo);

		if (strTestCaseTitle != null && strTestCaseTitle.length() > 0)
			logInfo(Status.INFO, "<b>Test Case Title: " + strTestCaseTitle
					+ "</b>");

		if (testDesc != null && testDesc.length() > 0)
			logInfo(Status.INFO, "<b>Test Case Description: " + "<br>"
					+ "</b>" + testDesc);

		CommonUtil cu = new CommonUtil();
		// Test Data from Excel.
		String htmlfile = cu.generateHTMLReportForInputTestData(testCaseData,
				strTestName, "Input XL Sheet Data for " + strTestName,
				"Input XL Sheet Data");
		logInfo(Status.INFO, htmlfile);

		return strTestName;
	}

	public synchronized static void displayServerLog(String testCaseID,
			String logName, String logDisplayName, String logData,
			String outputFileName) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
		Date date = new Date();
		String strDate = dateFormat.format(date);
		String htmlFileName = testCaseID + "_" + logName + "_" + strDate
				+ ".log";
		String htmlData = ExtentReportUtil.createHTMLFileDataForLogs(
				testCaseID, logData, logDisplayName);
		
		String relativedir = ".." + File.separator + "HTML" + File.separator;
		String reldirFile = relativedir + htmlFileName;

		String outputFile = null;
		outputFile = "<a href=" + reldirFile + " target=\"_blank\" >" + logDisplayName + "</a>";
				
		 String relativePathToRead=File.separator + "Extent" + File.separator
			+ "HTML" + File.separator + htmlFileName;
		
		 MessageContentPojo pojo = new MessageContentPojo();
		 pojo.setFilePath(relativePathToRead);
		 pojo.setHtmlContent(htmlData);
		 pojo.setHtmlLink(outputFile);
		 
		 Long threadId = new Long(Thread.currentThread().getId());
		 
		 pojo.setThreadId(threadId);
		 
		 String ip = null;
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String threadIdWithIp = threadId+"_"+ip;
			
			pojo.setThreadIdWithIp(threadIdWithIp);
		 
			writePojoObjectToPublisher("displayServerLog",pojo);
		 
	}
	
	
	private static Map<String, String> checkForhtmlFileAndRead(String strContent){
		
		String content = "";
			String regExp= Pattern.quote("=..") + "(.*?)" + Pattern.quote(".html");
			
			Pattern pattern = Pattern.compile(regExp);
			
			Matcher matcher = pattern.matcher(strContent);
			String textInBetween = null;
			
			while (matcher.find()) {
				  textInBetween = matcher.group(1); 
				}
			
			String htmlFilePath = "/Extent"+textInBetween+".html";
		    try {
		        BufferedReader in = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/TestResult"+htmlFilePath));
		        String str;
		        while ((str = in.readLine()) != null) {
		            content +=str;
		        }
		        in.close();
		    } catch (IOException e) {
		    }
		    
		    Map<String,String> htmlMap = new HashMap<String, String>();
		    htmlMap.put("htmlPath", htmlFilePath);
		    htmlMap.put("htmlFileContent", content);
		    
		    return htmlMap;
	}
		
	
	
	private static byte[] convertImageToByteArray(File file){
		
		ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
		BufferedImage img;
		
		byte[] bytearray = null;
				
		try {
			img = ImageIO.read(file);
		
		ImageIO.write(img, "jpg", baos);
		baos.flush();
 
		String base64String=Base64.encode(baos.toByteArray());
		baos.close();
 
		bytearray = Base64.decode(base64String);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return bytearray;
	}

	private static void writePojoObjectToPublisher(String operation,MessageContentPojo producerPojo) {
		
		String channel = BaseSuite.caPropMap.get("RedisPubSubChannel");
		
		Map<String,MessageContentPojo> inputMap = new HashMap<String, MessageContentPojo>();
		inputMap.put(operation, producerPojo);
		
		 byte [] data = null;
    	 ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(inputMap);
             oos.flush();
             data = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String encodedString = java.util.Base64.getEncoder().encodeToString(data);
		
		jedis.publish(channel,encodedString);
	}
	
	
}
