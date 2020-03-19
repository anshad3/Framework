package com.ca._3dsapi.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jettison.json.JSONObject;

import com.aventstack.extentreports.Status;
import com.ca._3ds.common.util.TdsQueries;
import com.ca._3dsapi.base.action.PersistData;
import com.ca._3dsapi.base.action.TrustedBeneficiary;
import com.ca._3dsapi.base.action.ValidateDB;
import com.ca._3dsapi.base.action.ValidateRA;
import com.ca.base.BaseSuite;
import com.ca.base.reports.ReportLogger;
import com.ca.util.APIResult;
import com.ca.util.APIUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;


//import kong.unirest.json.JSONException;

public class BrowserFlowEngine {

	HashMap<String, String> persistentDataMap = new HashMap<String, String>();
	LinkedHashMap<String, String> flowDataMap = new LinkedHashMap<String, String>();
	LinkedHashMap<String, String> jsonOutputMap = new LinkedHashMap<String, String>();
	String strAreqBrowserUrl = BaseSuite.getCAPropertyValue("AreqBrowserUrl");
	String strCreqBrowserUrl = BaseSuite.getCAPropertyValue("CreqBrowserUrl");
	String strAreqDefaultJson = null;
	JSONObject areqDefaultJson = null;
	String strThreeDSServerTransID = null;
	String strDsTransID = null;
	String strCardNumber = null;
	String testCaseStatus = "PASS";
	APIResult result = new APIResult();
	Integer creqCount = new Integer(1);

	public APIResult performBrowserTxnFlow(Map<String, String> testCaseData) {

		result.setTestCaseStatus(true);
		String testcaseid = testCaseData.get("TestCaseID");
		String strIssuerName = testCaseData.get("Issuer");
		String strMessageVersion = testCaseData.get("MessageVersion");
		if(strMessageVersion==null||strMessageVersion.length()==0||
				!(strMessageVersion.equalsIgnoreCase("2.1.0")||strMessageVersion.equalsIgnoreCase("2.2.0"))){
			strMessageVersion="2.1.0";
		}
		
		persistentDataMap.put("TestCaseID", testcaseid);
		strCardNumber = testCaseData.get("CardNumber");
		persistentDataMap.put("Issuer", strIssuerName);
		persistentDataMap.put("acctNumber", strCardNumber);
		persistentDataMap.put("CardNumber", strCardNumber);
		
		persistentDataMap.put("messageVersion", strMessageVersion);
		persistentDataMap.put("TestStatus", "PASS");
		
		
		
		
		TdsQueries tdsqueries = new TdsQueries();
		String strProxyPan = tdsqueries.getProxypanTmDb(strCardNumber, strIssuerName);
		persistentDataMap.put("ProxyPan", strProxyPan);
		

		for (String key : testCaseData.keySet()) {
			if (key.contains("Header")) {
				if(!(testCaseData.get(key)==null||testCaseData.get(key).trim().length()==0))
					flowDataMap.put(key, testCaseData.get(key));
			}
		}


		System.out.println("");
		String folderPath = System.getProperty("user.dir");
		String strAreqDefaultPath =  null;
		if(strMessageVersion.equalsIgnoreCase("2.2.0"))
			strAreqDefaultPath = BaseSuite.getCAPropertyValue("BrowserDefaultJson_220");
		else
			strAreqDefaultPath = BaseSuite.getCAPropertyValue("BrowserDefaultJson_210");
		
		String strAreqDefaultFullPath = folderPath + File.separator + strAreqDefaultPath;
		FileInputStream inFile = null;

		try {
			inFile = new FileInputStream(strAreqDefaultFullPath);
			byte[] str = new byte[inFile.available()];
			inFile.read(str);
			String text = new String(str);
			areqDefaultJson = new JSONObject(text);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.codehaus.jettison.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		flowEngine(flowDataMap);
		
		APIUtil util = new APIUtil();
		util.printJsonsToExtent(testcaseid, jsonOutputMap);

		return result;
	}

	private void flowEngine(LinkedHashMap<String, String> flowDataMap) {

		for (String key : flowDataMap.keySet()) {

			String value = flowDataMap.get(key);
			JSONObject json = null;
			
			String strActionFullList = null;
			try {
				json = new JSONObject(value);
				
				strActionFullList = json.getString("Action");
				String[] actionArray = strActionFullList.split(",");
				for (String action : actionArray) {
					if (action.equalsIgnoreCase("POST")) {
						
						String strMessageType = null;
						strMessageType = json.getString("MessageType");
						
						if (strMessageType.equalsIgnoreCase("AREQ")) {

							
							strThreeDSServerTransID = String.valueOf(UUID.randomUUID());
							strDsTransID = String.valueOf(UUID.randomUUID());
							persistentDataMap.put("threeDSServerTransID", strThreeDSServerTransID);
							persistentDataMap.put("dsTransID", strDsTransID);
							
							
							String strModifyJson = json.getString("Post");
							String strExtentMessage = json.getString("ExtentReport");
							logJsonToExtentReport(strExtentMessage, strModifyJson);
							AreqFlow areqFlow = new AreqFlow();
							JSONObject modifyJson = new JSONObject(strModifyJson);
							String strAreq = areqFlow.performBrowserAreqFlow(strCardNumber, strAreqBrowserUrl, areqDefaultJson, modifyJson,
									persistentDataMap,strExtentMessage);
							jsonOutputMap.put("AREQ Json", strAreq);
						}
						else if (strMessageType.equalsIgnoreCase("CreqBrowser1")) {

							creqCount = 1;
							String strModifyJson = json.getString("Post");
							String strExtentMessage = json.getString("ExtentReport");
							logJsonToExtentReport(strExtentMessage, strModifyJson);
							CreqBrowserFlow creqFlow = new CreqBrowserFlow();
							JSONObject creq1Json = new JSONObject(strModifyJson);
							creqFlow.performBrowserCreq1Flow(creq1Json, persistentDataMap,jsonOutputMap);
							
						}
						
						else if (strMessageType.equalsIgnoreCase("CreqBrowser2")) {

							creqCount++;
							String strModifyJson = json.getString("Post");
							String strExtentMessage = json.getString("ExtentReport");
							logJsonToExtentReport(strExtentMessage, strModifyJson);
							CreqBrowserFlow creqFlow = new CreqBrowserFlow();
							JSONObject creq1Json = new JSONObject(strModifyJson);
							creqFlow.performBrowserCreq2Flow(creq1Json, persistentDataMap,jsonOutputMap,creqCount);
							
						}
						
						
					}
					
					if(action.equalsIgnoreCase("Validate")) {
						String strMessageType = null;
						strMessageType = json.getString("MessageType");
						if (strMessageType.equalsIgnoreCase("ARES")) {
							
							String strValidateJson = json.getString("Validate");
							String strExtentMessage = json.getString("ExtentReport");
							AresFlow aresFlow = new AresFlow();
							String strAres = aresFlow.validateAresJson(strValidateJson, persistentDataMap,strExtentMessage,result);
							jsonOutputMap.put("ARES Json", strAres);
							if(!result.isTestCaseStatus()){
								return;
							}
						}
						
						if (strMessageType.equalsIgnoreCase("Cres3Browser")) {
							
							String strValidateJson = json.getString("Validate");
							String strExtentMessage = json.getString("ExtentReport");
							CresFlow cresFlow = new CresFlow();
							String strCres = cresFlow.validateCres3BrowserJson(strValidateJson, persistentDataMap,strExtentMessage,result);
							if(!result.isTestCaseStatus()){
								return;
							}
						}
					}
					
					
					if (action.equalsIgnoreCase("ValidateDB")) {
						String strValidateDBJson = json.getString("ValidateDB");
						String strExtentMessage = json.getString("ExtentReport");
						ValidateDB dbValidation = new ValidateDB();
						dbValidation.validateDB(strValidateDBJson, persistentDataMap, strExtentMessage, result);
						if(!result.isTestCaseStatus()){
							return;
						}
					}

					if (action.equalsIgnoreCase("Sleep")) {
						String strSleepJson = json.getString("Sleep");
						String strExtentMessage = json.getString("ExtentReport");
						JSONObject jsonsleep = new JSONObject(strSleepJson);
						String strSleep = jsonsleep.getString("TimeInSec");
						int sleep = Integer.parseInt(strSleep);
						APIUtil apiUtil = new APIUtil();
						apiUtil.sleep(sleep, strExtentMessage);
					}

					if (action.equalsIgnoreCase("Cancel")) {
						String strModifyJson = json.getString("Cancel");
						String strExtentMessage = json.getString("ExtentReport");
						logJsonToExtentReport(strExtentMessage, strModifyJson);
						CreqNativeFlow creqFlow = new CreqNativeFlow();
						JSONObject creqmodifyJson = new JSONObject(strModifyJson);
						creqCount++;
						creqFlow.performCancelTransaction(creqmodifyJson, persistentDataMap, creqCount, jsonOutputMap);

					}

					if (action.equalsIgnoreCase("ValidateRA_LVT")) {

						String strValidateRA_LVTJson = json.getString("ValidateRA_LVT");
						String strExtentMessage = json.getString("ExtentReport");
						ValidateRA validateRA = new ValidateRA();
						validateRA.validateRA_LVT(strValidateRA_LVTJson, persistentDataMap, strExtentMessage);

					}
					
					if (action.equalsIgnoreCase("ValidateRA_TB")) {

						String strValidateRA_TBJson = json.getString("ValidateRA_TB");
						String strExtentMessage = json.getString("ExtentReport");
						ValidateRA validateRA = new ValidateRA();
						validateRA.validateRA_TB(strValidateRA_TBJson, persistentDataMap, strExtentMessage,result);
						if(!result.isTestCaseStatus()){
							return;
						}

					}

					if (action.equalsIgnoreCase("DISABLE_TRUSTED_BENEFICIARY")) {

						String strTBJson = json.getString("DISABLE_TRUSTED_BENEFICIARY");
						String strExtentMessage = json.getString("ExtentReport");
						TrustedBeneficiary enableTB = new TrustedBeneficiary();
						enableTB.trustedBeneficiaryEnablement(strTBJson, persistentDataMap, strExtentMessage,result);
						if(!result.isTestCaseStatus()){
							return;
						}

					}
					
					if (action.equalsIgnoreCase("Check_RA_Triggered_Rules")) {

						String strValidateRA_RulesJson = json.getString("Check_RA_Triggered_Rules");
						String strExtentMessage = json.getString("ExtentReport");
						ValidateRA validateRA = new ValidateRA();
						validateRA.validateRARulesTriggered(strValidateRA_RulesJson, persistentDataMap, strExtentMessage,result);
						if(!result.isTestCaseStatus()){
							return;
						}

					}
					
					if (action.equalsIgnoreCase("PersistData")) {

						String strDataJson = json.getString("PersistData");
						PersistData persist = new PersistData();
						persist.persistData(strDataJson, persistentDataMap);
						
					}
					
					if (action.equalsIgnoreCase("PersistDataFromDB")) {

						String strDataJson = json.getString("PersistDataFromDB");
						String strExtentMessage = json.getString("ExtentReport");
						PersistData persist = new PersistData();
						persist.persistDataFromDB(strDataJson, persistentDataMap,strExtentMessage);
						
					}
					
					
				}

			} catch (org.codehaus.jettison.json.JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static byte[] read(String path) {
		try {
			return java.util.Base64.getDecoder().decode(Files.readAllBytes(new File(path).toPath()));
		} catch (IOException e) {
			throw new RuntimeException("Failed to read data from file: " + path, e);
		}
	}
	
	private void logJsonToExtentReport(String extentMessage, String strJson){
		
		APIUtil apiUtil = new APIUtil();
		String strPrettyJsonString = apiUtil.getPrettyJson(strJson);
		System.out.println("Modify Json: " + strPrettyJsonString + "\n\n\n\n");
		
		if(extentMessage!=null){
			StringBuilder strExtent = new StringBuilder();
			strExtent.append(extentMessage+"<br>");
			strPrettyJsonString = strPrettyJsonString.replaceAll(" ", "&emsp;");
			strPrettyJsonString = strPrettyJsonString.replaceAll("(\r\n|\n)", "<br />");
			strExtent.append(strPrettyJsonString);
			ReportLogger.logInfo(Status.INFO, strExtent.toString());
		}
	}

}
