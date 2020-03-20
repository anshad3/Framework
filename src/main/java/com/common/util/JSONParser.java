package com.common.util;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;

import com.fasterxml.jackson.databind.ObjectMapper;


public class JSONParser {

  public static <T> T convertToObject(Class<T> responseClass, String respJson)  {
    T responseObj = null;
    try {
    	
    	// Add validation for invalid json - sudha
      ObjectMapper objectMapper = new ObjectMapper();
     
      responseObj = objectMapper.readValue(respJson, responseClass);
    } catch (UnrecognizedPropertyException e) {
      e.printStackTrace();
      
    } catch (JsonParseException e) {
      e.printStackTrace();
     
    } catch (IOException e) {
      e.printStackTrace();
     
    } catch (Exception e) {
      e.printStackTrace();
    }

    return responseObj;
  }
  
  public static <R> String convertToJsonString(R requestPayload) {

    ObjectMapper objectMapper = new ObjectMapper();
   
    String JSONString = null;
    try {
      JSONString = objectMapper.writeValueAsString(requestPayload);
    } 
     catch (IOException e) {
      e.printStackTrace();
       }
    return JSONString;
  }
}

