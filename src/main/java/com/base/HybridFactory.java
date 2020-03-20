package com.base;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import com.base.pojos.ElementReposPojo;
import com.ca.util.ExcelException;
import com.ca.util.ReadExcel;

public class HybridFactory {

	public static boolean reposIntitialised;
	
	public static String elementReposFileName = null;
	
	public static Map<String,ElementReposPojo> elementReposMap = new HashMap<String,ElementReposPojo>();
	
	public synchronized static void initialiseRepos(){
		
		if(reposIntitialised)
			return;
		
		String strReposName = BaseSuite.getCAPropertyValue("ElementRepositoriesFileName");
		String folderPath = System.getProperty("user.dir");
		elementReposFileName = folderPath+File.separator+strReposName;
		
		ReadExcel excel = new ReadExcel();
		boolean fileExist = excel.isFileExist(elementReposFileName);
		if(!fileExist){
			Assert.fail("Element Repositories File Name : "+elementReposFileName+" doesnt exist");
		}
		List<String> lstSheetNames = excel.getSheetNames();
		for(String sheetName :lstSheetNames ){
			Map<String, Map<String, String>> mapOfMapExcel = null;
			try {
				mapOfMapExcel = excel.getTestAllDataDontResolveSymbols(elementReposFileName, sheetName, "ID");
				convertMapOfMapToMapOfPojo(mapOfMapExcel);
				System.out.println("");
			} catch (ExcelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private synchronized static void convertMapOfMapToMapOfPojo(Map<String, Map<String, String>> mapOfMapExcel) {
		
		if(mapOfMapExcel==null){
			return;
		}
		
		for(String reposId : mapOfMapExcel.keySet()){
			
			Map<String, String> reposMap = mapOfMapExcel.get(reposId);
			ElementReposPojo reposPojo = new ElementReposPojo(reposMap);
			elementReposMap.put(reposId, reposPojo);
		}
		System.out.println("");
	}
	
	
}
