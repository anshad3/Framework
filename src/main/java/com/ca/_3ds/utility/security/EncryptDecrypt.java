package com.ca._3ds.utility.security;

import java.sql.SQLException;

import com.ca._3ds.common.util.TdsQueries;

public class EncryptDecrypt {
	
	
	public String decryptData(String masterKey, String encType,
			String strIssuerName, String encryptedData) throws 
			SQLException, ClassNotFoundException {
		String strEnvType = encType.equalsIgnoreCase("software") ? "sw" : "hw";
		//DBConnection db = new DBConnection();
		TdsQueries td=new TdsQueries();
		
		Decrypter de = new Decrypter();
		String strBankKeyEncrypted = td.getBankKeyWithIssuerName(strIssuerName);
		String strBankKeyPlain = de.decryptString(strBankKeyEncrypted,
				masterKey, strEnvType);
		String plainData = de.decryptString(encryptedData, strBankKeyPlain,
				strEnvType);
		System.out.println("Encrypted data:" + encryptedData
				+ "\ndecrypted data:" + plainData);
		return plainData;
	}

	public String encryptData(String masterKey, String issuerName,
			String encType, String dataToBeEncrypted)
			throws ClassNotFoundException, SQLException {
		String strEnvType = encType.equalsIgnoreCase("software") ? "sw" : "hw";
		Encrypter enc = new Encrypter();
		TdsQueries td=new TdsQueries();
		String bankKeyEncrypted =  td.getBankKeyWithIssuerName(issuerName);

		Decrypter dec = new Decrypter();
		String bankKeyPlain = dec.decryptString(bankKeyEncrypted, masterKey,
				strEnvType);
		String encryptedData = enc.encryptString(dataToBeEncrypted,
				bankKeyPlain, strEnvType);
		System.out.println("plain data is:" + dataToBeEncrypted
				+ "\nencrypted data is:" + encryptedData);
		return encryptedData;

	}

}
