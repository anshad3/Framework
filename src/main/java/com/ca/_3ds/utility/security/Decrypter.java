package com.ca._3ds.utility.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ca.base.BaseSuite;

public class Decrypter {

	String osEnv = null;
	public String decryptString(String encryptedString, String key, String encType) {
		
		osEnv = BaseSuite.getCAPropertyValue("MachineOS");
		String gen3DSFileName = null;
		if(osEnv!=null && osEnv.equalsIgnoreCase("Linux")){
			gen3DSFileName="Gen3Des";
		}
		else{
			gen3DSFileName="Gen3Des.exe";
		}
		String plainString=null;
		String testType=null;
		String hsmPin = "dost1234";
		EncryptDecryptOnHSM encHSM = new EncryptDecryptOnHSM();
		if (encType.equalsIgnoreCase("sw")) {
			testType="-stest";
		}
		else if (encType.equalsIgnoreCase("hw")) {
			testType="-htest";
		}
		else {
			System.out.println("Encryption is not possible with empty type.");
			return "fail";
		}
		
		try {
			String Command = System.getProperty("user.dir")+File.separator+"supportingtools"+File.separator+gen3DSFileName+" "+ key + " " + hsmPin+" " + testType + " -d " + encryptedString;
			if(encType.equalsIgnoreCase("hw")) {
				String strDBValidation = BaseSuite.caPropMap.get("GEN_PASS_PATH");
				Command = strDBValidation +" "+ key + " " + hsmPin+" " + testType + " -d " + encryptedString;
				plainString = encHSM.encryptStringOnHSM(Command);
			} else {
				System.out.println("Here is the command: "+ Command);
				Process p=Runtime.getRuntime().exec(Command);
			//	System.out.println("process outpur is --- " + p);
			//	System.out.println("input stream is ---" +p.getInputStream());
				
				try {
					p.waitFor();
			//		System.out.println("in try block");
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(" exception in decrypter is ---" + e);
				}
			//	System.out.println("before buffer reader");
				BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
				
				if(reader!= null)
		//		System.out.println("reader is ---" + reader);
				plainString=reader.readLine().trim(); 
			//	System.out.println("plain string is ---" + plainString);
				plainString=plainString.replaceAll("(\\r|\\n)", "");
				System.out.println("Original String: " + encryptedString);
			}

			String pattern = "(Recovered Message ::)(.*)";
		//	System.out.println("patternis --" + pattern);
			Pattern r = Pattern.compile(pattern);
		//	System.out.println("pattern after compile is --" + r);
			Matcher m = r.matcher(plainString);
		//	System.out.println("matcher is --"+ m);
			if (m.find( )) {
				System.out.println("inside m.find ()");
				plainString=m.group(2);
				System.out.println("Plain string is ----"+plainString);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("exception in decrypter is ---- " +e);
		}
		System.out.println("plain string is ---" + plainString);
		return plainString;
	}
}
