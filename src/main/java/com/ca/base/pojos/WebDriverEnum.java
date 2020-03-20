package com.ca.base.pojos;

public enum WebDriverEnum {
	
	GolfStore1("Golfstore1"),
	GolfStore2("Golfstore2"),
	TMGLobalAdmin("TMGLobalAdmin"),
	TMMasterAdmin("TMMasterAdmin"),
	RAGLobalAdmin("RAGLobalAdmin"),
	RAMasterAdmin("RAMasterAdmin"),
	GolfStoreApp("GolfStoreApp"),
	flipkart("flipkart"),
	custApp("custApp"),
	Custom("Custom");


	private String value;

	WebDriverEnum() {
		value = "";
	}

	WebDriverEnum(String val) {
		value = val;
	}

	public String getValue() {
		return value;
	}
	
	public static String getDriverEnum(String driver) {
		
		WebDriverEnum mode=null;
        try{
		mode = WebDriverEnum.valueOf(driver);
        }
        catch(NullPointerException e){
        	return ("Blank status");
        }
		return mode.getValue();
	}

}
