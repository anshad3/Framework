package com.ca._3dsapi.base;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.A128GCMEncrypter;

import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.ECDHUtility;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.X509CertUtils;

import ca.paysec.commons.cryptoclient.JweDecryptResponse;
import ca.paysec.commons.error.ApplicationRuntimeException;
import ca.paysec.commons.error.ErrorInfo;
import ca.paysec.commons.json.JSONParser;
import ca.tds2.core.exception.ErrorCommonNew;
import ca.tds2.model.SessionKeys;

public class JoseServiceJWS {

   //private static ApplicationLogger _logger = new ApplicationLogger(JoseServiceJWS.class);

   public String jwsValidateSignatureAndReturnBody(String jws) throws ApplicationRuntimeException {
      JWSObject jwsObject = null;
      try {
         jwsObject = JWSObject.parse(jws);
      } catch (ParseException e) {
         throw new ApplicationRuntimeException(ErrorInfo.newErrorInfo(ErrorCommonNew.JSON_EXCEPTION), e);
      }
      List<com.nimbusds.jose.util.Base64> x509CertChain = jwsObject.getHeader().getX509CertChain();
      X509Certificate certificate = X509CertUtils.parse(x509CertChain.get(0).decode());
      if (certificate.getSubjectDN().getName().equals(certificate.getIssuerDN().getName())) {
         certificate = X509CertUtils.parse(x509CertChain.get(x509CertChain.size() - 1).decode());
      }

      PublicKey pubKey = certificate.getPublicKey();
      String algo = pubKey.getAlgorithm();
      if (algo.contains("RSA")) {
         //_logger.debug("Verifying Sign using RSA ");
         JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) certificate.getPublicKey());
         try {
            if (!jwsObject.verify(verifier)) {
               throw new ApplicationRuntimeException(ErrorInfo.newErrorInfo(ErrorCommonNew.VERIFICATION_FAILED));
            }
         } catch (JOSEException e) {
            throw new ApplicationRuntimeException(ErrorInfo.newErrorInfo(ErrorCommonNew.JOSE_EXCEPTION), e);
         }
         return jwsObject.getPayload().toString();
      } else {
         JWSVerifier verifier;
         try {
            //_logger.debug("Verifying Sign using EC ");
            verifier = new ECDSAVerifier((ECPublicKey) certificate.getPublicKey());
            if (!jwsObject.verify(verifier)) {
               throw new ApplicationRuntimeException(ErrorInfo.newErrorInfo(ErrorCommonNew.VERIFICATION_FAILED));
            }
         } catch (JOSEException e) {
            throw new ApplicationRuntimeException(ErrorInfo.newErrorInfo(ErrorCommonNew.JOSE_EXCEPTION), e);
         }
         return jwsObject.getPayload().toString();

      }

   }

   public String jwsValidateSignatureAndReturnBodyEC(String jws) throws ApplicationRuntimeException, JOSEException {
      JWSObject jwsObject = null;
      try {
         jwsObject = JWSObject.parse(jws);
      } catch (ParseException e) {
         throw new ApplicationRuntimeException(ErrorInfo.newErrorInfo(ErrorCommonNew.JSON_EXCEPTION), e);
      }
      List<com.nimbusds.jose.util.Base64> x509CertChain = jwsObject.getHeader().getX509CertChain();
      X509Certificate certificate = X509CertUtils.parse(x509CertChain.get(0).decode());
      if (certificate.getSubjectDN().getName().equals(certificate.getIssuerDN().getName())) {
         certificate = X509CertUtils.parse(x509CertChain.get(x509CertChain.size() - 1).decode());
      }
      JWSVerifier verifier = new ECDSAVerifier((ECPublicKey) certificate.getPublicKey());
      try {
         if (!jwsObject.verify(verifier)) {
            throw new ApplicationRuntimeException(ErrorInfo.newErrorInfo(ErrorCommonNew.VERIFICATION_FAILED));
         }
      } catch (JOSEException e) {
         throw new ApplicationRuntimeException(ErrorInfo.newErrorInfo(ErrorCommonNew.JOSE_EXCEPTION), e);
      }
      return jwsObject.getPayload().toString();
   }

   public String jweEncryptA128GCM(String data, String kid, byte[] sessionKey, int counter) throws ApplicationRuntimeException {
      try {
    	  
    	  
    	  
    	  
    	  
    	  
    	  //KeyFactory kf = KeyFactory.getInstance("EC"); // or "EC" or whatever
    	  //PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(sessionKey));
    	  //
    	  
    	  

         JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128GCM).keyID(kid).build();
         Payload payload = new Payload(data);
         JWEObject jweObject = new JWEObject(header, payload);
         
         jweObject.encrypt(new A128GCMEncrypter(new SecretKeySpec(Arrays.copyOfRange(sessionKey, 0, 16), "AES"), createIVForGCM(counter)));
         
         return jweObject.serialize();
      } catch (Exception e) {
         throw new ApplicationRuntimeException(ErrorInfo.newErrorInfo(ErrorCommonNew.JOSE_EXCEPTION), e);
      }
   }

   public String jweEncryptA128CBCHS256(String data, String kid, byte[] sessionKey, byte[] iv) throws ApplicationRuntimeException {

      try {

         JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256).keyID(kid).iv(Base64URL.encode(iv)).build();
         Payload payload = new Payload(data);
         JWEObject jweObject = new JWEObject(header, payload);
         jweObject.encrypt(new DirectEncrypter(sessionKey));
         return jweObject.serialize();
      } catch (Exception e) {
         throw new ApplicationRuntimeException(ErrorInfo.newErrorInfo(ErrorCommonNew.JOSE_EXCEPTION), e);
      }
   }

   public static JweDecryptResponse jweDecrypt(String data, byte[] sessionKey) throws ApplicationRuntimeException {
      try {
         JWEObject jweObject = JWEObject.parse(data);
         
         EncryptionMethod encryptionMethod = jweObject.getHeader().getEncryptionMethod();

         if (encryptionMethod == EncryptionMethod.A128GCM) {
            sessionKey = Arrays.copyOfRange(sessionKey, 16, 32);
         }
         
         jweObject.decrypt(new DirectDecrypter(sessionKey));
         JweDecryptResponse decryptResponse = new JweDecryptResponse();
         decryptResponse.setData(jweObject.getPayload().toString());
         decryptResponse.setAlgoJWE(jweObject.getHeader().getEncryptionMethod().getName());
         return decryptResponse;
      } catch (Exception e) {
         //_logger.info("App-CReq decryption failed");
         //_logger.error(ErrorInfo.newErrorInfo(ErrorCommonNew.JOSE_EXCEPTION), e);
    	  
    	  e.printStackTrace();
         return null;
      }
   }

   private static byte[] createIVForGCM(int counter) {
      byte[] iv = new byte[12];
      iv[11] = (byte) counter;
      for (int i = 0; i < 11; i++) {
         iv[i] = (byte) 255;
      }
      return iv;
   }
   
   public static SessionKeys deriveSessionKeys(String aReqTaskResult,PrivateKey privKey)
			throws ParseException, ca.paysec.commons.json.JSONException, JOSEException {
		SessionKeys sessionKeys;
		ECKey acsECKey = ECKey.parse(aReqTaskResult);
		// ECKey acsECKey =
		// ECKey.parse("{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"tPiG0OLwo7CxwyO_yrUXyKr7ONSA6cbPg22pKl2ok8o\",\"y\":\"4RhO6DOZT4_V-sNytquzHoiHfNDdNNFsIsLKjYnpgv0\"}");
		
		
		
		String sessionKey = ECDHUtility.generateSessionKey((ECPublicKey) acsECKey.toPublicKey(),
				(ECPrivateKey) privKey, "ABCDEF123456789");
		// sessionKey="m2YYM4VlJE2k3PoyhXUQPH6516ZpVX1mA0RqNfDX19Y=";
		sessionKeys = new SessionKeys();
		sessionKeys.setSessionKey(sessionKey);
		
		// aReqTaskResult.setAresParams(aresParams);
		//aReqTaskResult.getAresParams().setSessionKeys(sessionKeys);

		return sessionKeys;
	}
   
   public static JweDecryptResponse jweiDecrypt(String data, byte[] sessionKey) throws ApplicationRuntimeException {
	      try {
	         JWEObject jweObject = JWEObject.parse(data);
	         EncryptionMethod encryptionMethod = jweObject.getHeader().getEncryptionMethod();

	         if (encryptionMethod == EncryptionMethod.A128GCM) {
	            sessionKey = Arrays.copyOfRange(sessionKey, 0, 16);
	         }
	         jweObject.decrypt(new DirectDecrypter(sessionKey));
	         JweDecryptResponse decryptResponse = new JweDecryptResponse();
	         decryptResponse.setData(jweObject.getPayload().toString());
	         decryptResponse.setAlgoJWE(jweObject.getHeader().getEncryptionMethod().getName());
	         return decryptResponse;
	      } catch (Exception e) {
	         
	         return null;
	      }
	   }
   
   public static String getOtp(String acsId) {
		String otp = null;
		try {
			// step1 load the driver class
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// step2 create the connection object
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@10.253.20.103:1521:orcl", "TMCLU001", "dost1234");

			// step3 create the statement object
			Statement stmt = con.createStatement();

			// step4 execute query
			ResultSet rs = stmt.executeQuery("select VALUE from TD_OTP_LOG where txn_id='"+acsId+"'");
			rs.next();
			otp = rs.getString(1).trim();

			// step5 close the connection object
			con.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		
		return otp;

	}



}
