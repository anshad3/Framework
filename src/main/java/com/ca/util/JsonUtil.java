package com.ca.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONTokener;
import org.jsoup.Connection.KeyVal;
import org.spark_project.guava.collect.MapDifference;
import org.spark_project.guava.collect.Maps;
import org.testng.Assert;

public class JsonUtil {

	/**
	 * 
	 * @return The JSONObject which adds the property of given key and value
	 */

	public String addProperty(String fileName, String keyPath, Object value) {

		FileInputStream inFile = null;
		JSONObject resultJson = null;
		String finalJsonString = null;

		try {
			inFile = new FileInputStream(fileName);
			byte[] str = new byte[inFile.available()];
			inFile.read(str);
			String text = new String(str);
			JSONObject json = new JSONObject(text);
			resultJson = iterateAndAddTheValue(json, keyPath, value);
			finalJsonString = resultJson.toString(4).replaceAll("\\\\/", "/");
			finalJsonString = finalJsonString.replace("null,", "");
			System.out.println(finalJsonString);

			return finalJsonString;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return finalJsonString;

	}

	private JSONObject iterateAndAddTheValue(JSONObject js1, String keyPath, Object valueNew) throws JSONException {

		String[] keys = keyPath.split("\\.");

		if (keys.length == 1) {
			try {

				if ((valueNew.toString().contains("[") && (valueNew.toString().contains("]")))) {

					valueNew = ((String) valueNew).substring(((String) valueNew).indexOf('[') + 1,
							((String) valueNew).indexOf(']'));
					String[] str = ((String) valueNew).split(",");
					JSONArray array = new JSONArray();
					for (int j = 0; j < str.length; j++) {
						array.put(str[j]);
					}
					js1.put(keys[0], array);
				} else if((valueNew.toString().contains("{") && (valueNew.toString().contains("}")))){
					
					JSONObject json = new JSONObject(valueNew.toString());
					js1.put(keys[0], json);
					
				}else {
					js1.put(keys[0], valueNew);
				}
				return js1;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

			String subKeyPath = keyPath.substring(keyPath.indexOf('.') + 1);
			if (js1.optJSONObject(keys[0]) != null) {
				iterateAndAddTheValue(js1.optJSONObject(keys[0]), subKeyPath, valueNew);
				return js1;
			} 
			else if (js1.optJSONArray(keys[0]) != null) {

				JSONObject jsNest = new JSONObject();
				jsNest.put("key", subKeyPath);
				jsNest.put("value", valueNew);

				js1.getJSONArray(keys[0]).put(jsNest);
				return js1;

			} 			
			else  {

				JSONObject jsonValue = new JSONObject("{}");
				js1.put(keys[0], jsonValue);

				iterateAndAddTheValue(js1.optJSONObject(keys[0]), subKeyPath, valueNew);
				System.out.println("");
				return js1;

			} /*else {
				System.out.println("Invalid input");
			}*/
			
		}
		return js1;
	}

	/**
	 * 
	 * Compares two Json file and displays the difference and common contents
	 */

	public void compareTwoJsonFiles(String sourceFilename, String fileNameToCompare) {

		ObjectMapper mapper = new ObjectMapper();
		TypeReference<Map<String, Object>> type = new TypeReference<Map<String, Object>>() {
		};

		try {
			Map<String, Object> leftMap = mapper.readValue(new File(sourceFilename), type);
			Map<String, Object> rightMap = mapper.readValue(new File(fileNameToCompare), type);

			Map<String, Object> leftFlatMap = FlatMapUtil.flatten(leftMap);
			Map<String, Object> rightFlatMap = FlatMapUtil.flatten(rightMap);

			MapDifference<String, Object> difference = Maps.difference(leftFlatMap, rightFlatMap);

			System.out.println("Entries only on" + sourceFilename + "\n--------------------------");
			difference.entriesOnlyOnLeft().forEach((key, value) -> System.out.println(key + ": " + value));

			System.out.println("\n\nEntries only on" + fileNameToCompare + "\n--------------------------");
			difference.entriesOnlyOnRight().forEach((key, value) -> System.out.println(key + ": " + value));

			System.out.println("\n\nEntries differing\n--------------------------");
			difference.entriesDiffering().forEach((key, value) -> System.out.println(key + ": " + value));

			System.out.println("\n\nEntries in common\n--------------------------");
			difference.entriesInCommon().forEach((key, value) -> System.out.println(key + ": " + value));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @return The boolean value whether the given json is against the valid
	 *         schema.
	 * @param the
	 *            Json file to validate and schema json file
	 */

	public boolean validateJsonAgainstSchema(String jsonFile, String scemaFile) {

		try {
			/*
			 * InputStream inputStreamScema =
			 * getClass().getResourceAsStream(jsonFile); InputStream
			 * inputStreamJson = getClass().getResourceAsStream(scemaFile);
			 */

			FileInputStream inputStreamScema = new FileInputStream(scemaFile);
			FileInputStream inputStreamJson = new FileInputStream(jsonFile);

			org.json.JSONObject jsonSchema = new org.json.JSONObject(new JSONTokener(inputStreamScema));
			org.json.JSONObject jsonSubject = new org.json.JSONObject(new JSONTokener(inputStreamJson));

			Schema schema = SchemaLoader.load(jsonSchema);

			schema.validate(jsonSubject);

			System.out.println("No errors found. JSON validates against the schema");

			return true;

		} catch (ValidationException e) {

			System.out.println(e.getMessage());
			e.getCausingExceptions().stream().map(ValidationException::getMessage).forEach(System.out::println);
			return false;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param Json
	 *            file path, Key to update and value to update
	 * @return The string after updating the Key and values given, Written
	 *         especially for the IRIS team, To handle the json contains the
	 *         array of Json Objects.
	 */

	public String setProperty(String fileName, String keyPath, Object valueNew) {

		FileInputStream inFile = null;
		JSONObject resultJson = null;
		String finalJsonString = null;

		try {
			inFile = new FileInputStream(fileName);
			byte[] str = new byte[inFile.available()];
			inFile.read(str);
			String text = new String(str);
			JSONObject json = new JSONObject(text);
			resultJson = iterateAndAlterTheValue(json, keyPath, valueNew);
			finalJsonString = resultJson.toString(4).replaceAll("\\\\/", "/");
			finalJsonString = finalJsonString.replace("null,", "");
			System.out.println(finalJsonString);

			return finalJsonString;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return finalJsonString;
	}


	/**
	 * @param JsonString, List of key to get all the values from json  
	 * @return The map which contains the key path corresponding json values. 
	 */
	
	public Map<String,String> getProperty(String jsonContent,List<String> keys){
		
		Map<String,String> keyValue = new HashMap<String,String>();

		try {
			
			String text = new String(jsonContent);
			JSONObject json = new JSONObject(text);
			
			
			for(String key:keys){
				
				String value = iterateAndReturnTheValue(json,key);
				
				keyValue.put(key, value);
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return keyValue;
		
	}
	
	private String iterateAndReturnTheValue(JSONObject js1, String keyPath) throws JSONException {

		String[] keys = keyPath.split("\\.");
		String resultValue=null;

		if (keys.length == 1) {

			Iterator iterator = js1.keys();
			String key = null;
			while (iterator.hasNext()) {
				key = (String) iterator.next();

				if ((js1.optJSONArray(key) == null) && (js1.optJSONObject(key) == null)) {
					if ((key.equals(keyPath))) {
						resultValue = (String) js1.get(key);
						return resultValue;
					}
					
				}else if (js1.optJSONObject(key) != null) {
					if ((key.equals(keyPath))) {
					resultValue = js1.getJSONObject(key).toString();
					return resultValue;
					}
				}else if (js1.optJSONArray(key) != null) {
					if ((key.equals(keyPath))) {
					JSONArray jArray = js1.getJSONArray(key);
					resultValue= jArray.toString();
					return resultValue;
					}
				}
			}
		} else {

			String subKeyPath = keyPath.substring(keyPath.indexOf('.') + 1);
			resultValue = iterateAndReturnTheValue(js1.optJSONObject(keys[0]), subKeyPath);
			return resultValue;
		}
		return resultValue;
	}
	

	private JSONObject iterateAndAlterTheValue(JSONObject js1, String keyPath, Object valueNew) throws JSONException {

		String[] keys = keyPath.split("\\.");

		if (keys.length == 1) {

			Iterator iterator = js1.keys();
			String key = null;
			while (iterator.hasNext()) {
				key = (String) iterator.next();

				if ((js1.optJSONArray(key) == null) && (js1.optJSONObject(key) == null)) {
					if ((key.equals(keyPath))) {
						if(valueNew == null)
							js1.put(key, JSONObject.NULL);
						else
							js1.put(key, valueNew);
						return js1;
					}
					try {
						if (js1.get("key").equals(keyPath))
							js1.put("value", valueNew);
						return js1;
					} catch (JSONException e) {
					}
				}
				if (js1.optJSONObject(key) != null) {

					JSONObject js2 = js1.getJSONObject(key);
					iterateAndAlterTheValue(js2, keyPath, valueNew);

				}
				if (js1.optJSONArray(key) != null) {
					JSONArray jArray = js1.getJSONArray(key);
					for (int i = 0; i < jArray.length(); i++) {
						try {
							if (jArray.getJSONObject(i) != null) {
								JSONObject js2 = jArray.getJSONObject(i);
								iterateAndAlterTheValue(js2, keyPath, valueNew);
							}
						} catch (Exception e) {

							if ((key.equals(keyPath))) {

								String[] str = ((String) valueNew).split(",");
								JSONArray array = new JSONArray();
								for (int j = 0; j < str.length; j++) {
									array.put(str[j]);
								}
								js1.put(key, array);
								break;
							}
						}
					}
				}
			}
		} else {

			String subKeyPath = keyPath.substring(keyPath.indexOf('.') + 1);
			
			//Temp
			JSONObject tempjson = js1.optJSONObject(keys[0]);
			
			if (js1.optJSONObject(keys[0]) != null) {
				
				JSONObject js2 = iterateAndAlterTheValue(js1.optJSONObject(keys[0]), subKeyPath, valueNew);
				js1.put(keys[0], js2);
			} else if (js1.optJSONArray(keys[0]) != null) {

				JSONArray jArray = handleArraOfJsonObjectAdd(js1.getJSONArray(keys[0]), subKeyPath, valueNew);
				js1.put(keys[0], jArray);
			} else {
				System.out.println("Invalid input");
			}
		}

		return js1;
	}

	private JSONArray handleArraOfJsonObjectAdd(JSONArray jArray, String keyPath, Object valueNew) {

		for (int i = 0; i < jArray.length(); i++) {
			try {
				if (jArray.getJSONObject(i) != null) {
					JSONObject js2 = jArray.getJSONObject(i);
					JSONObject resultJs = iterateAndAlterTheValue(js2, keyPath, valueNew);
					jArray.put(i, resultJs);
				}
			} catch (Exception e) {

			}

		}
		return jArray;

	}

	/**
	 * @param Json
	 *            file path, Key to remove the field
	 * @return The string after removing the Key and values given, Written
	 *         especially for the IRIS team, To handle the json contains the
	 *         array of Json Objects.
	 */

	public String removeProperty(String fileName, String keyPath) {

		FileInputStream inFile = null;
		JSONObject resultJson = null;
		String finalJsonString = null;

		try {
			inFile = new FileInputStream(fileName);
			byte[] str = new byte[inFile.available()];
			inFile.read(str);
			String text = new String(str);
			JSONObject json = new JSONObject(text);
			resultJson = iterateAndRemoveTheValue(json, keyPath);
			finalJsonString = resultJson.toString(4).replaceAll("\\\\/", "/");
			finalJsonString = finalJsonString.replace("null,", "");
			System.out.println(finalJsonString);

			return finalJsonString;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return finalJsonString;
	}

	private JSONObject iterateAndRemoveTheValue(JSONObject js1, String keyPath) throws JSONException {

		String[] keys = keyPath.split("\\.");

		if (keys.length == 1) {

			Iterator iterator = js1.keys();
			String key = null;
			while (iterator.hasNext()) {
				key = (String) iterator.next();

				if ((js1.optJSONArray(key) == null) && (js1.optJSONObject(key) == null)) {
					if ((key.equals(keyPath))) {
						js1.remove(key);
						return js1;
					}
					try {
						if (js1.get("key").equals(keyPath)) {
							// js1.put("value", valueNew);
							js1.remove("key");
							js1.remove("value");
							return null;
						}
					} catch (JSONException e) {
					}
				}
				if (js1.optJSONObject(key) != null) {

					JSONObject js2 = js1.getJSONObject(key);
					iterateAndRemoveTheValue(js2, keyPath);

				}
				if (js1.optJSONArray(key) != null) {
					JSONArray jArray = js1.getJSONArray(key);
					for (int i = 0; i < jArray.length(); i++) {
						try {
							if (jArray.getJSONObject(i) != null) {
								JSONObject js2 = jArray.getJSONObject(i);
								iterateAndRemoveTheValue(js2, keyPath);
							}
						} catch (Exception e) {

							if ((key.equals(keyPath))) {

								String[] str = ((String) js1.get(keyPath)).split(",");
								JSONArray array = new JSONArray();
								for (int j = 0; j < str.length; j++) {
									array.put(str[j]);
								}
								js1.put(key, array);
								break;
							}
						}
					}
				}
			}
		} else {

			String subKeyPath = keyPath.substring(keyPath.indexOf('.') + 1);
			if (js1.optJSONObject(keys[0]) != null) {
				iterateAndRemoveTheValue(js1.optJSONObject(keys[0]), subKeyPath);
			} else if (js1.optJSONArray(keys[0]) != null) {

				JSONArray jArray = handleArraOfJsonObjectRemove(js1.getJSONArray(keys[0]), subKeyPath);

				js1.put(keys[0], jArray);
			} else {
				System.out.println("Invalid input");
			}
		}
		return js1;
	}
	

	private JSONArray handleArraOfJsonObjectRemove(JSONArray jArray, String keyPath) {

		for (int i = 0; i < jArray.length(); i++) {
			try {
				if (jArray.getJSONObject(i) != null) {
					JSONObject js2 = jArray.getJSONObject(i);
					JSONObject resultJs = iterateAndRemoveTheValue(js2, keyPath);
					jArray.put(i, resultJs);
				}
			} catch (Exception e) {

			}

		}
		return jArray;

	}

	/**
	 * @param Json
	 *            file path, Map which contains series of key value pair which
	 *            has to be changed in the json In key give the json key path
	 *            separated by '.'
	 * @return The string after updating the Key and values given,
	 */

	public String alterTheJsonValuesWithFile(String filePath, Map<String, String> inputMap) {

		FileInputStream inFile = null;
		String finalJsonString = null;

		try {
			inFile = new FileInputStream(filePath);
			byte[] str = new byte[inFile.available()];
			inFile.read(str);
			String text = new String(str);
			JSONObject json = new JSONObject(text);
			String jsonString = json.toString();

			for (Map.Entry<String, String> entry : inputMap.entrySet()) {
				if (entry.getValue().equalsIgnoreCase("**DELETE**"))
					json = iterateAndRemoveTheValue(json, entry.getKey());
				else if (!jsonString.contains(entry.getKey().split("\\.")[entry.getKey().split("\\.").length - 1]))
					json = iterateAndAddTheValue(json, entry.getKey(), entry.getValue());
				else
					json = iterateAndAlterTheValue(json, entry.getKey(), entry.getValue());
			}

			finalJsonString = json.toString(4).replaceAll("\\\\/", "/");
			finalJsonString = finalJsonString.replace("null,", "");
			System.out.println(finalJsonString);

			return finalJsonString;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return finalJsonString;

	}

	public String alterTheJsonValues(String jsonOriginal, Map<String, String> inputMap) {

		FileInputStream inFile = null;
		String finalJsonString = null;

		try {

			String text = new String(jsonOriginal);
			JSONObject json = new JSONObject(text);
			String jsonString = json.toString();

			for (Map.Entry<String, String> entry : inputMap.entrySet()) {
				/*
				 * if (entry.getValue() == null) {
				 * System.out.println("Null value sent for Key Value :" + entry.getKey());
				 * Assert.fail("Null value sent for Key Value :" + entry.getKey()); }
				 */
				try {
				if (entry.getValue().equalsIgnoreCase("**DELETE**"))
					iterateAndRemoveTheValue(json, entry.getKey());
				else if (!jsonString.contains(entry.getKey().split("\\.")[entry.getKey().split("\\.").length - 1]))
					iterateAndAddTheValue(json, entry.getKey(), entry.getValue());
				else
					iterateAndAlterTheValue(json, entry.getKey(), entry.getValue());
				}catch(Exception e) {
					String dummy = entry.getKey().split("\\.")[entry.getKey().split("\\.").length - 1];
					System.out.println(dummy);
					if (!jsonString.contains(entry.getKey().split("\\.")[entry.getKey().split("\\.").length - 1]))
					iterateAndAddTheValue(json, entry.getKey(), entry.getValue());
					else
					iterateAndAlterTheValue(json, entry.getKey(), entry.getValue());
				}
			}

			finalJsonString = json.toString(4).replaceAll("\\\\/", "/");
			//finalJsonString = finalJsonString.replace("null,", "");
			//System.out.println(finalJsonString);

			return finalJsonString;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return finalJsonString;

	}
}
