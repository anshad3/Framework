package com.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import oracle.sql.CLOB;

import org.testng.Assert;

import com.aventstack.extentreports.Status;
import com.base.reports.ReportLogger;
import com.ca.util.LogMode;
import com.db_connection.util.DBConnections;
import com.db_connection.util.DBConnections.DBEnum;
import com.jcraft.jsch.JSchException;
import com.utility.security.CryptoService;

public class TdsQueries {

	public int getMaxAuthtries(String strCardNumber, String strIssuerName) {
		int range_ID = getRangeID(strCardNumber, strIssuerName);
		String queryString = "select MaxAuthTries from ARBRANDINFO where RangeID=" + range_ID + "";
		List<Map<String, Object>> result = DBConnections.executeQueryIn3DSDB(queryString);
		int value = 0;

		for (int i = 0; i < result.size(); i++) {

			for (Map.Entry<String, Object> entry : result.get(i).entrySet()) {
				String key = entry.getKey();
				System.out.println(key);
				value = ((BigDecimal) entry.getValue()).intValue();
				System.out.println(value);
				System.out.println("in for loop");
			}

		}
		return value;
	}

	public int getOTPMaxChallengeAttempts(String strCardNumber, String strIssuerName) {
		int range_ID = getRangeID(strCardNumber, strIssuerName);
		String queryString = "select ATTRIBUTEVALUE from ARISSUERCONFIG where ATTRIBUTENAME='TD_OTP_MAX_CHALLENGE_ATTEMPTS' and Rangeid="
				+ +range_ID + "";
		System.out.println("max attempts query: " + queryString);
		List<Map<String, Object>> result = DBConnections.executeQueryIn3DSDB(queryString);
		int otpMaxChallengeAttempt = 0;

		for (int i = 0; i < result.size(); i++) {

			for (Map.Entry<String, Object> entry : result.get(i).entrySet()) {
				String key = entry.getKey();
				System.out.println(key);
				String maxAttempt = (String) entry.getValue();
				otpMaxChallengeAttempt = Integer.valueOf(maxAttempt);
				System.out.println("Max OTP challenge attempts:" + otpMaxChallengeAttempt);
				System.out.println("in for loop");
			}

		}
		return otpMaxChallengeAttempt;
	}

	public int getRangeID(String strCardNumber, String strIssuerName) {
		/*
		 * Encryptor3DS enc = new Encryptor3DS(); String encCardNumber =
		 * enc.encryptString(strCardNumber, strIssuerName);
		 */
		Long threadId = new Long(Thread.currentThread().getId());
		System.out.println("Entering getRangeID method for thread id: " + threadId);

		CryptoService crypto = new CryptoService();
		String encCardNumber = crypto.encryptCardRelated(strCardNumber, strIssuerName);

		String queryString = "select RangeID from ARACCTHOLDERAUTH  where CARDNUMBER='" + encCardNumber + "'";
		List<Map<String, Object>> result = DBConnections.executeQueryIn3DSDB(queryString);
		int value = 0;

		for (int i = 0; i < result.size(); i++) {

			for (Map.Entry<String, Object> entry : result.get(i).entrySet()) {
				// String key = entry.getKey();
				// System.out.println(key);
				value = ((BigDecimal) entry.getValue()).intValue();
				// System.out.println(value);
				// System.out.println("in for loop");
			}

		}
		System.out.println("End of getRangeID method for thread id: " + threadId);
		return value;
	}

	public String getRangeIDforResolvedSymbol(String strBeginRage, String strEndRange) throws SQLException {
		Connection con = DBConnections.get3DSDBConnection();
		Statement stmt = con.createStatement();
		String strRangeID = null;
		String query = "select rangeid from arbrandinfo" + " where beginrange between'" + strBeginRage + "' and '"
				+ strEndRange + "' or endrange between'" + strBeginRage + "' and '" + strEndRange + "'";
		try {

			System.out.println("Query = " + query);
			ResultSet res = stmt.executeQuery(query);

			while (res.next()) {
				strRangeID = res.getString(1);
				System.out.println("range id is>>>> " + strRangeID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strRangeID;

	}

	public int getNstrikes(String strCardNumber, String strIssuerName) {
		Encryptor3DS enc = new Encryptor3DS();
		String encCardNumber = enc.encryptString(strCardNumber, strIssuerName);

		String queryString = "select Nstrikes from ARACCTHOLDERAUTH where CARDNUMBER='" + encCardNumber + "'";
		List<Map<String, Object>> result = DBConnections.executeQueryIn3DSDB(queryString);
		int value = 0;

		for (int i = 0; i < result.size(); i++) {

			for (Map.Entry<String, Object> entry : result.get(i).entrySet()) {
				String key = entry.getKey();
				System.out.println(key);
				value = ((BigDecimal) entry.getValue()).intValue();
				System.out.println(value);
				System.out.println("in for loop");
			}

		}
		return value;
	}

	/**
	 * Description: This method gets RA_RISK_ADVICE from TD_TRANSACTION_LOG
	 * using 3DS_SERVER_TRANS_ID_AREQ as input by querying TM DB.
	 * 
	 * @param strAcsAccountID
	 *            (ACS_TRANS_ID_PK value as String)
	 * 
	 * @return RA_RISK_ADVICE (RA_RISK_ADVICE value as String)
	 * 
	 * @author veera04
	 */
	public String getRaAdviceFromTdTransactionLogTable(String strAcsAccountID) {

		String value = null;
		int count = 0;
		while (true) {

			try {
				Thread.sleep(2000);

				String strQuery = "SELECT RA_RISK_ADVICE FROM TD_TRANSACTION_LOG WHERE ACS_TRANS_ID_PK ='"
						+ strAcsAccountID + "'";
				System.out.println("Query to get risk advice for ACS txn id: " + strAcsAccountID + " is:" + strQuery);
				count++;

				List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
				Map.Entry<String, Object> entry = null;
				if (queryResult != null && !queryResult.isEmpty()) {
					entry = queryResult.get(0).entrySet().iterator().next();
					value = (String) entry.getValue();
				}
			} catch (Exception e) {
				System.out.println(
						"-----------------Exception in getRaAdviceFromTdTransactionLogTable-------------------");
				System.out.println("Exception : " + e.getMessage());
			} finally {

			}
			if (count == 10) {
				break;
			}
			if (value != null && value.length() > 0) {

				break;
			}
		}
		return value;
	}// end

	/**
	 * Description: This method gets Risk Integration from TD_SYSTEM_CONFIG by
	 * querying TM DB.
	 * 
	 * @return RA_RISK_ADVICE (Risk_Integration value(ENABLED/DISABLED) as
	 *         String)
	 * 
	 * @author veera04
	 */
	public String getRiskIntegrationFromTdSystemConfigTable() {

		String value = null;
		try {
			String strQuery = "SELECT PARAM_VALUE FROM TD_SYSTEM_CONFIG WHERE PARAM_NAME='risk_integration'";
			System.out.println("Query : " + strQuery);

			Map.Entry<String, Object> entry = null;
			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
			if (queryResult != null && !queryResult.isEmpty()) {
				entry = queryResult.get(0).entrySet().iterator().next();
				value = (String) entry.getValue();
			}
		} catch (Exception e) {
			System.out.println(
					"-----------------Exception in getRiskIntegrationFromTdSystemConfigTable-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		return value;
	}// end

	/**
	 * Description: This method gets RA_TXN_ID from TD_TRANSACTION_LOG by
	 * querying TM DB.
	 * 
	 * @return RA_TXN_ID (RA_TXN_ID value(Ex: 73:12072) as String)
	 * 
	 * @author veera04
	 */
	public String getRaTxnIdFromTdTransactionLogTable(String acsAccountID) {

		String value = null;
		try {
			String strQuery = "SELECT RA_TXN_ID from TD_TRANSACTION_LOG where ACS_TRANS_ID_PK='" + acsAccountID + "'";
			System.out.println("Query : " + strQuery);

			Map.Entry<String, Object> entry = null;
			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
			if (queryResult != null && !queryResult.isEmpty()) {
				entry = queryResult.get(0).entrySet().iterator().next();
				value = (String) entry.getValue();
			}
		} catch (Exception e) {
			System.out.println("-----------------Exception in getRaTxnIdFromTdTransactionLogTable-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		return value;
	}// end

	/**
	 * Description: This method gets RiskAdviceStatus Details from
	 * ARRFSYSAUDITLOG_3DSECURE by querying RA DB using RATxnID.Getting
	 * ADVICEID,SCORE,MATCHEDRULE,SESSIONID
	 * 
	 * @return HashMap<String, String> (RFADVICE as ADVICEID;RFRISKSCORE as
	 *         SCORE; RFRULEMATCH as MATCHEDRULE;RFTXNID as sessionid; value(Ex:
	 *         200) as String)
	 * 
	 * @author veera04
	 */
	public HashMap<String, String> getRaDbRiskAdviceDetails(String raTxnID) {

		String strQuery = "SELECT ADVICEID,SCORE,MATCHEDRULE,ACTION,SESSIONID FROM ARRFSYSAUDITLOG_3DSECURE WHERE SESSIONID='"
				+ raTxnID + "' AND TXNTYPE='1'";
		System.out.println("Query : " + strQuery);

		HashMap<String, String> raAdviceDetails = new HashMap<String, String>();
		List<Map<String, Object>> queryResult = DBConnections.executeQueryInRaDB(strQuery);
		for (int i = 0; i < queryResult.size(); i++) {
			for (Entry<String, Object> entryValues : queryResult.get(i).entrySet()) {
				String key = entryValues.getKey();
				String value = (String) entryValues.getValue().toString();
				raAdviceDetails.put(key, value);
			}
		}
		return raAdviceDetails;
	}// end

	/**
	 * Description: This method gets Allow CutOff Amount from TD_SYSTEM_CONFIG
	 * by querying TM DB.
	 * 
	 * @return Allow_Cutoff_Amount (Allow_Cutoff_Amount value(Ex: 200) as
	 *         String)
	 * 
	 * @author veera04
	 */

	public String getBankKeyWithIssuerName(String issuername) {

		String queryString = "select bankkey from arbankinfo where BANKNAME = '" + issuername + "'";
		List<Map<String, Object>> result = DBConnections.executeQueryIn3DSDB(queryString);
		String value = null;
		;

		for (int i = 0; i < result.size(); i++) {

			for (Map.Entry<String, Object> entry : result.get(i).entrySet()) {
				String key = entry.getKey();
				// System.out.println(key);
				value = (String) entry.getValue();
				// System.out.println(value);
				// System.out.println("in for loop");
			}

		}
		return value;
	}

	public int getCardStatus(String cardNumber, String issuerName) {
		Long threadId = new Long(Thread.currentThread().getId());

		System.out.println("Entering getCardStatus method for the card " + cardNumber + " for thread id: " + threadId);

		CryptoService crypto = new CryptoService();
		String encCardNumber = crypto.encryptCardRelated(cardNumber, issuerName);

		System.out.println("Encrypted card number ****" + encCardNumber);
		String queryString = "select ACTIVE from ARACCTHOLDERAUTH where CARDNUMBER='" + encCardNumber
				+ "' GROUP BY ACTIVE,CARDHOLDERNAME";
		System.out.println("Query to get card status for card number: " + cardNumber + " is: " + queryString
				+ " for thread id: " + threadId);
		ReportLogger.logInfo(Status.DEBUG,
				"Query to get card status for card number: " + cardNumber + " is: " + queryString, LogMode.DEBUG);
		List<Map<String, Object>> result = DBConnections.executeQueryIn3DSDB(queryString);
		int value = 0;

		for (int i = 0; i < result.size(); i++) {

			for (Map.Entry<String, Object> entry : result.get(i).entrySet()) {
				// String key = entry.getKey();
				// System.out.println(key);
				value = ((BigDecimal) entry.getValue()).intValue();
			}

		}
		System.out.println("Card number: " + cardNumber + " status is: " + value + " for thread id: " + threadId);
		System.out.println("End of getCardStatus method for thread id: " + threadId);
		return value;

	}

	/**
	 * Description: This method queries ProxyPan from ARACCTHOLDERAUTH using
	 * CardNumber and From ProxyPan value querying Attribute values(Contacts)
	 * from ARACCTHOLDERATTR and returns list by adding all Attribute values.
	 * 
	 * @param cardNumber
	 *            (16-19 digits Card Number(ex :'4243443320001804') as string)
	 * 
	 * @param strIssuerName
	 *            (Banks Name(ex :ICICI,HDFC) as string)
	 * 
	 * @return {@link ArrayList} (Object of Arraylist<String>,List of Contact
	 *         Values)
	 * 
	 * @author veera04
	 */
	// Currently adding encrypted value to list.Need to be changed once decrypt
	// is done.
	public ArrayList<String> getContactsFromTmDb(String cardNumber, String strIssuerName) {

		Long threadId = new Long(Thread.currentThread().getId());
		System.out.println("Entering getContactsFromTmDb method for thread id: " + threadId);
		String proxyPanValue = null;

		// Getting ProxyPan from ARACCTHOLDERAUTH using encrypted CardNumber
		/*
		 * Encryptor3DS enc = new Encryptor3DS(); String encryptedCardNumber =
		 * enc.encryptString(cardNumber, strIssuerName);
		 */

		CryptoService crypto = new CryptoService();
		String encryptedCardNumber = crypto.encryptCardRelated(cardNumber, strIssuerName);

		String strQuery = "SELECT PROXYPAN FROM ARACCTHOLDERAUTH WHERE CARDNUMBER='" + encryptedCardNumber + "'";
		System.out.println("Query : " + strQuery);
		Map.Entry<String, Object> entry = null;
		List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
		if (queryResult != null && !queryResult.isEmpty()) {
			entry = queryResult.get(0).entrySet().iterator().next();
			proxyPanValue = (String) entry.getValue();
		}
		// Getting Contacts count from ARACCTHOLDERATTR using proxyPanValue
		strQuery = "select EMAILADDR,MOBILEPHONE from ARACCTHOLDERAUTH where proxypan='" + proxyPanValue + "'";
		ArrayList<String> contactList = new ArrayList<String>();
		/*
		 * if(queryResult!=null && !queryResult.isEmpty()){ for(int
		 * i=0;i<queryResult.size();i++){ for(Entry<String, Object>
		 * entryValues:queryResult.get(i).entrySet()){ String key =
		 * entryValues.getKey();
		 * 
		 * //Currently adding encrypted value to list.Need to be changed once
		 * decrypt is done. //String decryptedValue = enc.decryptString((String)
		 * entryValues.getValue(), strIssuerName); contactList.add((String)
		 * entryValues.getValue()); } } }
		 */
		// Getting Contacts count from ARACCTHOLDERATTR using proxyPanValue
		// strQuery =
		// "SELECT ATTRIBUTEVALUE FROM ARACCTHOLDERATTR WHERE
		// PROXYPAN='"+proxyPanValue+"'";
		System.out.println(
				"For thread id: " + threadId + " , Query to get contact details for card number is: " + strQuery);

		queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
		// Adding contacts to list from map.
		if (queryResult != null && !queryResult.isEmpty()) {
			for (int i = 0; i < queryResult.size(); i++) {
				for (Entry<String, Object> entryValues : queryResult.get(i).entrySet()) {

					// Currently adding encrypted value to list.Need to be
					// changed once decrypt is done.
					// String decryptedValue = enc.decryptString((String)
					// entryValues.getValue(), strIssuerName);
					if (!(entryValues.getValue() == null))
						contactList.add((String) entryValues.getValue());

				}
			}
		}
		System.out.println("End of getContactsFromTmDb method for thread id: " + threadId);
		return contactList;

	}

	/**
	 * Description: This method fetches the specified columns details from
	 * TD_TRANSACTION_LOG for given ACS account ID from TM DB after the
	 * transaction is done.
	 * 
	 * @param strAcsAccoundId
	 *            (strAcsAccoundId as a String)
	 * 
	 * @return (Object of HashMap<String, String> with all column details)
	 * 
	 * @author subsu06
	 */
	public HashMap<String, String> getAllTransactionDetailsFromDB(String strAcsAccoundId) {

		HashMap<String, String> expectedMap = null;
		try {
			String strQuery = "select MERCHANT_NAME,PURCHASE_CURRENCY,DEVICE_CHANNEL,TXN_STATUS,PURCHASE_AMOUNT from TD_TRANSACTION_LOG where ACS_TRANS_ID_PK='"
					+ strAcsAccoundId + "'";
			System.out.println("Query : " + strQuery);

			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
			Map<String, Object> txnRowMap = queryResult.get(0);

			String strMerchantName = (String) txnRowMap.get("MERCHANT_NAME");
			String strCurrencyCode = (String) txnRowMap.get("PURCHASE_CURRENCY");
			String strDeviceChannelCode = (String) txnRowMap.get("DEVICE_CHANNEL");
			String strTxnStatusCode = (String) txnRowMap.get("TXN_STATUS");
			Object strPurchaseAmtObj = txnRowMap.get("PURCHASE_AMOUNT");
			String strPurchaseAmt = strPurchaseAmtObj.toString();
			String strDeviceChannel = null;
			if (strDeviceChannelCode.equalsIgnoreCase("02")) {
				strDeviceChannel = "PC Browser";
			} else if (strDeviceChannelCode.equalsIgnoreCase("01")) {
				strDeviceChannel = "Mobile";
			}

			String strTxnStatus = null;

			if (strTxnStatusCode.equalsIgnoreCase("Y")) {
				strTxnStatus = "Successful";
			} else if (strTxnStatusCode.equalsIgnoreCase("N")) {
				strTxnStatus = "Failed";
			} else if (strTxnStatusCode.equalsIgnoreCase("R")) {
				strTxnStatus = "Rejected";
			} else if (strTxnStatusCode.equalsIgnoreCase("U")) {
				strTxnStatus = "Abandoned";
			} else
				strTxnStatus = "N/A";

			System.out.println(strMerchantName + " " + strCurrencyCode + " " + strDeviceChannelCode + " "
					+ strPurchaseAmt + " " + strTxnStatus);

			strQuery = "select CURRTYPE from ARCURRENCY where CURRCODE='" + strCurrencyCode + "'";
			List<Map<String, Object>> currencyResult = DBConnections.executeQueryIn3DSDB(strQuery);
			Map<String, Object> currencyRowMap = currencyResult.get(0);
			String strCurrency = (String) currencyRowMap.get("CURRTYPE");
			System.out.println("Currency = " + strCurrency);

			expectedMap = new HashMap<String, String>();
			expectedMap.put("MERCHANT NAME", strMerchantName);
			expectedMap.put("CURRENCY", strCurrency);
			expectedMap.put("DEVICE", strDeviceChannel);
			expectedMap.put("TRANSACTION STATUS", strTxnStatus);
			expectedMap.put("AMOUNT", strPurchaseAmt);

		} catch (Exception e) {
			System.out.println(
					"-----------------Exception in getRiskIntegrationFromTdSystemConfigTable-------------------");
			System.out.println("Exception : " + e.getMessage());
		}

		return expectedMap;

	}

	public HashMap<String, String> getExtElementsFromExtElementsColumn(Object extElemObjData) {

		String extElemData = extElemObjData.toString();

		HashMap<String, String> map = new HashMap<String, String>();
		if (extElemData != null) {
			String data[] = extElemData.split("\n");
			for (int i = 0; i < data.length; i++) {
				String elements[] = data[i].split("=", 2);
				if (elements.length > 1)
					map.put(elements[0], elements[1]);
				else
					map.put(elements[0], "");
			}
		}
		return map;
	}

	private String clobToString(CLOB data) {
		StringBuilder sb = new StringBuilder();
		try {
			Reader reader = data.getCharacterStream();
			BufferedReader br = new BufferedReader(reader);

			String line;
			while (null != (line = br.readLine())) {
				sb.append(line);
				sb.append('\n');
			}
			br.close();
		} catch (SQLException e) {
			// handle this exception
		} catch (IOException e) {
			// handle this exception
		}
		return sb.toString();
	}

	/**
	 * Description: This method fetches all columns details from
	 * TD_TRANSACTION_LOG for given ACS account ID from TM DB after the
	 * transaction is done.
	 * 
	 * @param strAcsAccoundId
	 *            (strAcsAccoundId as a String)
	 * 
	 * @return (Object of HashMap<String, String> with all column details)
	 * 
	 * @author subsu06
	 */
	public HashMap<String, String> getTransactionDetailsFromTransactionLogTable(String strAcsAccoundId) {

		String value = null;
		HashMap<String, String> transactionDetails = new HashMap<String, String>();
		try {
			String strQuery = "select * from TD_TRANSACTION_LOG where ACS_TRANS_ID_PK='" + strAcsAccoundId + "'";
			System.out.println("Query : " + strQuery);

			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
			for (int i = 0; i < queryResult.size(); i++) {
				for (Entry<String, Object> entryValues : queryResult.get(i).entrySet()) {
					String key = entryValues.getKey();
					if (entryValues.getValue() != null) {
						value = ((Object) entryValues.getValue()).toString();
					} else {
						value = (String) entryValues.getValue();
					}
					transactionDetails.put(key, value);
				}
			}
			System.out.println("Expected Map:" + transactionDetails);

		} catch (Exception e) {
			System.out.println(
					"-----------------Exception in getRiskIntegrationFromTdSystemConfigTable-------------------");
			System.out.println("Exception : " + e.getMessage());
		}

		return transactionDetails;

	}

	/**
	 * @Description This method fetches all columns details from
	 *              TD_OTP_DELIVERY_LOG for given OTP_IDENTIFIER from TM DB
	 *              after the transaction is done.
	 * 
	 * @param OTP_IDENTIFIER
	 *            (OTP_IDENTIFIER as String,It is taken from TD_Transaction_Log)
	 * 
	 * @return (Object of LinkedHashMap<String, String> with all table details)
	 * 
	 * @author veera04
	 */
	public LinkedHashMap<String, String> getOtpDeliveryLogTableDetailsFromTmDb(String OTP_IDENTIFIER) {

		System.out.println("-------Inside getOtpDeliveryLogTableDetailsFromTmDb---------");
		LinkedHashMap<String, String> tableDetails = new LinkedHashMap<String, String>();
		try {
			// Creating query and executing query.
			String strQuery = "SELECT OTP_DELIVERY_LOG_PK,DELIVERY_MEDIUM,OTP_STATUS,RANGE_ID,BANK_ID,DATE_CREATED,OTP_IDENTIFIER,REFERENCE_ID "
					+ "from TD_OTP_DELIVERY_LOG WHERE OTP_IDENTIFIER='" + OTP_IDENTIFIER + "'";
			System.out.println("Query : " + strQuery);
			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);

			// Adding to map.
			String value;
			for (int i = 0; i < queryResult.size(); i++) {
				for (Entry<String, Object> entryValues : queryResult.get(i).entrySet()) {
					String key = entryValues.getKey();
					if (entryValues.getValue() == null)
						value = (String) entryValues.getValue();
					else
						value = (String) entryValues.getValue().toString();
					tableDetails.put(key, value);
				}
			}
		} catch (Exception e) {
			System.out.println("----------Exception in getOtpDeliveryLogTableDetailsFromTmDb--------------");
			System.out.println("Exception : " + e.getMessage());
		}
		System.out.println("Map of values for Query : " + tableDetails);
		return tableDetails;
	}// end

	/**
	 * @Description This method is used to get the OTP log details from the
	 *              database by taking in otpTxnID as input.
	 * 
	 * @param otpTxnID
	 *            (It is transactionId that has been sent as input)
	 * 
	 * @return Object of HashMap<String, String> which contains the details of
	 *         the table with key value pair.
	 * 
	 * @author banna04
	 */
	public HashMap<String, String> getTdOTPLogDetails(String otpTxnID) {
		HashMap<String, String> tdOtpLogMap = new HashMap<String, String>();
		try {
			// otpLogPrimaryKey = "a367f083-6400-4a59-a4e6-f53578aa9f03";
			String tmQuery = "SELECT * from TD_OTP_LOG where TXN_ID='" + otpTxnID + "'";
			System.out.println("TM Query is......" + tmQuery);

			List<Map<String, Object>> tmQueryResult = DBConnections.executeQueryIn3DSDB(tmQuery);
			Map<String, Object> txnRowMap = tmQueryResult.get(0);
			String bankId = convertBigDecimalToString(txnRowMap.get("BANK_ID"));
			String rangeId = convertBigDecimalToString(txnRowMap.get("RANGE_ID"));
			String txnId = (String) txnRowMap.get("TXN_ID");
			String value = (String) txnRowMap.get("VALUE");
			String otpType = convertBigDecimalToString(txnRowMap.get("OTP_TYPE"));
			String otpLength = convertBigDecimalToString(txnRowMap.get("OTP_LENGTH"));
			String otpStrikes = convertBigDecimalToString(txnRowMap.get("OTP_STRIKES"));
			String maxTries = convertBigDecimalToString(txnRowMap.get("MAX_TRIES"));
			String status = convertBigDecimalToString(txnRowMap.get("STATUS"));
			String referenceId = (String) (txnRowMap.get("REFERENCE_ID"));
			String resendCounter = convertBigDecimalToString(txnRowMap.get("RESEND_COUNTER"));
			String validitySecs = convertBigDecimalToString(txnRowMap.get("VALIDITY_SECONDS"));
			Timestamp dateCreated = (Timestamp) txnRowMap.get("DATE_CREATED");
			String dateToString = dateCreated.toString();

			tdOtpLogMap.put("BANK_ID", bankId);
			tdOtpLogMap.put("RANGE_ID", rangeId);
			tdOtpLogMap.put("TXN_ID", txnId);
			tdOtpLogMap.put("VALUE", value);

			tdOtpLogMap.put("OTP_TYPE", otpType);
			tdOtpLogMap.put("OTP_LENGTH", otpLength);
			tdOtpLogMap.put("OTP_STRIKES", otpStrikes);
			tdOtpLogMap.put("MAX_TRIES", maxTries);
			tdOtpLogMap.put("STATUS", status);
			tdOtpLogMap.put("REFERENCE_ID", referenceId);
			tdOtpLogMap.put("RESEND_COUNTER", resendCounter);
			tdOtpLogMap.put("VALIDITY_SECONDS", validitySecs);
			tdOtpLogMap.put("DATE_CREATED", dateToString);

		} catch (Exception e) {
			System.out.println("Exception occured in getTdOTPLogDetails of TdsQueries......" + e);
		}
		return tdOtpLogMap;
	}

	public String convertBigDecimalToString(Object object) {
		String intToStringValue = null;
		if (object != null) {
			BigDecimal number = (BigDecimal) object;
			Integer intValue = number.intValue();
			intToStringValue = String.valueOf(intValue);
		}
		return intToStringValue;
	}

	/**
	 * @Description This method is used to retrieving all the data from
	 *              TD_SYSTEM_CONFIG table
	 * 
	 * @return systemConfigDetails Object of HashMap<String, String> which
	 *         contains the details of the table with key value pair.
	 * 
	 * @author rampa06
	 */

	public HashMap<String, String> getTdSystemConfigTableDetails() {

		String value = null;
		HashMap<String, String> systemConfigDetails = new HashMap<String, String>();
		try {
			String strQuery = "select * from TD_SYSTEM_CONFIG";
			System.out.println("Query : " + strQuery);

			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
			for (int i = 0; i < queryResult.size(); i++) {
				for (Entry<String, Object> entryValues : queryResult.get(i).entrySet()) {
					String key = entryValues.getKey();
					if (entryValues.getValue() != null) {
						value = ((Object) entryValues.getValue()).toString();
					} else {
						value = (String) entryValues.getValue();
					}
					systemConfigDetails.put(key, value);
				}
			}
			System.out.println("Expected Map:" + systemConfigDetails);

		} catch (Exception e) {
			System.out.println(
					"-----------------Exception in getDataFromTdSystemConfigTable of TdsQueries-------------------");
			System.out.println("Exception : " + e.getMessage());
		}

		return systemConfigDetails;
	}

	/**
	 * @Description This method is used to lock the card by updating the ACTIVE
	 *              column in ARACCTHOLDERAUTH and set value to 0
	 * 
	 * @param cardNumber
	 *            (15-20 digits Card Number as string)
	 * 
	 * @param strIssuerName
	 *            (Bank Name as string)
	 * 
	 * 
	 * @author rampa06
	 */

	public void lockCard(String cardNumber, String strIssuerName) {

		Encryptor3DS enc = new Encryptor3DS();
		String encryptedCardNumber = enc.encryptString(cardNumber, strIssuerName);
		String strQuery = "update ARACCTHOLDERAUTH set ACTIVE = 0 where CARDNUMBER = '" + encryptedCardNumber + "'";
		PreparedStatement preparedStatement = null;
		try {
			Connection conn = DBConnections.get3DSDBConnection();
			preparedStatement = conn.prepareStatement(strQuery);
			preparedStatement.executeQuery();
			System.out.println("locked the Card");

		} catch (Exception e) {
			System.out.println("-----------------Exception in lockCard of TdsQueries-------------------");
			System.out.println("Exception : " + e.getMessage());
			ReportLogger.logInfo(Status.INFO, "Locked the card");
		}

	}

	/**
	 * @Description This method is used to unlock the card by updating the
	 *              ACTIVE column in ARACCTHOLDERAUTH and set value to 1
	 * 
	 * @param cardNumber
	 *            (15-20 digits Card Number as string)
	 * 
	 * @param strIssuerName
	 *            (Bank Name as string)
	 * 
	 * 
	 * @author rampa06
	 */

	public void unLockCard(String cardNumber, String strIssuerName) {

		Encryptor3DS enc = new Encryptor3DS();
		String encryptedCardNumber = enc.encryptString(cardNumber, strIssuerName);
		String strQuery = "update ARACCTHOLDERAUTH set ACTIVE = 1 where CARDNUMBER = '" + encryptedCardNumber + "'";
		PreparedStatement preparedStatement = null;
		try {
			Connection conn = DBConnections.get3DSDBConnection();
			preparedStatement = conn.prepareStatement(strQuery);
			preparedStatement.executeQuery();
			System.out.println("Unlocked the Card");
			ReportLogger.logInfo(Status.INFO, "Unlocked the card");
		}

		catch (Exception e) {
			System.out.println("-----------------Exception in unlockCard of TdsQueries-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
	}

	public String getProxypanTmDb(String cardNumber, String strIssuerName) {

		String proxyPanValue = null;

		// Getting ProxyPan from ARACCTHOLDERAUTH using encrypted CardNumber
		Encryptor3DS enc = new Encryptor3DS();
		String encryptedCardNumber = enc.encryptString(cardNumber, strIssuerName);
		String strQuery = "SELECT PROXYPAN FROM ARACCTHOLDERAUTH WHERE CARDNUMBER='" + encryptedCardNumber + "'";
		System.out.println("Query : " + strQuery);
		Map.Entry<String, Object> entry = null;
		List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
		if (queryResult != null && !queryResult.isEmpty()) {
			entry = queryResult.get(0).entrySet().iterator().next();
			proxyPanValue = (String) entry.getValue();
		}
		return proxyPanValue;
	}

	public String getLandingPageType(String strCardNumber, String strIssuerName) {

		Long threadId = new Long(Thread.currentThread().getId());
		System.out.println("Entering getLandingPageType method for thread id: " + threadId);
		int rangeId = getRangeID(strCardNumber, strIssuerName);

		String value = null;
		try {
			String strQuery = "SELECT ATTRIBUTEVALUE from ARISSUERCONFIG where RANGEID='" + rangeId + "'"
					+ " and ATTRIBUTENAME='TD_LANDING_PAGE_TYPE'";

			System.out.println("Query to get landing page type is : " + strQuery + "and thread id: " + threadId);

			Map.Entry<String, Object> entry = null;
			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
			if (queryResult != null && !queryResult.isEmpty()) {
				entry = queryResult.get(0).entrySet().iterator().next();
				value = (String) entry.getValue();
			}
		} catch (Exception e) {
			System.out.println("-----------------Exception in getLandingPageType-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		System.out.println("End of getLandingPageType method for thread id: " + threadId);
		return value;

	}

	public boolean getShowLandingPageStatus(String strCardNumber, String strIssuerName) {
		System.out.println("Inside getShowLandingPageStatus method for card number: " + strCardNumber);

		int rangeId = getRangeID(strCardNumber, strIssuerName);

		String queryString = "SELECT ATTRIBUTEVALUE from ARISSUERCONFIG where RANGEID='" + rangeId + "'"
				+ " and ATTRIBUTENAME='TD_SHOW_LANDING_PAGE'";

		List<Map<String, Object>> result = DBConnections.executeQueryIn3DSDB(queryString);
		String value = null;
		for (int i = 0; i < result.size(); i++) {

			for (Map.Entry<String, Object> entry : result.get(i).entrySet()) {
				String key = entry.getKey();
				System.out.println(key);
				value = (String) entry.getValue();
				System.out.println(value);
				System.out.println("in for loop");
			}

		}

		if (value.equals("1"))
			return true;
		else
			return false;

	}

	public boolean getChannelSelectionPage(String strCardNumber, String strIssuerName) {

		int rangeId = getRangeID(strCardNumber, strIssuerName);

		String value = null;
		try {
			String strQuery = "SELECT ATTRIBUTEVALUE from ARISSUERCONFIG where RANGEID='" + rangeId + "'"
					+ " and ATTRIBUTENAME='TD_OTP_AUTH_SHOW_CHANNEL_SELECTION_PAGE'";

			System.out.println("Query : " + strQuery);

			Map.Entry<String, Object> entry = null;
			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
			if (queryResult != null && !queryResult.isEmpty()) {
				entry = queryResult.get(0).entrySet().iterator().next();
				String key = entry.getKey();
				System.out.println(key);
				value = (String) entry.getValue();
			}
		} catch (Exception e) {
			System.out.println("-----------------Exception in getLandingPageType-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		if (value.equals("1"))
			return true;
		else
			return false;
	}

	public boolean getChannelSelectionPageForSingleContact(String strCardNumber, String strIssuerName) {

		int rangeId = getRangeID(strCardNumber, strIssuerName);

		String value = null;
		try {
			String strQuery = "SELECT ATTRIBUTEVALUE from ARISSUERCONFIG where RANGEID='" + rangeId + "'"
					+ " and ATTRIBUTENAME='TD_OTP_AUTH_CHANNEL_SELECTION_FOR_SINGLE_CONTACT'";

			System.out.println("Query : " + strQuery);

			Map.Entry<String, Object> entry = null;
			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
			if (queryResult != null && !queryResult.isEmpty()) {
				entry = queryResult.get(0).entrySet().iterator().next();
				String key = entry.getKey();
				System.out.println(key);
				value = (String) entry.getValue();
			}
		} catch (Exception e) {
			System.out.println("-----------------Exception in getLandingPageType-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		if (value.equals("1"))
			return true;
		else
			return false;
	}

	public boolean getChannelSelectionPageForMultiContact(String strCardNumber, String strIssuerName) {

		int rangeId = getRangeID(strCardNumber, strIssuerName);

		String value = null;
		try {
			String strQuery = "SELECT ATTRIBUTEVALUE from ARISSUERCONFIG where RANGEID='" + rangeId + "'"
					+ " and ATTRIBUTENAME='TD_OTP_AUTH_MULTIPLE_CHANNEL_SELECTION_ENABLED'";

			System.out.println("Query : " + strQuery);

			Map.Entry<String, Object> entry = null;
			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
			if (queryResult != null && !queryResult.isEmpty()) {
				entry = queryResult.get(0).entrySet().iterator().next();
				String key = entry.getKey();
				System.out.println(key);

				value = (String) entry.getValue();
			}
		} catch (Exception e) {
			System.out.println("-----------------Exception in getLandingPageType-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		if (value.equals("1"))
			return true;
		else
			return false;
	}

	public boolean getChannelSelectionPageForResend(String strCardNumber, String strIssuerName) {

		int rangeId = getRangeID(strCardNumber, strIssuerName);

		String value = null;
		try {
			String strQuery = "SELECT ATTRIBUTEVALUE from ARISSUERCONFIG where RANGEID='" + rangeId + "'"
					+ " and ATTRIBUTENAME='TD_OTP_AUTH_CHANNEL_SELECTION_FOR_RESEND'";

			System.out.println("Query : " + strQuery);

			Map.Entry<String, Object> entry = null;
			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
			if (queryResult != null && !queryResult.isEmpty()) {
				entry = queryResult.get(0).entrySet().iterator().next();
				String key = entry.getKey();
				System.out.println(key);
				value = (String) entry.getValue();
			}
		} catch (Exception e) {
			System.out.println("-----------------Exception in getLandingPageType-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		if (value.equals("1"))
			return true;
		else
			return false;
	}

	public boolean getChannelSelectionPagePreselectForResend(String strCardNumber, String strIssuerName) {

		int rangeId = getRangeID(strCardNumber, strIssuerName);

		String value = null;
		try {
			String strQuery = "SELECT ATTRIBUTEVALUE from ARISSUERCONFIG where RANGEID='" + rangeId + "'"
					+ " and ATTRIBUTENAME='TD_OTP_AUTH_PRE_SELECT_CHANNELS_FOR_RESEND'";

			System.out.println("Query : " + strQuery);

			Map.Entry<String, Object> entry = null;
			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
			if (queryResult != null && !queryResult.isEmpty()) {
				entry = queryResult.get(0).entrySet().iterator().next();
				String key = entry.getKey();
				System.out.println(key);
				value = (String) entry.getValue();
			}
		} catch (Exception e) {
			System.out.println("-----------------Exception in getLandingPageType-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		if (value.equals("1"))
			return true;
		else
			return false;
	}

	public LinkedHashMap<String, String> getRiskAdviceAction(String strAcsAccountID) {

		LinkedHashMap<String, String> riskAdviceAction = new LinkedHashMap<String, String>();
		try {

			String strQuery = "select ALLOW_ACTION,ALERT_ACTION,INCREASEAUTH_ACTION,DENY_ACTION from TD_RISK_ADVICE_ACTION where RISK_ADVICE_ACTION_PK in"
					+ " (select CONFIG_ID from TD_RANGE_CONFIG a inner join TD_TRANSACTION_LOG b "
					+ "on(a.BANK_ID=b.BANK_ID) and (a.RANGE_ID=b.RANGE_ID) and a.CONFIG_TYPE='RISK_ADVICE_ACTION' "
					+ "and b.ACS_TRANS_ID_PK ='" + strAcsAccountID + "' )";

			System.out.println("Query is : " + strQuery);

			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);

			for (int i = 0; i < queryResult.size(); i++) {
				for (Entry<String, Object> entryValues : queryResult.get(i).entrySet()) {
					String key = entryValues.getKey();
					String value = (String) entryValues.getValue().toString();
					riskAdviceAction.put(key, value);
				}
			}

		} catch (Exception e) {
			System.out.println("-----------------Exception in getTransactionLog Table-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		System.out.println("expected map size is???????" + riskAdviceAction.size());

		return riskAdviceAction;

	}

	public LinkedHashMap<String, String> get3DS2EValRiskTransactionDetailsFromDB(String strAcsAccountID) {
		System.out.println("StrAcc = " + strAcsAccountID);
		String strQuery = "select RA_RISK_ACTION,RA_TXN_ID,RA_RISK_SCORE,RA_RISK_ADVICE,DEVICE_CHANNEL,RISKFORT_COOKIE_OUT,RISKFORT_TLC_COOKIE_OUT,TXN_STATUS from TD_TRANSACTION_LOG where ACS_TRANS_ID_PK = '"
				+ strAcsAccountID + "'";

		System.out.println("Query : " + strQuery);

		LinkedHashMap<String, String> expectedMap = new LinkedHashMap<String, String>();

		List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
		Map<String, Object> txnRowMap = queryResult.get(0);

		String strRARiskAction = (String) txnRowMap.get("RA_RISK_ACTION");
		String strRATxnId = (String) txnRowMap.get("RA_TXN_ID");
		String strRARskScore = String.valueOf(txnRowMap.get("RA_RISK_SCORE"));

		String strRARiskAdv = (String) txnRowMap.get("RA_RISK_ADVICE");

		String strDeviceChannelCode = (String) txnRowMap.get("DEVICE_CHANNEL");
		String strDeviceChannel = null;
		if (strDeviceChannelCode.equalsIgnoreCase("02")) {
			strDeviceChannel = "PC";
		} else if (strDeviceChannelCode.equalsIgnoreCase("01")) {
			strDeviceChannel = "MOBILE";
		}

		String strRFCookieOut = (String) txnRowMap.get("RISKFORT_COOKIE_OUT");
		// String strRFTLCCookieOut= (String)
		// txnRowMap.get("RISKFORT_TLC_COOKIE_OUT");
		String strTxnStatus = (String) txnRowMap.get("TXN_STATUS");

		if (strTxnStatus.equalsIgnoreCase("R")) {
			expectedMap.put("TRANSACTION STATUS", "Reject");
		} else if (strTxnStatus.equalsIgnoreCase("Y")) {
			expectedMap.put("TRANSACTION STATUS", "Successful");
		} else {
			expectedMap.put("TRANSACTION STATUS", "Failure");
		}
		expectedMap.put("RA Txn ID", strRATxnId);
		expectedMap.put("Risk Score", strRARskScore);
		expectedMap.put("Risk Action", strRARiskAction);
		expectedMap.put("Risk Advice", strRARiskAdv);
		expectedMap.put("Device Type", strDeviceChannel);
		expectedMap.put("Outgoing DeviceID", strRFCookieOut);
		expectedMap.put("Http Device Id", strRFCookieOut);
		return expectedMap;

	}

	public long getEvalRiskAdviceTxnIDFromRADB(String raTxnID) {

		long evalRiskTxnID = 0;
		try {
			String strQuery = "SELECT TXID FROM ARRFSYSAUDITLOG_3DSECURE WHERE SESSIONID='" + raTxnID
					+ "' AND TXNTYPE='1'";

			System.out.println("Query : " + strQuery);

			Map.Entry<String, Object> entry = null;
			List<Map<String, Object>> queryResult = DBConnections.executeQueryInRaDB(strQuery);
			if (queryResult != null && !queryResult.isEmpty()) {
				entry = queryResult.get(0).entrySet().iterator().next();
				evalRiskTxnID = ((BigDecimal) entry.getValue()).longValue();
			}
		} catch (Exception e) {
			System.out.println("-----------------Exception in getLandingPageType-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		return evalRiskTxnID;

	}

	public long getPostEvalRiskAdviceTxnIDFromRADB(String raTxnID) {

		long evalRiskTxnID = 0;
		try {
			String strQuery = "SELECT TXID FROM ARRFSYSAUDITLOG_3DSECURE WHERE SESSIONID='" + raTxnID
					+ "' AND TXNTYPE='2'";

			System.out.println("Query : " + strQuery);

			Map.Entry<String, Object> entry = null;
			List<Map<String, Object>> queryResult = DBConnections.executeQueryInRaDB(strQuery);
			if (queryResult != null && !queryResult.isEmpty()) {
				entry = queryResult.get(0).entrySet().iterator().next();
				evalRiskTxnID = ((BigDecimal) entry.getValue()).longValue();
			}
		} catch (Exception e) {
			System.out.println("-----------------Exception in getLandingPageType-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		return evalRiskTxnID;

	}

	public String get3DS2ACSTxnIDUsingCHName(String strCardName, String strCardNumber, String strIssuerName) {

		Long threadId = new Long(Thread.currentThread().getId());
		System.out.println("Entering get3DS2ACSTxnIDForAppTxn method for thread id: " + threadId);

		CryptoService crypto = new CryptoService();
		String encCardNumber = crypto.encryptCardRelated(strCardNumber, strIssuerName);
		String encCardName = crypto.encryptCardRelated(strCardName, strIssuerName);

		String acsTxnID = null;
		try {
			String strQuery = "SELECT ACS_TRANS_ID_PK FROM TD_TRANSACTION_LOG WHERE CARD_NUMBER='" + encCardNumber
					+ "' AND CARDHOLDER_NAME='" + encCardName + "'";
			System.out.println("For thread id: " + threadId + ", query to get ACS txn id for thread id: " + strQuery);

			Map.Entry<String, Object> entry = null;
			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
			if (queryResult != null && !queryResult.isEmpty()) {
				entry = queryResult.get(0).entrySet().iterator().next();
				acsTxnID = (String) entry.getValue();
			}
		} catch (Exception e) {
			System.out.println("-----------------Exception in get3DS2ACSTxnIDForAppTxn-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		return acsTxnID;
	}// end

	public HashMap<String, Integer> getAbandonedTimeoutDetailsFromTdSystemConfig() {

		Long threadId = new Long(Thread.currentThread().getId());
		System.out.println("Entering getAbandonedTimeoutDetailsFromTdSystemConfig method for thread id: " + threadId);
		// Creating Query
		String strQuery = "SELECT PARAM_NAME,PARAM_VALUE FROM TD_SYSTEM_CONFIG WHERE PARAM_GROUP_NAME IN ('protocol_timeouts','transaction_abandonment')";
		System.out.println("For thread id: " + threadId + ", query to get trasnaction timeout " + strQuery);

		// Putting results to map
		HashMap<String, Integer> timeoutDetails = new HashMap<String, Integer>();
		List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
		for (int i = 0; i < queryResult.size(); i++) {
			String value = null;
			int intValue = 0;
			for (Entry<String, Object> entryValues : queryResult.get(i).entrySet()) {
				if (entryValues.getKey().contains("VALUE")) {
					intValue = Integer.parseInt(entryValues.getValue().toString());
					timeoutDetails.put(value, intValue);
				} else {
					value = entryValues.getValue().toString();
					timeoutDetails.put(value, intValue);
				}
			}
		}
		System.out.println("End of getAbandonedTimeoutDetailsFromTdSystemConfig method for thread id: " + threadId);
		return timeoutDetails;
	}//

	/**
	 * Description: This method will returns the otp expire time for particular
	 * card number and issuer given.
	 * 
	 * @author sidsu05
	 */

	public int getOTPExpiryTime(String strCardNumber, String strIssuerName) {

		Long threadId = new Long(Thread.currentThread().getId());
		System.out.println("Entering getOTPExpiryTime method for thread id: " + threadId);
		int rangeID = getRangeID(strCardNumber, strIssuerName);
		String queryString = "select OTP_EXPIRY_TIME from TD_OTP_CONFIG where OTP_CONFIG_PK=(select CONFIG_ID from TD_RANGE_CONFIG where CONFIG_TYPE='OTP' and RANGE_ID='"
				+ rangeID + "')";

		System.out.println("For thread id: " + threadId + ", query to get transaction details : " + queryString);
		ReportLogger.logInfo(Status.DEBUG, "Query to get OTP expiry time: " + queryString, LogMode.DEBUG);
		List<Map<String, Object>> result = DBConnections.executeQueryIn3DSDB(queryString);
		int otpExpiryTime = 0;

		for (int i = 0; i < result.size(); i++) {

			for (Map.Entry<String, Object> entry : result.get(i).entrySet()) {
				String key = entry.getKey();
				// System.out.println(key);
				otpExpiryTime = ((BigDecimal) entry.getValue()).intValue();
				/*
				 * System.out.println(otpExpiryTime);
				 * System.out.println("in for loop");
				 */
			}

		}
		System.out.println("End of getOTPExpiryTime method for thread id: " + threadId);
		return otpExpiryTime;
	}

	public HashMap<String, String> getTransactionDetailsFromDB(String strAcsAccountID) {
		Long threadId = new Long(Thread.currentThread().getId());
		System.out.println("Entering getTransactionDetailsFromDB method for thread id: " + threadId);

		String strQuery = "select MERCHANT_NAME,PURCHASE_AMOUNT,PURCHASE_CURRENCY,RA_RISK_SCORE,RA_RISK_ADVICE,DEVICE_CHANNEL,DATE_CREATED,RANGE_ID,TXN_STATUS from TD_TRANSACTION_LOG where ACS_TRANS_ID_PK = '"
				+ strAcsAccountID + "'";

		System.out.println("For thread id: " + threadId + ", query to get transaction details : " + strQuery);

		HashMap<String, String> expectedMap = new HashMap<String, String>();

		List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
		Map<String, Object> txnRowMap = queryResult.get(0);

		String strMrchtName = (String) txnRowMap.get("MERCHANT_NAME");
		String strPurchaseAmount = String.valueOf(txnRowMap.get("PURCHASE_AMOUNT"));

		String strCurrencyCode = (String) txnRowMap.get("PURCHASE_CURRENCY");
		String strRARskScore = String.valueOf(txnRowMap.get("RA_RISK_SCORE"));

		String strRARiskAdv = (String) txnRowMap.get("RA_RISK_ADVICE");

		String strDeviceChannelCode = (String) txnRowMap.get("DEVICE_CHANNEL");
		String strDeviceChannel = null;
		if (strDeviceChannelCode.equalsIgnoreCase("02")) {
			strDeviceChannel = "PC";
		} else if (strDeviceChannelCode.equalsIgnoreCase("01")) {
			strDeviceChannel = "MOBILE";
		}

		// String strDateCreated = (String) txnRowMap.get("DATE_CREATED");

		strQuery = "select CURRTYPE from ARCURRENCY where CURRCODE='" + strCurrencyCode + "'";
		List<Map<String, Object>> currencyResult = DBConnections.executeQueryIn3DSDB(strQuery);
		Map<String, Object> currencyRowMap = currencyResult.get(0);
		String strCurrency = (String) currencyRowMap.get("CURRTYPE");

		String strRangeId = String.valueOf(txnRowMap.get("RANGE_ID"));

		strQuery = "select CARDTYPE from ARBRANDINFO where RANGEID =" + strRangeId;
		List<Map<String, Object>> cardResult = DBConnections.executeQueryIn3DSDB(strQuery);
		Map<String, Object> cardRowMap = cardResult.get(0);
		String strCardType = (String) cardRowMap.get("CARDTYPE");

		String strTxnStatus = (String) txnRowMap.get("TXN_STATUS");

		if (strTxnStatus.equalsIgnoreCase("R")) {
			expectedMap.put("TRANSACTION STATUS", "Reject");
		} else if (strTxnStatus.equalsIgnoreCase("Y")) {
			expectedMap.put("TRANSACTION STATUS", "Successful");
		} else {
			expectedMap.put("TRANSACTION STATUS", "Failure");
		}

		expectedMap.put("MERCHANT", strMrchtName);
		expectedMap.put("AMOUNT", strPurchaseAmount);
		expectedMap.put("Currency", strCurrency);
		expectedMap.put("Risk Score", strRARskScore);
		expectedMap.put("Risk Advice", strRARiskAdv);
		expectedMap.put("Device Type", strDeviceChannel);
		// expectedMap.put("Transaction Date", strDateCreated);
		expectedMap.put("Card Type", strCardType);
		System.out.println("End of getTransactionDetailsFromDB method for thread id: " + threadId);
		return expectedMap;

	}

	public String getPurcahseID(String ACS_ACCOUNT_ID) throws SQLException, JSchException, IOException {

		Long threadId = new Long(Thread.currentThread().getId());
		System.out.println("Entering getPurcahseID method for thread id: " + threadId);

		String Str_3DS_SERVER_TRANS_ID_AREQ = null;
		Connection con = DBConnections.get3DSDBConnection();
		String query = "select \"3DS_SERVER_TRANS_ID_AREQ\" from TD_TRANSACTION_LOG where ACS_TRANS_ID_PK=?";
		PreparedStatement pstm = con.prepareStatement(query);
		System.out
				.println("Query = select \"3DS_SERVER_TRANS_ID_AREQ\" from TD_TRANSACTION_LOG where ACS_TRANS_ID_PK= '"
						+ ACS_ACCOUNT_ID + "'");
		pstm.setString(1, ACS_ACCOUNT_ID);

		try {
			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {
				Str_3DS_SERVER_TRANS_ID_AREQ = rs.getString(1);
				break;
			}
		} catch (SQLException sqe) {
			sqe.printStackTrace();
		} finally {
			if (pstm != null) {
				pstm.close();
			}
			if (con != null) {
				con.close();
			}
		}

		System.out.println("For thread is: " + threadId + ", ACS_Account_ID = " + ACS_ACCOUNT_ID
				+ " 3DS_SERVER_TRANS_ID_AREQ = " + Str_3DS_SERVER_TRANS_ID_AREQ);
		System.out.println("End of getPurcahseID method for thread id: " + threadId);
		return Str_3DS_SERVER_TRANS_ID_AREQ;

	}

	public String getOTPValue(String ACS_ACCOUNT_ID, String strIssuerName)
			throws SQLException, JSchException, IOException {

		Long threadId = new Long(Thread.currentThread().getId());
		System.out.println("Entering getOTPValue method for thread id: " + threadId);
		String strdecryptedotp = null;
		String value = null;
		int count = 0;
		while (count != 5) {
			Connection con = DBConnections.get3DSDBConnection();
			String query = "SELECT VALUE FROM TD_OTP_LOG where TXN_ID=? order by DATE_CREATED desc";
			PreparedStatement pstm = con.prepareStatement(query);
			System.out.println("Query = SELECT VALUE FROM TD_OTP_LOG where TXN_ID='" + ACS_ACCOUNT_ID
					+ "' order by DATE_CREATED desc");
			pstm.setString(1, ACS_ACCOUNT_ID);

			try {

				ResultSet rs = pstm.executeQuery();
				while (rs.next()) {
					value = rs.getString(1);
					// System.out.println("Enc OTP Value for TXn ID
					// "+ACS_ACCOUNT_ID+" is:" +value +"Count is:"+count);

					break;
				}
				if (value == null || value == "") {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					count = count + 1;
				} else {
					// System.out.println("For thread id: "+threadId+", Breaking
					// out from loop as Encrypted OTP is not null for
					// txn:"+ACS_ACCOUNT_ID);
					break;
				}
			}

			catch (Exception sqe) {
				sqe.printStackTrace();
			} finally {
				if (pstm != null) {
					pstm.close();
				}
				if (con != null) {
					con.close();
				}
			}
		}
		ReportLogger.logInfo(Status.DEBUG, "Query to get OTP:SELECT VALUE FROM TD_OTP_LOG where TXN_ID='"
				+ ACS_ACCOUNT_ID + "' order by DATE_CREATED desc", LogMode.DEBUG);
		try {
			// Hard Coding Need to be removed
			if (value == null || value == "") {
				Assert.fail("Database returned null Enrypted OTP for Txn id :" + ACS_ACCOUNT_ID
						+ " , Hence terminating test case execution");
			}
			CryptoService crypto = new CryptoService();
			strdecryptedotp = crypto.decryptCardRelated(value, strIssuerName);

			ReportLogger.logInfo(Status.DEBUG,
					"Decrypted OTP for ACS Transaction ID:" + ACS_ACCOUNT_ID + " is: " + strdecryptedotp,
					LogMode.DEBUG);
			System.out.println(" For ACS Txn ID " + ACS_ACCOUNT_ID + " Encrypted OTP = " + value + " Decrypted OTP = "
					+ strdecryptedotp);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("End of getOTPValue method for thread id: " + threadId);
		return strdecryptedotp;

	}

	public LinkedHashMap<String, String> getChannelSelectionDetails(String strCardNumber, String strIssuerName) {

		Long threadId = new Long(Thread.currentThread().getId());
		System.out.println("Inside getChannelSelectionDetails method for thread id: " + threadId);
		int rangeId = getRangeID(strCardNumber, strIssuerName);
		LinkedHashMap<String, String> issuerConfigDetails = new LinkedHashMap<String, String>();
		try {
			String strQuery = "SELECT ATTRIBUTENAME,ATTRIBUTEVALUE from ARISSUERCONFIG  where ATTRIBUTENAME in ('TD_OTP_AUTH_SHOW_CHANNEL_SELECTION_PAGE','TD_OTP_AUTH_MULTIPLE_CHANNEL_SELECTION_ENABLED','TD_OTP_AUTH_CHANNEL_SELECTION_FOR_SINGLE_CONTACT','TD_OTP_AUTH_CHANNEL_SELECTION_FOR_RESEND','TD_OTP_AUTH_PRE_SELECT_CHANNELS_FOR_RESEND') and RANGEID='"
					+ rangeId + "'";
			System.out.println(
					"For thread id: " + threadId + " Query to get channel selection configuration : " + strQuery);

			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
			for (int i = 0; i < queryResult.size(); i++) {
				String strKey = null;
				String strValue = null;
				for (Entry<String, Object> entryValues : queryResult.get(i).entrySet()) {
					if (entryValues.getKey().contains("VALUE")) {
						strValue = entryValues.getValue().toString();
						issuerConfigDetails.put(strKey, strValue);
					} else {
						strKey = entryValues.getValue().toString();
						issuerConfigDetails.put(strKey, strValue);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("-----------------Exception in getChannelSelectionDetails-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		System.out.println("End of getChannelSelectionDetails method for thread id: " + threadId);
		return issuerConfigDetails;

	}

	public LinkedHashMap<String, String> executeQueryIn3DSDB(String strQuery) {

		LinkedHashMap<String, String> outputMap = new LinkedHashMap<String, String>();
		try {

			System.out.println("Query is : " + strQuery);
			System.out.println("");

			List<Map<String, Object>> queryResult = DBConnections.executeQueryIn3DSDB(strQuery);

			for (int i = 0; i < queryResult.size(); i++) {
				for (Entry<String, Object> entryValues : queryResult.get(i).entrySet()) {
					String key = entryValues.getKey();
					if (entryValues.getValue() == null)
						outputMap.put(key, null);
					else {
						String value = (String) entryValues.getValue().toString();
						outputMap.put(key, value);
					}
				}
			}

		} catch (Exception e) {
			System.out.println("-----------------Exception in executing query " + strQuery
					+ " in 3DS Database-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		// System.out.println("expected map size is???????" +
		// riskAdviceAction.size());

		return outputMap;

	}

	public void updateQueryIn3DSDB(String strQuery) {

		DBConnections.updateQueryInDB(strQuery, DBEnum.TM);

	}

	public Map<String, String> getKeyValuePairFrom3DSDB(String strQuery, String keyString, String valueString) {

		Map<String,String> keyValueMap =getKeyValuePairFromDB(strQuery, keyString, valueString, DBEnum.TM);
		return keyValueMap;
	}
	
	public Map<String, String> getKeyValuePairFromRAB(String strQuery, String keyString, String valueString) {

		Map<String,String> keyValueMap =getKeyValuePairFromDB(strQuery, keyString, valueString, DBEnum.RA);
		return keyValueMap;
	}

	public Map<String, String> getKeyValuePairFromDB(String strQuery, String keyString, String valueString, DBEnum dbenum) {
		
		List<Map<String, Object>> queryResult =  null;
		Map<String,String> keyValueMap = new LinkedHashMap<String,String>();
		if(dbenum.equals(DBEnum.RA)){
			queryResult = DBConnections.executeQueryInRaDB(strQuery);
		}
		else if(dbenum.equals(DBEnum.TM)){
			queryResult = DBConnections.executeQueryIn3DSDB(strQuery);
		}
		
		for (int i = 0; i < queryResult.size(); i++) {
			
			Map<String,Object> dbMap = queryResult.get(i);
			String strKey = (String) dbMap.get(keyString).toString();
			String strValue = (String) dbMap.get(valueString).toString();
			keyValueMap.put(strKey, strValue);
		}

		return keyValueMap;
	}

	public LinkedHashMap<String, String> executeQueryInRADB(String strQuery) {

		LinkedHashMap<String, String> outputMap = new LinkedHashMap<String, String>();
		try {

			System.out.println("Query is : " + strQuery);

			List<Map<String, Object>> queryResult = DBConnections.executeQueryInRaDB(strQuery);

			for (int i = 0; i < queryResult.size(); i++) {
				for (Entry<String, Object> entryValues : queryResult.get(i).entrySet()) {
					String key = entryValues.getKey();
					String value = (String) entryValues.getValue().toString();
					outputMap.put(key, value);
				}
			}

		} catch (Exception e) {
			System.out.println(
					"-----------------Exception in executing query " + strQuery + " in RA Database-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		// System.out.println("expected map size is???????" +
		// riskAdviceAction.size());

		return outputMap;

	}
	
	
	public List<String> getTrigerredRuleListFromRA(String strRATxnId) {

		String strQuery = "SELECT TRIGGERED_RULES from ARRFSYSAUDITLOG_3DSECURE where TXNTYPE=1 and SESSIONID='"+strRATxnId+"'";
		String strTrigerredRules = null;
		try {

			System.out.println("Query is : " + strQuery);

			List<Map<String, Object>> queryResult = DBConnections.executeQueryInRaDB(strQuery);
			Map<String, Object> trigerredRuleRowMap = queryResult.get(0);
			strTrigerredRules = (String) trigerredRuleRowMap.get("TRIGGERED_RULES");

			

		} catch (Exception e) {
			System.out.println(
					"-----------------Exception in executing query " + strQuery + " in RA Database-------------------");
			System.out.println("Exception : " + e.getMessage());
		}
		ArrayList<String> lstTrigerredRule = new ArrayList<String>();
		String[] ruleArray = strTrigerredRules.split(",");
		for(String rule : ruleArray)
			lstTrigerredRule.add(rule);
		return lstTrigerredRule;

	}
}

