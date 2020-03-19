package com.ca._3dsapi.base;

import java.io.IOException;
import java.security.PrivateKey;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ca._3ds.common.util.TdsQueries;
import com.ca.base.BaseSuite;
import com.ca.util.APIUtil;
import com.ca.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.JSchException;
import com.mashape.unirest.http.HttpResponse;
import com.nimbusds.jose.JOSEException;

import ca.tds2.model.SessionKeys;

public class CreqBrowserFlow {

	public void performBrowserCreq1Flow(JSONObject modifyJson, HashMap<String, String> persistentDataMap,LinkedHashMap<String, String> jsonOutputMap) {

		String testcaseID = persistentDataMap.get("TestCaseID");
		String strThreeDSServerTransID = persistentDataMap.get("threeDSServerTransID");
		String strAcsTransID = persistentDataMap.get("acsTransID");
		String strMessageVersion = persistentDataMap.get("messageVersion");
		String strCreqBrowserUrl = BaseSuite.getCAPropertyValue("CreqBrowserUrl1");
		String strChallengeWindowSize = "04";
		
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> modifyJsonMap = null;
		try {
			modifyJsonMap = mapper.readValue(modifyJson.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(modifyJsonMap.containsKey("challengeWindowSize")){
			strChallengeWindowSize = modifyJsonMap.get("challengeWindowSize");
		}
		
		if(modifyJsonMap.containsKey("messageVersion"))
			strMessageVersion = modifyJsonMap.get("messageVersion");
		
		APIUtil apiUtil = new APIUtil();

		JSONObject creqInitialJson = new JSONObject();
		try {
			creqInitialJson.put("threeDSServerTransID", strThreeDSServerTransID);
			creqInitialJson.put("acsTransID", strAcsTransID);
			creqInitialJson.put("challengeWindowSize", strChallengeWindowSize);
			creqInitialJson.put("messageType", "CReq");
			creqInitialJson.put("messageVersion", strMessageVersion);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		apiUtil.logJsonToExtentReportInHtml("CREQ " + "1" + " Posted", apiUtil.getPrettyJson(creqInitialJson.toString()), testcaseID);
		Base64.Encoder encoder = Base64.getEncoder();
		String strEncodedJson = encoder.encodeToString(creqInitialJson.toString().getBytes());
		
		String strCreqPost = "creq="+strEncodedJson+"&threeDSSessionData=merchantData";
		System.out.println("Creq 1 Posted : \n"+strCreqPost);
		
		HttpResponse<String> response = apiUtil.postCreqBrowser1Json(strCreqBrowserUrl, strCreqPost);
		System.out.println("Response  : \n"+response.getBody());
		System.out.println("");
		
		persistentDataMap.put("Cres", response.getBody());

		jsonOutputMap.put("CREQ 1 JSON", response.getBody());
		jsonOutputMap.put("CRES 1 JSON", response.getBody());


	}
	
	public void performBrowserCreq2Flow(JSONObject modifyJson, HashMap<String, String> persistentDataMap,LinkedHashMap<String, String> jsonOutputMap, int creqCount) {

		String testcaseID = persistentDataMap.get("TestCaseID");
		String strAcsTransID = persistentDataMap.get("acsTransID");
		String strCreqBrowserUrl = BaseSuite.getCAPropertyValue("CreqBrowserUrl2");
		
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> modifyJsonMap = null;
		try {
			modifyJsonMap = mapper.readValue(modifyJson.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		APIUtil apiUtil = new APIUtil();
		apiUtil.resoveSymbolsInMap(modifyJsonMap, persistentDataMap);
		
		String strContact = modifyJsonMap.get("selectedOptions");
		
		DateFormat dateFormat = new SimpleDateFormat("MMddhhmmss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
		
        JSONArray array = new JSONArray();
        array.put(strContact);
		

		JSONObject creqJson = new JSONObject();
		try {
			creqJson.put("acsAccountId", strAcsTransID);
			creqJson.put("selectedOptions", array);
			creqJson.put("challengeRcvdTime", dateFormat.format(date));
			creqJson.put("clientIPAddress", "155.35.135.91");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JsonUtil jsonUtil = new JsonUtil();
		modifyJsonMap.remove("selectedOptions");
		String updatedJson = jsonUtil.alterTheJsonValues(creqJson.toString(), modifyJsonMap);
		
		String creqPrettyJsonString = apiUtil.getPrettyJson(updatedJson);
		System.out.println("Creq " + creqCount + " : \n" + creqPrettyJsonString + "\n\n\n");
		apiUtil.logJsonToExtentReportInHtml("CREQ " + creqCount + " Posted", creqPrettyJsonString, testcaseID);

		
		
		HttpResponse<String> response = apiUtil.postJson(strCreqBrowserUrl, updatedJson);
		String cresJsonString = response.getBody();

		String cresPrettyJsonString = apiUtil.getPrettyJson(cresJsonString);
		apiUtil.logJsonToExtentReportInHtml("CRES " + creqCount + " returned", cresPrettyJsonString, testcaseID);
		System.out.println("Cres : " + cresPrettyJsonString + "\n\n\n\n");
		persistentDataMap.put("Cres", cresPrettyJsonString);

		jsonOutputMap.put("CREQ " + creqCount + " JSON", creqPrettyJsonString);
		jsonOutputMap.put("CRES " + creqCount + " JSON", cresPrettyJsonString);


	}
	
	


	
	
	public void postCreq(JSONObject creqJson, Map<String, String> modifyJsonMap,
			HashMap<String, String> persistentDataMap, Integer creqCount, LinkedHashMap<String, String> jsonOutputMap) {

		APIUtil apiUtil = new APIUtil();
		String strCreqNativeUrl = BaseSuite.getCAPropertyValue("CreqNativeUrl");
		String testcaseID = persistentDataMap.get("TestCaseID");
		JsonUtil jsonUtil = new JsonUtil();
		String updatedJson = jsonUtil.alterTheJsonValues(creqJson.toString(), modifyJsonMap);

		String creqPrettyJsonString = apiUtil.getPrettyJson(updatedJson);
		System.out.println("Creq " + creqCount + " : \n" + creqPrettyJsonString + "\n\n\n");

		String encryptedCreq = apiUtil.encryptJsonForAPPFlow(creqPrettyJsonString, persistentDataMap);
		persistentDataMap.put("Creq", creqPrettyJsonString);

		apiUtil.logJsonToExtentReportInHtml("CREQ " + creqCount + " Posted", creqPrettyJsonString, testcaseID);
		HttpResponse<String> response = apiUtil.postCreqAppJson(strCreqNativeUrl, encryptedCreq);
		if (response == null) {

			// TODO Vinod put asertion failure
		}
		System.out.println("Response : " + response.getBody());
		String cresJsonString = null;
		if (response.getBody().contains("\"messageType\":\"Erro\"")) {
			cresJsonString = response.getBody();
		} else {
			cresJsonString = apiUtil.decryptCResJSON(response.getBody(), persistentDataMap);
		}

		String cresPrettyJsonString = apiUtil.getPrettyJson(cresJsonString);
		apiUtil.logJsonToExtentReportInHtml("CRES " + creqCount + " returned", cresPrettyJsonString, testcaseID);
		System.out.println("Cres : " + cresPrettyJsonString + "\n\n\n\n");
		persistentDataMap.put("Cres", cresPrettyJsonString);

		jsonOutputMap.put("CREQ " + creqCount + " JSON", creqPrettyJsonString);
		jsonOutputMap.put("CRES " + creqCount + " JSON", cresPrettyJsonString);
	}

}
