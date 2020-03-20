package com.utility.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.base.BaseSuite;

public class Encrypter {

	String osEnv = null;
	public synchronized String encryptString(String plainString, String key, String encType) {
		
		osEnv = BaseSuite.getCAPropertyValue("MachineOS");
		String gen3DSFileName = null;
		if(osEnv!=null && osEnv.equalsIgnoreCase("Linux")){
			gen3DSFileName="Gen3Des";
		}
		else{
			gen3DSFileName="Gen3Des.exe";
		}
		String encryptedString=null;
		String testType=null;
		EncryptDecryptOnHSM encHSM = new EncryptDecryptOnHSM();
		String hsmPin = "dost1234";
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
			String Command = "supportingtools"+File.separator+gen3DSFileName+" "+ key + " " + hsmPin+" " + testType + " -e " + plainString;

			if(encType.equalsIgnoreCase("hw")) {
				String strDBValidation = BaseSuite.caPropMap.get("GEN_PASS_PATH");
					
				Command = strDBValidation +" "+ key + " " + hsmPin+" " + testType + " -e " + plainString;
				encryptedString = encHSM.encryptStringOnHSM(Command);
			} else {
				Process p=Runtime.getRuntime().exec(Command);
				try {
					p.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
				encryptedString=reader.readLine().trim(); 
				encryptedString=encryptedString.replaceAll("(\\r|\\n)", "");

				encryptedString=reader.readLine();
			}
			
			String pattern = "(Cipher is )(.*)";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(encryptedString);
			if (m.find( )) {
				encryptedString=m.group(2);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return encryptedString;
	}
}
