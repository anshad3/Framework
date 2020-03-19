package com.ca._3dsapi.base;

import java.util.Map;

import com.ca.util.APIResult;

public class APIEngine {

	public APIResult performNativeAndroidTxnFlow(Map<String, String> testCaseData) {

		APINativeFlowEngine flowEngine = new APINativeFlowEngine();
		APIResult result = flowEngine.performNativeAndroidTxnFlow(testCaseData);
		return result;

	}

	public APIResult performBrowserTxnFlow(Map<String, String> testCaseData) {

		BrowserFlowEngine flowEngine = new BrowserFlowEngine();
		APIResult result = flowEngine.performBrowserTxnFlow(testCaseData);
		return result;

	}

}
