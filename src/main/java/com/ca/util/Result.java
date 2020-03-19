package com.ca.util;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Result {

	private boolean uiSuccess = false;
	private String uiOutputMsg = null;
	private String expectedMessage = null;
	
	private boolean dbSucess = true;
	private String dbOutputMsg = null;
	private boolean PSQAMode = false;
	
	private String outputMessage = null;
	private boolean isSuccess = true;
	
	private String str3DSServerTxnId = null;
	private String strAmount = null;
	private String strIssuerName = null;
	private String purchaseId = null;
	
	private String strAcsAccountID = null;
	public String getStrAcsAccountID() {
		return strAcsAccountID;
	}

	public void setStrAcsAccountID(String strAcsAccountID) {
		this.strAcsAccountID = strAcsAccountID;
	}

	public String getStr3DSServerTxnId() {
		return str3DSServerTxnId;
	}

	public void setStr3DSServerTxnId(String str3dsServerTxnId) {
		str3DSServerTxnId = str3dsServerTxnId;
	}

	public String getStrAmount() {
		return strAmount;
	}

	public void setStrAmount(String strAmount) {
		this.strAmount = strAmount;
	}

	public String getStrIssuerName() {
		return strIssuerName;
	}

	public void setStrIssuerName(String strIssuerName) {
		this.strIssuerName = strIssuerName;
	}

	public String getPurchaseId() {
		return purchaseId;
	}

	public void setPurchaseId(String purchaseId) {
		this.purchaseId = purchaseId;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	private HashMap<String, String> returnMapObject = null;
	private LinkedHashMap<String, String> actualMap = null;
	private LinkedHashMap<String, String> expectedMap = null;
	
	/**
	 * added for checking no of records in UI
	 */
	private String processID=null;
	

	public String getExpectedMessage() {
		return expectedMessage;
	}

	public void setExpectedMessage(String expectedMessage) {
		this.expectedMessage = expectedMessage;
	}

	public HashMap<String, String> getReturnMapObject() {
		return returnMapObject;
	}

	public void setReturnMapObject(HashMap<String, String> returnMapObject) {
		this.returnMapObject = returnMapObject;
	}

	public boolean isUiSuccess() {
		return uiSuccess;
	}

	public void setUiSuccess(boolean uiSuccess) {
		this.uiSuccess = uiSuccess;
	}

	public String getUiOutputMsg() {
		return uiOutputMsg;
	}

	public void setUiOutputMsg(String uiOutputMsg) {
		this.uiOutputMsg = uiOutputMsg;
	}

	public boolean isDbSucess() {
		return dbSucess;
	}

	public void setDbSucess(boolean dbSucess) {
		this.dbSucess = dbSucess;
	}

	public LinkedHashMap<String, String> getActualMap() {
		return actualMap;
	}

	public void setActualMap(LinkedHashMap<String, String> actualMap) {
		this.actualMap = actualMap;
	}

	public LinkedHashMap<String, String> getExpectedMap() {
		return expectedMap;
	}

	public void setExpectedMap(LinkedHashMap<String, String> expectedMap) {
		this.expectedMap = expectedMap;
	}

	public String getDbOutputMsg() {
		return dbOutputMsg;
	}

	public void setDbOutputMsg(String dbOutputMsg) {
		this.dbOutputMsg = dbOutputMsg;
	}

	public boolean isPSQAMode() {
		return PSQAMode;
	}

	public void setPSQAMode(boolean pSQAMode) {
		PSQAMode = pSQAMode;
	}

	public String getProcessID() {
		return processID;
	}

	public void setProcessID(String processID) {
		this.processID = processID;
	}

	public String getOutputMessage() {
		return outputMessage;
	}

	public void setOutputMessage(String outputMessage) {
		this.outputMessage = outputMessage;
	}


}
