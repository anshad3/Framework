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

public class APINativeFlowEngine {

	HashMap<String, String> persistentDataMap = new HashMap<String, String>();
	LinkedHashMap<String, String> flowDataMap = new LinkedHashMap<String, String>();
	LinkedHashMap<String, String> jsonOutputMap = new LinkedHashMap<String, String>();
	String strAreqNativeUrl = BaseSuite.getCAPropertyValue("AreqNativeUrl");
	String strCreqNativeUrl = BaseSuite.getCAPropertyValue("CreqNativeUrl");
	String strAreqDefaultJson = null;
	JSONObject areqDefaultJson = null;
	String pubKeyString = null;
	PrivateKey privateKey = null;
	String strSdkTransID = null;
	String strThreeDSServerTransID = null;
	String strDsTransID = null;
	String strCardNumber = null;
	String testCaseStatus = "PASS";
	Integer creqCount = new Integer(1);
	APIResult result = new APIResult();

	public APIResult performNativeAndroidTxnFlow(Map<String, String> testCaseData) {

		result.setTestCaseStatus(true);
		String testcaseid = testCaseData.get("TestCaseID");
		String strIssuerName = testCaseData.get("Issuer");
		String strMessageVersion = testCaseData.get("MessageVersion");
		if (strMessageVersion == null || strMessageVersion.length() == 0
				|| !(strMessageVersion.equalsIgnoreCase("2.1.0") || strMessageVersion.equalsIgnoreCase("2.2.0"))) {
			strMessageVersion = "2.1.0";
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
				if (!(testCaseData.get(key) == null || testCaseData.get(key).trim().length() == 0))
					flowDataMap.put(key, testCaseData.get(key));
			}
		}

		Path pripath = Paths.get("APIKeys/prikey");
		Path priabsolutePath = pripath.toAbsolutePath().normalize();

		Path pubpath = Paths.get("APIKeys/pubkey");
		Path pubabsolutePath = pubpath.toAbsolutePath().normalize();

		String X, Y;
		byte[] privateKeyBytes = read(priabsolutePath.toString());
		byte[] publicKeyBytes = read(pubabsolutePath.toString());

		PublicKey publicKey = null;

		KeyFactory kf;
		try {
			kf = KeyFactory.getInstance("EC");
			privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
			publicKey = (PublicKey) kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
			System.out.println(" Private Key : " + privateKey.toString());
			System.out.println(" Public Key : " + publicKey.toString());
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JWK jwk = new ECKey.Builder(Curve.P_256, (ECPublicKey) publicKey).privateKey((ECPrivateKey) privateKey).build();

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = null;
		try {
			map = mapper.readValue(jwk.toJSONString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		X = map.get("x");
		Y = map.get("y");

		pubKeyString = "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"" + X + "\",\"y\":\"" + Y + "\"}";
		persistentDataMap.put("sdkEphemPubKey", pubKeyString);

		System.out.println("");
		String folderPath = System.getProperty("user.dir");
		String strAreqDefaultPath = null;
		if (strMessageVersion.equalsIgnoreCase("2.2.0"))
			strAreqDefaultPath = BaseSuite.getCAPropertyValue("AppNativeDefaultJson_220");
		else
			strAreqDefaultPath = BaseSuite.getCAPropertyValue("AppNativeDefaultJson_210");

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
							strSdkTransID = String.valueOf(UUID.randomUUID());
							persistentDataMap.put("threeDSServerTransID", strThreeDSServerTransID);
							persistentDataMap.put("dsTransID", strDsTransID);
							persistentDataMap.put("sdkTransID", strSdkTransID);
							
							String strModifyJson = json.getString("Post");
							String strExtentMessage = json.getString("ExtentReport");
							logJsonToExtentReport(strExtentMessage, strModifyJson);
							AreqFlow areqFlow = new AreqFlow();
							JSONObject modifyJson = new JSONObject(strModifyJson);
							String strAreq = areqFlow.performNativeAreqFlow(strCardNumber, strAreqNativeUrl,
									areqDefaultJson, modifyJson, persistentDataMap, strExtentMessage);
							jsonOutputMap.put("AREQ Json", strAreq);
						} else if (strMessageType.equalsIgnoreCase("CreqNative1")) {

							String strModifyJson = json.getString("Post");
							String strExtentMessage = json.getString("ExtentReport");
							logJsonToExtentReport(strExtentMessage, strModifyJson);
							CreqNativeFlow creqFlow = new CreqNativeFlow();
							JSONObject creq1Json = new JSONObject(strModifyJson);
							creqCount = 1;
							creqFlow.performNativeCreq1Flow(creq1Json, persistentDataMap, creqCount, privateKey,
									jsonOutputMap);

						}

						else if (strMessageType.equalsIgnoreCase("CreqNative2")) {

							String strModifyJson = json.getString("Post");
							String strExtentMessage = json.getString("ExtentReport");
							logJsonToExtentReport(strExtentMessage, strModifyJson);
							CreqNativeFlow creqFlow = new CreqNativeFlow();
							JSONObject creq1Json = new JSONObject(strModifyJson);
							creqCount++;
							creqFlow.performNativeCreqSubsequentFlow(creq1Json, persistentDataMap, creqCount,
									jsonOutputMap);

						}
					}

					if (action.equalsIgnoreCase("Validate")) {
						String strMessageType = null;
						strMessageType = json.getString("MessageType");
						if (strMessageType.equalsIgnoreCase("ARES")) {

							String strValidateJson = json.getString("Validate");
							String strExtentMessage = json.getString("ExtentReport");
							AresFlow aresFlow = new AresFlow();
							String strAres = aresFlow.validateAresJson(strValidateJson, persistentDataMap,
									strExtentMessage, result);
							jsonOutputMap.put("ARES Json", strAres);
							if(!result.isTestCaseStatus()){
								return;
							}
						}

						if (strMessageType.equalsIgnoreCase("CRES")) {

							String strValidateJson = json.getString("Validate");
							String strExtentMessage = json.getString("ExtentReport");
							CresFlow cresFlow = new CresFlow();
							String strCres = cresFlow.validateCresJson(strValidateJson, persistentDataMap,
									strExtentMessage, result);
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

	private void logJsonToExtentReport(String extentMessage, String strJson) {

		APIUtil apiUtil = new APIUtil();
		String strPrettyJsonString = apiUtil.getPrettyJson(strJson);
		System.out.println("Modify Json: " + strPrettyJsonString + "\n\n\n\n");

		if (extentMessage != null) {
			StringBuilder strExtent = new StringBuilder();
			strExtent.append(extentMessage + "<br>");
			strPrettyJsonString = strPrettyJsonString.replaceAll(" ", "&emsp;");
			strPrettyJsonString = strPrettyJsonString.replaceAll("(\r\n|\n)", "<br />");
			strExtent.append(strPrettyJsonString);
			ReportLogger.logInfo(Status.INFO, strExtent.toString());
		}
	}

}
