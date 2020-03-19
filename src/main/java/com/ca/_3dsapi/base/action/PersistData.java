package com.ca._3dsapi.base.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.aventstack.extentreports.Status;
import com.ca._3ds.common.util.TdsQueries;
import com.ca._3ds.utility.security.CryptoService;
import com.ca.base.reports.ReportLogger;
import com.ca.db_connection.util.DBConnections;
import com.ca.util.APIResult;
import com.ca.util.APIUtil;
import com.ca.util.CommonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PersistData {

	public void persistData(String dataJson, Map<String, String> persistentDataMap){

		
		APIUtil apiUtil = new APIUtil();

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> dataMap = null;
		try {
			dataMap = mapper.readValue(dataJson.toString(), Map.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(String key : dataMap.keySet()){
			
			String value = dataMap.get(key);
			if(value.contains("**"))
				value = apiUtil.resoveSymbol(value, persistentDataMap);
			persistentDataMap.put(key, value);
		}

	}

	public void persistDataFromDB(String strDataJson, HashMap<String, String> persistentDataMap,
			String strExtentMessage) {
		
		JSONObject json = null;
		JSONArray jsonWhere = null;
		JSONObject jsonValues = null;
		String strDBComponent = null;
		String strTableName = null;
		String strExtentReport = null;
		HashMap<String, String> whereMap = new HashMap<String, String>();
		Map<String, String> valuesToPersistMap = null;
		APIUtil apiUtil = new APIUtil();

		try {
			json = new JSONObject(strDataJson);
			strDBComponent = json.getString("DBComponent");
			strTableName = json.getString("TableName");
			jsonWhere = json.getJSONArray("Where");

			for (int i = 0; i < jsonWhere.length(); i++) {
				JSONObject jsonWhereCondition = (JSONObject) jsonWhere.get(i);
				String key = jsonWhereCondition.getString("Key");
				String value = jsonWhereCondition.getString("Value");				
				whereMap.put(key, value);
			}
			jsonValues = json.getJSONObject("ValuesToPersist");

		} catch (org.codehaus.jettison.json.JSONException e) {
			e.printStackTrace();
		}

		apiUtil.resoveSymbolsInMap(whereMap, persistentDataMap);
		ObjectMapper mapper = new ObjectMapper();
		try {
			valuesToPersistMap = mapper.readValue(jsonValues.toString(), Map.class);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String query = apiUtil.generateQuery(strTableName, valuesToPersistMap, whereMap);
		System.out.println("Query : " + query);

		TdsQueries tdsQueries = new TdsQueries();
		Map<String, String> outputMapFromDB = tdsQueries.executeQueryIn3DSDB(query);

		for (Map.Entry<String,String> entry : outputMapFromDB.entrySet())  {
			String key=entry.getKey();
			String value=entry.getValue();
			if(value.contains("(null)")) {
				value =null;
			}
			String strPersistKey = valuesToPersistMap.get(key);
			persistentDataMap.put(strPersistKey, value);

		} 


		if (strExtentMessage == null || strExtentMessage.length() == 0)
			strExtentMessage = "Persistance of Data";
		
		System.out.println("");
		
		
	}
	
	
	

}
