package com.base.pojos;

public enum LocatorEnum {
	
	Id("Id"),
	Name("Name"),
	ClassName("ClassName"),
	TagName("TagName"),
	LinkText("LinkText"),
	PartialLinkText("PartialLinkText"),
	Css("Css"),
	Xpath("Xpath");


	private String value;

	LocatorEnum() {
		value = "";
	}

	LocatorEnum(String val) {
		value = val;
	}

	public String getValue() {
		return value;
	}
	
	public static String getLocatorEnum(String strLocator) {
		
		LocatorEnum mode=null;
        try{
		mode = LocatorEnum.valueOf(strLocator);
        }
        catch(NullPointerException e){
        	return ("Blank status");
        }
		return mode.getValue();
	}
	
	public static LocatorEnum getLocatorEnumForString(String strLocator){
		
		if(strLocator.equalsIgnoreCase("Id"))
			return LocatorEnum.Id;
		else if(strLocator.equalsIgnoreCase("Name"))
			return LocatorEnum.Name;
		else if(strLocator.equalsIgnoreCase("ClassName"))
			return LocatorEnum.ClassName;
		else if(strLocator.equalsIgnoreCase("TagName"))
			return LocatorEnum.TagName;
		else if(strLocator.equalsIgnoreCase("LinkText"))
			return LocatorEnum.LinkText;
		else if(strLocator.equalsIgnoreCase("PartialLinkText"))
			return LocatorEnum.PartialLinkText;
		else if(strLocator.equalsIgnoreCase("Css"))
			return LocatorEnum.Css;
		else 
			return LocatorEnum.Xpath;
		
	}

}
