package com.ca.util;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.base.BaseSuite;
import com.db_connection.util.DBConnections;
import com.google.common.base.Strings;


//import ca.com.UtilityFiles.CardNumber;

public class ResolveSymbolsData {
	
	public static final String ISSUER = "Issuer";
	public static final String BEGIN_RANGE = "BeginRange";
	public static final String END_RANGE = "EndRange";
	public static final String CONTACT = "EmailorSMSInput";
	public CardNumber cardNumber = new CardNumber();
	private static Map<String, String> symbolMap = new HashMap<String, String>();

	/**
	 * <h1>getSymbolsToReplaceWithActualData()</h1>
	 * <p>
	 * This method reads the input map 'values' and verifies the symbols
	 * ${NEW_CARD}, ${EXISTING_CARD}, ${PARTIALLY_REG_CARD}, ${CARD_FROM_OTHER},
	 * ${CURRENT_DATE_MONTH_YEAR} ${CURRENT_DATE} and plus, ${CURRENT_DATE} and
	 * minus, ${CURRENT_DAY}, ${TODAY_DAY_NUMBER} ${CURRENT_MONTH} and plus,
	 * ${CURRENT_YEAR} and plus, ${CURRENT_TIME}, ${CURRENT_SEC}
	 * ${CURRENT_MINUTE}, ${CURRENT_HOUR}, ${RANDOM_STRING(16)},
	 * ${RANDOM_STRING(16,1234)} ${RANDOM_LONG}, ${RANDOM_INT(10,20)},
	 * ${RANDOM_DOUBLE}, ${RANDOM_DOUBLE(16.2,20.3)} If values contains the
	 * symbols will invoke respective API function to get actual data and will
	 * replace the value with symbol in input {@link Map}
	 * </p>
	 * <b>Few Scenarios:</b> 1) If 'Issuer' contains one ore more symbols
	 * instead of actual issuer name, then first resolving the symbols and
	 * passing that value to respective API function to get actual card
	 * number.<br>
	 * 2) Any key could contain one or more symbols.
	 * 
	 * Example scenarios:<br>
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * | K1 | Issuer |BeginRange | EndRange | K2 |
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * |${NEW_CARD} | HSBC |44xxxxxx200|44xxxxxx400| ${CURRENT_MONTH}+2
	 * ${CURRENT_DATE}+2 ${CURRENT_YEAR} |
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * |${EXISTING_CARD} | ${RANDOM_STRING(16,134567)} |45xxxxxx200|45xxxxxx400|
	 * ${CURRENT_DATE} |
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * |${PARTIALLY_REG_CARD} | ${CURRENT_DATE_MONTH_YEAR} ${CURRENT_DAY}
	 * |46xxxxxx200|46xxxxxx400| ${CURRENT_DATE_MONTH_YEAR} |
	 * -----------------------------------------------------------------------------------------------------------------------------------------------------
	 * |${CARD_FROM_OTHER} | ${RANDOM_STRING(16,134567)}
	 * |47xxxxxx200|47xxxxxx400| ${RANDOM_LONG} |
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * Result data for above scenario:
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * | K1 | Issuer |BeginRange | EndRange | K2 |
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * |8822113344556622 | HSBC |44xxxxxx200|44xxxxxx400| 03 13 2016 |
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * |8822113344556600 | 1345763165771345 |45xxxxxx200|45xxxxxx400| 11 |
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * |8822113344556611 | 11-01-2016 MONDAY |46xxxxxx200|46xxxxxx400|
	 * 11-01-2016 |
	 * -----------------------------------------------------------------------------------------------------------------------------------------------------
	 * |8822113344556633 | 1345763165771343 |47xxxxxx200|47xxxxxx400|
	 * 321423546547657556 |
	 * ------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * @param inputMap
	 *            {@link Map<String, String>} contains keys and values. The
	 *            value can contain symbols like explained above
	 * @return Map<String,String> the symbols will be resolved new map will be
	 *         returned.
	 * @throws Exception
	 *             if symbols cannot be resolved then exception is thrown
	 * 
	 */
	public Map<String, String> getSymbolsToReplaceWithActualData(Map<String, String> inputMap) throws Exception {
		Map<String, String> outputMap = new LinkedHashMap<String, String>();
		System.out.println("Map Contents Before resolving symbols are : " + inputMap);
		Set<String> keys = inputMap.keySet();
		String[] keyArray = keys.toArray(new String[keys.size()]);
		for (int i = 0; i < keyArray.length; i++) {
			String key = keyArray[i];
			String value = inputMap.get(key);
			String outputValue = getResolvedSymbolData(value, inputMap);

			if (value == null || value.equalsIgnoreCase("${ReadMAPFromProperties}")) {

				// outputMap = inputMap;
				for (Map.Entry<String, String> e : inputMap.entrySet()) {
					// inputMap.putIfAbsent(e.getKey(), e.getValue());
					if (!outputMap.containsKey(e.getKey()))
						outputMap.put(e.getKey(), e.getValue());
				}
				// System.out.println("Reached out of forloop");

			} else {
				outputMap.put(key, outputValue);
			}
		}
		return outputMap;
	}

	private String getResolvedSymbolData(String value, Map<String, String> inputMap) throws Exception {
		if (value == null || (!value.contains("$") || value.length() < 1)) {
			return value;
		} else { // contains $ symbol
			String[] tokens = getMoreSymbolsFromColumn(value);
			return getSymbolData(tokens, inputMap);
		}
	}

	/**
	 * This method splits the input value when more symbols present.
	 * 
	 * @param value
	 *            is like "${CURRENT_DATE_MONTH_YEAR} ${CURRENT_DAY}"
	 * @return Array of symbols
	 */
	private String[] getMoreSymbolsFromColumn(String value) {
		String[] tokens = null;
		if (value != null && value.contains("$"))
			tokens = value.split("\\$");

		return tokens;
	}

	/**
	 * This method returns the final output value of the given KEY when value
	 * contains one or more symbols.
	 * 
	 * @param tokens
	 *            :list of symbols like "${CURRENT_DATE_MONTH_YEAR}
	 *            ${CURRENT_DAY}"
	 * @param inputMap
	 * @return String of final value of specific key.
	 * @throws Exception
	 */
	private String getSymbolData(String[] tokens, Map<String, String> inputMap) throws Exception {
		StringBuilder tempString = new StringBuilder();
		for (String s : tokens) {
			if (s.startsWith("{") && s.contains("}")) {
				
				if(s.contains("{MAX_ENDRANGE_VALUE}")){
					
					tempString.append(resolveSymbol(s, inputMap));
					
				}else{
				String actual = s.substring(s.indexOf("{"), s.indexOf("}") + 1);
				tempString.append(resolveSymbol(actual, inputMap));
				System.out.println("actual string :" + actual);
				String remaining = s.substring(s.indexOf("}") + 1, s.length());
				tempString.append(remaining);
				}
			} else {
				tempString.append(s);
			}
		}
		// System.out.println("Builder value : " + tempString);
		return tempString.toString();
	}

	/**
	 * This method checks the input value symbol and invokes respective API
	 * function
	 * 
	 * @param value
	 *            is any symbol like ${NEW_CARD} or ${CURRENT_DATE} or
	 *            ${RANDOM_STRING(16,1234)}
	 * @param inputMap
	 *            contains keys and values
	 * @return String Resolved symbol data
	 * @throws Exception
	 */
	private String resolveSymbol(String value, Map<String, String> inputMap) throws Exception {
		String value2 = null;
		String tdsEnabledCard = null;
		if (value.contains("{ReadMapFromProperties}")) {
			//loadMapFromProperties(inputMap);
		} else if (value.contains("{3DS2_NEW_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			tdsEnabledCard = getNewCardNumber(issuerValue, beginRgValue, endRgValue);
			//enable3DS2ForCard(tdsEnabledCard, issuerValue, inputMap);
			value2 = tdsEnabledCard;
		} else if (value.contains("{3DS2_NEW_VISA_CREDIT_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			int cardType = 1;
			tdsEnabledCard = getNew3DS2CardNumber(issuerValue, beginRgValue, endRgValue, cardType);
			//enable3DS2ForCard(tdsEnabledCard, issuerValue, inputMap);
			value2 = tdsEnabledCard;
		} else if (value.contains("{3DS2_NEW_VISA_DEBIT_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			int cardType = 2;
			tdsEnabledCard = getNew3DS2CardNumber(issuerValue, beginRgValue, endRgValue, cardType);
			//enable3DS2ForCard(tdsEnabledCard, issuerValue, inputMap);
			value2 = tdsEnabledCard;
		} else if (value.contains("{3DS2_NEW_MASTER_CREDIT_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			int cardType = 3;
			tdsEnabledCard = getNew3DS2CardNumber(issuerValue, beginRgValue, endRgValue, cardType);
			//enable3DS2ForCard(tdsEnabledCard, issuerValue, inputMap);
			value2 = tdsEnabledCard;
		} else if (value.contains("{3DS2_NEW_MASTER_DEBIT_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			int cardType = 4;
			tdsEnabledCard = getNew3DS2CardNumber(issuerValue, beginRgValue, endRgValue, cardType);
			//enable3DS2ForCard(tdsEnabledCard, issuerValue, inputMap);
			value2 = tdsEnabledCard;
		} else if (value.contains("{3DS2_NEW_AMEX_CREDIT_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			int cardType = 5;
			tdsEnabledCard = getNew3DS2CardNumber(issuerValue, beginRgValue, endRgValue, cardType);
			//enable3DS2ForCard(tdsEnabledCard, issuerValue, inputMap);
			value2 = tdsEnabledCard;
		} else if (value.contains("{3DS2_NEW_AMEX_PREPAID_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			int cardType = 6;
			tdsEnabledCard = getNew3DS2CardNumber(issuerValue, beginRgValue, endRgValue, cardType);
			//enable3DS2ForCard(tdsEnabledCard, issuerValue, inputMap);
			value2 = tdsEnabledCard;
		} else if (value.contains("{3DS2_NEW_AMEX_PROPRIETARY_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			int cardType = 7;
			tdsEnabledCard = getNew3DS2CardNumber(issuerValue, beginRgValue, endRgValue, cardType);
			//enable3DS2ForCard(tdsEnabledCard, issuerValue, inputMap);
			value2 = tdsEnabledCard;
		} else if (value.contains("{3DS2_EXISTING_CARD_MULTI}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			System.out.println("Issuer value" + issuerValue);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			if (endRgValue.length() < 10)
				endRgValue = String.valueOf((Long.parseLong(beginRgValue) + Long.parseLong(endRgValue)));

			value2 = getExistingCardNumberFor3DS2(issuerValue, beginRgValue, endRgValue, value);
		} else if (value.contains("{3DS2_EXISTING_CARD_MULTI_EMAIL}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			System.out.println("Issuer value" + issuerValue);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			value2 = getExistingCardNumberFor3DS2(issuerValue, beginRgValue, endRgValue, value);
		} else if (value.contains("{3DS2_EXISTING_CARD_MULTI_SMS}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			System.out.println("Issuer value" + issuerValue);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			value2 = getExistingCardNumberFor3DS2(issuerValue, beginRgValue, endRgValue, value);
		} else if (value.contains("{3DS2_EXISTING_CARD_SINGLE}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			System.out.println("Issuer value" + issuerValue);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			value2 = getExistingCardNumberFor3DS2(issuerValue, beginRgValue, endRgValue, value);
		} else if (value.contains("{3DS2_EXISTING_CARD_SINGLE_EMAIL}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			System.out.println("Issuer value" + issuerValue);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			value2 = getExistingCardNumberFor3DS2(issuerValue, beginRgValue, endRgValue, value);
		} else if (value.contains("{3DS2_EXISTING_CARD_SINGLE_SMS}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			System.out.println("Issuer value" + issuerValue);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			value2 = getExistingCardNumberFor3DS2(issuerValue, beginRgValue, endRgValue, value);
		} else if (value.contains("{3DS2_EXISTING_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue=null,endRgValue=null;
			try{
			beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			}catch(Exception e){
			
			}
			if(beginRgValue==null || endRgValue == null || beginRgValue.equals("") || endRgValue.equals("")){
				String rangeName = inputMap.get("Range");
				if(rangeName==null || rangeName.length()<1) {
					rangeName=inputMap.get("RangeName");
				}
				if(rangeName==null ||rangeName.length()<1) {
					System.out.println("***************ERROR   Range Name is not passed");
				}
				System.out.println("############"+issuerValue+rangeName);
				List<String> beginEndRange = getBeginAndEndRange(issuerValue,rangeName);
				beginRgValue = beginEndRange.get(0);
				endRgValue = beginEndRange.get(1);
			}
			
			if (endRgValue.length() < 10)
				endRgValue = String.valueOf((Long.parseLong(beginRgValue) + Long.parseLong(endRgValue)));

			value2 = getExistingCardNumberFor3DS2(issuerValue, beginRgValue, endRgValue, value);
		} else if (value.contains("{3DS2_LOCKED_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			System.out.println("Issuer value" + issuerValue);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			value2 = getExistingCardNumberFor3DS2(issuerValue, beginRgValue, endRgValue, value);
		} else if (value.contains("{NEW_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			if (endRgValue.length() < 10)
				endRgValue = String.valueOf((Long.parseLong(beginRgValue) + Long.parseLong(endRgValue)));

			value2 = getNewCardNumber(issuerValue, beginRgValue, endRgValue);
		} else if (value.contains("{EXISTING_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			value2 = getExistingCardNumber(issuerValue, beginRgValue, endRgValue);
		}else if(value.contains("{MAX_ENDRANGE_VALUE}")){
			
			String query = "SELECT max(endrange) FROM arbrandinfo WHERE REGEXP_LIKE (endrange, '^[0-9]+')";
			List<Map<String, Object>> dbResult = DBConnections.executeQueryIn3DSDB(query);
			
			Long maxRangeValue = null;
			
			for (int i = 0; i < dbResult.size(); i++) {
				for (Entry<String, Object> entryValues : dbResult.get(i)
						.entrySet()) {
					maxRangeValue = Long.parseLong(entryValues.getValue().toString());
				}
			}

			if (value.contains("+")) {
				String[] addValues = value.split("\\+");
				for (int i = 1; i < addValues.length; i++) {
					try{
					Long rangeValue =  + Long.parseLong(addValues[i]);
					maxRangeValue = maxRangeValue  + rangeValue;
					}catch(NumberFormatException e){
						System.out.println("number format exception:"+e.getMessage());
						e.printStackTrace();
					}
				}
			}
			value2 = String.valueOf(maxRangeValue);
			
		}else if(value.contains("{MAX_VISAENDRANGE_VALUE}")){
			
			List<Map<String, Object>> dbResult = null;
			if(BaseSuite.caPropMap.get("dbType3DS").equalsIgnoreCase("Oracle")){
			String query = "SELECT max(endrange) FROM arbrandinfo WHERE REGEXP_LIKE (endrange, '^[5]+')";
			dbResult = DBConnections.executeQueryIn3DSDB(query);
			}else{
				
				String query = "SELECT max(endrange) FROM arbrandinfo WHERE endrange ~ '^[4]+'";
				dbResult = DBConnections.executeQueryIn3DSDB(query);
			}
			
			Long maxRangeValue = null;
			
			for (int i = 0; i < dbResult.size(); i++) {
				for (Entry<String, Object> entryValues : dbResult.get(i)
						.entrySet()) {
					maxRangeValue = Long.parseLong(entryValues.getValue().toString());
				}
			}

			if (value.contains("+")) {
				String[] addValues = value.split("\\+");
				for (int i = 1; i < addValues.length; i++) {
					try{
					Long rangeValue =  + Long.parseLong(addValues[i]);
					maxRangeValue = maxRangeValue  + rangeValue;
					}catch(NumberFormatException e){
						System.out.println("number format exception:"+e.getMessage());
						e.printStackTrace();
					}
				}
			}
			value2 = String.valueOf(maxRangeValue);
		
		}else if(value.contains("{MAX_MCENDRANGE_VALUE}")){
			
			List<Map<String, Object>> dbResult = null;
			if(BaseSuite.caPropMap.get("dbType3DS").equalsIgnoreCase("Oracle")){
			String query = "SELECT max(endrange) FROM arbrandinfo WHERE REGEXP_LIKE (endrange, '^[5]+')";
			dbResult = DBConnections.executeQueryIn3DSDB(query);
			}else{
				
				String query = "SELECT max(endrange) FROM arbrandinfo WHERE endrange ~ '^[6]+'";
				dbResult = DBConnections.executeQueryIn3DSDB(query);
			}
			
			Long maxRangeValue = null;
			
			for (int i = 0; i < dbResult.size(); i++) {
				for (Entry<String, Object> entryValues : dbResult.get(i)
						.entrySet()) {
					maxRangeValue = Long.parseLong(entryValues.getValue().toString());
				}
			}

			if (value.contains("+")) {
				String[] addValues = value.split("\\+");
				for (int i = 1; i < addValues.length; i++) {
					try{
					Long rangeValue =  + Long.parseLong(addValues[i]);
					maxRangeValue = maxRangeValue  + rangeValue;
					}catch(NumberFormatException e){
						System.out.println("number format exception:"+e.getMessage());
						e.printStackTrace();
					}
				}
			}
			value2 = String.valueOf(maxRangeValue);
		
		}else if(value.contains("{MAX_AMEXENDRANGE_VALUE}")){
			
			List<Map<String, Object>> dbResult = null;
			if(BaseSuite.caPropMap.get("dbType3DS").equalsIgnoreCase("Oracle")){
			String query = "SELECT max(endrange) FROM arbrandinfo WHERE REGEXP_LIKE (endrange, '^[5]+')";
			dbResult = DBConnections.executeQueryIn3DSDB(query);
			}else{
				
				String query = "SELECT max(endrange) FROM arbrandinfo WHERE endrange ~ '^[3]+'";
				dbResult = DBConnections.executeQueryIn3DSDB(query);
			}
			
			Long maxRangeValue = null;
			
			for (int i = 0; i < dbResult.size(); i++) {
				for (Entry<String, Object> entryValues : dbResult.get(i)
						.entrySet()) {
					maxRangeValue = Long.parseLong(entryValues.getValue().toString());
				}
			}

			if (value.contains("+")) {
				String[] addValues = value.split("\\+");
				for (int i = 1; i < addValues.length; i++) {
					try{
					Long rangeValue =  + Long.parseLong(addValues[i]);
					maxRangeValue = maxRangeValue  + rangeValue;
					}catch(NumberFormatException e){
						System.out.println("number format exception:"+e.getMessage());
						e.printStackTrace();
					}
				}
			}
			value2 = String.valueOf(maxRangeValue);
		
		}else if (value.contains("{EXISTING_PREENROLLED_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);

			value2 = getExistingPreEnrolledCardNum(issuerValue);
		} else if (value.contains("{EXISTING_SEC_CH_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			value2 = getExistingSecCHCardNumber(issuerValue, beginRgValue, endRgValue);
		} else if (value.contains("{EXISTING_SINGLE_CH_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			value2 = getExistingSingleCHCardNumber(issuerValue, beginRgValue, endRgValue);
		} else if (value.contains("{EXISTING_ISSUER_3DS2}")) {
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			value2 = getExistingIssuer(beginRgValue, endRgValue);
		} else if (value.contains("{VISA_CREDIT_RANGE_EXISTING_ISSUER}")) {
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			int cardType = 1;
			value2 = getExistingIssuer(beginRgValue, endRgValue, cardType);
		} else if (value.contains("{VISA_DEBIT_RANGE_EXISTING_ISSUER}")) {
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			int cardType = 2;
			value2 = getExistingIssuer(beginRgValue, endRgValue, cardType);
		} else if (value.contains("{MASTER_CREDIT_RANGE_EXISTING_ISSUER}")) {
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			int cardType = 3;
			value2 = getExistingIssuer(beginRgValue, endRgValue, cardType);
		} else if (value.contains("{MASTER_DEBIT_RANGE_EXISTING_ISSUER}")) {
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			int cardType = 4;
			value2 = getExistingIssuer(beginRgValue, endRgValue, cardType);
		} else if (value.contains("{AMEX_CREDIT_RANGE_EXISTING_ISSUER}")) {
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			int cardType = 5;

			value2 = getExistingIssuer(beginRgValue, endRgValue, cardType);
		} else if (value.contains("{AMEX_PREPAID_RANGE_EXISTING_ISSUER}")) {
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			int cardType = 6;
			value2 = getExistingIssuer(beginRgValue, endRgValue, cardType);
		} else if (value.contains("{AMEX_PROPRIETARY_RANGE_EXISTING_ISSUER}")) {
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			int cardType = 7;
			value2 = getExistingIssuer(beginRgValue, endRgValue, cardType);
		} else if (value.contains("{PARTIALLY_REG_CARD}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			value2 = getPartiallyRegCardNumber(issuerValue, beginRgValue, endRgValue);
		} else if (value.contains("{CARD_FROM_OTHER}")) {
			String issuerValue = getResolvedSymbolData(inputMap.get(ISSUER), inputMap);
			String beginRgValue = getResolvedSymbolData(inputMap.get(BEGIN_RANGE), inputMap);
			String endRgValue = getResolvedSymbolData(inputMap.get(END_RANGE), inputMap);
			value2 = getOtherIssuerCard(issuerValue, beginRgValue, endRgValue);
		} else if (value.contains("{CURRENT_DATE_MONTH_YEAR}")) {
			value2 = getCurrentDateMonthYear();
		} else if (value.contains("{CURRENT_DATE}")) {
			value2 = getTodayDate();
		} else if (value.matches("\\{CURRENT_DATE_PLUS\\(\\d+\\)}")) {
			value2 = getTodayDatePlus(value);
		} else if (value.matches("\\{CURRENT_DATE_MINUS\\(\\d+\\)}")) {
			value2 = getTodayDateMinus(value);
		} else if (value.contains("{CURRENT_DAY}")) {
			value2 = getCurrentDay();
		} else if (value.contains("{TODAY_DAY_NUMBER}")) {
			value2 = getCurrentDayNumber();
		} else if (value.contains("{CURRENT_MONTH}")) {
			value2 = getTodaysMonth();
		} else if (value.matches("\\{CURRENT_MONTH_PLUS\\(\\d+\\)}")) {
			value2 = getCurrentMonthPlus(value);
		} else if (value.contains("{CURRENT_YEAR}")) {
			value2 = getTodaysYear();
		} else if (value.matches("\\{CURRENT_YEAR_PLUS\\(\\d+\\)}")) {
			value2 = getCurrentYearPlus(value);
		} else if (value.contains("{CURRENT_TIME}")) {
			value2 = getCurrentTime(value);
		} else if (value.contains("{CURRENT_SEC}")) {
			value2 = getCurrentSec(value);
		} else if (value.contains("{CURRENT_MINUTE}")) {
			value2 = getCurrentMint(value);
		} else if (value.contains("{CURRENT_HOUR}")) {
			value2 = getCurrentHour(value);
		} else if (value.matches("\\{RANDOM_STRING\\(\\d+\\)}") || value.matches("\\{RANDOM_STRING\\(\\d+\\,.+\\)}")) {
			value2 = getRandomString(value);
		} else if (value.matches("\\{FULL_EXEC_RANDOM_STRING\\(\\d+\\)}")
				|| value.matches("\\{FULL_EXEC_RANDOM_STRING\\(\\d+\\,.+\\)}")) {
			if (symbolMap.containsKey(value)) {
				value2 = symbolMap.get(value);
			} else {
				value2 = getRandomString(value);
				symbolMap.put(value, value2);

			}
		} else if (value.contains("{RANDOM_LONG}")) {
			value2 = getRandomLong(value);
		} else if (value.matches("\\{RANDOM_INT\\(\\d+,\\d+\\)}")) {
			value2 = getRandomInteger(value);
		} else if (value.contains("{RANDOM_DOUBLE}")) {
			value2 = getRandomDouble(value);
		} else if (value.matches("\\{RANDOM_DOUBLE\\(\\d+(\\.\\d+)?\\,\\d+(\\.\\d+)?\\)}")) {
			value2 = getRandomDoubleWithParams(value);
		} else if (value.contains("TIME_STAMP_STR")) {
			// value2 = getCurrentTimeStampString(value);
			value2 = getRandom_TIME_STAMP_STR(value);
			System.out.println(value2);
		}else if (value.contains("DATE_STR")) {
			// value2 = getCurrentTimeStampString(value);
			value2 = getRandom_DATE_STR(value);
			System.out.println(value2);
		}else if (value.contains("RANDOM_PHONE_NUMBER")) {
			value2 = getRandomPhoneNumber();
			System.out.println("RANDOM_PHONE_NUMBER is ......" + value2);
		} else if (value.contains("RANDOM_VISA_CARD_RANGE")) {
			value2 = getRandomVisaCardRange(value);
			System.out.println("RANDOM_VISA_CARD_RANGE ......" + value2);
		} else if (value.contains("NEWISSUER")) {
			value2 = getRandom_ISSUER_NAME(value);
			System.out.println("Random Issuer Name is----------:" + value2);
		} else if (value.contains("FUTURE_DATE")) {
			value2 = getDate(value);
		}

		else {
			value2 = value;
		}
		return value2;
	}

	private List<String> getBeginAndEndRange(String issuerValue,
			String rangeName) {
		// TODO Auto-generated method stub
		
		String query = "select BEGINRANGE,ENDRANGE from ARBRANDINFO where CARDRANGENAME='"+rangeName+"' and BANKID in(select BANKID from ARBANKINFO where BANKNAME='"+issuerValue+"')";
		
		List<Map<String, Object>> dbResult = DBConnections.executeQueryIn3DSDB(query);
		
		List<String> result = new ArrayList<String>();
		
		result.add(dbResult.get(0).get("BEGINRANGE").toString());
		result.add(dbResult.get(0).get("ENDRANGE").toString());
		
		return result;
	}

	public String getRandomPhoneNumber() {
		String phoneNumber = null;
		long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
		phoneNumber = Long.toString(number);
		return phoneNumber;
	}

	public static String getRandom_TIME_STAMP_STR(String value) throws Exception {

		String generatedValue = null;

		if (symbolMap.containsKey(value)) {
			generatedValue = symbolMap.get(value);
		} else {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			Date date = new Date();
			String str = dateFormat.format(date);
			// return str;
			// String s = RandomStringUtils.random(value.length(), value);
			symbolMap.put(value, str);
			generatedValue = symbolMap.get(value);

		}

		return generatedValue;
	}

	public static String getRandom_DATE_STR(String value) {
		String generatedValue = null;
		if (symbolMap.containsKey(value)) {
			generatedValue = symbolMap.get(value);
		} else {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			String str = dateFormat.format(date);
			symbolMap.put(value, str);
			generatedValue = symbolMap.get(value);
		}

		return generatedValue;
	}

	public static String getRandomVisaCardRange(String value) throws Exception {
		String generatedValue = null;
		if (symbolMap.containsKey(value)) {
			generatedValue = symbolMap.get(value);
		} else {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			Date date = new Date();
			String str = dateFormat.format(date);
			str = "400" + str.substring(4);
			symbolMap.put(value, str);
			generatedValue = symbolMap.get(value);
		}
		return generatedValue;
	}

	/**
	 * Based on input details invoking respective method to get 'NEW CARD'
	 * number. Both the scenarios Issuer is mandatory.
	 * 
	 * @param issuer
	 *            is either Issuer name or one/more symbols data like 'HSBC' or
	 *            "${CURRENT_DATE_MONTH_YEAR} ${CURRENT_DAY}" or
	 *            ${RANDOM_STRING(16,134567)}.
	 * @param bRange
	 *            is Begin range of the card for issuer.
	 * @param eRange
	 *            is End range of the card for issuer.
	 * @return Card Number
	 * @throws Exception
	 */

	private String getNewCardNumber(String issuer, String bRange, String eRange) throws Exception {
		String value2 = null;
		if (!Strings.isNullOrEmpty(issuer) && !Strings.isNullOrEmpty(bRange) && !Strings.isNullOrEmpty(eRange)) {
			value2 = cardNumber.getNewCardNumber(issuer, bRange, eRange);
			if (value2 == null) {
				String cNum = cardNumber.getCardNumberFromGivenRange(bRange, eRange);
				System.out.println("one cardNumber from given range ->" + cNum);
				boolean isTrue = cardNumber.convertCancelCardToNewCard(cNum, issuer);
				if (isTrue)
					value2 = cNum;
			}
		} else {

			value2 = cardNumber.getNewCardNumber(issuer);
		}
		return value2;
	}

	private String getNew3DS2CardNumber(String issuer, String bRange, String eRange, int cardType) throws Exception {
		String value2 = null;
		if (!Strings.isNullOrEmpty(issuer) && !Strings.isNullOrEmpty(bRange) && !Strings.isNullOrEmpty(eRange)) {
			value2 = cardNumber.getNewCardNumber(issuer, bRange, eRange);
			if (value2 == null) {
				String cNum = cardNumber.getCardNumberFromGivenRange(bRange, eRange);
				System.out.println("one cardNumber from given range ->" + cNum);
				boolean isTrue = cardNumber.convertCancelCardToNewCard(cNum, issuer);
				if (isTrue)
					value2 = cNum;
			}
		} else {

			value2 = cardNumber.get3DS2NewCardNumber(issuer, cardType);
		}
		return value2;
	}

	/**
	 * Based on input details invoking respective method to get 'EXISTING CARD'
	 * number. Both the scenarios Issuer is mandatory.
	 * 
	 * @param issuer
	 *            is either Issuer name or one/more symbols data like 'HSBC' or
	 *            "${CURRENT_DATE_MONTH_YEAR} ${CURRENT_DAY}" or
	 *            ${RANDOM_STRING(16,134567)}.
	 * @param bRange
	 *            is Begin range of the card for issuer
	 * @param eRange
	 *            is End range of the card for issuer
	 * @return Card Number
	 * @throws Exception
	 */
	private String getExistingCardNumber(String issuer, String bRange, String eRange) throws Exception {
		String value2 = null;
		if (!Strings.isNullOrEmpty(issuer) && !Strings.isNullOrEmpty(bRange) && !Strings.isNullOrEmpty(eRange)) {
			// value2 = cardNumber.getExistingCardNumberFromAnIssuer(issuer,
			// bRange, eRange);
			value2 = cardNumber.getExistingCardNumberFromAnIssuerForGivenRange(issuer, bRange, eRange);

		} else {
			value2 = cardNumber.getExistingCardNumberFromAnIssuer(issuer);
		}
		System.out.println("card number>>>>" + value2);
		return value2;
	}

	private String getExistingPreEnrolledCardNum(String issuer) throws Exception {

		String value = cardNumber.getExistingPreEnrolledCardNumFromAnIssuer(issuer);

		System.out.println("card number>>>>" + value);
		return value;
	}

	private String getExistingSecCHCardNumber(String issuer, String bRange, String eRange) throws Exception {
		String value2 = null;
		if (!Strings.isNullOrEmpty(issuer) && !Strings.isNullOrEmpty(bRange) && !Strings.isNullOrEmpty(eRange)) {
			value2 = cardNumber.getExistingSecCHCardNumberFromAnIssuerForGivenRange(issuer, bRange, eRange);

		} else {
			value2 = cardNumber.getExistingSecCHCardNumberFromAnIssuer(issuer);
		}
		System.out.println("card number>>>>>" + value2);

		return value2;
	}

	private String getExistingSingleCHCardNumber(String issuer, String bRange, String eRange) throws Exception {
		String value2 = null;
		if (!Strings.isNullOrEmpty(issuer) && !Strings.isNullOrEmpty(bRange) && !Strings.isNullOrEmpty(eRange)) {
			value2 = cardNumber.getExistingSingleCHCardNumberFromAnIssuerForGivenRange(issuer, bRange, eRange);

		} else {
			value2 = cardNumber.getExistingSingleCHCardNumberFromAnIssuer(issuer);
		}
		System.out.println("card number>>>>" + value2);
		return value2;
	}

	public static String getRandom_ISSUER_NAME(String value) throws Exception {

		String issuerName = null;

		if (symbolMap.containsKey(value)) {
			issuerName = symbolMap.get(value);
		} else {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			Date date = new Date();
			String str = dateFormat.format(date);
			str = "Issuer" + str;
			symbolMap.put(value, str);
			issuerName = symbolMap.get(value);
		}
		return issuerName;
	}

	/**
	 * Based on input details invoking respective method to get 'PARTIALLY CARD'
	 * number. Both the scenarios Issuer is mandatory.
	 * 
	 * @param issuer
	 *            is either Issuer name or one/more symbols data like 'HSBC' or
	 *            "${CURRENT_DATE_MONTH_YEAR} ${CURRENT_DAY}" or
	 *            ${RANDOM_STRING(16,134567)}.
	 * @param bRange
	 *            is Begin range of the card for issuer
	 * @param eRange
	 *            is End range of the card for issuer
	 * @return Card Number
	 * @throws Exception
	 */
	private String getPartiallyRegCardNumber(String issuer, String bRange, String eRange) throws Exception {
		String value2 = null;
		if (!Strings.isNullOrEmpty(issuer) && !Strings.isNullOrEmpty(bRange) && !Strings.isNullOrEmpty(eRange)) {
			value2 = cardNumber.getPartiallyRegCardNumberFromAnIssuer(issuer, bRange, eRange);
		} else {
			value2 = cardNumber.getPartiallyRegCardNumberFromAnIssuer(issuer);
		}
		return value2;
	}

	/**
	 * Based on input details invoking respective method to get 'OTHERISSUER
	 * CARD' number. Both the scenarios Issuer is mandatory.
	 * 
	 * @param issuer
	 *            is either Issuer name or one/more symbols data like 'HSBC' or
	 *            "${CURRENT_DATE_MONTH_YEAR} ${CURRENT_DAY}" or
	 *            ${RANDOM_STRING(16,134567)}.
	 * @param bRange
	 *            is Begin range of the card for issuer
	 * @param eRange
	 *            is End range of the card for issuer
	 * @return Card Number
	 * @throws Exception
	 */
	private String getOtherIssuerCard(String issuer, String bRange, String eRange) throws Exception {
		String value2 = null;
		if (!Strings.isNullOrEmpty(issuer) && !Strings.isNullOrEmpty(bRange) && !Strings.isNullOrEmpty(eRange)) {
			value2 = cardNumber.getCardFromOtherIssuer(issuer, bRange, eRange);
		} else {
			value2 = cardNumber.getCardFromOtherIssuer(issuer);
		}
		return value2;
	}

	private String getSymbolValues(String value2, Map<String, String> inputMap) throws Exception {
		String[] tokens = getMoreSymbolsFromColumn(value2);
		String temp = getSymbolData(tokens, inputMap);
		return temp;
	}

	/**
	 * This method invokes the {@link DateTimeFunction} API functions to get
	 * current day number according to input symbol
	 * 
	 * @param value
	 *            is ${CURRENT_DAY_NUMBER}
	 * @return Today's day number like, if SUNDAY number is 0, MONDAY number is
	 *         1...etc
	 */
	private String getCurrentDayNumber() {
		DateTimeFunction dtf = new DateTimeFunction();
		return dtf.todaysdayNum() + "";
	}

	/**
	 * This method invokes the {@link DateTimeFunction} API functions to get
	 * current day according to input symbol
	 * 
	 * @param value
	 *            is ${CURRENT_DAY}
	 * @return Today's day like SUNDAY/MONDAY
	 */
	private String getCurrentDay() {
		DateTimeFunction dtf = new DateTimeFunction();
		return dtf.todaysday();
	}

	/**
	 * This method invokes the {@link DateTimeFunction} API functions to get
	 * current date month and year according to input ${CURRENT_DATE_MONTH_YEAR}
	 * 
	 * @return Current Date in dd-MM-yyyy
	 */
	private String getCurrentDateMonthYear() {
		DateTimeFunction dtf = new DateTimeFunction();
		return dtf.currentDate();
	}

	/**
	 * This method invokes the {@link DateTimeFunction} API functions to get
	 * current hour according to input symbol
	 * 
	 * @param value
	 *            is ${CURRENT_HOUR}
	 * @return Current Hour
	 */
	private String getCurrentHour(String value) {
		DateTimeFunction dtf = new DateTimeFunction();
		return dtf.currenthour() + "";
	}

	/**
	 * This method invokes the {@link DateTimeFunction} API functions to get
	 * current minute according to input symbol
	 * 
	 * @param value
	 *            is ${CURRENT_MINUTE}
	 * @return Current Minute
	 */
	private String getCurrentMint(String value) {
		DateTimeFunction dtf = new DateTimeFunction();
		return dtf.currentminute() + "";
	}

	/**
	 * This method invokes the {@link DateTimeFunction} API functions to get
	 * current seconds according to input symbol
	 * 
	 * @param value
	 *            is ${CURRENT_SEC}
	 * @return Current Second
	 */
	private String getCurrentSec(String value) {
		DateTimeFunction dtf = new DateTimeFunction();
		return dtf.currentsec() + "";
	}

	/**
	 * This method invokes the {@link DateTimeFunction} API functions to get
	 * current time according to input symbol
	 * 
	 * @param value
	 *            is ${CURRENT_TIME}
	 * @return Current Time
	 */
	private String getCurrentTime(String value) {
		DateTimeFunction dtf = new DateTimeFunction();
		return dtf.currenttime() + "";
	}

	/**
	 * This method invokes the {@link RandomNumberAndString} API functions
	 * according to input value. It checks whether the value contains open("(")
	 * and closing(")") parenthesis or not, if there it continues to find the
	 * parameters before invoking the API
	 * 
	 * @param value
	 *            could expect like ${RANDOM_STRING(16)} or
	 *            ${RANDOM_STRING(16,34567)}
	 * @return Generated random string.
	 * @throws Exception
	 */
	// private String getRandomString(String value) throws Exception {
	// if(value.contains("(") && value.contains(")")) {
	// String sb = value.substring(value.indexOf("(")+1, value.indexOf(")"));
	// RandomNumberAndString randomData = new RandomNumberAndString();
	// if(sb.contains(",")){
	// String[] tokens = sb.split("\\,");
	// value =
	// randomData.generateRandomString(getIntegerValue(tokens[0].trim()),
	// tokens[1].trim());
	// } else{
	// value = randomData.generateRandomString(getIntegerValue(sb.trim()));
	// }
	// }
	// return value;
	// }
	private String getRandomString(String value) throws Exception {
		if (value.contains("(") && value.contains(")")) {
			String sb = value.substring(value.indexOf("(") + 1, value.indexOf(")"));
			RandomNumberAndString randomData = new RandomNumberAndString();
			if (sb.contains(",")) {
				String[] tokens = sb.split("\\,");
				value = randomData.generateRandomString(getIntegerValue(tokens[0].trim()), tokens[1].trim());
			} else {
				value = randomData.generateRandomString(getIntegerValue(sb.trim()));
			}
		}
		return value;
	}

	// private Integer getIntegerValue(String value) {
	// return Integer.parseInt(value.trim());
	// }

	/**
	 * This method invokes the {@link RandomNumberAndString} API function
	 * according to input value.
	 * 
	 * @param value
	 *            is ${RANDOM_LONG}
	 * @return String is Generated Random Long
	 * @throws Exception
	 */
	private String getRandomLong(String value) throws Exception {
		RandomNumberAndString randomData = new RandomNumberAndString();
		Long returnValue = randomData.generateRandLong();
		if (returnValue != null)
			value = returnValue.toString();
		return value;
	}

	/**
	 * This method invokes the {@link RandomNumberAndString} API function
	 * according to input value. It checks whether the value contains open("(")
	 * and closing(")") parenthesis or not, if there it continues to find the
	 * parameters before invoking the API
	 * 
	 * @param value
	 *            could expect like ${RANDOM_INT(10,20)}
	 * @return String is Generated random Integer.
	 * @throws Exception
	 */
	private String getRandomInteger(String value) throws Exception {
		Long returnValue = null;
		// if (value.contains("(") && value.contains(")")) {
		String sb = value.substring(value.indexOf("(") + 1, value.indexOf(")"));
		RandomNumberAndString randomData = new RandomNumberAndString();
		if (sb.contains(",")) {
			String[] tokens = sb.split("\\,");
			returnValue = randomData.generateRandInt(getIntegerValue(tokens[0].trim()),
					getIntegerValue(tokens[1].trim()));
		}
		// }
		if (returnValue != null)
			value = returnValue.toString();
		return value;
	}

	/**
	 * This method invokes the {@link RandomNumberAndString} API functions
	 * according to input value. It checks whether the value contains open("(")
	 * and closing(")") parenthesis or not, if there it continues to find the
	 * parameters before invoking the API
	 * 
	 * @param value
	 *            could expect ${RANDOM_DOUBLE} or like
	 *            ${RANDOM_DOUBLE(10.2,20.3)}
	 * @return String is Generated random Double.
	 * @throws Exception
	 */
	private String getRandomDouble(String value) throws Exception {
		RandomNumberAndString randomData = new RandomNumberAndString();
		Double returnValue = null;
		returnValue = randomData.generateRandDouble();
		if (returnValue != null)
			value = returnValue.toString();
		return value;
	}

	private String getRandomDoubleWithParams(String value) throws Exception {
		RandomNumberAndString randomData = new RandomNumberAndString();
		Double returnValue = null;
		// if (value.contains("(") && value.contains(")")) {
		String sb = value.substring(value.indexOf("(") + 1, value.indexOf(")"));
		if (sb.contains(",")) {
			String[] tokens = sb.split("\\,");
			returnValue = randomData.generateRandDouble(getFloatValue(tokens[0]), getFloatValue(tokens[1]));
		}
		// }

		if (returnValue != null)
			value = returnValue.toString();
		return value;
	}

	//

	/**
	 * This method invokes the {@link DateTimeFunction} API functions according
	 * to input value. It checks whether the value contains plus(+) or Minus(-),
	 * if exists will take the value and invoke the respective API function
	 * 
	 * @param date
	 *            is an input String ${CURRENT_DATE} or like ${CURRENT_DATE}+6
	 *            or like ${CURRENT_DATE}-3
	 * @return Date
	 * @throws Exception
	 */
	private String getActualDate(String date) throws Exception {
		String noOfDays = null;
		Integer actualDate = null;
		DateTimeFunction dtf = new DateTimeFunction();
		if (date.contains("+")) {
			noOfDays = StringUtils.substringAfter(date, "+");
			// Adding the no.of given days to current date
			if (noOfDays != null) {
				actualDate = dtf.todayPlus(getIntegerValue(noOfDays));
			}
		}
		// Expecting the date less than today's date.
		else if (date.contains("-")) {
			noOfDays = StringUtils.substringAfter(date, "-");
			if (noOfDays != null) {
				actualDate = dtf.todayMinus(getIntegerValue(noOfDays));
			}
		} else { // Returning today's date
			actualDate = dtf.today();
		}
		if (actualDate != null) {
			date = actualDate.toString();
		}
		return date;
	}

	private String getTodayDate() throws Exception {
		Integer actualDate = null;
		DateTimeFunction dtf = new DateTimeFunction();
		actualDate = dtf.today();
		return actualDate.toString();
	}

	private String getTodayDatePlus(String noOfDays) throws Exception {
		Integer actualDate = null;
		DateTimeFunction dtf = new DateTimeFunction();
		noOfDays = noOfDays.substring(noOfDays.indexOf("(") + 1, noOfDays.indexOf(")"));
		actualDate = dtf.todayPlus(getIntegerValue(noOfDays));
		if (actualDate != null) {
			noOfDays = actualDate.toString();
		}
		return noOfDays;
	}

	private String getTodayDateMinus(String value) throws Exception {
		Integer actualDate = null;
		DateTimeFunction dtf = new DateTimeFunction();
		String noOfDays = value.substring(value.indexOf("(") + 1, value.indexOf(")"));
		if (noOfDays != null || noOfDays != "") {
			actualDate = dtf.todayMinus(getIntegerValue(noOfDays));
			if (actualDate != null) {
				value = actualDate.toString();
			}
		}
		return value;
	}

	/**
	 * This method invokes the {@link DateTimeFunction} API functions according
	 * to input value. It checks whether the value contains plus(+) or not, if
	 * exists will take the value and invoke the respective API function
	 * 
	 * @param month
	 *            is an input String ${CURRENT_MONTH} or like ${CURRENT_MONTH}+1
	 * @return Month
	 * @throws Exception
	 */
	private String getActualMonth(String month) {
		String addMonths = null;
		if (month.contains("+")) {
			addMonths = StringUtils.substringAfter(month, "+");
		}
		DateTimeFunction dtf = new DateTimeFunction();
		Integer actualMonth = null;
		// Adding the months to current month
		if (addMonths != null) {
			actualMonth = dtf.currentMonthPlus(getIntegerValue(addMonths));
		} else {
			actualMonth = dtf.todaysmonth();
		}
		if (actualMonth != null) {
			month = actualMonth + "";
		}
		return month;
	}

	private String getTodaysMonth() throws Exception {
		Integer actualDate = null;
		DateTimeFunction dtf = new DateTimeFunction();
		actualDate = dtf.todaysmonth();
		return actualDate.toString();
	}

	private String getCurrentMonthPlus(String value) throws Exception {
		Integer actualMonth = null;
		DateTimeFunction dtf = new DateTimeFunction();
		String noOfMonths = value.substring(value.indexOf("(") + 1, value.indexOf(")"));
		if (noOfMonths != null || noOfMonths != "") {
			actualMonth = dtf.currentMonthPlus(getIntegerValue(noOfMonths));
			if (actualMonth != null) {
				value = actualMonth.toString();
			}
		}
		return value;
	}

	/**
	 * This method invokes the {@link DateTimeFunction} API functions according
	 * to input value. It checks whether the value contains plus(+) or not, if
	 * exists will take the value and invoke the respective API function
	 * 
	 * @param year
	 *            is an input String ${CURRENT_YEAR} or like ${CURRENT_YEAR}+3
	 * @return Year
	 * @throws Exception
	 */
	private String getActualYear(String year) {
		String addYears = null;
		if (year.contains("+")) {
			addYears = StringUtils.substringAfter(year, "+");
		}
		DateTimeFunction dtf = new DateTimeFunction();
		Integer actualYear = null;
		// Adding the years to current year
		if (addYears != null) {
			int noOfYearsToIncrease = getIntegerValue(addYears);
			actualYear = dtf.yearPlus(noOfYearsToIncrease);
			// TODO year plus function is not yet available1.
		} else {
			actualYear = dtf.todaysyear();
		}
		if (actualYear != null) {
			year = actualYear + "";
		}
		return year;
	}

	private String getTodaysYear() throws Exception {
		Integer actualYear = null;
		DateTimeFunction dtf = new DateTimeFunction();
		actualYear = dtf.todaysyear();
		return actualYear.toString();
	}

	private String getCurrentYearPlus(String value) throws Exception {
		Integer actualDate = null;
		DateTimeFunction dtf = new DateTimeFunction();
		String noOfYears = value.substring(value.indexOf("(") + 1, value.indexOf(")"));
		if (noOfYears != null || noOfYears != "") {
			actualDate = dtf.yearPlus(getIntegerValue(noOfYears));
			if (actualDate != null) {
				value = actualDate.toString();
			}
		}
		return value;
	}

	private Integer getIntegerValue(String value) {
		return Integer.parseInt(value.trim());
	}

	private Float getFloatValue(String value) {
		return Float.parseFloat(value);
	}

	private String getCurrentTimeStampString(String value) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date date = new Date();
		String str = dateFormat.format(date);
		return str;
	}

	private String getDate(String value) {
		String sb = value.substring(value.indexOf("(") + 1, value.indexOf(")"));
		DateTimeFunction date = new DateTimeFunction();
		String value2 = date.getAddDaysToCurrentDate(sb);
		return value2;
	}

	private String getExistingIssuer(String beginRange, String endRange, int cardType) throws Exception {
		String issuerName = null;
		if (!Strings.isNullOrEmpty(beginRange) && !Strings.isNullOrEmpty(endRange)) {
			// value2 = cardNumber.getExistingCardNumberFromAnIssuer(issuer,
			// bRange, eRange);
			issuerName = cardNumber.getExistingIssuer(beginRange, endRange, cardType);

		} else {
			issuerName = cardNumber.getExistingIssuer();
		}
		System.out.println("Issuer Name:" + issuerName);
		return issuerName;
	}

	private String getExistingIssuer(String beginRange, String endRange) throws Exception {
		String issuerName = null;
		if (!Strings.isNullOrEmpty(beginRange) && !Strings.isNullOrEmpty(endRange)) {
			// value2 = cardNumber.getExistingCardNumberFromAnIssuer(issuer,
			// bRange, eRange);
			issuerName = cardNumber.getExistingIssuer(beginRange, endRange);

		} else {
			issuerName = cardNumber.getExistingIssuer();
		}
		System.out.println("Issuer Name:" + issuerName);
		return issuerName;
	}

	/*public void enable3DS2ForCard(String strCardNumber, String issuerName, Map<String, String> testCaseData)
			throws CardNumberException, SQLException {
		Set<String> setEmailValues = new HashSet<String>();
		Set<String> setSMSValues = new HashSet<String>();
		String validatePhoneNumber = null;
		boolean checkValue = false;
		Map<String, Set<String>> emailOrSMSinput = new LinkedHashMap<String, Set<String>>();

		String emailOrSMSValue = null;
		TDS2CardConfigDetails tds2CardConfigDetails = new TDS2CardConfigDetails();
		tds2CardConfigDetails.setCardNumber(strCardNumber);
		tds2CardConfigDetails.setPam(strCardNumber);
		tds2CardConfigDetails.setTmLogin(BaseSuite.caPropMap.get("TMGlobalAdminLogin"));
		tds2CardConfigDetails.setTmPassword(BaseSuite.caPropMap.get("TMGlobalAdminPwd"));
		boolean userIDenabled = cardNumber.isUserIDEnabled(issuerName);
		System.out.println("User ID:" + userIDenabled);
		if (userIDenabled) {
			tds2CardConfigDetails.setChUserID(strCardNumber);
		}

		if (testCaseData.get(CONTACT) != "" && !testCaseData.get(CONTACT).isEmpty()) {
			String[] tempArray = testCaseData.get(CONTACT).split("\\r\\n|\\n|\\r");
			for (int i = 0; i < tempArray.length; i++) {
				if (tempArray[i].matches("[0-9+]+")) {
					emailOrSMSValue = tempArray[i].substring(tempArray[i].indexOf(':') + 1, tempArray[i].length());
					if (emailOrSMSValue.isEmpty() || emailOrSMSValue == null) {
						checkValue = true;
					}
					setSMSValues.add(emailOrSMSValue);
					emailOrSMSinput.put("SMS", setSMSValues);
				} else {

					emailOrSMSValue = tempArray[i].substring(tempArray[i].indexOf(':') + 1, tempArray[i].length());
					if (emailOrSMSValue.isEmpty() || emailOrSMSValue == null) {
						checkValue = true;
					}
					setEmailValues.add(emailOrSMSValue);
					emailOrSMSinput.put("EMAIL", setEmailValues);
				}

			}
			System.out.println("Email Values ***" + setEmailValues);
			System.out.println("Email SMS ***" + setSMSValues);
			System.out.println("MAP output***" + emailOrSMSinput);
			if (emailOrSMSinput.containsKey("EMAIL") && emailOrSMSinput.containsKey("SMS")) {
				Set<String> emailValues = emailOrSMSinput.get("EMAIL");
				Set<String> smsValues = emailOrSMSinput.get("SMS");
				System.out.println("Value considered for EMAIL is *****" + emailValues.iterator().next());
				System.out.println("Value considered for SMS is *****" + smsValues.iterator().next());
				tds2CardConfigDetails.setEmailAddress(emailValues.iterator().next());
				validatePhoneNumber = validateMobileNumberForEnable(smsValues.iterator().next());
				tds2CardConfigDetails.setMobilePhoneNumber(validatePhoneNumber);
			} else if (emailOrSMSinput.containsKey("SMS")) {
				Set<String> smsValues = emailOrSMSinput.get("SMS");
				System.out.println("Value considered SMS is ***" + smsValues.iterator().next());
				validatePhoneNumber = validateMobileNumberForEnable(smsValues.iterator().next());
				tds2CardConfigDetails.setMobilePhoneNumber(validatePhoneNumber);
			} else if (emailOrSMSinput.containsKey("EMAIL")) {
				Set<String> emailValues = emailOrSMSinput.get("EMAIL");
				System.out.println("Value considered EMAIL is ***" + emailValues.iterator().next());
				tds2CardConfigDetails.setEmailAddress(emailValues.iterator().next());
			} else {
				throw new CardNumberException("CONTACT field should have values as EMAIL:email_value SMS:sms_value ");
			}

		} else {
			throw new CardNumberException("CONTACT field is empty. Please add either SMS or EMAIL value ");

		}

		if (checkValue) {
			throw new CardNumberException("No values provided for the SMS or EMAIL field");
		}

		try {
			EnableCardFor3ds2API enableCardFor3ds2api = new EnableCardFor3ds2API();
			enableCardFor3ds2api.setConfigureCardFor3DS2(tds2CardConfigDetails,
					BaseSuite3DS.get3DSPropertyValue("TM_IP"));
			updateAttributes(tds2CardConfigDetails, strCardNumber, emailOrSMSinput);
		} catch (Exception e) {
			System.out.println("Exception found while enabling the card for 3DS2.0:" + e.getMessage());
		}
	}*/

	/*public void updateAttributes(TDS2CardConfigDetails tds2CardConfigDetails, String strCardNumber,
			Map<String, Set<String>> emailOrSMS) {
		try {
			tds2CardConfigDetails.setCardNumber(strCardNumber);
			tds2CardConfigDetails.setCardHolderName(tds2CardConfigDetails.getCardHolderName());
			tds2CardConfigDetails.setChUserID(strCardNumber);

			EnableCardFor3ds2API enableCardFor3ds2api = new EnableCardFor3ds2API();
			enableCardFor3ds2api.updateCardHolderAttribute(tds2CardConfigDetails, emailOrSMS);

		} catch (Exception e) {
			System.out.println("Exception found while updating the attributes: " + e.getMessage());
		}
	}*/

	private String validateMobileNumberForEnable(String phoneNumber) {
		String strMobileNumber = null;
		if (phoneNumber != null && !phoneNumber.isEmpty()) {
			if (phoneNumber.length() > 10) {
				strMobileNumber = phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length());
			} else {
				strMobileNumber = phoneNumber;
			}
		}

		return strMobileNumber;
	}

	private String getExistingCardNumberFor3DS2(String issuer, String bRange, String eRange, String symbol)
			throws Exception {
		String value2 = null;
		if (!Strings.isNullOrEmpty(issuer) && !Strings.isNullOrEmpty(bRange) && !Strings.isNullOrEmpty(eRange)) {
			// value2 = cardNumber.getExistingCardNumberFromAnIssuer(issuer,
			// bRange, eRange);
			value2 = cardNumber.getExistingCardNumberFromAnIssuerFor3DS2(issuer, bRange, eRange, symbol);

		} else {
			value2 = cardNumber.getExistingCardNumberFromAnIssuerFor3DS2(issuer, symbol);
		}
		System.out.println("card number>>>>" + value2);
		return value2;
	}

	/*private void loadMapFromProperties(Map<String, String> inputMap) {

		for (Map.Entry<String, String> e : BaseSuite3DS.rangeConfigMap.entrySet()) {
			// inputMap.putIfAbsent(e.getKey(), e.getValue());
			if (!inputMap.containsKey(e.getKey())) {
				try {
					String outputValue = getResolvedSymbolData(e.getValue(), BaseSuite3DS.rangeConfigMap);
					inputMap.put(e.getKey(), outputValue);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}

	}*/

	  
	  
}// End of class
