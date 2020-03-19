package com.ca._3dsapi.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;


import com.aventstack.extentreports.Status;
import com.ca.base.reports.ReportLogger;
import com.ca.util.APIResult;
import com.ca.util.CommonUtil;
import com.ca.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AresFlow {
	
	
	public String validateAresJson(String expectedAresJsonValues, HashMap<String, String> persistentDataMap, String strExtentMessage, APIResult result){
		
		String aresResponse = persistentDataMap.get("Ares");
		String testcaseID = persistentDataMap.get("TestCaseID");
		JSONObject json = null;
		String strAcsTxnId = null;
		try {
			json = new JSONObject(aresResponse);
			strAcsTxnId =  json.getString("acsTransID");
		} catch (org.codehaus.jettison.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		persistentDataMap.put("acsTransID", strAcsTxnId);
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> expectedAresJsonValueMap = null;
		try {
			expectedAresJsonValueMap = mapper.readValue(expectedAresJsonValues.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(expectedAresJsonValueMap);
		ArrayList<String> aresFieldValidationList = new ArrayList<String>(expectedAresJsonValueMap.keySet());
		JsonUtil jsonUtil = new JsonUtil();
		Map<String, String> actualAresJsonValueMap = jsonUtil.getProperty(aresResponse, aresFieldValidationList);
				
		
		if(strExtentMessage==null||strExtentMessage.length()==0)
			strExtentMessage = "ARES Validation";
		CommonUtil commonUtil = new CommonUtil();
		String extentInfo = commonUtil.generateHTMLReportExpectedAndActualResult
								(expectedAresJsonValueMap, actualAresJsonValueMap, testcaseID,strExtentMessage);
		
		if(extentInfo.contains("#FF0000")){
			ReportLogger.logInfo(Status.FAIL, extentInfo);			
			result.setTestCaseStatus(false);
			String message = result.getStrOutputMsg();
			if(message==null){
				result.setStrOutputMsg("ARES Validation Failed");
			}
			else{
				result.setStrOutputMsg(message+"<br>ARES Validation Failed");
			}
		}
		else if(extentInfo.contains("#00FF00"))
			ReportLogger.logInfo(Status.PASS, extentInfo);
		else
			ReportLogger.logInfo(Status.INFO, extentInfo);
		System.out.println("");
		
		return aresResponse;
		
	}

}
