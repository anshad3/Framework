package com.api.base.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.classification.InterfaceAudience.Public;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.aventstack.extentreports.Status;
import com.base.reports.ReportLogger;
import com.ca.util.APIResult;
import com.ca.util.APIUtil;
import com.ca.util.CommonUtil;
import com.common.util.TdsQueries;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utility.security.CryptoService;

import junit.framework.Assert;

public class ValidateDB {

	String strIssuer = null;

	public void validateDB(String validateDBJson, Map<String, String> persistentDataMap, String strExtentMessage,
			APIResult result) {

		JSONObject json = null;
		JSONArray jsonWhere = null;
		JSONObject jsonValues = null;
		String strDBComponent = null;
		String strTableName = null;
		String strExtentReport = null;
		HashMap<String, String> whereMap = new HashMap<String, String>();
		Map<String, String> valuesMap = null;
		strIssuer =persistentDataMap.get("Issuer");
		APIUtil apiUtil = new APIUtil();

		try {
			json = new JSONObject(validateDBJson);
			strDBComponent = json.getString("DBComponent");
			strTableName = json.getString("TableName");
			jsonWhere = json.getJSONArray("Where");

			for (int i = 0; i < jsonWhere.length(); i++) {
				JSONObject jsonWhereCondition = (JSONObject) jsonWhere.get(i);
				String key = jsonWhereCondition.getString("Key");
				String value = jsonWhereCondition.getString("Value");
				/*
				 * if(value.contains("**")){ String substring =
				 * value.substring(2, value.length()-2);
				 * if(persistentDataMap.containsKey(substring)) value =
				 * persistentDataMap.get(substring); System.out.println(""); }
				 */
				whereMap.put(key, value);
			}
			jsonValues = json.getJSONObject("Values");

		} catch (org.codehaus.jettison.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println();
		apiUtil.resoveSymbolsInMap(whereMap, persistentDataMap);
		ObjectMapper mapper = new ObjectMapper();
		try {
			valuesMap = mapper.readValue(jsonValues.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		apiUtil.resoveSymbolsInMap(valuesMap, persistentDataMap);

		String query = apiUtil.generateQuery(strTableName, valuesMap, whereMap);
		System.out.println("Query : " + query);

		TdsQueries tdsQueries = new TdsQueries();
		Map<String, String> outputMapFromDB = tdsQueries.executeQueryIn3DSDB(query);

		for (Map.Entry<String,String> entry : outputMapFromDB.entrySet())  {
			String key=entry.getKey();
			String value=entry.getValue();
			if(value.contains("(null)")) {
				outputMapFromDB.replace(key, null);
			}

		} 


		if (strExtentMessage == null || strExtentMessage.length() == 0)
			strExtentMessage = "DB Validation of Table " + strTableName;
		CommonUtil commonUtil = new CommonUtil();
		String testcaseID = persistentDataMap.get("TestCaseID");
		String extentInfo = commonUtil.generateHTMLReportExpectedAndActualResult(valuesMap, outputMapFromDB, testcaseID,
				strExtentMessage);

		if (extentInfo.contains("#FF0000")) {
			ReportLogger.logInfo(Status.FAIL, extentInfo);
			result.setTestCaseStatus(false);
			String message = result.getStrOutputMsg();
			if (message == null) {
				result.setStrOutputMsg("DB Validation Validation Failed");
			} else {
				result.setStrOutputMsg(message + "<br>DB Validation Failed");
			}
		} else if (extentInfo.contains("#00FF00"))
			ReportLogger.logInfo(Status.PASS, extentInfo);
		else
			ReportLogger.logInfo(Status.INFO, extentInfo);
		System.out.println("");

	}

}
