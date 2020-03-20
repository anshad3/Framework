package com.ca.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;

import com.api.base.JoseServiceJWS;
import com.aventstack.extentreports.Status;
import com.base.reports.ReportLogger;
import com.common.util.TdsQueries;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jcraft.jsch.JSchException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.A128GCMEncrypter;
import com.utility.security.CryptoService;

import ca.paysec.commons.cryptoclient.JweDecryptResponse;
import ca.paysec.commons.error.ApplicationRuntimeException;
import ca.paysec.commons.error.ErrorInfo;
import ca.tds2.core.exception.ErrorCommonNew;
import ca.tds2.model.SessionKeys;

public class APIUtil {

	public String resoveSymbol(String valuetoBeExtracted, Map<String, String> persistentDataMap) {

		if (valuetoBeExtracted.contains("ENCRYPT_WITH_BANKKEY")) {

			String strValue = getInBetweenString(valuetoBeExtracted, "ENCRYPT_WITH_BANKKEY(", ")");
			System.out.println(strValue);

			if (strValue.contains("**"))
				strValue = resoveSymbol(strValue, persistentDataMap);

			CryptoService crypto = new CryptoService();
			String encString = crypto.encryptCardRelated(strValue, persistentDataMap.get("Issuer"));
			System.out.println(encString);
			return encString;
		} else if (valuetoBeExtracted.contains("ENCRYPT_WITH_MASTERKEY")) {

		} else if (valuetoBeExtracted.contains("**SUBSTRING")) {

			String strValue = getInBetweenString(valuetoBeExtracted, "SUBSTRING(", ")");
			System.out.println(strValue);
			String[] substringArray = strValue.split(",");
			if (substringArray.length != 3) {
				Assert.fail("SubString Action failed because of Invalid String : " + valuetoBeExtracted);
			}
			String strOriginal = substringArray[0];
			if (strOriginal.contains("**"))
				strOriginal = resoveSymbol(strOriginal, persistentDataMap);

			String strBeginIndex = substringArray[1];
			String strNoOfChars = substringArray[2];
			int beginIndex = Integer.parseInt(strBeginIndex);
			int noOfChars = Integer.parseInt(strNoOfChars);
			if (beginIndex <= 0) {
				Assert.fail("SubString Action failed because of Invalid Begin Index :" + beginIndex
						+ " in the String : " + valuetoBeExtracted);
			}
			int totalCharExtracted = beginIndex - 1 + noOfChars;
			if (totalCharExtracted > strOriginal.length()) {
				Assert.fail("SubString Action failed because of Index out of Bound. Please check original String is  :"
						+ strOriginal + " and sub string action is : " + valuetoBeExtracted);
			}

			String strSubString = strOriginal.substring(beginIndex - 1, totalCharExtracted);
			return strSubString;
		} else if (valuetoBeExtracted.equalsIgnoreCase("**OTP**")) {

			String strIssuer = persistentDataMap.get("Issuer");
			String strAcsTransID = persistentDataMap.get("acsTransID");
			TdsQueries tdsQuery = new TdsQueries();
			String strOTP = null;
			try {
				strOTP = tdsQuery.getOTPValue(strAcsTransID, strIssuer);
			} catch (SQLException | JSchException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return strOTP;

		}

		String strValueWithoutAsterix = getInBetweenString(valuetoBeExtracted, "**", "**");
		String strExtracted = null;
		if (persistentDataMap.containsKey(strValueWithoutAsterix)){
			strExtracted = persistentDataMap.get(strValueWithoutAsterix);
			System.out.println("Original String for Extraction -> " + valuetoBeExtracted);
			String replacedString = valuetoBeExtracted.replace("**"+strValueWithoutAsterix+"**", strExtracted);
			valuetoBeExtracted = replacedString;
			System.out.println("String After Extraction -> " + valuetoBeExtracted);
		}
				
		
		return strExtracted;

	}

	public void resoveSymbolsInMap(Map<String, String> inputMap, Map<String, String> persistentDataMap) {

		System.out.println();
		for (String key : inputMap.keySet()) {
			String value = (String)inputMap.get(key);
		 if(value!=null&&value.equalsIgnoreCase("null")) {
			 inputMap.put(key, null);
			 
		 }
		 else if (value!=null&&value.contains("**")) {
				value = resoveSymbol(value, persistentDataMap);
				inputMap.put(key, value);
			}

		}

	}

	public void sleep(int secs, String extentMessage) {

		ReportLogger.logInfo(Status.INFO, extentMessage);
		int millsec = secs * 1000;
		try {
			Thread.sleep(millsec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public HttpResponse<String> postJson(String postUrl, String postJson) {
		
		String keySTorePath = System.getProperty("user.dir")+"\\KeysToUpload\\OpenSift\\server-certstore.p12";
		//char [] password= {'d','o','s','t','1','2','3','4'};
		//Unirest.config().clientCertificateStore(keySTorePath, "dost1234");
		
		SSLContext sslcontext=null;
		
		try {
			sslcontext = SSLContexts.custom()
			        .loadTrustMaterial(new TrustSelfSignedStrategy())
			        .build();
		} catch (KeyManagementException | NoSuchAlgorithmException
				| KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
		CloseableHttpClient httpclient = HttpClients.custom()
                         .setSSLSocketFactory(sslsf)
                         .build();
		
		Unirest.setHttpClient(httpclient);
		
		
		
		HttpResponse<String> response = null;
		try {
			response = Unirest.post(postUrl).header("cache-control", "no-cache")
					.header("content-type", "application/json")
					.header("postman-token", "ebf61ce1-b994-f110-bd83-705483bd5d69").body(postJson).asString();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return response;
	}

	public HttpResponse<String> postCreqBrowser1Json(String postUrl, String postJson) {
		
		
		String keySTorePath = System.getProperty("user.dir")+"\\KeysToUpload\\OpenSift\\server-certstore.p12";
		//char [] password= {'d','o','s','t','1','2','3','4'};
		//Unirest.config().clientCertificateStore(keySTorePath, "dost1234");
		
		SSLContext sslcontext=null;
		
		try {
			sslcontext = SSLContexts.custom()
			        .loadTrustMaterial(new TrustSelfSignedStrategy())
			        .build();
		} catch (KeyManagementException | NoSuchAlgorithmException
				| KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
		CloseableHttpClient httpclient = HttpClients.custom()
                         .setSSLSocketFactory(sslsf)
                         .build();
		
		Unirest.setHttpClient(httpclient);
		
		HttpResponse<String> response=null;
		try {
			response = Unirest.post(postUrl)
					.header("content-type", "application/x-www-form-urlencoded")
					.header("cache-control", "no-cache")
					.header("postman-token", "ebf61ce1-b994-f110-bd83-705483bd5d69").body(postJson).asString();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	public HttpResponse<String> postCreqAppJson(String postUrl, String postJson) {
		
		String keySTorePath = System.getProperty("user.dir")+"\\KeysToUpload\\OpenSift\\server-certstore.p12";
		//char [] password= {'d','o','s','t','1','2','3','4'};
		//Unirest.config().clientCertificateStore(keySTorePath, "dost1234");
		
		SSLContext sslcontext=null;
		
		try {
			sslcontext = SSLContexts.custom()
			        .loadTrustMaterial(new TrustSelfSignedStrategy())
			        .build();
		} catch (KeyManagementException | NoSuchAlgorithmException
				| KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
		CloseableHttpClient httpclient = HttpClients.custom()
                         .setSSLSocketFactory(sslsf)
                         .build();
		
		Unirest.setHttpClient(httpclient);
		
		
		HttpResponse<String> response=null;
		try {
			response = Unirest.post(postUrl).header("content-type", "application/jose; charset=utf-8")
					.body(postJson).asString();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * response = Unirest.post(
		 * "http://10.253.20.101:1080/acs/api/tds2/txn/app/v1/creq")
		 * .header("content-type",
		 * "application/jose; charset=utf-8").body(creqbody).asString();
		 */

		return response;
	}

	public void logJsonToExtentReportInHtml(String extentMessage, String strJson, String testcaseId) {

		APIUtil apiUtil = new APIUtil();
		String strPrettyJsonString = apiUtil.getPrettyJson(strJson);
		String strAst = "*************************************************************************************************************************************************";
		StringBuilder strExtent = new StringBuilder();
		if (extentMessage != null) {

			strExtent.append(strAst + "<br>");
			strExtent.append("<h2 style=\"display:inline;\">");
			strExtent
					.append("&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;"
							+ extentMessage);
			strExtent.append("</h2><br>");
			strExtent.append(strAst);
			strExtent.append("<br><br>");

			strPrettyJsonString = strPrettyJsonString.replaceAll(" ", "&emsp;");
			strPrettyJsonString = strPrettyJsonString.replaceAll("(\r\n|\n)", "<br />");
			strExtent.append(strPrettyJsonString);
			strExtent.append("<br><br>");
			strExtent.append(strAst);
			// ReportLogger.logInfo(Status.INFO, strExtent.toString());
		}

		CommonUtil util = new CommonUtil();
		String strExtentOutput = util.generateHTMLReportForText(testcaseId, extentMessage, strExtent.toString());
		ReportLogger.logInfo(Status.INFO, strExtentOutput);
	}

	public void printJsonsToExtent(String testCaseName, Map<String, String> jsonMap) {

		String strAst = "*************************************************************************************************************************************************";
		StringBuilder strJsons = new StringBuilder();

		for (String key : jsonMap.keySet()) {

			strJsons.append(strAst + "<br>");
			strJsons.append("<h2 style=\"display:inline;\">");
			strJsons.append(
					"&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;" + key);
			strJsons.append("</h2><br>");
			strJsons.append(strAst);
			strJsons.append("<br><br>");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = null;
			String strprettyJsonString = null;
			je = jp.parse(jsonMap.get(key));
			strprettyJsonString = gson.toJson(je);
			strprettyJsonString = strprettyJsonString.replaceAll(" ", "&emsp;");
			strprettyJsonString = strprettyJsonString.replaceAll("(\r\n|\n)", "<br />");
			strJsons.append(strprettyJsonString);
			strJsons.append("<br><br>");
		}

		CommonUtil util = new CommonUtil();
		String strExtent = util.generateHTMLReportForText(testCaseName, "Final JSONs", strJsons.toString());
		ReportLogger.logInfo(Status.INFO, strExtent);

	}

	public String getPrettyJson(JSONObject jsonObj) {

		String strPrettyJsonString = null;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = null;

		je = jp.parse(jsonObj.toString());
		strPrettyJsonString = gson.toJson(je);

		return strPrettyJsonString;
	}

	public String getPrettyJson(String strJson) {

		String strPrettyJsonString = null;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = null;

		je = jp.parse(strJson);
		strPrettyJsonString = gson.toJson(je);

		return strPrettyJsonString;
	}

	public String encryptCReqJSON(SessionKeys sessionKeys, String value, String acsAccountId, int sdkCounterStoA)
			throws ApplicationRuntimeException {
		System.out.println("Session Key : " + sessionKeys.getSessionKey());
		System.out.println("Value to Encrypted : " + value);
		System.out.println("ACS TXN ID : " + acsAccountId);
		System.out.println("sdkCounterStoA : " + sdkCounterStoA);
		byte[] sessionKey = org.apache.commons.codec.binary.Base64.decodeBase64(sessionKeys.getSessionKey().getBytes());
		// byte[] encKeySDK = Arrays.copyOfRange(sessionKey, 48, 64);

		value = jweEncryptA128GCM(value, acsAccountId, sessionKey, sdkCounterStoA);

		return value;
	}

	public String jweEncryptA128GCM(String data, String kid, byte[] sessionKey, int counter)
			throws ApplicationRuntimeException {
		try {

			JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128GCM).keyID(kid).build();
			Payload payload = new Payload(data);
			JWEObject jweObject = new JWEObject(header, payload);

			jweObject.encrypt(new A128GCMEncrypter(new SecretKeySpec(Arrays.copyOfRange(sessionKey, 0, 16), "AES"),
					createIVForGCM(counter)));

			return jweObject.serialize();
		} catch (Exception e) {
			throw new ApplicationRuntimeException(ErrorInfo.newErrorInfo(ErrorCommonNew.JOSE_EXCEPTION), e);
		}
	}

	private static byte[] createIVForGCM(int counter) {
		byte[] iv = new byte[12];
		iv[11] = (byte) counter;
		for (int i = 0; i < 11; i++) {
			iv[i] = (byte) 255;// random.nextInt(256);
		}

		return iv;
	}

	private static byte[] read(String path) {
		try {
			return java.util.Base64.getDecoder().decode(Files.readAllBytes(new File(path).toPath()));
		} catch (IOException e) {
			throw new RuntimeException("Failed to read data from file: " + path, e);
		}
	}

	public static String getKidFromHeader(String compactStr) {
		JWEObject jweObject;
		try {
			jweObject = JWEObject.parse(compactStr);
			return jweObject.getHeader().getKeyID();
		} catch (ParseException e) {

		}
		return null;
	}

	public String encryptJsonForAPPFlow(String prettyCreqJsonString, HashMap<String, String> persistentDataMap) {
		// TODO Auto-generated method stub

		SessionKeys sessionKeys = new SessionKeys();
		sessionKeys.setSessionKey(persistentDataMap.get("SessionKey"));

		byte[] bsessionKey = org.apache.commons.codec.binary.Base64
				.decodeBase64(sessionKeys.getSessionKey().getBytes());

		String encryptedJson = encryptCReqJSON(sessionKeys, prettyCreqJsonString, persistentDataMap.get("acsTransID"),
				0);

		return encryptedJson;
	}

	public String decryptCResJSON(String value, HashMap<String, String> persistentDataMap)
			throws ApplicationRuntimeException {

		byte[] sessionKey = org.apache.commons.codec.binary.Base64
				.decodeBase64(persistentDataMap.get("SessionKey").getBytes());
		// byte[] encKeySDK = Arrays.copyOfRange(sessionKey, 0, 32);
		JoseServiceJWS jwe = new JoseServiceJWS();
		JweDecryptResponse res = null;
		try {
			res = jwe.jweDecrypt(value, sessionKey);
		} catch (Exception e) {
			JweDecryptResponse decryptResponse = new JweDecryptResponse();
			decryptResponse.setData(value);
			return decryptResponse.getData();
		}
		if (res == null) {
			Assert.fail("Error While Decrption");
		}
		return res.getData();

	}

	public String getInBetweenString(String strOriginal, String strFirstOccurence, String strLastOccurence) {

		int start = strOriginal.indexOf(strFirstOccurence) + strFirstOccurence.length();
		int end = strOriginal.lastIndexOf(strLastOccurence);
		if (start > -1 && end > start) {
			System.out.println(strOriginal.substring(start, end));
			return strOriginal.substring(start, end);
		}

		Assert.fail("Error While finding In between String of Original String : " + strOriginal
				+ "  First Occurence String : " + strFirstOccurence + "  last Occurence String : " + strLastOccurence);
		return null;
	}
	
	public String generateQuery(String strTableName, Map<String, String> valuesMap, HashMap<String, String> whereMap) {

		StringBuilder query = new StringBuilder();
		query.append("SELECT ");

		String prefix = "";
		for (String selectField : valuesMap.keySet()) {
			query.append(prefix);
			prefix = ",";
			query.append(selectField);
		}
		query.append(" FROM " + strTableName + " WHERE ");
		prefix = "";
		for (String whereClause : whereMap.keySet()) {
			query.append(prefix);
			prefix = " AND ";
			query.append(whereClause + "='" + whereMap.get(whereClause) + "'");
		}

		return query.toString();
	}

}
