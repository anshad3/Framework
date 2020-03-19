package com.ca.util;

import java.io.IOException;
import java.io.InputStream;

import com.aventstack.extentreports.Status;
import com.ca.base.BaseSuite;
import com.ca.base.reports.ReportLogger;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class RestartServers {
	
	
	public synchronized static void restartDSserver(){
		
		stopDSserver();
		
		startDSserver();
	}

	
	public synchronized static void stopDSserver(){
		

		String dsHostName = BaseSuite.caPropMap.get("DS_IP");
		String dsUserName = BaseSuite.caPropMap.get("DS_UserName");
		String dsPassword = BaseSuite.caPropMap.get("DS_Password");
		String dsServiceName = BaseSuite.caPropMap.get("DS_ServiceName");
		
		String stopDSserverCommand = "net stop \""+dsServiceName+"\"";
		//String stopTomcatCommand = "start /D \"C:\\Program Files\\apache-tomcat-9.0.13\\bin\" /W shutdown.bat";
		
		String stopTomcatCommand = "net stop \"Tomcat\"";
		
		try {
			executeCommandOverChannel(dsHostName, dsUserName, dsPassword, 22, stopDSserverCommand);
			executeCommandOverChannel(dsHostName, dsUserName, dsPassword, 22, stopTomcatCommand);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized static void startDSserver(){
		
		String dsHostName = BaseSuite.caPropMap.get("DS_IP");
		String dsUserName = BaseSuite.caPropMap.get("DS_UserName");
		String dsPassword = BaseSuite.caPropMap.get("DS_Password");
		String dsServiceName = BaseSuite.caPropMap.get("DS_ServiceName");
		
		String startDSserverCommand = "net start \""+dsServiceName+"\"";
		//String startTomcatCommand = "start /D \"C:\\Program Files\\apache-tomcat-9.0.13\\bin\" /W startup.bat";
		
		//String startTomcatCommand = "cd \"C:\\Program Files\\apache-tomcat-9.0.13\\bin\" && startup";
		
		//String startTomcatCommand = "start /D \"C:\\Program Files\\apache-tomcat-9.0.13\\bin\" /W startup.bat";;
		
		String startTomcatCommand = "net start \"Tomcat\"";
		String result = null;
		
		try {
			result = executeCommandOverChannel(dsHostName, dsUserName, dsPassword, 22, startDSserverCommand);
			System.out.println(result);
			result = executeCommandOverChannel(dsHostName, dsUserName, dsPassword, 22, startTomcatCommand);
			System.out.println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public synchronized static void restart3DS2(){
	
		String tdsHostName = BaseSuite.caPropMap.get("env_3DS_Host");
		String tdsUserName = BaseSuite.caPropMap.get("env_3DS_UserName");
		String tdsPassword = BaseSuite.caPropMap.get("env_3DS_Password");
		String tdsCurrentVersion = BaseSuite.caPropMap.get("env_3DS_CurrentVersion");
		String restart3dsCommand = "/home/3DSserviceScripts/restartAll3DSServices.sh";
		String deleteEntryInLog = "sed -i \"/\\b\\CA Transaction Manager ACS 2.0 - ACS  "+tdsCurrentVersion+" is started\\b/d\" /opt/tds2-acs/tds2-home/logs/acs-server.log";
		String logVerifyCommand = "grep -i \"CA Transaction Manager ACS 2.0 - ACS  "+tdsCurrentVersion+" is started\" /opt/tds2-acs/tds2-home/logs/acs-server.log";
		
		String result = null;
		try {
			
			executeCommandOverChannel(tdsHostName, tdsUserName, tdsPassword, 22, deleteEntryInLog);
			executeCommandOverChannel(tdsHostName, tdsUserName, tdsPassword, 22, restart3dsCommand);
			
			Thread.sleep(240000);
			
			result = executeCommandOverChannel(tdsHostName, tdsUserName, tdsPassword, 22, logVerifyCommand);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(result!=null){
			
			ReportLogger.logInfo(Status.PASS, "3DS server sucessfully restarted");
		}else{
			ReportLogger.logInfo(Status.FAIL, "3DS server is not started");
		}
		
	}
	
	public synchronized static void restartRA(){
		
		stopRATxnServer();
		startRATxnServer();
	
	}
	
	
	public synchronized static void startRATxnServer(){
		
		String raHostName = BaseSuite.caPropMap.get("env_RA_Trans_Host");
		String raUserName = BaseSuite.caPropMap.get("env_RA_Trans_UserName");
		String raPassword = BaseSuite.caPropMap.get("env_RA_Trans_Password");
		String raArcotHome = BaseSuite.caPropMap.get("env_RA_Trans_ArcotHome");
		
		String logClearCommand = "sed -i \"/\\b\\Arcot Risk Analytics Service READY\\b/d\" "+raArcotHome+"/logs/arcotriskfortstartup.log";
		String logFetchCommand = "grep -i \"Arcot Risk Analytics Service READY\" "+raArcotHome+"/logs/arcotriskfortstartup.log";
		String raStartCommand = "/home/servicesScripts/startRATxnServer.sh";
		
		String logReult=null;
		try {
			executeCommandOverChannel(raHostName, raUserName, raPassword, 22, logClearCommand);
			executeCommandOverChannel(raHostName, raUserName, raPassword, 22, raStartCommand);
	
				Thread.sleep(4000);
			logReult = executeCommandOverChannel(raHostName, raUserName, raPassword, 22, logFetchCommand);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(logReult);
		if(logReult!=null)
			ReportLogger.logInfo(Status.PASS, "Riskfort server started successfully");
		else
			ReportLogger.logInfo(Status.FAIL, "Riskfort server is not started");
	}
	
	public synchronized static void stopRATxnServer(){
		
		String raHostName = BaseSuite.caPropMap.get("env_RA_Trans_Host");
		String raUserName = BaseSuite.caPropMap.get("env_RA_Trans_UserName");
		String raPassword = BaseSuite.caPropMap.get("env_RA_Trans_Password");
		
		String raStopCommand = "/home/servicesScripts/stopRATxnServer.sh";
		
		try {
			executeCommandOverChannel(raHostName, raUserName, raPassword, 22, raStopCommand);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public synchronized static void stopIdsServer(){
		
		String idsHostName = BaseSuite.caPropMap.get("env_IDS_Host");
		String idsUserName = BaseSuite.caPropMap.get("env_IDS_UserName");
		String idsPassword = BaseSuite.caPropMap.get("env_IDS_Password");
		
		String idsLogEntryClear = "sed -i \"/\\b\\CA Payment Security - Identity Service stopped\\b/d\" /opt/ids/ids-logs/identity-service.log";
		String idsStopCommand = "/opt/ids/setIDSenv.sh;/opt/ids/stopServices.sh";
		
		String verifyStopCommand = "grep -i \"CA Payment Security - Identity Service stopped\" /opt/ids/ids-logs/identity-service.log";
		
		String logReult = null;
		try {
			LinuxUntil.executeCommandOverChannel(idsHostName, idsUserName, idsPassword, 22, idsLogEntryClear);
			LinuxUntil.executeCommandOverChannel(idsHostName, idsUserName, idsPassword, 22, idsStopCommand);
			
			Thread.sleep(5000);
			
			logReult = executeCommandOverChannel(idsHostName, idsUserName, idsPassword, 22, verifyStopCommand);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(logReult);
		if(logReult!=null)
			ReportLogger.logInfo(Status.PASS, "IDS server stoped successfully");
		else
			ReportLogger.logInfo(Status.FAIL, "IDS server is not stoped");
		
	}
	
	public synchronized static void startIdsServer(){
		
		String idsHostName = BaseSuite.caPropMap.get("env_IDS_Host");
		String idsUserName = BaseSuite.caPropMap.get("env_IDS_UserName");
		String idsPassword = BaseSuite.caPropMap.get("env_IDS_Password");
		
		String idsLogEntryClear = "sed -i \"/\\b\\CA Payment Security - Identity Service.*is started\\b/d\" /opt/ids/ids-logs/identity-service.log";
		
		String idsStartCommand = "/opt/ids/setIDSenv.sh;/opt/ids/startServices.sh";
		
		String verifyStartCommand = "grep -i \"CA Payment Security - Identity Service.*is started\" /opt/ids/ids-logs/identity-service.log";
		String logReult = null;
		try {
			executeCommandOverChannel(idsHostName, idsUserName, idsPassword, 22, idsLogEntryClear);
			executeCommandOverChannel(idsHostName, idsUserName, idsPassword, 22, idsStartCommand);
			
			Thread.sleep(10000);
			
			logReult = executeCommandOverChannel(idsHostName, idsUserName, idsPassword, 22, verifyStartCommand);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(logReult);
		if(logReult!=null)
			ReportLogger.logInfo(Status.PASS, "IDS server started successfully");
		else
			ReportLogger.logInfo(Status.FAIL, "IDS server is not started");
	}
	
	
	public synchronized static String executeCommandOverChannel(String hostname, String userName, String password, int port, String command)
			throws IOException, JSchException {
		
		
		Session session = LinuxUntil.getJsession(userName, hostname, port,
				password);
		System.out.println("comand is ---> " + command);
		Channel channel = LinuxUntil.getChannel(session, "exec");
		StringBuilder output = new StringBuilder();
		if(channel != null) {
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			((ChannelExec) channel).setPty(false);
			

			InputStream in = channel.getInputStream();
			System.out.println("In for first command : " + in);
			System.out.println("input available is ---> " + in.available());
			byte[] tmp = new byte[1024];
			long startTime = 0, endTime = 0;

			channel.connect();
			startTime = System.currentTimeMillis();
			int count = 0;
			while (true) {
				count++;
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					output.append(new String(tmp, 0, i));
					//System.out.print("Input Stream for command:" + command + " is: " + output);
				}
				//System.out.println("Nothing to read as of now:");
				if (channel.isClosed()) {
					if (channel.getExitStatus() == 0) {
						System.out.println("exit-status: "+ channel.getExitStatus());
						System.out.println("exit- status:success");
					} else {
						System.out.println("exit- status:fail");
					}
					break;
				}
				try {
					Thread.sleep(300);
				} catch (Exception ee) {
				}
				if(count>6)
					break;
			}

			endTime = System.currentTimeMillis();
			//System.out.println("output from the command :" + command + " is:"+ output);
			System.out.println("command: " + command + " took "+ (endTime - startTime) + " milli seconds");
		} else {
			System.out.println("Channel is null");
		}
		
		return output.toString();

	}
	
public static void restartAdminVpas(){
		
		
		String tdsHostName = BaseSuite.caPropMap.get("env_3DS_Host");
		String tdsUserName = BaseSuite.caPropMap.get("env_3DS_UserName");
		String tdsPassword = BaseSuite.caPropMap.get("env_3DS_Password");
		String tomcatHome = BaseSuite.caPropMap.get("env_3DS_TomcatHome");
		
		String tomcatLogEntryClear = "sed -i \"/\\b\\Catalina.start Server startup\\b/d\""+tomcatHome+"/logs/catalina.out";
		
		String tomcatStopCommand = "source /opt/arcot/scripts/arctenv;"+tomcatHome+"/bin/shutdown.sh";
		
		String tomcatStartCommand = "source /opt/arcot/scripts/arctenv;"+tomcatHome+"/bin/startup.sh";
		
		String verifyStartCommand = "grep -i \"Catalina.start Server startup\" "+tomcatHome+"/logs/catalina.out";
		String logReult = null;
		try {
			executeCommandOverChannel(tdsHostName, tdsUserName, tdsPassword, 22, tomcatLogEntryClear);
			executeCommandOverChannel(tdsHostName, tdsUserName, tdsPassword, 22, tomcatStopCommand);
			Thread.sleep(10000);
			
			executeCommandOverChannel(tdsHostName, tdsUserName, tdsPassword, 22, tomcatStartCommand);
			Thread.sleep(30000);
			
			logReult = executeCommandOverChannel(tdsHostName, tdsUserName, tdsPassword, 22, verifyStartCommand);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(logReult);
		if(logReult!=null)
			ReportLogger.logInfo(Status.PASS, "VPAS started successfully");
		else
			ReportLogger.logInfo(Status.FAIL, "VPAS is not started");
		
		
	}
	
}
