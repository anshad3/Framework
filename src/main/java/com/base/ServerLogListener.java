package com.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.ISuite;
import org.testng.ITestResult;

import com.base.pojos.ServerLogInputPojo;
import com.base.pojos.ServerLogOutputPojo;
import com.base.reports.ReportLogger;
import com.ca.util.CAUtil;
import com.ca.util.LinuxServerPojo;
import com.ca.util.LinuxUntil;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ServerLogListener {
	
	public static boolean serverLogEnabled ;
	public static boolean loggerInitialised;
	public static boolean logCleanup;
	public static String ServerLoggingMode = null;
	public static String logOutputFolderName = null;
	public static String testCaseLogLocation = null;
	public static String scriptCommand = null;
	public static String serverPropFilePath = null;
	public static String caPropFilePath = null;
	public static String jsonFileName = null;
	public static Map<String,ServerLogInputPojo> serverLogInputMap = null;
	public static Map<String,Map<String,ServerLogOutputPojo>> serverLogOutputMap = null;
	public static Map<String, String> serverLogPropMap = null;
	public static Map<String, String> caPropMap = null;
	public static Map<String, List<String>> testIdLogsListMap = null;
	public static Map<String, String> testIdLogsEnabled = null;
	
	
	public static void initialiseLogger(ISuite suite){
		
		String strPropertyfileName = suite.getParameter("PropertyFileName");
		initialiseCAProperties(strPropertyfileName);
		serverPropFilePath = caPropMap.get("ServerLogPropertiesFileName");
		initialiseServerLogProperties(serverPropFilePath);
		String logKnob = serverLogPropMap.get("ServerLogging");
		if(loggerInitialised){
			return;
		}
		if(logKnob!=null&&logKnob.equalsIgnoreCase("Enable")){
			
			//serverPropFilePath = ReportLogger.caPropMap.get("ServerLogPropertiesFileName");
			if(serverPropFilePath!=null){
				serverLogEnabled= true;
				
				initialiseServerLogData();
				loggerInitialised = true;
			}
		}
		System.out.println("");
		
	}
	
	private static void initialiseServerLogData() {
		
		String folderPath = System.getProperty("user.dir");
		jsonFileName = folderPath+File.separator+"config"+File.separator+serverLogPropMap.get("JsonFileName");
		serverLogInputMap = new HashMap<String,ServerLogInputPojo>();
		serverLogOutputMap = new HashMap<String,Map<String,ServerLogOutputPojo>>();
		testIdLogsListMap = new HashMap<String, List<String>>(); 
		Random rand = new Random();
		int randInt = rand.nextInt()& Integer.MAX_VALUE;
		//String strDate = "20190729";
		DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss_SSS");
		Date date = new Date();
		String strDate = dateFormat.format(date);
		logOutputFolderName = strDate;
		
		try {
			JSONParser parser = new JSONParser();
			Reader reader = new FileReader(jsonFileName);
			
			Object jsonObj = parser.parse(reader);
			JSONArray serverLogList = (JSONArray) jsonObj;
			Iterator<JSONObject> it = serverLogList.iterator();
			while (it.hasNext()) {
				JSONObject serverLogObj = it.next();
				ServerLogInputPojo serverLogInputPojo = new ServerLogInputPojo(serverLogObj);
				String logName = (String) serverLogObj.get("name");
				serverLogInputMap.put(logName, serverLogInputPojo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		System.out.println("initialiseServerLogData method ended");
		
	}

	
	
	public synchronized static void startServerLogging(ITestResult result) {
		
		String strTestCaseId = initialiseLogsForTestCase(result);
		startServerLogs(strTestCaseId);
	}
	
	public synchronized static void stopServerLogging(ITestResult result) {
		
		String strTestCaseId = initialiseLogsForTestCase(result);
		stopServerLogs(strTestCaseId);
		
	}
	
	private synchronized static void startServerLogs(String strTestCaseId) {
		
		List<String> serverLogsList = testIdLogsListMap.get(strTestCaseId);
		Map<String,ServerLogOutputPojo> logNameServerOutputMap = new HashMap<String,ServerLogOutputPojo>();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS");
		Date date = new Date();
		String strDate = dateFormat.format(date);
		
		
		for(String LogName : serverLogsList){
			
			ServerLogInputPojo logInput = serverLogInputMap.get(LogName);
			String logPath = logInput.getPath();
			String logFileName = logInput.getLogFileName();
			String logFullPath = logPath+"/"+logFileName;
			String outputFileName = strTestCaseId+"_"+strDate+".log";
			String outputFilePath = testCaseLogLocation+logOutputFolderName;
			
			ServerLogOutputPojo logOutput = new ServerLogOutputPojo();
			logOutput.setLogName(LogName);
			logOutput.setLogDsiplayName(logInput.getLogDisplayName());
			logOutput.setOutputFileName(outputFileName);
			logOutput.setOutputFilePath(outputFilePath);
			logOutput.setTestCaseId(strTestCaseId);
			
			if(!logInput.getEnvType().equalsIgnoreCase("OpenShift")){
			String command = scriptCommand+" "+logFullPath+" "+outputFilePath+"/"+outputFileName;
			String mkdirCommand = "mkdir -p "+outputFilePath;
			LinuxServerPojo serverPojo = new LinuxServerPojo(logInput, mkdirCommand);
			LinuxUntil.executeCommand(serverPojo);
			serverPojo.setCommand(command);
			LinuxUntil.executeCommand(serverPojo);
			System.out.println("Process Id : "+serverPojo.getOutput());
			logOutput.setPid(serverPojo.getOutput());
			
			}
			logNameServerOutputMap.put(LogName, logOutput);
			
		}
		serverLogOutputMap.put(strTestCaseId, logNameServerOutputMap);
		
	}
	
	private synchronized static void stopServerLogs(String strTestCaseId) {
		
		Map<String,ServerLogOutputPojo> logNameServerOutputMap = serverLogOutputMap.get(strTestCaseId);				
		
		for(String LogName : logNameServerOutputMap.keySet()){
			
			ServerLogInputPojo logInput = serverLogInputMap.get(LogName);
			ServerLogOutputPojo outputPojo = logNameServerOutputMap.get(LogName);
			
			if(!logInput.getEnvType().equalsIgnoreCase("OpenShift")){
			String pid= outputPojo.getPid();
			pid = pid.replaceAll("[\\n\\t ]", "");
			
			String command = "kill -15 "+pid;
			LinuxServerPojo serverPojo = new LinuxServerPojo(logInput, command);
			LinuxUntil.executeCommand(serverPojo);
			
			String logOutputFullPath = outputPojo.getOutputFilePath()+"/"+outputPojo.getOutputFileName();
			command = "cat "+logOutputFullPath;
			serverPojo.setCommand(command);
			LinuxUntil.executeCommand(serverPojo);			
			//System.out.println(" Output : "+serverPojo.getOutput());
			outputPojo.setLogOutput(serverPojo.getOutput());
			
			if(logCleanup){
				command = "rm -rf "+logOutputFullPath;
				serverPojo.setCommand(command);
				LinuxUntil.executeCommand(serverPojo);
			}
			}else{
				
				if(LogName.equalsIgnoreCase("3DSAcsLog") && BaseSuite.tdsAcsTransId!=null){
					
					String pemPath = caPropMap.get("OpenShiftPemKey");
					String openShiftProjectName = caPropMap.get("OpenShiftProjectName");
					String nodeIps = caPropMap.get("OpenShiftNodeIp");
					String nodeUserName = caPropMap.get("OpenShiftNodeUserName");
					
					String allNodeIps []= nodeIps.split(";");
					
					for(String nodeIp:allNodeIps){
						
					Session session = LinuxUntil.getJsessionByPrivateKey(nodeUserName, nodeIp, 22, pemPath);
					
					String last12digitOfAcsTransId = BaseSuite.tdsAcsTransId.substring(BaseSuite.tdsAcsTransId.length()-12);
					String command = "sudo su -c 'cd /paysec/logs/tds2/"+openShiftProjectName+"; find . -name \"acs-server.log\" -type f -exec grep \""+last12digitOfAcsTransId+"\" {} \\;'";
					String logContent = null;
					try {
						//LinuxUntil.executeCommandOverChannel(session,"sudo su");
						logContent = LinuxUntil.executeCommandOverChannel(session,command);
					} catch (IOException | JSchException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(logContent!=null && !logContent.isEmpty()){
						
						outputPojo.setLogOutput(logContent);
						break;
					}
					
					session.disconnect();
					
					}
				}
			}
		}
	}
	
	private static void initialiseServerLogProperties(String strPropertyFileName ) {
		try {

			if(strPropertyFileName==null||strPropertyFileName.length()==0){
				serverPropFilePath = "config"+File.separator+"sit"+File.separator+"local"+File.separator+"ServerLogs.properties";
			}
			else{
				serverPropFilePath = "config"+File.separator+strPropertyFileName;
			}
			
			
			Properties properties = new Properties();

			FileInputStream fis = new FileInputStream(serverPropFilePath);
			properties.load(fis);
			fis.close();
			
			serverLogPropMap = new HashMap<String,String>();
			for (String name : properties.stringPropertyNames())
				serverLogPropMap.put(name, properties.getProperty(name));
			
			if(serverLogPropMap.get("ServerLogging")!=null && 
							serverLogPropMap.get("ServerLogging").equalsIgnoreCase("Enable")){
				serverLogEnabled = true;
			}
			
			if(serverLogPropMap.get("CleanupLogsAfterExecution")!=null && 
							serverLogPropMap.get("CleanupLogsAfterExecution").equalsIgnoreCase("true")){
				logCleanup = true;
			}
			
			if(serverLogPropMap.get("ServerLoggingMode")!=null){ 
				if(serverLogPropMap.get("ServerLoggingMode").equalsIgnoreCase("failure")){
					ServerLoggingMode = "failure";
				}
				else 
					ServerLoggingMode = "default";
			}
			
			testCaseLogLocation = serverLogPropMap.get("TestCaseLogLocation");
			scriptCommand = serverLogPropMap.get("scriptCommand");
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void initialiseCAProperties(String strPropertyFileName ) {
		try {

			if(strPropertyFileName==null||strPropertyFileName.length()==0){
				caPropFilePath = "config"+File.separator+"sit"+File.separator+"local"+File.separator+"ca.properties";
			}
			else{
				caPropFilePath = "config"+File.separator+strPropertyFileName;
			}
			
			Properties properties = new Properties();

			FileInputStream fis = new FileInputStream(caPropFilePath);
			properties.load(fis);
			fis.close();
			
			caPropMap = new HashMap<String,String>();
			for (String name : properties.stringPropertyNames())
				caPropMap.put(name, properties.getProperty(name));
			
			CAUtil util = new CAUtil();
			util.replaceWithSystemVariables(caPropMap);
			
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static String getTestCaseIDfromITestResult(ITestResult result){
		
		String strTestCaseId = null;
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
					break;
					
				}
			}				
		}
		
		return strTestCaseId;
		
	}
	
	public synchronized static String initialiseLogsForTestCase(ITestResult result) {
		
		String strTestCaseId = null;
		System.out.println("");
		
		String strServerLogs = result.getTestContext().getCurrentXmlTest().getParameter("ServerLogs");
		if(strServerLogs==null){
			strServerLogs = serverLogPropMap.get("LogsToBeDsiplayed");
		}
		
		ArrayList<String> logsList = new ArrayList<String>();
		
		if(strServerLogs!=null){
			String[] logs = strServerLogs.split(";");
			for(String log : logs)
				logsList.add(log);
				
		}
		strTestCaseId = getTestCaseIDfromITestResult(result);
		
		testIdLogsListMap.put(strTestCaseId, logsList);
		return strTestCaseId;
		
	}

}
