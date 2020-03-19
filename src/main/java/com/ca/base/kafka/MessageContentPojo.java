package com.ca.base.kafka;

import java.io.File;
import java.io.Serializable;

import com.aventstack.extentreports.Status;
import com.ca.util.LogMode;


public class MessageContentPojo implements Serializable {
	
	
	private byte[] screenShotcontent;
	private String htmlContent;
	private String testCaseName;
	private String filePath;
	private String stepName;
	private Status status;
	private String htmlLink;
	private String suiteName;
	private String xmlTestName;
	private String methodName;
	private Long threadId;
	private String imgBase64string;
	private String threadIdWithIp;

	
	public String getThreadIdWithIp() {
		return threadIdWithIp;
	}
	public void setThreadIdWithIp(String threadIdWithIp) {
		this.threadIdWithIp = threadIdWithIp;
	}
	public String getImgBase64string() {
		return imgBase64string;
	}
	public void setImgBase64string(String imgBase64string) {
		this.imgBase64string = imgBase64string;
	}
	public Long getThreadId() {
		return threadId;
	}
	public void setThreadId(Long threadId) {
		this.threadId = threadId;
	}
	public byte[] getScreenShotcontent() {
		return screenShotcontent;
	}
	public void setScreenShotcontent(byte[] screenShotcontent) {
		this.screenShotcontent = screenShotcontent;
	}
	public String getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	public String getTestCaseName() {
		return testCaseName;
	}
	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getStepName() {
		return stepName;
	}
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

	public String getHtmlLink() {
		return htmlLink;
	}
	public void setHtmlLink(String htmlLink) {
		this.htmlLink = htmlLink;
	}
	public String getSuiteName() {
		return suiteName;
	}
	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}
	public String getXmlTestName() {
		return xmlTestName;
	}
	public void setXmlTestName(String xmlTestName) {
		this.xmlTestName = xmlTestName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

}
