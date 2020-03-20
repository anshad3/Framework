package com.db_connection.util;

public class DBConnectionDetails {

	private String sHost = null;
	private String sPort = null;
	private String sSid = null;
	private String sUserid = null;
	private String sPwd = null;

	public String getsHost() {
		return sHost;
	}

	public void setsHost(String sHost) {
		this.sHost = sHost;
	}

	public String getsPort() {
		return sPort;
	}

	public void setsPort(String sPort) {
		this.sPort = sPort;
	}

	public String getsSid() {
		return sSid;
	}

	public void setsSid(String sSid) {
		this.sSid = sSid;
	}

	public String getsUserid() {
		return sUserid;
	}

	public void setsUserid(String sUserid) {
		this.sUserid = sUserid;
	}

	public String getsPwd() {
		return sPwd;
	}

	public void setsPwd(String sPwd) {
		this.sPwd = sPwd;
	}

}
