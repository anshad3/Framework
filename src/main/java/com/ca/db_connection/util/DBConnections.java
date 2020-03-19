package com.ca.db_connection.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ca._3ds.common.util.Encryptor3DS;
import com.ca.base.BaseSuite;
import com.ca.base.pojos.WebDriverEnum;
import com.jcraft.jsch.JSchException;

public class DBConnections {

	private static String sHost3DS = null;
	private static String sPort3DS = null;
	private static String sSid3DS = null;
	private static String usr3DS = null;
	private static String pwd3DS = null; 
	private static String dbType3DS = null;
	private static String postgresDbName3DS = null;

	private static String sHostRA = null;
	private static String sPortRA = null;
	private static String sSidRA = null;
	private static String usrRA = null;
	private static String pwdRA = null;
	private static String dbTypeRA = null;
	private static String postgresDbNameRA = null;

	private static String sHostDSP = null;
	private static String sPortDSP = null;
	private static String sSidDSP = null;
	private static String usrDSP = null;
	private static String pwdDSP = null;
	private static String dbTypeDSP = null;
	private static String postgresDbNameDSP = null;

	private static Connection db3DSConnection = null;
	private static Connection dbRaConnection = null;
	private static Connection dbDspConnection = null;
	
	private static boolean dbPropertyInitialised = false;
	

	private DBConnections() {
	}

	

	private static synchronized Connection getConnection(DBEnum dbEnum) {
		
		Connection conn = null;
		initialiseDBProperties();
		String sHost = null;
		String sPort = null;
		String sSid = null;
		String usr = null;
		String pwd = null;
		String dbType = null;
		String dbName = null;
		
		if(dbEnum.equals(DBEnum.RA)){
			sHost = sHostRA;
			sPort = sPortRA;
			sSid =sSidRA;
			usr = usrRA;
			pwd= pwdRA;
			dbType = dbTypeRA;
			dbName = postgresDbNameRA;
			conn = dbRaConnection;
		}
		else if(dbEnum.equals(DBEnum.DSP)){
			sHost = sHostDSP;
			sPort = sPortDSP;
			sSid =sSidDSP;
			usr = usrDSP;
			pwd= pwdDSP;
			dbType = dbTypeDSP;
			dbName = postgresDbNameDSP;
			conn = dbDspConnection;
		}
		else{
			sHost = sHost3DS;
			sPort = sPort3DS;
			sSid =sSid3DS;
			usr = usr3DS;
			pwd= pwd3DS;
			dbType = dbType3DS;
			dbName = postgresDbName3DS;
			conn = db3DSConnection;
		}
		try {
			if (conn == null || conn.isClosed()) {

				if(dbType.equalsIgnoreCase("postgres")){
					
					conn = DriverManager.getConnection("jdbc:postgresql://"+sHost+":"+sPort+"/"+dbName, usr, pwd);
				
				}else{
					conn = DriverManager.getConnection("jdbc:oracle:thin:@"
						+ sHost + ":" + sPort + ":" + sSid, usr, pwd);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return conn;
	}
	
	public static synchronized Connection get3DSDBConnection() {
		
		db3DSConnection = getConnection(DBEnum.TM);

		return db3DSConnection;
	}

	public static synchronized Connection getRaDBConnection() {

		dbRaConnection = getConnection(DBEnum.RA);
		return dbRaConnection;
	}

	public static synchronized Connection getDspDBConnection() {

		dbDspConnection = getConnection(DBEnum.DSP);
		return dbDspConnection;
	}

	public static synchronized void closeConnection() {

		try {
			if (db3DSConnection == null || db3DSConnection.isClosed())
				return;
			if(dbRaConnection == null || dbRaConnection.isClosed())
				return;
			if(dbDspConnection == null || dbDspConnection.isClosed())
				return;
			
			
			db3DSConnection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*public static DBConnectionDetails getTMDBDetails() {

		if (sHost3DS == null || sPort3DS == null || sSid3DS == null || usr3DS == null
				|| pwd3DS == null) {
			initialiseDBProperties();
		}

		DBConnectionDetails dbConn = new DBConnectionDetails();
		dbConn.setsHost(sHost3DS);
		dbConn.setsPort(sPort3DS);
		dbConn.setsSid(sSid3DS);
		dbConn.setsUserid(usr3DS);
		dbConn.setsPwd(pwd3DS);

		return dbConn;
	}*/

	private synchronized static void initialiseDBProperties() {
		
		if(dbPropertyInitialised)
			return;
			
		sHost3DS = BaseSuite.caPropMap.get("host3DS").trim();
		sPort3DS = BaseSuite.caPropMap.get("port3DS").trim();
		usr3DS = BaseSuite.caPropMap.get("userid3DS").trim();
		pwd3DS = BaseSuite.caPropMap.get("password3DS").trim();
		dbType3DS = BaseSuite.caPropMap.get("dbType3DS").trim();
		if(dbType3DS.equalsIgnoreCase("postgres"))
			postgresDbName3DS = BaseSuite.caPropMap.get("postgresDbName3DS").trim();
		else
			sSid3DS = BaseSuite.caPropMap.get("sid3DS").trim();
		
		// Loading RA DB Details
		sHostRA = BaseSuite.caPropMap.get("hostRA").trim();
		sPortRA = BaseSuite.caPropMap.get("portRA").trim();
		usrRA = BaseSuite.caPropMap.get("useridRA").trim();
		pwdRA = BaseSuite.caPropMap.get("passwordRA").trim();
		dbTypeRA = BaseSuite.caPropMap.get("dbTypeRA").trim();
		if(dbTypeRA.equalsIgnoreCase("postgres"))
			postgresDbNameRA = BaseSuite.caPropMap.get("postgresDbNameRA").trim();
		else
			sSidRA = BaseSuite.caPropMap.get("sidRA").trim();

		
		// Loading DSP DB Details
		sHostDSP = BaseSuite.caPropMap.get("hostDSP").trim();
		sPortDSP = BaseSuite.caPropMap.get("portDSP").trim();
		usrDSP = BaseSuite.caPropMap.get("useridDSP").trim();
		pwdDSP = BaseSuite.caPropMap.get("passwordDSP").trim();
		dbTypeDSP = BaseSuite.caPropMap.get("dbTypeDSP").trim();
		if(dbTypeDSP.equalsIgnoreCase("postgres"))
			postgresDbNameDSP = BaseSuite.caPropMap.get("postgresDbNameDSP").trim();
		else
			sSidDSP = BaseSuite.caPropMap.get("sidDSP").trim();
		
		
		dbPropertyInitialised = true;

	}

	
	public synchronized static List<Map<String, Object>> executeQueryInDB(String queryString,DBEnum dbEnum) {
		List<Map<String, Object>> list = new ArrayList<>(50);
		PreparedStatement preparedStatement = null;
		try {
			Connection conn = null;
			if(dbEnum.equals(DBEnum.RA)){
				conn = DBConnections.getRaDBConnection();
			}
			else if(dbEnum.equals(DBEnum.DSP)){
				
				conn = DBConnections.getDspDBConnection();
			}
			else{
				conn = DBConnections.get3DSDBConnection();
			}
			preparedStatement = conn.prepareStatement(queryString);
			ResultSet rs = preparedStatement.executeQuery();

			ResultSetMetaData md = rs.getMetaData();
			int columns = md.getColumnCount();

			while (rs.next()) {
				Map<String, Object> row = new HashMap<>(columns);
				for (int i = 1; i <= columns; i++) {
					int n = i + 1;
				/*	System.out.println(md.getColumnName(i));
					System.out.println(rs.getObject(i));*/
					row.put(md.getColumnName(i).toUpperCase(), rs.getObject(i));

				}
				list.add(row);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return list;

	}
	
	public synchronized static void updateQueryInDB(String queryString,DBEnum dbEnum) {
		List<Map<String, Object>> list = new ArrayList<>(50);
		PreparedStatement preparedStatement = null;
		try {
			Connection conn = null;
			if(dbEnum.equals(DBEnum.RA)){
				conn = DBConnections.getRaDBConnection();
			}
			else if(dbEnum.equals(DBEnum.DSP)){
				
				conn = DBConnections.getDspDBConnection();
			}
			else{
				conn = DBConnections.get3DSDBConnection();
			}
			preparedStatement = conn.prepareStatement(queryString);
			int row = preparedStatement.executeUpdate();
			System.out.println("No of Rows affected "+row+" while running the query : "+queryString);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public synchronized static List<Map<String, Object>> executeQueryIn3DSDB(String queryString) {
		
		List<Map<String, Object>> list = executeQueryInDB(queryString, DBEnum.TM);
		
		return list;

	}

	public synchronized static List<Map<String, Object>> executeQueryInRaDB(
			String queryString) {
		List<Map<String, Object>> list = executeQueryInDB(queryString, DBEnum.RA);
		return list;

	}
	
	public synchronized static List<Map<String, Object>> executeQueryInDSPDB(
			String queryString) {
		
		List<Map<String, Object>> list = executeQueryInDB(queryString, DBEnum.RA);
		return list;

	}

	
	
	public enum DBEnum {
		
		TM("TM"),		
		RA("RA"),
		DSP("DSP");


		private String value;

		DBEnum() {
			value = "";
		}

		DBEnum(String val) {
			value = val;
		}

		public String getValue() {
			return value;
		}
		
		public synchronized static String getDBEnum(String type) {
			
			WebDriverEnum mode=null;
	        try{
			mode = WebDriverEnum.valueOf(type);
	        }
	        catch(NullPointerException e){
	        	return ("Blank status");
	        }
			return mode.getValue();
		}

	}

	
}
