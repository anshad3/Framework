package com.ca._3dsapi.base.action;

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

public class TrustedBeneficiary {

	public void trustedBeneficiaryEnablement(String tbJson, Map<String, String> persistentDataMap,
			String strExtentMessage, APIResult result) {

		JSONObject json = null;
		JSONArray jsonWhere = null;
		JSONObject jsonValues = null;
		String strIssuer = null;
		String strCardNumber = null;
		String strMechantName = null;
		String strMode = null;
		String strExtentReport = null;
		strIssuer = persistentDataMap.get("Issuer");
		APIUtil apiUtil = new APIUtil();

		try {
			json = new JSONObject(tbJson);
			strCardNumber = json.getString("CardNumber");
			strMechantName = json.getString("MerchantName");
		} catch (org.codehaus.jettison.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (strCardNumber.contains("**"))
			strCardNumber = apiUtil.resoveSymbol(strCardNumber, persistentDataMap);

		CryptoService crypto = new CryptoService();
		String encryptedCardNumber = crypto.encryptCardRelated(strCardNumber, strIssuer);
		String encryptedMerchantName = crypto.encryptCardRelated(strMechantName, strIssuer);

		
		String strDeleteQuery = "DELETE FROM dc_acct_beneficiary WHERE acct_id='"+encryptedCardNumber+"' and beneficiary_value='"+encryptedMerchantName+"'";
		
		//String query = "UPDATE dc_acct_beneficiary SET status = "+status+" WHERE acct_id='"+encryptedCardNumber+"' and beneficiary_value='"+encryptedMerchantName+"'";
		System.out.println("Query for TB Disablement : "+strDeleteQuery);
		TdsQueries tdsqueries = new TdsQueries();
		tdsqueries.updateQueryIn3DSDB(strDeleteQuery);
		ReportLogger.logInfo(Status.INFO, strExtentMessage);

	}

}
