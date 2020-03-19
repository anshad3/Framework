package com.ca.util;

import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelExec;

public class LinuxUntil {
	
	/*public static String strStartLogging = "/home/automation/startlogging.sh";
	public static String strKillProcess = "kill -15";
	*/
	
	public synchronized static void executeCommand(LinuxServerPojo serverPojo){
		
		try {
			String output = executeCommandOverChannel(serverPojo.getHost(),serverPojo.getUserName(),
					serverPojo.getPassword(),serverPojo.getPort(),serverPojo.getCommand());
			serverPojo.setOutput(output);
		} catch (IOException | JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public synchronized static String executeCommandOverChannel(String hostname, String userName, String password, int port, String command)
			throws IOException, JSchException {
		
		
		Session session = getJsession(userName, hostname, port,
				password);
		System.out.println("comand is ---> " + command);
		Channel channel = getChannel(session, "exec");
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
			while (true) {
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
			}

			endTime = System.currentTimeMillis();
			//System.out.println("output from the command :" + command + " is:"+ output);
			System.out.println("command: " + command + " took "+ (endTime - startTime) + " milli seconds");
		} else {
			System.out.println("Channel is null");
		}
		
		return output.toString();

	}
	
	
	public synchronized static Session getJsession(String user, String host, int port,
			String password) {
		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession(user, host, port);
		} catch (JSchException e1) {
			System.out.println("Jsession getsession exception "
					+ e1.getMessage());
			e1.printStackTrace();
			e1.printStackTrace();
		}
		session.setPassword(password);
		//session.setConfig(config);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setConfig("PreferredAuthentications", 
                "publickey,keyboard-interactive,password");
		try {
			session.connect();
		} catch (JSchException e) {
			System.out.println("connection exception " + e.getMessage());
			e.printStackTrace();
		}

		return session;
	}
	
	public synchronized static Channel getChannel(Session session, String command) {
		Channel channel = null;
		try {
			channel = session.openChannel(command);
		} catch (JSchException e) {
			System.out.println("open channel exception:" + e.getMessage());
			e.printStackTrace();
		}
		return channel;
	}
	
	
	public synchronized static String executeCommandOverChannelForPrivateKeyAuth(String hostname, String userName, String pemPath, int port, String command)
			throws IOException, JSchException {
		
		
		Session session = getJsessionByPrivateKey(userName, hostname, port,
				pemPath);
		System.out.println("comand is ---> " + command);
		Channel channel = getChannel(session, "exec");
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
			while (true) {
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
			}

			endTime = System.currentTimeMillis();
			//System.out.println("output from the command :" + command + " is:"+ output);
			System.out.println("command: " + command + " took "+ (endTime - startTime) + " milli seconds");
		} else {
			System.out.println("Channel is null");
		}
		
		return output.toString();

	}
	
	
	public synchronized static Session getJsessionByPrivateKey(String user, String host, int port,String pemPath) {
		JSch jsch = new JSch();
		Session session = null;
		
		try {
			jsch.addIdentity(System.getProperty("user.dir")+pemPath);
			session = jsch.getSession(user, host, port);
		} catch (JSchException e1) {
			System.out.println("Jsession getsession exception "
					+ e1.getMessage());
			e1.printStackTrace();
			e1.printStackTrace();
		}
		//session.setPassword(password);
		//session.setConfig(config);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setConfig("PreferredAuthentications", 
                "publickey,keyboard-interactive,password");
		try {
			session.connect();
		} catch (JSchException e) {
			System.out.println("connection exception " + e.getMessage());
			e.printStackTrace();
		}

		return session;
	}
	
	public synchronized static String executeCommandOverChannel(Session session,String command)
			throws IOException, JSchException {
		
		System.out.println("comand is ---> " + command);
		Channel channel = getChannel(session, "exec");
		StringBuilder output = new StringBuilder();
		if(channel != null) {
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			((ChannelExec) channel).setPty(true);
			

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
			}

			endTime = System.currentTimeMillis();
			//System.out.println("output from the command :" + command + " is:"+ output);
			System.out.println("command: " + command + " took "+ (endTime - startTime) + " milli seconds");
		} else {
			System.out.println("Channel is null");
		}
		
		return output.toString();

	}

}
