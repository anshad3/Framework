package com.utility.security;

import static io.restassured.RestAssured.given;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Random;

import org.json.simple.JSONObject;

import com.base.BaseSuite;
import com.common.util.TdsQueries;

public class CryptoService {

	String strMasterKey = null;
	String strEncrptionType = null;

	public CryptoService() {

		strMasterKey = BaseSuite.getCAPropertyValue("masterkey");
		strEncrptionType = BaseSuite.getCAPropertyValue("encType");
	}

	public String decryptData(CryptoPojo pojo) {

		String cryptoUrl = BaseSuite.getCAPropertyValue("CryptoUrl");
		String decryptUrl = cryptoUrl + "decrypt";
		JSONObject requestParams = new JSONObject();
		requestParams.put("storage", pojo.getStorage());
		requestParams.put("algorithm", pojo.getAlgorithm());
		requestParams.put("key", pojo.getKey());
		requestParams.put("cipher", pojo.getData());
		Random rnd = new Random();
		int n = 100000000 + rnd.nextInt(900000000);
		String txnid = String.valueOf(n);
		Response response=postAndGetResponse("transaction-id",txnid ,requestParams.toJSONString(),decryptUrl);
		
		JsonPath a = response.jsonPath();

		String decryptedData = response.jsonPath().get("data");

		return decryptedData;
	}

	public String encryptData(CryptoPojo pojo) {

		String cryptoUrl = BaseSuite.getCAPropertyValue("CryptoUrl");
		String encryptUrl = cryptoUrl + "encrypt";
		JSONObject requestParams = new JSONObject();
		requestParams.put("storage", pojo.getStorage());
		requestParams.put("algorithm", pojo.getAlgorithm());
		requestParams.put("key", pojo.getKey());
		requestParams.put("data", pojo.getData().trim());
		Random rnd = new Random();
		int n = 100000000 + rnd.nextInt(900000000);
		String txnid = String.valueOf(n);
		
		Response response=postAndGetResponse("transaction-id",txnid ,requestParams.toJSONString(),encryptUrl);
		
		// if this works
		
		String encryptedData = response.getBody().jsonPath().get("cipher");
		//String encryptedData = response.jsonPath().get("cipher");

		return encryptedData;
	}

	private Response postAndGetResponse(String headerKey, Object headerObject,
			String body, String urlToBePosted) {
		Response response = null;
		try{
		
		response=given().header(headerKey, headerObject).body(body).when().relaxedHTTPSValidation()
		.post(urlToBePosted);
		}catch(Exception e){
			System.err.println("Got Exception: Message:"+e.getMessage());
			e.printStackTrace();
		}
		
		if (response == null) {
			System.err.println("Response is null");
			return null;
		}
		System.out.println("Sunil:Debugging the response is:"+response.getBody().asString());
		return response;
	}

	public synchronized String encryptCardRelated(String strToBeEncrypted, String strIssuerName) {

		Long threadId = new Long(Thread.currentThread().getId());
		System.out.println("Entering encryptCardRelated method for thread id: "+threadId+ " and string To Be Encrypted : "+strToBeEncrypted+" for Issuer Name : "+strIssuerName);
		TdsQueries td = new TdsQueries();
		String strEncryptedBankKey = td.getBankKeyWithIssuerName(strIssuerName);

		CryptoPojo bankKeyPojo = new CryptoPojo(strEncrptionType, "3DES", strMasterKey, strEncryptedBankKey);
		String strPlainBankKey = decryptData(bankKeyPojo);

		CryptoPojo cardEncryptionPojo = new CryptoPojo(strEncrptionType, "3DES", strPlainBankKey, strToBeEncrypted);
		String strEncryptedValue = encryptData(cardEncryptionPojo);
		System.out.println("End of encryptCardRelated method for thread id: "+threadId+ " and string To Be Encrypted : "+strToBeEncrypted+" for Issuer Name : "+strIssuerName);
		return strEncryptedValue;
	}
	
	
	public synchronized String decryptCardRelated(String encryptedData, String strIssuerName) {
		
		Long threadId = new Long(Thread.currentThread().getId());
		System.out.println("Entering decryptCardRelated method for thread id: "+threadId+ " and string To Be Decrypted : "+encryptedData+" for Issuer Name : "+strIssuerName);

		TdsQueries td = new TdsQueries();
		String strEncryptedBankKey = td.getBankKeyWithIssuerName(strIssuerName);

		CryptoPojo bankKeyPojo = new CryptoPojo(strEncrptionType, "3DES", strMasterKey, strEncryptedBankKey);
		String strPlainBankKey = decryptData(bankKeyPojo);

		CryptoPojo cardEncryptionPojo = new CryptoPojo(strEncrptionType, "3DES", strPlainBankKey, encryptedData);
		String strPlainData = decryptData(cardEncryptionPojo);
		
		System.out.println("End of decryptCardRelated method for thread id: "+threadId+ " and string To Be Decrypted : "+encryptedData+" for Issuer Name : "+strIssuerName);

		return strPlainData;
	}

	

}
