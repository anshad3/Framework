package com.base.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExtentReportUtil {

	public synchronized static String getDestDirFileFullPath(String testCaseName) {
		String destdir1 = System.getProperty("user.dir");
		String destDirFile = destdir1 + File.separator + "TestResult" + File.separator + "Extent" + File.separator
				+ "HTML" + File.separator + testCaseName;
		System.out.println("dest dir File===========>" + destDirFile);
		return destDirFile;
	}

	public synchronized static String createHTMLFileDataForLogs(String testCaseID,String fileData, String logFileDisplayName) {
		StringBuilder sb = new StringBuilder();
		//		String header = "<!DOCTYPE html><html><head><title>" + logFileDisplayName + "</title></head><body> ";
		//fileData = fileData.replaceAll("[\\n]", "<br>");
		String closingTags = "</body></html>";
		//sb.append(header);
		sb.append("***************************************************************************************************************************************\n");
		sb.append("                                     Contents of "+logFileDisplayName +" for Test Case ID : "+testCaseID+"  \n");
		sb.append("******************************************************************************************************************************************\n\n");
		sb.append(fileData);
		sb.append(System.getProperty("line.separator"));
		//sb.append(closingTags);
		return sb.toString();
	}

	public synchronized static String createHTMLFileForLogs(String destDirFile, String htmlData, String logHtmlFileName,
			String logFileDisplayName) {
		String relativedir = ".." + File.separator + "HTML" + File.separator;
		File fss = new File(destDirFile);
		fss.getParentFile().mkdirs();
		FileWriter fw = null;
		try {
			fss.createNewFile();
			fw = new FileWriter(fss.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(htmlData);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String reldirFile = relativedir + logHtmlFileName;
		//String relFileName = "../../" + logFileName;

		String outputFile = null;

		
		outputFile = "<a href=" + reldirFile + " target=\"_blank\" >" + logFileDisplayName + "</a>";
				/*+ "&nbsp;&nbsp;&nbsp; <a href=" + relFileName + " download=" + logFileDisplayName
				+ "><p style=\"text-decoration:underline;\">Click here to download " + logFileDisplayName
				+ "</p></a>";*/
		System.out.println("Output file:" + outputFile);
		
		return outputFile;
	}

}
