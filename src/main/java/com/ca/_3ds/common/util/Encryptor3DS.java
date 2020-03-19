package com.ca._3ds.common.util;

import java.sql.SQLException;

import com.ca._3ds.utility.security.Decrypter;
import com.ca._3ds.utility.security.EncryptDecrypt;
import com.ca._3ds.utility.security.Encrypter;
import com.ca.base.BaseSuite;

public class Encryptor3DS {

	
	String strEncType = null;
	String strMasterKey = null;

	public Encryptor3DS() {
		strMasterKey = BaseSuite.caPropMap.get("masterkey");
		strEncType = BaseSuite.caPropMap.get("encType");
		
	}
	

	public synchronized String encryptString(String strToBeEncrypted,
			String strIssuerName) {

		EncryptDecrypt enc = new EncryptDecrypt();

		String strEncryptedString = null;
		try {
			strEncryptedString = enc.encryptData(strMasterKey, strIssuerName,
					strEncType, strToBeEncrypted);
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("SQL excception occured");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(" String to be Encrypted = " + strToBeEncrypted);
		System.out.println("Encrypted String = " + strEncryptedString);
		return strEncryptedString;
	}

	public synchronized String decryptString(String strToBeDecrypted,
			String strIssuerName) {

		String strPlainString = null;
		EncryptDecrypt enc = new EncryptDecrypt();

		try {
			strPlainString = enc.decryptData(strMasterKey, strIssuerName,
					strEncType, strToBeDecrypted);
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("SQL excception occured");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(" Encrypted String = " + strToBeDecrypted);
		System.out.println("Plain String afterdecryption = " + strPlainString);
		return strPlainString;
	}
	
	public synchronized String decryptCardRelatedData(String strIssuerName, String encryptedData) throws 
			SQLException, ClassNotFoundException {
		
		String strEnvType = strEncType.equalsIgnoreCase("software") ? "sw" : "hw";
		
		Decrypter de = new Decrypter();
		
		TdsQueries td=new TdsQueries();
		String strBankKeyEncrypted = td.getBankKeyWithIssuerName(strIssuerName);
		String strBankKeyPlain = de.decryptString(strBankKeyEncrypted,
				strMasterKey, strEnvType);
		String plainData = de.decryptString(encryptedData, strBankKeyPlain,
				strEnvType);
		System.out.println("Encrypted data:" + encryptedData
				+ "\ndecrypted data:" + plainData);
		return plainData;
	}

	public synchronized String encryptCardRelatedData(String strIssuerName, String dataToBeEncrypted)
			throws ClassNotFoundException, SQLException {
		String strEnvType = strEncType.equalsIgnoreCase("software") ? "sw" : "hw";
		Encrypter enc = new Encrypter();
		
		Decrypter de = new Decrypter();
		TdsQueries td=new TdsQueries();
		String strBankKeyEncrypted = td.getBankKeyWithIssuerName(strIssuerName);
		String strBankKeyPlain = de.decryptString(strBankKeyEncrypted,
				strMasterKey, strEnvType);

		Decrypter dec = new Decrypter();
		
		String encryptedData = enc.encryptString(dataToBeEncrypted,
				strBankKeyPlain, strEnvType);
		System.out.println("plain data is:" + dataToBeEncrypted
				+ "\nencrypted data is:" + encryptedData);
		return encryptedData;

	}

}
