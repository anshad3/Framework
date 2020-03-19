package com.ca.util;

public class APIResult {

	private boolean testCaseStatus = false;
	private String strOutputMsg = null;

	public boolean isTestCaseStatus() {
		return testCaseStatus;
	}

	public void setTestCaseStatus(boolean testCaseStatus) {
		this.testCaseStatus = testCaseStatus;
	}

	public String getStrOutputMsg() {
		return strOutputMsg;
	}

	public void setStrOutputMsg(String strOutputMsg) {
		this.strOutputMsg = strOutputMsg;
	}
}
