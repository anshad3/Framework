package com.ca._3dsapi.base.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.aventstack.extentreports.Status;
import com.ca._3ds.common.util.TdsQueries;
import com.ca.base.reports.ReportLogger;
import com.ca.db_connection.util.DBConnections;
import com.ca.util.APIResult;
import com.ca.util.CommonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ValidateRA {

	public void validateRA_LVT(String strValidateRA_LVTJson, HashMap<String, String> persistentDataMap,
			String strExtentMessage) {

		String strAcsTransID = persistentDataMap.get("acsTransID");

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> lvtExpectedMap = null;
		try {
			lvtExpectedMap = mapper.readValue(strValidateRA_LVTJson.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		TdsQueries tdsqueries = new TdsQueries();
		String strRASessionId = tdsqueries.getRaTxnIdFromTdTransactionLogTable(strAcsTransID);

		String raQuery = " SELECT RA_ATN_AMOUNTEUR, HISTORYBASEDVALUESJSON FROM ARRFSYSAUDITLOG_3DSECURE "
				+ "where SESSIONID='" + strRASessionId + "' and TXNTYPE=1";

		LinkedHashMap<String, String> raQueryResult = tdsqueries.executeQueryInRADB(raQuery);
		String strRA_atn_amount = raQueryResult.get("RA_ATN_AMOUNTEUR");
		String strHistoryJson = raQueryResult.get("HISTORYBASEDVALUESJSON");
		String[] historyArray = strHistoryJson.split("\n");
		HashMap<String, String> historyMap = new HashMap<String, String>();
		for (String historyValue : historyArray) {
			String[] strKeyValue = historyValue.split("=");
			if (strKeyValue.length == 2 && strKeyValue[0] != null && strKeyValue[1] != null) {
				historyMap.put(strKeyValue[0].trim(), strKeyValue[1].trim());
			}
		}

		Map<String, String> lvtActualMap = new HashMap<String, String>();
		for (String key : lvtExpectedMap.keySet()) {

			if (key.equals("RA_atn_amountEUR")) {
				lvtActualMap.put("RA_atn_amountEUR", strRA_atn_amount);
			} else {

				String value = historyMap.get(key);
				lvtActualMap.put(key, value);
			}

		}

		System.out.println(" ");

	}

	public void validateRA_TB(String strValidateRA_TBJson, HashMap<String, String> persistentDataMap,
			String strExtentMessage,APIResult result) {

		String strAcsTransID = persistentDataMap.get("acsTransID");

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> tbExpectedMap = null;
		try {
			tbExpectedMap = mapper.readValue(strValidateRA_TBJson.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		TdsQueries tdsqueries = new TdsQueries();
		String strRASessionId = tdsqueries.getRaTxnIdFromTdTransactionLogTable(strAcsTransID);
		//strRASessionId = "129:4398049816912";
		
		

		String raQuery = " SELECT FM_ATN_BENEFICIARYOP, RA_COM_EXEMPTIONCLAIMED, FM_COM_ISEXEMPTIONOVERRIDE FROM ARRFSYSAUDITLOG_3DSECURE "
				+ "where SESSIONID='" + strRASessionId + "' and TXNTYPE=1";

		LinkedHashMap<String, String> raQueryResult = tdsqueries.executeQueryInRADB(raQuery);
		String strFM_ATN_BENEFICIARYOP = raQueryResult.get("FM_ATN_BENEFICIARYOP");
		String strDB_RA_COM_EXEMPTIONCLAIMED = raQueryResult.get("RA_COM_EXEMPTIONCLAIMED");
		String strDB_FM_COM_ISEXEMPTIONOVERRIDE = raQueryResult.get("FM_COM_ISEXEMPTIONOVERRIDE");
		
		
		Map<String,String> keyValueMap = getExemptionsMapFromDB();
		String strRA_COM_EXEMPTIONCLAIMED = keyValueMap.get(strDB_RA_COM_EXEMPTIONCLAIMED);
		String strFM_COM_ISEXEMPTIONOVERRIDE = null;
		
		if(strDB_FM_COM_ISEXEMPTIONOVERRIDE.equalsIgnoreCase("N"))
			strFM_COM_ISEXEMPTIONOVERRIDE = "NO";
		else
			strFM_COM_ISEXEMPTIONOVERRIDE = "YES";
		
		
		

		Map<String, String> tbActualMap = new HashMap<String, String>();
		tbActualMap.put("FM_atn_beneficiaryOp", strFM_ATN_BENEFICIARYOP);
		tbActualMap.put("RA_com_exemptionClaimed",strRA_COM_EXEMPTIONCLAIMED );
		tbActualMap.put("FM_com_isExemptionOverride",strFM_COM_ISEXEMPTIONOVERRIDE );
		
		CommonUtil commonUtil = new CommonUtil();
		String testcaseID = persistentDataMap.get("TestCaseID");
		String extentInfo = commonUtil.generateHTMLReportExpectedAndActualResult(tbExpectedMap, tbActualMap, testcaseID,
				strExtentMessage);

		if (extentInfo.contains("#FF0000")) {
			ReportLogger.logInfo(Status.FAIL, extentInfo);
			result.setTestCaseStatus(false);
			String message = result.getStrOutputMsg();
			if (message == null) {
				result.setStrOutputMsg("Trusted Beneficiary RA Elements Validation Failed");
			} else {
				result.setStrOutputMsg(message + "<br>DTrusted Beneficiary RA Elements  Validation Failed");
			}
		} else if (extentInfo.contains("#00FF00"))
			ReportLogger.logInfo(Status.PASS, extentInfo);
		else
			ReportLogger.logInfo(Status.INFO, extentInfo);

		System.out.println(" ");

	}
	
	public void validateRARulesTriggered(String strValidateRulesJson, HashMap<String, String> persistentDataMap,
			String strExtentMessage,APIResult result) {

		String strAcsTransID = persistentDataMap.get("acsTransID");

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> tbExpectedMap = null;
		try {
			tbExpectedMap = mapper.readValue(strValidateRulesJson.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		TdsQueries tdsqueries = new TdsQueries();
		String strRASessionId = tdsqueries.getRaTxnIdFromTdTransactionLogTable(strAcsTransID);
		List<String> lstTrigerredRule = tdsqueries.getTrigerredRuleListFromRA(strRASessionId);

		Map<String, String> tbActualMap = new LinkedHashMap<String, String>();
		for(String rule : tbExpectedMap.keySet()){
			String original = rule;
			if(rule.length()>32)
				rule = rule.substring(0, 32);
			if(lstTrigerredRule.contains(rule.toUpperCase()))
				tbActualMap.put(original, "YES");
			else
				tbActualMap.put(original, "NO");
		}
		
		CommonUtil commonUtil = new CommonUtil();
		String testcaseID = persistentDataMap.get("TestCaseID");
		String extentInfo = commonUtil.generateHTMLReportExpectedAndActualResult(tbExpectedMap, tbActualMap, testcaseID,
				strExtentMessage);

		if (extentInfo.contains("#FF0000")) {
			ReportLogger.logInfo(Status.FAIL, extentInfo);
			result.setTestCaseStatus(false);
			String message = result.getStrOutputMsg();
			if (message == null) {
				result.setStrOutputMsg("Validation of RA Rules Trigerred Failed");
			} else {
				result.setStrOutputMsg(message + "<br>Validation of RA Rules Trigerred Failed for ACS Transaction ID : "+strAcsTransID);
			}
		} else if (extentInfo.contains("#00FF00"))
			ReportLogger.logInfo(Status.PASS, extentInfo);
		else
			ReportLogger.logInfo(Status.INFO, extentInfo);

		System.out.println(" ");

	}
	
	public Map<String,String> getExemptionsMapFromDB(){
		
		String keyValueMapQuery = "SELECT EXEMPTIONID,DISPLAYNAME FROM ARRFEXEMPTIONS";
		TdsQueries tdsqueries = new TdsQueries();
		Map<String,String> keyValueMap = tdsqueries.getKeyValuePairFromRAB(keyValueMapQuery, "EXEMPTIONID", "DISPLAYNAME");
		return keyValueMap;
		
	}

}
