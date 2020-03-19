package com.ca.base.pojos;

import org.json.simple.JSONObject;

public class ServerLogInputPojo {

	public String logName = null;
	public String logDisplayName = null;
	public String host = null;
	public String userName = null;
	public String password = null;
	public String port = null;
	public String logFileName = null;
	public String path = null;
	public String envType = null;

	public ServerLogInputPojo(JSONObject serverLogObj){
		
		logName = (String) serverLogObj.get("name");
		logDisplayName = (String) serverLogObj.get("displayname");
		host = (String) serverLogObj.get("host");
		userName = (String) serverLogObj.get("userName");
		password = (String) serverLogObj.get("password");
		long portLong = (long) serverLogObj.get("port");
		port = Long.toString(portLong);
		logFileName = (String) serverLogObj.get("logFileName");
		path = (String) serverLogObj.get("path");
		envType = (String) serverLogObj.get("envType");
		
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
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

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getLogFileName() {
		return logFileName;
	}

	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getLogDisplayName() {
		return logDisplayName;
	}

	public void setLogDisplayName(String logDisplayName) {
		this.logDisplayName = logDisplayName;
	}
	
	public String getEnvType() {
		return envType;
	}

	public void setEnvType(String envType) {
		this.envType = envType;
	}


}
