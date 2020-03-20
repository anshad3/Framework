package com.api.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.aventstack.extentreports.Status;
import com.base.reports.ReportLogger;
import com.ca.util.APIResult;
import com.ca.util.CommonUtil;
import com.ca.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CresFlow {

	public String validateCresJson(String expectedCresJsonValues, HashMap<String, String> persistentDataMap,
			String strExtentMessage, APIResult result) {

		String cresResponse = persistentDataMap.get("Cres");
		String testcaseID = persistentDataMap.get("TestCaseID");
		String creqCount = persistentDataMap.get("CreqCount");

		JSONObject json = null;
		/*
		 * json = new JSONObject(cresResponse); String strValidateRA_RulesJson =
		 * cresResponse.getString("Check_RA_Triggered_Rules"); JSONObject json =
		 * null;
		 */
		/*String strAcsTxnId = null;
		try {
			json = new JSONObject(cresResponse);
		} catch (org.codehaus.jettison.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> expectedCresJsonValueMap = null;
		try {
			expectedCresJsonValueMap = mapper.readValue(expectedCresJsonValues.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(expectedCresJsonValueMap);
		ArrayList<String> cresFieldValidationList = new ArrayList<String>(expectedCresJsonValueMap.keySet());
		JsonUtil jsonUtil = new JsonUtil();
		Map<String, String> actualCresJsonValueMap = jsonUtil.getProperty(cresResponse, cresFieldValidationList);

		if (strExtentMessage == null || strExtentMessage.length() == 0)
			strExtentMessage = "CRES " + creqCount + "Validation";
		CommonUtil commonUtil = new CommonUtil();
		String extentInfo = commonUtil.generateHTMLReportExpectedAndActualResult(expectedCresJsonValueMap,
				actualCresJsonValueMap, testcaseID, strExtentMessage);

		if (extentInfo.contains("#FF0000")) {
			ReportLogger.logInfo(Status.FAIL, extentInfo);
			result.setTestCaseStatus(false);
			String message = result.getStrOutputMsg();
			if (message == null) {
				result.setStrOutputMsg("CRES " + creqCount + "Validation Failed");
			} else {
				result.setStrOutputMsg(message + "<br>CRES " + creqCount + "Validation Failed");
			}
		} else if (extentInfo.contains("#00FF00"))
			ReportLogger.logInfo(Status.PASS, extentInfo);
		else
			ReportLogger.logInfo(Status.INFO, extentInfo);
		System.out.println("");

		return cresResponse;

	}

	public String validateCres3BrowserJson(String expectedCresJsonValues, HashMap<String, String> persistentDataMap,
			String strExtentMessage, APIResult result) {

		String cresResponse = persistentDataMap.get("Cres");
		String testcaseID = persistentDataMap.get("TestCaseID");
		String creqCount = persistentDataMap.get("CreqCount");
		JSONObject json = null;
		String strCResBase64 = null;
		try {
			json = new JSONObject(cresResponse);
			String strstatus = json.getString("status");
			if(strstatus!=null && strstatus.equalsIgnoreCase("success")){
				String strDynamicContent = json.getString("dynamicContent");
				JSONObject jsonDynamic = new JSONObject(strDynamicContent);
				strCResBase64 = jsonDynamic.getString("cResBase64");
			}
			else{
				result.setTestCaseStatus(false);
				String message = result.getStrOutputMsg();
				if (message == null) {
					result.setStrOutputMsg("Final CRes didnt sent success status");
				} 
				else {
					result.setStrOutputMsg(message + "<br>Final CRes didnt sent success status");
				}
				return cresResponse;
			}
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] byteDecodedJson = decoder.decode(strCResBase64);
		
		String strDecodedJson = new String(byteDecodedJson);
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> expectedCresJsonValueMap = null;
		try {
			expectedCresJsonValueMap = mapper.readValue(expectedCresJsonValues.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(expectedCresJsonValueMap);
		ArrayList<String> cresFieldValidationList = new ArrayList<String>(expectedCresJsonValueMap.keySet());
		JsonUtil jsonUtil = new JsonUtil();
		Map<String, String> actualCresJsonValueMap = jsonUtil.getProperty(strDecodedJson, cresFieldValidationList);

		if (strExtentMessage == null || strExtentMessage.length() == 0)
			strExtentMessage = "CRES " + creqCount + "Validation";
		CommonUtil commonUtil = new CommonUtil();
		String extentInfo = commonUtil.generateHTMLReportExpectedAndActualResult(expectedCresJsonValueMap,
				actualCresJsonValueMap, testcaseID, strExtentMessage);

		if (extentInfo.contains("#FF0000")) {
			ReportLogger.logInfo(Status.FAIL, extentInfo);
			result.setTestCaseStatus(false);
			String message = result.getStrOutputMsg();
			if (message == null) {
				result.setStrOutputMsg("CRES " + creqCount + "Validation Failed");
			} else {
				result.setStrOutputMsg(message + "<br>CRES " + creqCount + "Validation Failed");
			}
		} else if (extentInfo.contains("#00FF00"))
			ReportLogger.logInfo(Status.PASS, extentInfo);
		else
			ReportLogger.logInfo(Status.INFO, extentInfo);
		System.out.println("");

		return cresResponse;

	}

}
