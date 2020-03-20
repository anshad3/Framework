package com.ca.util;

import com.base.pojos.ServerLogInputPojo;

public class LinuxServerPojo {

	public String host = null;
	public String userName = null;
	public String password = null;
	public int port;
	public String command = null;
	public String output = null;
	
	public LinuxServerPojo(){
		
	}

	public LinuxServerPojo(ServerLogInputPojo serverPojo, String strCommand) {

		host = serverPojo.getHost();
		userName = serverPojo.getUserName();
		password = serverPojo.getPassword();
		port = Integer.parseInt(serverPojo.getPort());
		command = strCommand;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
