package com.ca._3dsapi.base;

import java.io.IOException;
import java.security.PrivateKey;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

public class CreqNativeFlow {

	public void performNativeCreq1Flow(JSONObject modifyJson, HashMap<String, String> persistentDataMap,
			Integer creqCount, PrivateKey privateKey, LinkedHashMap<String, String> jsonOutputMap) {

		String testcaseID = persistentDataMap.get("TestCaseID");
		String strSdkTransID = persistentDataMap.get("sdkTransID");
		String strThreeDSServerTransID = persistentDataMap.get("threeDSServerTransID");
		String strAcsTransID = persistentDataMap.get("acsTransID");
		String strMessageVersion = persistentDataMap.get("messageVersion");
		String strCreqNativeUrl = BaseSuite.getCAPropertyValue("CreqNativeUrl");
		String strCounter = String.valueOf(1);
		String aresResponse = persistentDataMap.get("Ares");
		persistentDataMap.put("CreqCount", strCounter);
		APIUtil apiUtil = new APIUtil();

		JSONObject creqJson = new JSONObject();
		try {
			creqJson.put("threeDSServerTransID", strThreeDSServerTransID);
			creqJson.put("acsTransID", strAcsTransID);
			creqJson.put("sdkTransID", strSdkTransID);
			creqJson.put("messageType", "CReq");
			creqJson.put("messageVersion", strMessageVersion);
			creqJson.put("sdkCounterStoA", "000");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> modifyJsonMap = null;
		try {
			modifyJsonMap = mapper.readValue(modifyJson.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JSONObject json = null;
		Base64.Decoder decoder = Base64.getDecoder();
		SessionKeys sessionKey = null;
		String token = null;

		try {
			json = new JSONObject(aresResponse);
			token = (String) json.get("acsSignedContent");
			persistentDataMap.put("acsSignedContent", token);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DecodedJWT jwt = JWT.decode(token);
		String encodedString = new String(decoder.decode(jwt.getPayload().getBytes()));

		try {
			json = new JSONObject(encodedString);
			sessionKey = JoseServiceJWS.deriveSessionKeys(json.get("acsEphemPubKey").toString(), privateKey);
			persistentDataMap.put("SessionKey", sessionKey.getSessionKey());
			System.out.println("Session Key " + sessionKey);
		} catch (ParseException | JOSEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException | ca.paysec.commons.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		postCreq(creqJson, modifyJsonMap, persistentDataMap, 1, jsonOutputMap);

	}


	public void performNativeCreqSubsequentFlow(JSONObject modifyJson, HashMap<String, String> persistentDataMap,
			Integer creqCount, LinkedHashMap<String, String> jsonOutputMap) {

		String testcaseID = persistentDataMap.get("TestCaseID");
		String strSdkTransID = persistentDataMap.get("sdkTransID");
		String strThreeDSServerTransID = persistentDataMap.get("threeDSServerTransID");
		String strAcsTransID = persistentDataMap.get("acsTransID");
		String strMessageVersion = persistentDataMap.get("messageVersion");
		String strCreqNativeUrl = BaseSuite.getCAPropertyValue("CreqNativeUrl");
		String strCounter = String.valueOf(creqCount.intValue());
		persistentDataMap.put("CreqCount", strCounter);
		String strCreqCounter = null;
		String aresResponse = persistentDataMap.get("Ares");
		APIUtil apiUtil = new APIUtil();

		int count = creqCount - 1;
		if (strCounter.length() == 1) {

			strCreqCounter = "00" + count;
		} else if (strCounter.length() == 2) {
			strCreqCounter = "0" + count;
		}

		JSONObject creqJson = new JSONObject();
		try {
			creqJson.put("threeDSServerTransID", strThreeDSServerTransID);
			creqJson.put("acsTransID", strAcsTransID);
			creqJson.put("sdkTransID", strSdkTransID);
			creqJson.put("messageType", "CReq");
			creqJson.put("messageVersion", strMessageVersion);
			//creqJson.put("challengeDataEntry", "mobilenumber1");
			creqJson.put("sdkCounterStoA", strCreqCounter);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> modifyJsonMap = null;
		try {
			modifyJsonMap = mapper.readValue(modifyJson.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		apiUtil.resoveSymbolsInMap(modifyJsonMap, persistentDataMap);
		
		/*String strchallengeDataEntry = modifyJsonMap.get("challengeDataEntry");
		if (strchallengeDataEntry.contains("**")) {
			strchallengeDataEntry = apiUtil.resoveSymbol(strchallengeDataEntry, persistentDataMap);
			modifyJsonMap.put("challengeDataEntry", strchallengeDataEntry);
			
		}*/

		postCreq(creqJson, modifyJsonMap, persistentDataMap, creqCount, jsonOutputMap);
	}

	public void performCancelTransaction(JSONObject modifyJson, HashMap<String, String> persistentDataMap,
			Integer creqCount, LinkedHashMap<String, String> jsonOutputMap) {

		String testcaseID = persistentDataMap.get("TestCaseID");
		String strSdkTransID = persistentDataMap.get("sdkTransID");
		String strThreeDSServerTransID = persistentDataMap.get("threeDSServerTransID");
		String strAcsTransID = persistentDataMap.get("acsTransID");
		String strMessageVersion = persistentDataMap.get("messageVersion");
		String strCreqNativeUrl = BaseSuite.getCAPropertyValue("CreqNativeUrl");
		String strCounter = String.valueOf(creqCount.intValue());
		persistentDataMap.put("CreqCount", strCounter);
		String strCreqCounter = null;

		int count = creqCount - 1;
		if (strCounter.length() == 1) {

			strCreqCounter = "00" + count;
		} else if (strCounter.length() == 2) {
			strCreqCounter = "0" + count;
		}

		JSONObject creqJson = new JSONObject();
		try {
			creqJson.put("acsTransID", strAcsTransID);
			creqJson.put("challengeCancel", "01");
			creqJson.put("messageType", "CReq");
			creqJson.put("messageVersion", strMessageVersion);
			creqJson.put("sdkCounterStoA", strCreqCounter);
			creqJson.put("sdkTransID", strSdkTransID);
			creqJson.put("threeDSServerTransID", strThreeDSServerTransID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> modifyJsonMap = null;
		try {
			modifyJsonMap = mapper.readValue(modifyJson.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		postCreq(creqJson, modifyJsonMap, persistentDataMap, creqCount, jsonOutputMap);

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
