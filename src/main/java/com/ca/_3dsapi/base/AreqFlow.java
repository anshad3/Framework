package com.ca._3dsapi.base;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;

import com.aventstack.extentreports.Status;
import com.ca.base.BaseSuite;
import com.ca.base.reports.ReportLogger;
import com.ca.util.APIUtil;
import com.ca.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;


public class AreqFlow {

	public String performNativeAreqFlow(String strCardNumber, String postUrl, JSONObject areqDefaultJson,
			JSONObject modifyJson, HashMap<String, String> persistentDataMap,String strExtentMessage) {

		String testcaseID = persistentDataMap.get("TestCaseID");
		String strSdkEphemPubKey = persistentDataMap.get("sdkEphemPubKey");
		String strSdkTransID = persistentDataMap.get("sdkTransID");
		String strThreeDSServerTransID = persistentDataMap.get("threeDSServerTransID");
		String strDsTransID = persistentDataMap.get("dsTransID");
		String strMessageVersion = persistentDataMap.get("messageVersion");
	

		String strThreeDSServerURL = BaseSuite.getCAPropertyValue("ThreeDSServerURL");
		String StrDsURL = BaseSuite.getCAPropertyValue("DsURL");
		String strthreeDSServerRefNumber = BaseSuite.getCAPropertyValue("threeDSServerRefNumber");
		APIUtil apiUtil = new APIUtil();
		
		String strAreqprettyJsonString = apiUtil.getPrettyJson(areqDefaultJson);
		String strModifyAreqprettyJsonString = apiUtil.getPrettyJson(modifyJson);
		System.out.println("Modify Json: " + strModifyAreqprettyJsonString + "\n\n\n\n");
				

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> modifyJsonMap = null;
		try {
			modifyJsonMap = mapper.readValue(modifyJson.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		modifyJsonMap.put("threeDSServerTransID", strThreeDSServerTransID);
		modifyJsonMap.put("sdkTransID", strSdkTransID);
		modifyJsonMap.put("dsTransID", strDsTransID);
		modifyJsonMap.put("sdkEphemPubKey", strSdkEphemPubKey);
		modifyJsonMap.put("messageVersion", strMessageVersion);
		
		System.out.println("sdkEphemPubKey : "+strSdkEphemPubKey);
		if(modifyJsonMap.containsKey("acctNumber")){
			persistentDataMap.put("CardNumber", modifyJsonMap.get("acctNumber"));
			persistentDataMap.put("acctNumber", modifyJsonMap.get("acctNumber"));
		}
		else
			modifyJsonMap.put("acctNumber", strCardNumber);
		
		modifyJsonMap.put("threeDSServerURL", strThreeDSServerURL);
		modifyJsonMap.put("dsURL", StrDsURL);
		modifyJsonMap.put("threeDSServerRefNumber", strthreeDSServerRefNumber);
		
		apiUtil.resoveSymbolsInMap(modifyJsonMap, persistentDataMap);
		
		System.out.println(" Areq String : "+areqDefaultJson.toString());
		JsonUtil jsonUtil = new JsonUtil();
		String updatedJson = jsonUtil.alterTheJsonValues(areqDefaultJson.toString(), modifyJsonMap);

		strAreqprettyJsonString = apiUtil.getPrettyJson(updatedJson);
		System.out.println("Updated Areq JSON: \n" + strAreqprettyJsonString + "\n\n\n\n");
		
		apiUtil.logJsonToExtentReportInHtml("AREQ Posted", strAreqprettyJsonString,testcaseID);
		HttpResponse<String> response = apiUtil.postJson(postUrl, updatedJson);

		String aresPrettyJsonString = apiUtil.getPrettyJson(response.getBody());
		System.out.println("Ares : " + aresPrettyJsonString + "\n\n\n\n");
		persistentDataMap.put("Ares", aresPrettyJsonString);
		apiUtil.logJsonToExtentReportInHtml("ARES returned", aresPrettyJsonString,testcaseID);
		
		System.out.println("");
		
		return strAreqprettyJsonString;
	}
	
	public String performBrowserAreqFlow(String strCardNumber, String postUrl, JSONObject areqDefaultJson,
			JSONObject modifyJson, HashMap<String, String> persistentDataMap,String strExtentMessage) {

		String testcaseID = persistentDataMap.get("TestCaseID");
		String strThreeDSServerTransID = persistentDataMap.get("threeDSServerTransID");
		String strDsTransID = persistentDataMap.get("dsTransID");
		String strMessageVersion = persistentDataMap.get("messageVersion");

		String strThreeDSServerURL = BaseSuite.getCAPropertyValue("ThreeDSServerURL");
		String StrDsURL = BaseSuite.getCAPropertyValue("DsURL");
		String strthreeDSServerRefNumber = BaseSuite.getCAPropertyValue("threeDSServerRefNumber");
		APIUtil apiUtil = new APIUtil();
		
		String strAreqprettyJsonString = apiUtil.getPrettyJson(areqDefaultJson);
		String strModifyAreqprettyJsonString = apiUtil.getPrettyJson(modifyJson);
		System.out.println("Modify Json: " + strModifyAreqprettyJsonString + "\n\n\n\n");
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> modifyJsonMap = null;
		try {
			modifyJsonMap = mapper.readValue(modifyJson.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		modifyJsonMap.put("threeDSServerTransID", strThreeDSServerTransID);
		modifyJsonMap.put("dsTransID", strDsTransID);
		
		modifyJsonMap.put("acctNumber", strCardNumber);
		modifyJsonMap.put("threeDSServerURL", strThreeDSServerURL);
		modifyJsonMap.put("dsURL", StrDsURL);
		modifyJsonMap.put("threeDSServerRefNumber", strthreeDSServerRefNumber);
		modifyJsonMap.put("messageVersion", strMessageVersion);
		
		apiUtil.resoveSymbolsInMap(modifyJsonMap, persistentDataMap);
		
		
		System.out.println(" Areq String : "+areqDefaultJson.toString());
		JsonUtil jsonUtil = new JsonUtil();
		String updatedJson = jsonUtil.alterTheJsonValues(areqDefaultJson.toString(), modifyJsonMap);

		strAreqprettyJsonString = apiUtil.getPrettyJson(updatedJson);
		System.out.println("Updated Areq JSON: \n" + strAreqprettyJsonString + "\n\n\n\n");
		
		apiUtil.logJsonToExtentReportInHtml("AREQ Posted", strAreqprettyJsonString,testcaseID);
		HttpResponse<String> response = apiUtil.postJson(postUrl, updatedJson);

		String aresPrettyJsonString = apiUtil.getPrettyJson(response.getBody());
		System.out.println("Ares : " + aresPrettyJsonString + "\n\n\n\n");
		persistentDataMap.put("Ares", aresPrettyJsonString);
		apiUtil.logJsonToExtentReportInHtml("ARES returned", aresPrettyJsonString,testcaseID);
		
		System.out.println("");
		
		return strAreqprettyJsonString;
	}

}
