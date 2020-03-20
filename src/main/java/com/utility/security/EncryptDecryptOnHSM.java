package com.utility.security;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.base.BaseSuite;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelExec;

public class EncryptDecryptOnHSM {

	
	public static Logger logger = Logger.getLogger(EncryptDecryptOnHSM.class);
	private String strEnv_ACS = BaseSuite.caPropMap.get("env_ACS");
	public synchronized String encryptStringOnHSM(String cmdString) {
		System.out.println("Inside encrypting the string on HSM acs server method");
		System.out.println("Environment is :" + strEnv_ACS);
		String encryptedString = null;
		if (strEnv_ACS.equalsIgnoreCase("Linux")) {
			String host = BaseSuite.caPropMap.get("env_ACS_Host");
			String user = BaseSuite.caPropMap.get("env_ACS_UserName");
			String password = BaseSuite.caPropMap.get("env_ACS_Password");
			String strPort =BaseSuite.caPropMap.get("env_ACS_Port");
			int port = Integer.parseInt(strPort);
			System.out.println("host=" + host + "\nport=" + port + "\n user="+ user);
			try {
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				Session session = getJsession(user, host, port, password, config);
				if (session != null) {
					System.out.println("Connected to the ACS HSM");
					encryptedString = executeCommandOverChannel(session, cmdString);
				} else {
					System.out.println("Session is for host=" + host + "\nport=" + port + "\n user="+ user);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSchException e) {
				e.printStackTrace();
			}
		}
		return encryptedString;
	}
	
	private synchronized String executeCommandOverChannel(Session session, String command)
			throws IOException, JSchException {
		System.out.println("comand is ---> " + command);
		Channel channel = getChannel(session, "exec");
		StringBuilder output = new StringBuilder();
		if(channel != null) {
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();
			System.out.println("In for first command : " + in);
			System.out.println("input available is ---> " + in.available());
			byte[] tmp = new byte[1024];
			long startTime = 0, endTime = 0;

			channel.connect();
			startTime = System.currentTimeMillis();
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					output.append(new String(tmp, 0, i));
					System.out.print("Input Stream for command:" + command + " is: " + output);
				}
				System.out.println("Nothing to read as of now:");
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
			}

			endTime = System.currentTimeMillis();
			System.out.println("output from the command :" + command + " is:"+ output);
			System.out.println("command: " + command + " took "+ (endTime - startTime) + " milli seconds");
		} else {
			System.out.println("Channel is null");
		}
		
		return output.toString();

	}
	
	private synchronized Channel getChannel(Session session, String command) {
		Channel channel = null;
		try {
			channel = session.openChannel(command);
		} catch (JSchException e) {
			System.out.println("open channel exception:" + e.getMessage());
			e.printStackTrace();
		}
		return channel;
	}
	
	private synchronized Session getJsession(String user, String host, int port,
			String password, Properties config) {
		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession(user, host, port);
		} catch (JSchException e1) {
			System.out.println("Jsession getsession exception "+ e1.getMessage());
			e1.printStackTrace();
		}
		session.setPassword(password);
		session.setConfig(config);
		try {
			session.connect();
		} catch (JSchException e) {
			System.out.println("connection exception " + e.getMessage());
			e.printStackTrace();
		}
		return session;
	}
}
