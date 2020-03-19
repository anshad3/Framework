package com.ca.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ca._3ds.common.util.TdsQueries;
import com.ca._3ds.utility.security.Decrypter;
import com.ca._3ds.utility.security.EncryptDecrypt;
import com.ca._3ds.utility.security.Encrypter;
import com.ca.base.BaseSuite;
import com.ca.db_connection.util.DBConnections;

public class CardNumber {


private Connection con = null;
private static String strEnv_ACS = BaseSuite.caPropMap.get("env_ACS");
private static String strSWorHW = BaseSuite.caPropMap.get("envType");
private static String strMasterKey = BaseSuite.caPropMap.get("MasterKey");
//protected static String issuerName = TxnManagerMasterPage.getPropertyValue("bankname");

private boolean isCardNumberInDB(String strEncCard, Statement stmt) {
int iEncCardNumber = 0;
boolean flag = false;
try {
String query1 = "select count(*) from ARACCTHOLDERAUTH where cardnumber='"
+ strEncCard + "' and rownum<=1";
System.out.println("query1 is --" + query1);
ResultSet res1 = stmt.executeQuery(query1);
while (res1.next()) {
iEncCardNumber = res1.getInt(1);
if (iEncCardNumber > 0) {
flag = true;
return flag;
}
}
String query2 = "select count(*) from ARESTEMPPASSWORD  where cardnumber='"
+ strEncCard + "' and rownum<=1";
System.out.println("query2 is --" + query2);
ResultSet res2 = stmt.executeQuery(query2);
while (res2.next()) {
iEncCardNumber = res2.getInt(1);
if (iEncCardNumber > 0) {
flag = true;
return flag;
}
}

String query3 = "select count(*) from ARISSUERANSWERS  where PAN='"
+ strEncCard + "' and rownum<=1";
System.out.println("query3 is --" + query3);
ResultSet res3 = stmt.executeQuery(query3);
while (res3.next()) {
iEncCardNumber = res3.getInt(1);
if (iEncCardNumber > 0) {
flag = true;
return flag;
}
}

String query4 = "select count(*) from ARISSUERANSWERSHASH where PAN='"
+ strEncCard + "' and rownum<=1";
System.out.println("query4 is --" + query4);
ResultSet res4 = stmt.executeQuery(query4);
while (res4.next()) {
iEncCardNumber = res4.getInt(1);
if (iEncCardNumber > 0) {
flag = true;

return flag;
}
}
String query5 = "select count(*) from ARDELETEDACCTHOLDERAUTH where cardnumber='"
+ strEncCard + "' and rownum<=1";
System.out.println("query5 is --" + query5);
ResultSet res5 = stmt.executeQuery(query5);
while (res5.next()) {
iEncCardNumber = res5.getInt(1);
if (iEncCardNumber > 0) {
flag = true;

return flag;
}
}

} catch (Exception e) {
e.printStackTrace();
}
return flag;

}

private boolean iscardNumberBasedOnRange(String strbankID,
String beginRange, String endRange, Statement stmt)
throws SQLException {
String query = "select count(*) from ARBRANDINFO where beginrange='"
+ beginRange + "' AND endrange = '" + endRange
+ "' AND bankid=" + strbankID;
ResultSet rs = stmt.executeQuery(query);

boolean flag = false;
while (rs.next()) {
int count = rs.getInt(1);
if (count > 0) {
flag = true;
break;
}
}

return flag;
}

public String getCardNumberFromGivenRange(String beginrange, String endrange){
String cardNumber = null;
ArrayList<String> cList = null;
try {
cList = this.generateCardNumbers(beginrange, endrange);
} catch (IOException | InterruptedException e) {
e.printStackTrace();
}
RandomNumberAndString rs = new RandomNumberAndString();
if (cList != null && cList.size() >1) {
int ranInt = rs.generateRandInt(0, cList.size()-1).intValue();
cardNumber = cList.get(ranInt);
} else if (cList != null && cList.size() ==1) {
cardNumber = cList.get(0);
}
return cardNumber;
}

/* public boolean convertCancelCardToNewCard(String strCardNumber, String issuerName) {

}*/

@SuppressWarnings("resource")
public boolean convertCancelCardToNewCard(String strCardNumber, String issuerName) {
System.out.println("Card number to be delete from the tables: "+strCardNumber);
boolean isBecameNew = false;
Connection con = null;
Statement stmt = null;
ResultSet rs = null;
EncryptDecrypt enc = new EncryptDecrypt();
String strEncCard = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
strEncCard = enc.encryptData(strMasterKey, issuerName, strSWorHW, strCardNumber);
} catch (ClassNotFoundException | SQLException e1) {
e1.printStackTrace();
}

boolean isAvailable = false;
Integer deletedRowCount = null;
try {
String query1 = "select * from ARACCTHOLDERAUTH where cardnumber='" + strEncCard + "'" ;//and rownum<=1";
System.out.println("query1 is --" + query1);
rs = stmt.executeQuery(query1);
while (rs.next()) {
isAvailable = true;
isBecameNew = true;
}
if(isAvailable) {
String queryDel1 = "delete from ARACCTHOLDERAUTH where cardnumber='" + strEncCard + "'";
deletedRowCount = stmt.executeUpdate(queryDel1);
System.out.println("deletedRowCount from ARACCTHOLDERAUTH-->"+deletedRowCount);
isAvailable = false;
}

String query2 = "select * from ARESTEMPPASSWORD  where cardnumber='" + strEncCard + "'";
System.out.println("query2 is --" + query2);
rs = stmt.executeQuery(query2);
while (rs.next()) {
isAvailable = true;
isBecameNew = true;
}
if(isAvailable) {
String queryDel2 = "delete from ARESTEMPPASSWORD where cardnumber='"+ strEncCard + "'";
deletedRowCount = stmt.executeUpdate(queryDel2);
System.out.println("deletedRowCount from ARESTEMPPASSWORD-->"+deletedRowCount);
isAvailable = false;
}

String query3 = "select * from ARISSUERANSWERS  where PAN='"+ strEncCard + "'";
System.out.println("query3 is --" + query3);
rs = stmt.executeQuery(query3);
while (rs.next()) {
isAvailable = true;
isBecameNew = true;
}
if(isAvailable) {
String queryDel3 = "delete from ARISSUERANSWERS where PAN='"+ strEncCard + "'";
deletedRowCount = stmt.executeUpdate(queryDel3);
System.out.println("deletedRowCount from ARISSUERANSWERS-->"+deletedRowCount);
isAvailable = false;
}

String query4 = "select * from ARISSUERANSWERSHASH where PAN='"+ strEncCard + "' ";
System.out.println("query4 is --" + query4);
rs = stmt.executeQuery(query4);
while (rs.next()) {
isAvailable = true;
isBecameNew = true;
}
if(isAvailable) {
String queryDel4 = "delete from ARISSUERANSWERSHASH where PAN='"+ strEncCard + "'";
deletedRowCount = stmt.executeUpdate(queryDel4);
System.out.println("deletedRowCount from ARISSUERANSWERSHASH-->"+deletedRowCount);
isAvailable = false;
}

String query5 = "select * from ARDELETEDACCTHOLDERAUTH where cardnumber='"+ strEncCard + "' ";
System.out.println("query5 is --" + query5);
rs = stmt.executeQuery(query5);
while (rs.next()) {
isAvailable = true;
isBecameNew = true;
}
if(isAvailable) {
String queryDel5 = "delete from ARDELETEDACCTHOLDERAUTH where cardnumber='"+ strEncCard + "'";
deletedRowCount = stmt.executeUpdate(queryDel5);
System.out.println("deletedRowCount from ARDELETEDACCTHOLDERAUTH-->"+deletedRowCount);
}
} catch (SQLException e) {
System.out.println(e);
e.printStackTrace();
} finally{
try {
if (rs != null)
rs.close();
if (stmt != null)
stmt.close();
if (con != null)
con.close();
} catch (SQLException e) {
e.printStackTrace();
}
       
}
return isBecameNew;

}

/**
* This method is used to get unique card number . for Example
* getNewCardNumber("HSBC") this function will return cardNumber =
* "4000100010001006".
*
* @param issuer
* @return NewCardNumber
* @throws Exception
*/
public String getNewCardNumber(String issuer) throws Exception {
String sCardNum = null;
Statement stmt = null;
ArrayList<String> beginendrange = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);
if (strbankID == null) {

throw new CardNumberException("Issuer " + issuer
+ " is not exist");
} else {

ResultSet result = stmt
.executeQuery("Select beginrange , endrange from ARBRANDINFO where bankid="
+ strbankID);
beginendrange = new ArrayList<String>();
while (result.next()) {
beginendrange.add(result.getString(1));
beginendrange.add(result.getString(2));
}
System.out.println("ArrayList :" + beginendrange);
}

Iterator<String> iteratebeginendrange = beginendrange.iterator();

outerloop: while (iteratebeginendrange.hasNext()) {
String beginrange = iteratebeginendrange.next();
String endrange = iteratebeginendrange.next();

Long diff = diff(beginrange, endrange);

if (diff <= 100) {
ArrayList<String> sCardNumberList = generateCardNumbers(
beginrange, endrange);

for (String cardNum : sCardNumberList) {
sCardNum = cardNum;
String strEncCard = getEncryptedCardNumber(strbankID,
sCardNum, stmt);

if (isCardNumberInDB(strEncCard, stmt) == false) {
System.out.println("New Card Number is :"
+ sCardNum);
break outerloop;
}

}
} else {
ArrayList<Long> splitendrange = splitdiff(beginrange,
endrange, diff);

for (Long slitrange : splitendrange) {
String chunkendrange = slitrange + "";

ArrayList<String> sCardNumberList = generateCardNumbers(
beginrange, chunkendrange);

for (String cardNum : sCardNumberList) {
sCardNum = cardNum;
String strEncCard = getEncryptedCardNumber(
strbankID, sCardNum, stmt);

if (isCardNumberInDB(strEncCard, stmt) == false) {
System.out.println("New Card Number is :"
+ sCardNum);
break outerloop;
}

}

}
}

}
}

finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}
if (sCardNum == null)
throw new Exception("no new card found");
return sCardNum;

}

private ArrayList<Long> splitdiff(String beginRange, String endRange,
Long diff) {
Long x = Long.valueOf(beginRange);
ArrayList<Long> endranges = new ArrayList<Long>();

for (int i = 100; i <= diff;) {
x = x + 100;
endranges.add(x);
diff = diff - 100;
}

return endranges;
}

private Long diff(String beginRange, String endRange) {

Long x = Long.valueOf(endRange);
Long y = Long.valueOf(beginRange);

Long diff = x - y;
System.out.println("Diff:" + diff);
return diff;

}

/**
* This method is used to get unique card number . for example
* getNewCardNumber("HSBC" , "4000100010001000" , "4000100090009000") this
* method will return cardnumber = "4000100010001006";
*
* @param issuer
* @param beginrange
* @param endrange
* @return NewCardNumber
* @throws Exception
*/
public String getNewCardNumber(String issuer, String beginrange,
String endrange) throws Exception {
long startTime=System.currentTimeMillis();
String newCard=null;
String sCardNum = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);

if (strbankID != null) {
if (iscardNumberBasedOnRange(strbankID, beginrange, endrange,
stmt) == true) {

Long diff = diff(beginrange, endrange);

if (diff <= 100) {
ArrayList<String> sCardNumberList = generateCardNumbers(
beginrange, endrange);
int noOfCards = sCardNumberList.size();
for (int i = 0; i < noOfCards; i++) {
int index = new RandomNumberAndString()
.generateRandInt(0, noOfCards - 1)
.intValue();
sCardNum = sCardNumberList.get(index);
String strEncCard = getEncryptedCardNumber(
strbankID, sCardNum, stmt);

if (isCardNumberInDB(strEncCard, stmt) == false) {
System.out.println("New Card Number is :"
+ sCardNum);
newCard=sCardNum;
break;
}

}
} else {
ArrayList<Long> splitendrange = splitdiff(beginrange,
endrange, diff);
outerloop: for (Long slitrange : splitendrange) {
String chunkendrange = slitrange + "";
ArrayList<String> sCardNumberList = generateCardNumbers(
beginrange, chunkendrange);
int noOfCards = sCardNumberList.size();
for (int i = 0; i < noOfCards; i++) {
int index = new RandomNumberAndString()
.generateRandInt(0, noOfCards - 1)
.intValue();
sCardNum = sCardNumberList.get(index);
String strEncCard = getEncryptedCardNumber(
strbankID, sCardNum, stmt);

if (isCardNumberInDB(strEncCard, stmt) == false) {
System.out.println("New Card Number is :"
+ sCardNum);
newCard=sCardNum;

break outerloop;
}
}

/*for (String cardNum : sCardNumberList) {
sCardNum = cardNum;
String strEncCard = getEncryptedCardNumber(
strbankID, sCardNum, stmt);

if (isCardNumberInDB(strEncCard, stmt) == false) {
System.out.println("New Card Number is :"
+ sCardNum);
newCard=sCardNum;

break outerloop;
}

}*/
}
}
} else {
throw new CardNumberException("Given range (beginrange="
+ beginrange + ") and (endrange=" + endrange
+ ")is not related to issuer=" + issuer);

}
} else {
throw new CardNumberException("Issuer " + issuer
+ " is not exist");
}

} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}

System.out.println("total time spent in new card="+(System.currentTimeMillis()-startTime)+" milli seconds");
return newCard;

}

/**
* This method is used to get unique card number from other issuer and also
* this data will help to test negative test cases. For Example
* getCardFromOtherIssuer("HSBC") , this method will return cardnumer =
* "4181100010001007".
*
* @param issuer
* @return NewCardNumberFromOtherIssuer
* @throws Exception
*/

public String getCardFromOtherIssuer(String issuer) throws Exception {
String sCardNum = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String query = "select bankname from arbankinfo where bankname!='"
+ issuer + "'";
ResultSet result = stmt.executeQuery(query);

result.next();
while (result.next()) {

String issuername = result.getString(1);
System.out.println("issuername :" + issuername);
sCardNum = getNewCardNumber(issuername);

if (sCardNum != null) {
break;
}
}

}

finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}

return sCardNum;

}

/**
* This method is used to get unique card number from other issuer and also
* this data will help to test negative test cases. For Example
* getCardFromOtherIssuer("HSBC" , "4181100010001000" , "4181100090009000")
* this method will return cardnumber ="4181100010001007"
*
* @param issuer
* @param cardRangeStart
* @param cardRangeEnd
* @return NewCardNumberFromOtherIssuer
* @throws Exception
*/

public String getCardFromOtherIssuer(String issuer, String cardRangeStart,
String cardRangeEnd) throws Exception {
String sCardNum = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String query = "select bankname from arbankinfo where bankname!='"
+ issuer + "'";
ResultSet result = stmt.executeQuery(query);

result.next();
while (result.next()) {

String issuername = result.getString(1);
System.out.println("issuername :" + issuername);
sCardNum = getNewCardNumber(issuername, cardRangeStart,
cardRangeEnd);

if (sCardNum != null) {
break;
}

}

} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}

return sCardNum;

}

/**
* This method is used to get plain existing card number for given issuer.
* For Example getExistingCardNumberFromAnIssuer("testTMSmoke42") this
* method will return CardNumber = "4000400090124243".
*
* @param issuer
* @return ExistingCardNumberFromAnIssuer
* @throws Exception
*/

public String getExistingCardNumberFromAnIssuer(String issuer)
throws Exception {
String plaincardnum = null;
String strEnvType = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);
if (strbankID != null) {
String encCardNumber = getexistcardNumber(strbankID, stmt);
String bankKey = getBankKeyForIssuer(strbankID, stmt);
System.out.println("after trimming --" + encCardNumber);

if (encCardNumber != null) {
System.out.println("encCardNumber:" + encCardNumber);

if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}

Decrypter dec = new Decrypter();

String strDecBankKey = dec.decryptString(bankKey,
strMasterKey, strEnvType);
plaincardnum = dec.decryptString(encCardNumber,
strDecBankKey, strEnvType);
} else {
System.out
.println("CardNumber is not available for this issuer:"
+ issuer);
}

} else {
throw new CardNumberException("Issuer " + issuer
+ " is not exist");
}

} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}
return plaincardnum;

}


/**
* This method is used to get plain existing Pre Enrolled card number for given issuer.
* For Example getExistingCardNumberFromAnIssuer("testTMSmoke42") this
* method will return CardNumber = "4000400090124243".
*
* @param issuer
* @return ExistingCardNumberFromAnIssuer
* @throws Exception
*/

public String getExistingPreEnrolledCardNumFromAnIssuer(String issuer)
throws Exception {
String plaincardnum = null;
String strEnvType = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);
if (strbankID != null) {
String encCardNumber = getexistPreEnrolledcardNumber(strbankID, stmt);
String bankKey = getBankKeyForIssuer(strbankID, stmt);
System.out.println("after trimming --" + encCardNumber);

if (encCardNumber != null) {
System.out.println("encCardNumber:" + encCardNumber);

if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}

Decrypter dec = new Decrypter();

String strDecBankKey = dec.decryptString(bankKey,
strMasterKey, strEnvType);
plaincardnum = dec.decryptString(encCardNumber,
strDecBankKey, strEnvType);
} else {
System.out
.println("CardNumber is not available for this issuer:"
+ issuer);
}

} else {
throw new CardNumberException("Issuer " + issuer
+ " is not exist");
}

} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}
return plaincardnum;

}

public String getExistingSecCHCardNumberFromAnIssuer(String issuer)
throws Exception {
String plaincardnum = null;
String strEnvType = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);
if (strbankID != null) {
String encCardNumber = getexistSecCHcardNumber(strbankID, stmt);
String bankKey = getBankKeyForIssuer(strbankID, stmt);
System.out.println("after trimming --" + encCardNumber);

if (encCardNumber != null) {
System.out.println("encCardNumber:" + encCardNumber);

if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}

Decrypter dec = new Decrypter();

String strDecBankKey = dec.decryptString(bankKey,
strMasterKey, strEnvType);
plaincardnum = dec.decryptString(encCardNumber,
strDecBankKey, strEnvType);
} else {
System.out
.println("CardNumber is not available for this issuer:"
+ issuer);
}

} else {
throw new CardNumberException("Issuer " + issuer
+ " is not exist");
}

} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}
return plaincardnum;
}

public String getExistingSingleCHCardNumberFromAnIssuer(String issuer)
throws Exception {
String plaincardnum = null;
String strEnvType = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);
if (strbankID != null) {
String encCardNumber = getexistSingleCHcardNumber(strbankID, stmt);
String bankKey = getBankKeyForIssuer(strbankID, stmt);
System.out.println("after trimming ---" + encCardNumber);

if (encCardNumber != null) {
System.out.println("encCardNumber:" + encCardNumber);

if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}

Decrypter dec = new Decrypter();

String strDecBankKey = dec.decryptString(bankKey,
strMasterKey, strEnvType);
plaincardnum = dec.decryptString(encCardNumber,
strDecBankKey, strEnvType);
} else {
System.out
.println("CardNumber is not available for this issuer:"
+ issuer);
}

} else {
throw new CardNumberException("Issuer " + issuer
+ " is not exist");
}

} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}

return plaincardnum;
}

/**
* This method is used to get plain CardHolder Name for given issuer and
* plain cardnumber. For Example
* getCardNameForExistingCardNumber("testTMSmoke42", "4000400090124201",
* "4000400090126200") this method will return CardName = VIJAYA.
*
* @param issuer
* @param strCardNo
* @return plain CardHolder Name
* @throws Exception
*/

public String getEncCardNumber(String issuer, String strCardNo)
throws Exception {
String encCardnumber = null;
String strEnvType = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);
Encrypter ec = new Encrypter();
String bankKey = getBankKeyForIssuer(strbankID, stmt);
if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}
Decrypter dec = new Decrypter();
System.out.println("bankKey prchu>>>"+bankKey);
String strDecBankKey = dec.decryptString(bankKey,
strMasterKey, strEnvType);
encCardnumber = ec.encryptString(strCardNo, strDecBankKey, "sw");
} catch (Exception e) {
e.printStackTrace();
}

return encCardnumber;
}

public String getCardNameForExistingCardNumber(String issuer,
String strCardNo) throws Exception {
String plaincardnum = null;
String encCardnumber = null;
String strEnvType = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);

Encrypter ec = new Encrypter();
String bankKey = getBankKeyForIssuer(strbankID, stmt);
encCardnumber = ec.encryptString(strCardNo, bankKey, strSWorHW);
if (strbankID != null) {
String encCardNumber = getexistcardHolderName(strbankID, stmt,
encCardnumber);
String bankKey1 = getBankKeyForIssuer(strbankID, stmt);
System.out.println("after trimming --" + encCardNumber);

if (encCardNumber != null) {
System.out.println("encCardNumber:" + encCardNumber);

if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}

Decrypter dec = new Decrypter();

String strDecBankKey = dec.decryptString(bankKey1,
strMasterKey, strEnvType);
plaincardnum = dec.decryptString(encCardNumber,
strDecBankKey, strEnvType);
} else {
System.out
.println("CardNumber is not available for this issuer:"
+ issuer);
}

} else {
throw new CardNumberException("Issuer " + issuer
+ " is not exist");
}

} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}
return plaincardnum;

}


/**
* This method is used to get plain existing card number for given issuer
* with ranges. For Example
* getExistingCardNumberFromAnIssuer("testTMSmoke42", "4000400090124201",
* "4000400090126200") this method will return cardNumber =
* "4000400090124243".
*
* @param issuer
* @param beginRange
* @param endRange
* @return existing cardNumber
* @throws Exception
*/
public String getExistingCardNumberFromAnIssuerForGivenRange(String issuer,
String beginRange, String endRange) throws Exception {

System.out.println("*************** Getting Existing Card Number For an Issuer ************************");
System.out.println("Issuer Name ="+issuer);
System.out.println("Begin Range ="+beginRange);
System.out.println("End  Name ="+endRange);
String plaincardnum = null;
String strEnvType = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);
System.out.println("Bank id: " + strbankID);
TdsQueries tds=new TdsQueries();
String strRangeid =tds.getRangeIDforResolvedSymbol(beginRange, endRange);
System.out.println("strRangeid Rama " + strRangeid);
if (strRangeid != null) {
String encCardNumber = getexistcardNumberForRange(strbankID,strRangeid,stmt);
if (encCardNumber != null) {
System.out.println(encCardNumber+";;;;;;;;;");
if (iscardNumberBasedOnRange(strbankID, beginRange,
endRange, stmt) == true) {
if (encCardNumber != null) {
System.out
.println("encCardNumber:>>>>>>>>>>>>" + encCardNumber);

if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}
Decrypter dec = new Decrypter();
String bankKey = getBankKeyForIssuer(strbankID,
stmt);
System.out.println("bankKey Rama>>>"+bankKey);
String strDecBankKey = dec.decryptString(bankKey,
strMasterKey, strEnvType);
System.out.println("encCardNumber Rama>>>"+encCardNumber);
plaincardnum = dec.decryptString(encCardNumber,
strDecBankKey, strEnvType);
System.out.println("plaincardnum Rama>>>"+plaincardnum);
}
} else {
throw new CardNumberException(
"Given range (beginrange=" + beginRange
+ ") and (endrange=" + endRange
+ ")is not related to issuer=" + issuer);
}
} else {
throw new CardNumberException(
"CardNumber is not exist in DB ");
}
} else {
throw new CardNumberException("Issuer " + issuer
+ " is not exist");
}
} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}

return plaincardnum;

}

public String getExistingSecCHCardNumberFromAnIssuerForGivenRange(String issuer,
String beginRange, String endRange) throws Exception {

System.out.println("*************** Getting Existing Card Number For an Issuer ************************");
System.out.println("Issuer Name ="+issuer);
System.out.println("Begin Range ="+beginRange);
System.out.println("End  Name ="+endRange);
String plaincardnum = null;
String strEnvType = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);
System.out.println("Bank id: " + strbankID);
TdsQueries tds=new TdsQueries();
String strRangeid =tds.getRangeIDforResolvedSymbol(beginRange, endRange);
System.out.println("strRangeid Rama " + strRangeid);
if (strRangeid != null) {
String encCardNumber = getexistSecCHcardNumberForRange(strbankID,strRangeid,stmt);
if (encCardNumber != null) {
System.out.println(encCardNumber+";;;;;;;;;");
if (iscardNumberBasedOnRange(strbankID, beginRange,
endRange, stmt) == true) {
if (encCardNumber != null) {
System.out
.println("encCardNumber:>>>>>>>>>>>>" + encCardNumber);

if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}
Decrypter dec = new Decrypter();
String bankKey = getBankKeyForIssuer(strbankID,
stmt);
System.out.println("bankKey Rama>>>"+bankKey);
String strDecBankKey = dec.decryptString(bankKey,
strMasterKey, strEnvType);
System.out.println("encCardNumber Rama>>>"+encCardNumber);
plaincardnum = dec.decryptString(encCardNumber,
strDecBankKey, strEnvType);
System.out.println("plaincardnum Rama>>>"+plaincardnum);
}
} else {
throw new CardNumberException(
"Given range (beginrange=" + beginRange
+ ") and (endrange=" + endRange
+ ")is not related to issuer=" + issuer);
}
} else {
throw new CardNumberException(
"CardNumber is not exist in DB ");
}
} else {
throw new CardNumberException("Issuer " + issuer
+ " is not exist");
}
} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}

return plaincardnum;

}

public String getExistingSingleCHCardNumberFromAnIssuerForGivenRange(String issuer,
String beginRange, String endRange) throws Exception {

System.out.println("*************** Getting Existing Card Number For an Issuer ************************");
System.out.println("Issuer Name ="+issuer);
System.out.println("Begin Range ="+beginRange);
System.out.println("End  Name ="+endRange);
String plaincardnum = null;
String strEnvType = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);
System.out.println("Bank id: " + strbankID);
TdsQueries tds=new TdsQueries();
String strRangeid =tds.getRangeIDforResolvedSymbol(beginRange, endRange);
System.out.println("strRangeid Rama " + strRangeid);
if (strRangeid != null) {
String encCardNumber = getexistSingleCHcardNumberForRange(strbankID,strRangeid,stmt);
if (encCardNumber != null) {
System.out.println(encCardNumber+";;;;;;;;;");
if (iscardNumberBasedOnRange(strbankID, beginRange,
endRange, stmt) == true) {
if (encCardNumber != null) {
System.out
.println("encCardNumber:>>>>>>>>>>>>" + encCardNumber);

if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}
Decrypter dec = new Decrypter();
String bankKey = getBankKeyForIssuer(strbankID,
stmt);
System.out.println("bankKey Rama>>>"+bankKey);
String strDecBankKey = dec.decryptString(bankKey,
strMasterKey, strEnvType);
System.out.println("encCardNumber Rama>>>"+encCardNumber);
plaincardnum = dec.decryptString(encCardNumber,
strDecBankKey, strEnvType);
System.out.println("plaincardnum Rama>>>"+plaincardnum);
}
} else {
throw new CardNumberException(
"Given range (beginrange=" + beginRange
+ ") and (endrange=" + endRange
+ ")is not related to issuer=" + issuer);
}
} else {
throw new CardNumberException(
"CardNumber is not exist in DB ");
}
} else {
throw new CardNumberException("Issuer " + issuer
+ " is not exist");
}
} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}

return plaincardnum;

}

/**
* This method is used to get plain existing card number for given issuer
* with ranges. For Example
* getExistingCardNumberFromAnIssuer("testTMSmoke42", "4000400090124201",
* "4000400090126200") this method will return cardNumber =
* "4000400090124243".
*
* @param issuer
* @param beginRange
* @param endRange
* @return existing cardNumber
* @throws Exception
*/
public String getExistingCardNumberFromAnIssuer(String issuer,
String beginRange, String endRange) throws Exception {

System.out.println("*************** Getting Existing Card Number For an Issuer ************************");
System.out.println("Issuer Name ="+issuer);
System.out.println("Begin Range ="+beginRange);
System.out.println("End  Name ="+endRange);
String plaincardnum = null;
String strEnvType = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);
System.out.println("Bank id: " + strbankID);
if (strbankID != null) {
String encCardNumber = getexistcardNumber(strbankID, stmt);
// boolean CheckInDB = getexistcardNumberBasedOnRange(strbankID
// , beginRange , endRange ,stmt);

if (encCardNumber != null) {
if (iscardNumberBasedOnRange(strbankID, beginRange,
endRange, stmt) == true) {
if (encCardNumber != null) {
System.out
.println("encCardNumber:>>>>>>>>>>>>" + encCardNumber);

if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}

Decrypter dec = new Decrypter();
String bankKey = getBankKeyForIssuer(strbankID,
stmt);
String strDecBankKey = dec.decryptString(bankKey,
strMasterKey, strEnvType);
plaincardnum = dec.decryptString(encCardNumber,
strDecBankKey, strEnvType);
}
} else {
throw new CardNumberException(
"Given range (beginrange=" + beginRange
+ ") and (endrange=" + endRange
+ ")is not related to issuer=" + issuer);
}
} else {
throw new CardNumberException(
"CardNumber is not exist in DB ");
}
} else {
throw new CardNumberException("Issuer " + issuer
+ " is not exist");
}
} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}

return plaincardnum;

}

/**
* This method is used to get plain Partially Registered Existing card
* number for given issuer For Example
* getPartiallyRegCardNumberFromAnIssuer("testTMSmoke42") this method will
* return cardNumber="4000400090124201";
*
* @param issuer
* @return Partially Registered Existing card Number
* @throws Exception
*/
public String getPartiallyRegCardNumberFromAnIssuer(String issuer)
throws Exception {
String plaincardnum = null;
String strEnvType = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);
if (strbankID != null) {
String encCardNumber = getPartiallyRegcardNumber(strbankID,
stmt);
String bankKey = getBankKeyForIssuer(strbankID, stmt);

if (encCardNumber != null) {
System.out.println("encCardNumber:" + encCardNumber);

if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}

Decrypter dec = new Decrypter();

String strDecBankKey = dec.decryptString(bankKey,
strMasterKey, strEnvType);
plaincardnum = dec.decryptString(encCardNumber,
strDecBankKey, strEnvType);
} else {
throw new CardNumberException(
"CardNumber is not available for this issuer:"
+ issuer);
}

} else {
throw new CardNumberException("Issuer " + issuer
+ " is not exist");
}

} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}
return plaincardnum;

}

/**
* This method is used to get plain Partially Registered Existing card
* number for given issuer and given ranges For Example
* getPartiallyRegCardNumberFromAnIssuer("testTMSmoke42",
* "4000400090124201", "4000400090126200") this method will return
* cardNumber="4000400090124201";
*
* @param issuer
* @param beginRange
* @param endRange
* @return Partially Registered Existing card Number
* @throws Exception
*/
public String getPartiallyRegCardNumberFromAnIssuer(String issuer,
String beginRange, String endRange) throws Exception {

String plaincardnum = null;
String strEnvType = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);

if (strbankID != null) {
String encCardNumber = getPartiallyRegcardNumber(strbankID,
stmt);
// boolean CheckInDB = getexistcardNumberBasedOnRange(strbankID
// , beginRange , endRange ,stmt);

if (encCardNumber != null) {
if (iscardNumberBasedOnRange(strbankID, beginRange,
endRange, stmt) == true) {
if (encCardNumber != null) {
System.out
.println("encCardNumber:" + encCardNumber);

if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}

Decrypter dec = new Decrypter();
String bankKey = getBankKeyForIssuer(strbankID,
stmt);
String strDecBankKey = dec.decryptString(bankKey,
strMasterKey, strEnvType);
plaincardnum = dec.decryptString(encCardNumber,
strDecBankKey, strEnvType);
}
} else {
throw new CardNumberException(
"Given range (beginrange=" + beginRange
+ ") and (endrange=" + endRange
+ ")is not related to issuer=" + issuer);
}
} else {
throw new CardNumberException(
"CardNumber is not available for this issuer:"
+ issuer);
}
} else {
throw new CardNumberException("Issuer " + issuer
+ " is not exist");
}
} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}

return plaincardnum;

}


/**
* Auther Rama Phalguni
* @param strbankID
* @param strRangeid
* @param stmt
* @return cardnumber
* @throws SQLException
*/
private String getexistcardNumberForRange(String strbankID, String strRangeid, Statement stmt)
throws SQLException {
String EncCardNum = null;
ResultSet result1 = stmt
.executeQuery("select cardnumber from ARACCTHOLDERAUTH where rangeid="
+ strRangeid + " and ISABRIDGEDREGISTRATION = 3"); //and cardnumber in (Select CARDNUMBER FROM ARACCTHOLDERAUTH GROUP BY  CARDNUMBER HAVING COUNT(CARDNUMBER) = 1)");

List<String> encCardNumbers = new ArrayList<String>();

while (result1.next()) {
EncCardNum = result1.getString(1);
encCardNumbers.add(EncCardNum);
System.out.println("enc card number " + EncCardNum);
System.out.println("encCardNumbers size is --"
+ encCardNumbers.size());
}

if (encCardNumbers.size() > 0) {
long index = 0;
try {
index = new RandomNumberAndString().generateRandInt(0,
encCardNumbers.size() - 1);
} catch (Exception e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
return encCardNumbers.get((int) index);
}


return EncCardNum;
}

private String getexistSecCHcardNumberForRange(String strbankID, String strRangeid, Statement stmt)
throws SQLException {
String EncCardNum = null;
ResultSet result1 = stmt
.executeQuery("select cardnumber from ARACCTHOLDERAUTH where active=1 and rangeid="
+ strRangeid + " group by CARDNUMBER having count(*)>=2");

List<String> encCardNumbers = new ArrayList<String>();

while (result1.next()) {
EncCardNum = result1.getString(1);
encCardNumbers.add(EncCardNum);
System.out.println("enc card number " + EncCardNum);
System.out.println("encCardNumbers size is --"
+ encCardNumbers.size());
}

if (encCardNumbers.size() > 0) {
long index = 0;
try {
index = new RandomNumberAndString().generateRandInt(0,
encCardNumbers.size() - 1);
} catch (Exception e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
return encCardNumbers.get((int) index);
}


return EncCardNum;
}

private String getexistSingleCHcardNumberForRange(String strbankID, String strRangeid, Statement stmt)
throws SQLException {
String EncCardNum = null;
ResultSet result1 = stmt
.executeQuery("select cardnumber from ARACCTHOLDERAUTH where active=1 and rangeid="
+ strRangeid + " group by CARDNUMBER having count(*)=1");

List<String> encCardNumbers = new ArrayList<String>();

while (result1.next()) {
EncCardNum = result1.getString(1);
encCardNumbers.add(EncCardNum);
System.out.println("enc card number " + EncCardNum);
System.out.println("encCardNumbers size is --"
+ encCardNumbers.size());
}

if (encCardNumbers.size() > 0) {
long index = 0;
try {
index = new RandomNumberAndString().generateRandInt(0,
encCardNumbers.size() - 1);
} catch (Exception e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
return encCardNumbers.get((int) index);
}


return EncCardNum;
}


private String getexistcardNumber(String strbankID, Statement stmt)
throws SQLException {
String EncCardNum = null;
ResultSet result1 = stmt
.executeQuery("select cardnumber from ARACCTHOLDERAUTH where bankid="
+ strbankID + " and ISABRIDGEDREGISTRATION = 3");

List<String> encCardNumbers = new ArrayList<String>();

while (result1.next()) {
EncCardNum = result1.getString(1);
encCardNumbers.add(EncCardNum);
System.out.println("enc card number " + EncCardNum);
System.out.println("encCardNumbers size is --"
+ encCardNumbers.size());
}

if (encCardNumbers.size() > 0) {
long index = 0;
try {
index = new RandomNumberAndString().generateRandInt(0,
encCardNumbers.size() - 1);
} catch (Exception e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
return encCardNumbers.get((int) index);
}

// Ideally we have to get the existing card from ARACCTHOLDERAUTH hence commenting the below code

/*ResultSet result2 = stmt
.executeQuery("select cardnumber from ARESTEMPPASSWORD where bankid="
+ strbankID);
while (result2.next()) {
EncCardNum = result2.getString(1);
return EncCardNum;
}

ResultSet result3 = stmt
.executeQuery("select pan from ARISSUERANSWERS where bankid="
+ strbankID);
while (result3.next()) {
EncCardNum = result3.getString(1);
return EncCardNum;
}

ResultSet result4 = stmt
.executeQuery("select pan from ARISSUERANSWERSHASH where bankid="
+ strbankID);
while (result4.next()) {
EncCardNum = result4.getString(1);
return EncCardNum;
}

ResultSet result5 = stmt
.executeQuery("select cardnumber from ARDELETEDACCTHOLDERAUTH where bankid="
+ strbankID);
while (result5.next()) {
EncCardNum = result5.getString(1);
break;
}*/
return EncCardNum;
}

private String getexistPreEnrolledcardNumber(String strbankID, Statement stmt)
throws SQLException {
String EncCardNum = null;
ResultSet result1 = stmt
.executeQuery("select PAN from ARISSUERANSWERS where BANKID="
+ strbankID +" AND PAN  NOT IN (select CARDNUMBER from ARACCTHOLDERAUTH)");

List<String> encCardNumbers = new ArrayList<String>();

while (result1.next()) {
EncCardNum = result1.getString(1);
encCardNumbers.add(EncCardNum);
System.out.println("enc card number " + EncCardNum);
System.out.println("encCardNumbers size is --"
+ encCardNumbers.size());
}

if (encCardNumbers.size() > 0) {
long index = 0;
try {
index = new RandomNumberAndString().generateRandInt(0,
encCardNumbers.size() - 1);
} catch (Exception e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
return encCardNumbers.get((int) index);
}


return EncCardNum;
}

private String getexistSecCHcardNumber(String strbankID, Statement stmt)
throws SQLException {
String EncCardNum = null;
ResultSet result1 = stmt
.executeQuery("select cardnumber from ARACCTHOLDERAUTH where active=1 and bankid="
+ strbankID + " group by CARDNUMBER having count(*)>=2");

List<String> encCardNumbers = new ArrayList<String>();

while (result1.next()) {
EncCardNum = result1.getString(1);
encCardNumbers.add(EncCardNum);
System.out.println("enc card number " + EncCardNum);
System.out.println("encCardNumbers size is --"
+ encCardNumbers.size());
}

if (encCardNumbers.size() > 0) {
long index = 0;
try {
index = new RandomNumberAndString().generateRandInt(0,
encCardNumbers.size() - 1);
} catch (Exception e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
return encCardNumbers.get((int) index);
}
return EncCardNum;
}

private String getexistSingleCHcardNumber(String strbankID, Statement stmt)
throws SQLException {
String EncCardNum = null;
ResultSet result1 = stmt
.executeQuery("select cardnumber from ARACCTHOLDERAUTH where active=1 and bankid="
+ strbankID + " group by CARDNUMBER having count(*)=1");

List<String> encCardNumbers = new ArrayList<String>();

while (result1.next()) {
EncCardNum = result1.getString(1);
encCardNumbers.add(EncCardNum);
System.out.println("enc card number " + EncCardNum);
System.out.println("encCardNumbers size is --"
+ encCardNumbers.size());
}

if (encCardNumbers.size() > 0) {
long index = 0;
try {
index = new RandomNumberAndString().generateRandInt(0,
encCardNumbers.size() - 1);
} catch (Exception e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
return encCardNumbers.get((int) index);
}
return EncCardNum;
}

private String getexistcardHolderName(String strbankID, Statement stmt,
String strCardNo) throws SQLException {
String EncCardNum = null;
ResultSet result1 = stmt
.executeQuery("select cardholdername from ARACCTHOLDERAUTH where bankid="
+ strbankID + " and ISABRIDGEDREGISTRATION = 3");

while (result1.next()) {
EncCardNum = result1.getString(1);
System.out.println("enc card number " + EncCardNum);
return EncCardNum;
}

ResultSet result2 = stmt
.executeQuery("select cardholdername from ARESTEMPPASSWORD where bankid="
+ strbankID);
while (result2.next()) {
EncCardNum = result2.getString(1);
return EncCardNum;
}

ResultSet result3 = stmt
.executeQuery("select cardholdername from ARISSUERANSWERS where bankid="
+ strbankID);
while (result3.next()) {
EncCardNum = result3.getString(1);
return EncCardNum;
}

ResultSet result4 = stmt
.executeQuery("select cardholdername from ARISSUERANSWERSHASH where bankid="
+ strbankID);
while (result4.next()) {
EncCardNum = result4.getString(1);
return EncCardNum;
}

ResultSet result5 = stmt
.executeQuery("select cardholdername from ARDELETEDACCTHOLDERAUTH where bankid="
+ strbankID);
while (result5.next()) {
EncCardNum = result5.getString(1);
break;
}
return EncCardNum;
}

private String getPartiallyRegcardNumber(String strbankID, Statement stmt)
throws SQLException {
String EncCardNum = null;
ResultSet result1 = stmt
.executeQuery("select cardnumber from ARACCTHOLDERAUTH where bankid="
+ strbankID);

while (result1.next()) {
EncCardNum = result1.getString(1);
System.out.println("enc card number " + EncCardNum);
return EncCardNum;
}

return EncCardNum;
}

private String getEncryptedCardNumber(String strbankID,
String strCardNumber, Statement stmt) throws SQLException {
String strEnvType = null;
Encrypter enc = new Encrypter();
String strBankKey = getBankKeyForIssuer(strbankID, stmt);
if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}
Decrypter dec = new Decrypter();
String strDecBankKey = dec.decryptString(strBankKey, strMasterKey,
strEnvType);
String strEncCardNum = enc.encryptString(strCardNumber, strDecBankKey,
strEnvType);

return strEncCardNum;
}

private String getBankKeyForIssuer(String strbankID, Statement stmt)
throws SQLException {
String strBankKey = null;
String query = "select bankkey from arbankinfo where bankid = '"
+ strbankID + "'";
ResultSet res = stmt.executeQuery(query);
while (res.next()) {
strBankKey = res.getString(1);
System.out.println("bankk keyt is --" + strBankKey);
}
return strBankKey;
}

private ArrayList<String> generateCardNumbers(String beginrange,
String endrange) throws IOException, InterruptedException {
String sCommand = BaseSuite.caPropMap.get("pathofgcnexe");
sCommand = System.getProperty("user.dir") + sCommand + "  "
+ beginrange + "  " + endrange;
String line = null;
ArrayList<String> cardnumber = new ArrayList<String>();
if (strEnv_ACS.equalsIgnoreCase("Linux")) {
try {
Process p = Runtime.getRuntime().exec(sCommand);
BufferedReader in = new BufferedReader(new InputStreamReader(
p.getInputStream()));
while ((line = in.readLine()) != null) {
// System.out.println(line);
cardnumber.add(line);

}
p.waitFor();

} catch (IOException e) {
e.printStackTrace();
}

}

return cardnumber;
}

private String getBankID(String strIssuerName, Statement stmt)
throws SQLException {
String strBankID = null;
try {
System.out.println("IssuerName issssss:  " + strIssuerName);
String query = "select bankid from arbankinfo where bankname='"
+ strIssuerName + "'";
System.out.println("Query = "+query);
ResultSet res = stmt.executeQuery(query);

while (res.next()) {
strBankID = res.getString(1);
System.out.println("bank id is>>>> " + strBankID);
}

} catch (Exception e) {
e.printStackTrace();
}
return strBankID;
}


public String get3DS2NewCardNumber(String issuer,int cardType) throws Exception {
String sCardNum = null;
Statement stmt = null;
ArrayList<String> beginendrange = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);
if (strbankID == null) {

throw new CardNumberException("Issuer " + issuer
+ " is not exist");
} else {

ResultSet result = stmt
.executeQuery("Select beginrange , endrange from ARBRANDINFO where CARDTYPE="+cardType+" and STATUS=1 AND TD_TDS2_SUPPORTED=1 and bankid="
+ strbankID);
beginendrange = new ArrayList<String>();
while (result.next()) {
beginendrange.add(result.getString(1));
beginendrange.add(result.getString(2));
}
System.out.println("ArrayList :" + beginendrange);
}

Iterator<String> iteratebeginendrange = beginendrange.iterator();

outerloop: while (iteratebeginendrange.hasNext()) {
String beginrange = iteratebeginendrange.next();
String endrange = iteratebeginendrange.next();

Long diff = diff(beginrange, endrange);

if (diff <= 100) {
ArrayList<String> sCardNumberList = generateCardNumbers(
beginrange, endrange);

for (String cardNum : sCardNumberList) {
sCardNum = cardNum;
String strEncCard = getEncryptedCardNumber(strbankID,
sCardNum, stmt);

if (isCardNumberInDB(strEncCard, stmt) == false) {
System.out.println("New Card Number is :"
+ sCardNum);
break outerloop;
}

}
} else {
ArrayList<Long> splitendrange = splitdiff(beginrange,
endrange, diff);

for (Long slitrange : splitendrange) {
String chunkendrange = slitrange + "";

ArrayList<String> sCardNumberList = generateCardNumbers(
beginrange, chunkendrange);

for (String cardNum : sCardNumberList) {
sCardNum = cardNum;
String strEncCard = getEncryptedCardNumber(
strbankID, sCardNum, stmt);

if (isCardNumberInDB(strEncCard, stmt) == false) {
System.out.println("New Card Number is :"
+ sCardNum);
break outerloop;
}

}

}
}

}
}

finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}
if (sCardNum == null)
throw new Exception("no new card found");
return sCardNum;

}

public String getExistingIssuer() throws Exception {
String issuerName=null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String query = "select BANKNAME from ARBANKINFO where BANKID=(select BANKID from ARBRANDINFO where STATUS=1 AND TD_TDS2_SUPPORTED=1 and ROWNUM = 1)";

ResultSet res = stmt.executeQuery(query);
while (res.next()) {
issuerName = res.getString(1);
System.out.println("Bank Name:" + issuerName);
}
if(issuerName==null)
{
throw new CardNumberException("Issuer does not exists");
}
}
catch(Exception e)
{
e.printStackTrace();
}
finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}
return issuerName;
}

public String getExistingIssuer(String beginRange, String endRange,int cardType) throws Exception{
String issuerName=null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();


String query = "select BANKNAME from ARBANKINFO where BANKID=(select BANKID from ARBRANDINFO where CARDTYPE="+cardType+" and STATUS=1 AND TD_TDS2_SUPPORTED=1 "
+ "and BEGINRANGE='"+ beginRange +"' and ENDRANGE='"+endRange+"')";

System.out.println("Query: "+query);

ResultSet res = stmt.executeQuery(query);
while (res.next()) {
issuerName = res.getString(1);
System.out.println("Bank Name:" + issuerName);
}
if(issuerName==null)
{
throw new CardNumberException("Amex Proprietary card issuer for given Begin range:"+beginRange+" and End range:"+endRange+", does not exists");
}
}
catch(Exception e)
{
e.printStackTrace();
}
finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}
return issuerName;
}

public String getExistingIssuer(String beginRange, String endRange) throws Exception{
String issuerName=null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();

String query = "select BANKNAME from ARBANKINFO where BANKID=(select BANKID from ARBRANDINFO where STATUS=1 AND TD_TDS2_SUPPORTED=1 "
+ "and BEGINRANGE='"+ beginRange +"' and ENDRANGE='"+endRange+"')";

System.out.println("Query: "+query);

ResultSet res = stmt.executeQuery(query);
while (res.next()) {
issuerName = res.getString(1);
System.out.println("Bank Name:" + issuerName);
}
if(issuerName==null)
{
throw new CardNumberException("Issuer for given Begin range:"+beginRange+" and End range:"+endRange+", does not exists");
}
}
catch(Exception e)
{
e.printStackTrace();
}
finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}
return issuerName;
}

public boolean isUserIDEnabled(String issuerName) throws SQLException {
String userIDenabled=null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuerName, stmt);
String query = "select useridenabled from ARBANKINFO where bankid='"+strbankID+"'";

ResultSet res = stmt.executeQuery(query);
while (res.next()) {
userIDenabled = res.getString(1);
System.out.println("User ID enabled:" + userIDenabled);
}
int userID=Integer.parseInt(userIDenabled);
System.out.println("UID:"+userID);

if (userIDenabled == null) {

throw new CardNumberException("Issuer " + issuerName + ", user id enabled is null");

}
else if(userID==0)
{
return false;
}


}
catch(Exception e)
{
e.printStackTrace();
}
finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}
return true;
}

public String getExistingCardNumberFromAnIssuerFor3DS2(String issuer,
String beginRange, String endRange,String value) throws Exception {

System.out.println("*************** Getting Existing Card Number For an Issuer ************************");
System.out.println("Issuer Name ="+issuer);
System.out.println("Begin Range ="+beginRange);
System.out.println("End  Name ="+endRange);
String plaincardnum = null;
String strEnvType = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);
System.out.println("Bank id: " + strbankID);
if (strbankID != null) {
String encCardNumber = getexistcardNumberFor3DS2(strbankID, stmt,value);
//String encCardNumber = getexistcardNumberFor3DS2(strbankID, stmt,value,beginRange,endRange);
if(encCardNumber==null || encCardNumber.isEmpty() || encCardNumber.equals("")){

encCardNumber = getexistcardNumberFor3DS2(strbankID, stmt,value,beginRange,endRange);

}
if (encCardNumber != null) {
if (iscardNumberBasedOnRange(strbankID, beginRange,
endRange, stmt) == true) {
if (encCardNumber != null) {
System.out
.println("encCardNumber:>>>>>>>>>>>>" + encCardNumber);

if(strSWorHW==null) {
	strSWorHW="sw";
}
else if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}

Decrypter dec = new Decrypter();
String bankKey = getBankKeyForIssuer(strbankID,
stmt);
if(strMasterKey==null) {
	strMasterKey="MasterKey";
}
String strDecBankKey = dec.decryptString(bankKey,
strMasterKey, strEnvType);
plaincardnum = dec.decryptString(encCardNumber,
strDecBankKey, strEnvType);
}
} else {
throw new CardNumberException(
"Given range (beginrange=" + beginRange
+ ") and (endrange=" + endRange
+ ")is not related to issuer=" + issuer);
}
} else {
throw new CardNumberException(
"CardNumber is not exist in DB ");
}
} else {
throw new CardNumberException("Issuer " + issuer+ " is not exist");
}
} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}

return plaincardnum;
}


private String getexistcardNumberFor3DS2(String strbankID, Statement stmt,
String value, String beginRange, String endRange) {

String EncCardNum = null;
String query= "select cardnumber from ARACCTHOLDERAUTH where active=1 and chipsecret is not null and BANKID = "+strbankID+" and RANGEID in (select rangeid from ARBRANDINFO where BEGINRANGE ='"+beginRange+"' and ENDRANGE = '"+endRange+"')";
System.out.println("Query considered is ** "+query);
ResultSet result1=null;
try {
result1 = stmt.executeQuery(query);
} catch (SQLException e1) {
// TODO Auto-generated catch block
e1.printStackTrace();
}

List<String> encCardNumbers = new ArrayList<String>();

try {
while (result1.next()) {
EncCardNum = result1.getString(1);
encCardNumbers.add(EncCardNum);
System.out.println("enc card number " + EncCardNum);
System.out.println("encCardNumbers size is="
+ encCardNumbers.size());
}
} catch (SQLException e1) {
// TODO Auto-generated catch block
e1.printStackTrace();
}

		if (encCardNumbers.size() > 0) {
			long index = 0;
			if(encCardNumbers.size()==1) {
				index=0;
			}
			else {
			try {
				index = new RandomNumberAndString().generateRandInt(0, encCardNumbers.size() - 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			}
			return encCardNumbers.get((int) index);
		}

return EncCardNum;

}

public String getExistingCardNumberFromAnIssuerFor3DS2(String issuer,String value)
throws Exception {
String plaincardnum = null;
String strEnvType = null;
Connection con = null;
Statement stmt = null;
try {
con = DBConnections.get3DSDBConnection();
stmt = con.createStatement();
String strbankID = getBankID(issuer, stmt);
if (strbankID != null) {
String encCardNumber = getexistcardNumberFor3DS2(strbankID, stmt,value);
String bankKey = getBankKeyForIssuer(strbankID, stmt);
System.out.println("after trimming --" + encCardNumber);

if (encCardNumber != null) {
System.out.println("encCardNumber:" + encCardNumber);

if (strSWorHW.equalsIgnoreCase("software")) {
strEnvType = "sw";
} else {
strEnvType = "hw";
}

Decrypter dec = new Decrypter();

String strDecBankKey = dec.decryptString(bankKey,
strMasterKey, strEnvType);
plaincardnum = dec.decryptString(encCardNumber,
strDecBankKey, strEnvType);
} else {
throw new CardNumberException("CardNumber is not available for this issuer:"
+ issuer);
}

} else {
throw new CardNumberException("Issuer " + issuer
+ " is not exist");
}

} finally {
if (con != null) {
con.close();
System.out.println("Connection closed successfully: " + con);
}
}
return plaincardnum;

}

private String getexistcardNumberFor3DS2(String strbankID, Statement stmt,String value)
throws SQLException {
String EncCardNum = null;
String query=getQueryForExistingCards(value,strbankID);
System.out.println("Query considered is **"+query);
ResultSet result1 = stmt
.executeQuery(query);

List<String> encCardNumbers = new ArrayList<String>();

while (result1.next()) {
EncCardNum = result1.getString(1);
encCardNumbers.add(EncCardNum);
System.out.println("enc card number " + EncCardNum);
System.out.println("encCardNumbers size is --"
+ encCardNumbers.size());
}

if (encCardNumbers.size() > 0) {
long index = 0;
try {
index = new RandomNumberAndString().generateRandInt(0,
encCardNumbers.size() - 1);
} catch (Exception e) {
e.printStackTrace();
}
return encCardNumbers.get((int) index);
}

return EncCardNum;
}

private String getQueryForExistingCards(String value, String strBankID){

String query=null;
if(value.contains("{3DS2_EXISTING_CARD_MULTI}")){

query="select distinct cardnumber from ARACCTHOLDERAUTH where active=1 and MOBILEPHONE is not null and emailaddr is not null and bankid= "+ strBankID;

}else if(value.contains("{3DS2_EXISTING_CARD_MULTI_EMAIL}")){

query="select distinct a.cardnumber from ARACCTHOLDERAUTH a INNER JOIN ARACCTHOLDERATTR b on a.PROXYPAN=b.PROXYPAN and a.CARDHOLDERNAME=b.CARDHOLDERNAME "
+ "and a.ACTIVE=1 and a.PROXYPAN in (select proxypan from ARACCTHOLDERATTR where active=1 group by userid,cardholdername,proxypan having count(proxypan)>=2 and count(attributename)>=2) "
+ "and b.bankid= "+ strBankID + " and b.ATTRIBUTENAME='EMAIL' INNER JOIN ARBRANDINFO e on e.panlength=16 and a.bankid=e.bankid and a.rangeid=e.rangeid";

}else if(value.contains("{3DS2_EXISTING_CARD_MULTI_SMS}")){

query="select distinct a.cardnumber from ARACCTHOLDERAUTH a INNER JOIN ARACCTHOLDERATTR b on a.PROXYPAN=b.PROXYPAN and a.CARDHOLDERNAME=b.CARDHOLDERNAME "
+ "anda.ACTIVE=1 and a.PROXYPAN in (select proxypan from ARACCTHOLDERATTR where active=1 group by userid,cardholdername,proxypan having count(proxypan)>=2 and count(attributename)>=2) "
+ "and b.bankid= "+ strBankID + " and b.ATTRIBUTENAME='MOBILENUMBER' INNER JOIN ARBRANDINFO e on e.panlength=16 and a.bankid=e.bankid and a.rangeid=e.rangeid";

}else if(value.contains("{3DS2_EXISTING_CARD_SINGLE}")){

query="select distinct(a.cardnumber) from ARACCTHOLDERAUTH a INNER JOIN ARACCTHOLDERATTR b "
+ "on (a.PROXYPAN=b.PROXYPAN) and a.CARDHOLDERNAME=b.CARDHOLDERNAME and a.ACTIVE=1 and a.PROXYPAN in (select proxypan from ARACCTHOLDERATTR group by "
+ "userid,cardholdername,proxypan having count(*)=1) and b.bankid= "+ strBankID + " INNER JOIN ARBRANDINFO e on e.panlength=16 and a.bankid=e.bankid and a.rangeid=e.rangeid";

}else if(value.contains("{3DS2_EXISTING_CARD_SINGLE_EMAIL}")){

query="select distinct(a.cardnumber) from ARACCTHOLDERAUTH a INNER JOIN ARACCTHOLDERATTR b on (a.PROXYPAN=b.PROXYPAN) and a.ACTIVE=1 and a.CARDHOLDERNAME=b.CARDHOLDERNAME "
+ "and a.PROXYPAN in (select proxypan from ARACCTHOLDERATTR  group by proxypan having count(*)=1 and count(attributename)=1) "
+ "and b.ATTRIBUTENAME='EMAIL' and b.bankid="+ strBankID + " INNER JOIN ARBRANDINFO e on e.panlength=16 and a.bankid=e.bankid and a.rangeid=e.rangeid";

}else if(value.contains("{3DS2_EXISTING_CARD_SINGLE_SMS}")){

query="select distinct(a.cardnumber) from ARACCTHOLDERAUTH a INNER JOIN ARACCTHOLDERATTR b on (a.PROXYPAN=b.PROXYPAN) and a.ACTIVE=1 and a.CARDHOLDERNAME=b.CARDHOLDERNAME "
+ "and a.PROXYPAN in (select proxypan from ARACCTHOLDERATTR  group by proxypan having count(*)=1 and count(attributename)=1) "
+ "and b.ATTRIBUTENAME='MOBILENUMBER' and b.bankid= "+ strBankID + " INNER JOIN ARBRANDINFO e on e.panlength=16 and a.bankid=e.bankid and a.rangeid=e.rangeid";

}else if(value.contains("{3DS2_EXISTING_CARD}")){
query="select a.cardnumber from ARACCTHOLDERAUTH a, ARACCTHOLDERATTR b, ARBRANDINFO d "
+ "where a.PROXYPAN in (select proxypan from ARACCTHOLDERATTR where active=1 and chipsecret is not null group by userid,cardholdername,proxypan having count(proxypan)>=1)"
+ " and a.CARDHOLDERNAME=b.CARDHOLDERNAME and a.ACTIVE=1 and b.BANKID = "+ strBankID + " and d.panlength=16 and a.bankid=d.bankid and a.rangeid=d.rangeid";
/*+ "and c.TXN_STATUS='Y' "
+" and a.CARDNUMBER=c.CARD_NUMBER and c.txn_status_ares='C'";*/
}else if(value.contains("{3DS2_LOCKED_CARD}")){
query="select a.cardnumber from ARACCTHOLDERAUTH a, ARACCTHOLDERATTR b, ARBRANDINFO d "
+ "where a.PROXYPAN in (select proxypan from ARACCTHOLDERATTR where active=0 and chipsecret is not null group by userid,cardholdername,proxypan having count(proxypan)>=1)  "
+ "and a.CARDHOLDERNAME=b.CARDHOLDERNAME and a.ACTIVE=0 b.BANKID = "+ strBankID + " and d.panlength=16 and a.bankid=d.bankid and a.rangeid=d.rangeid";

}

return query;

}

}
