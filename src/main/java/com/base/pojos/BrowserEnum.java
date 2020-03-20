package com.base.pojos;

public enum BrowserEnum {
	
	Chrome("Chrome"),
	Firefox("Firefox"),
	Edge("Edge"),
	IE("IE"),
	Headless("Headless"),
	HeadlessWithChrome("HeadlessWithChrome"),
	HeadlessWithFirefox("HeadlessWithFirefox");


	private String value;

	BrowserEnum() {
		value = "";
	}

	BrowserEnum(String val) {
		value = val;
	}

	public String getValue() {
		return value;
	}
	
	public static BrowserEnum getBrowserEnumForString(String strBrowser){
		
		if(strBrowser.equalsIgnoreCase("Chrome"))
			return BrowserEnum.Chrome;
		else if(strBrowser.equalsIgnoreCase("Firefox"))
			return BrowserEnum.Firefox;
		else if(strBrowser.equalsIgnoreCase("Edge"))
			return BrowserEnum.Edge;
		else if(strBrowser.equalsIgnoreCase("IE"))
			return BrowserEnum.IE;
		else if(strBrowser.equalsIgnoreCase("Headless"))
			return BrowserEnum.Headless;
		else if(strBrowser.equalsIgnoreCase("HeadlessWithChrome"))
			return BrowserEnum.HeadlessWithChrome;
		else if(strBrowser.equalsIgnoreCase("HeadlessWithFirefox"))
			return BrowserEnum.HeadlessWithFirefox;
		else 
			return BrowserEnum.Chrome;
		
	}
	
	public static String getBrowserEnum(String browser) {
		
		BrowserEnum mode=null;
        try{
        	mode = BrowserEnum.valueOf(browser);
        }
        catch(NullPointerException e){
        	return ("Blank status");
        }
		return mode.getValue();
	}

}
