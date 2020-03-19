package com.ca._3ds.common.util;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.json.simple.parser.JSONParser;

public class JsonToString {
	
	public Object readFromFileAndCovertToJsonString(String jsonFile)
	{
		Object obj= null;
		JSONParser parser = new JSONParser();
		try {
			Reader reader = new FileReader(System.getProperty("user.dir")+File.separator+jsonFile);
			 obj = parser.parse(reader);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

}
