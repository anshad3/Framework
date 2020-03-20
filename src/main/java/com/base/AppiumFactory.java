package com.base;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.base.pojos.AppInfoPojo;
import com.base.pojos.BrowserEnum;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class AppiumFactory {

	public static AppiumDriverLocalService service = null;

	public static String appInfoJsonPath = null;
	public static String nodeBinaryPath = null;
	public static String appiumBinaryPath = null;
	public static String appiumIP = null;
	public static String appiumPort;
	public static boolean serverStarted;

	public static Map<String, AppInfoPojo> appInfoMap = new HashMap<String, AppInfoPojo>();

	public static void initialiseAppium() {

		if (serverStarted)
			return;
		loadAppInfoMapData();
		nodeBinaryPath = BaseSuite.getCAPropertyValue("NodeBinaryPath");
		appiumBinaryPath = BaseSuite.getCAPropertyValue("AppiumBinaryPath");
		appiumIP = BaseSuite.getCAPropertyValue("AppiumIP");
		// String strAppiumPort = BaseSuite.getCAPropertyValue("AppiumPort");
		appiumPort = BaseSuite.getCAPropertyValue("AppiumPort");

		AppiumServiceBuilder serviceBuilder = new AppiumServiceBuilder();
		serviceBuilder.usingDriverExecutable(new File(nodeBinaryPath));
		// serviceBuilder.withAppiumJS(new File(appiumBinaryPath));

		service = AppiumDriverLocalService.buildService(serviceBuilder);
		service.start();

		serverStarted = true;

	}

	public synchronized static WebDriver getAppiumDriver(String deviceName, String appName) {

		initialiseAppium();
		AppInfoPojo appInfoPojo = appInfoMap.get(appName);

		File app = new File(System.getProperty("user.dir") + File.separator + appInfoPojo.getAppPath());
		AppiumDriver driver = null;

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
		capabilities.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());
		capabilities.setCapability(MobileCapabilityType.FULL_RESET, "true");
		capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 1500);
		capabilities.setCapability("unicodeKeyboard", true);
		capabilities.setCapability("resetKeyboard", true);

		if (deviceName.isEmpty() || deviceName.equals("")) {
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android");
		} else {
			System.out.println("Device Name is not empty");
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
			capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, appInfoPojo.getAppPackage());
			capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, appInfoPojo.getAppActivity());
			capabilities.setCapability("automationName", "uiautomator2");
			try {
				driver = new AndroidDriver(new URL("http://" + appiumIP.trim() + ":" + appiumPort.trim() + "/wd/hub"),
						capabilities);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return driver;
	}
	
	public synchronized static WebDriver getAppiumBrowserDriver(String deviceName, BrowserEnum browserType) {

		initialiseAppium();
		AppiumDriver driver = null;

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(CapabilityType.BROWSER_NAME,browserType);
		capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 5000);
		capabilities.setCapability("unicodeKeyboard", true);
		capabilities.setCapability("resetKeyboard", true);

		if (deviceName.isEmpty() || deviceName.equals("")) {
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android");
		} else {
			System.out.println("Device Name is not empty");
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
			capabilities.setCapability("automationName", "uiautomator2");
			try {
				driver = new AndroidDriver(new URL("http://" + appiumIP.trim() + ":" + appiumPort.trim() + "/wd/hub"),
						capabilities);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return driver;
	}

	private static void loadAppInfoMapData() {

		String folderPath = System.getProperty("user.dir");
		appInfoJsonPath = folderPath + File.separator + "config" + File.separator
				+ BaseSuite.getCAPropertyValue("AppInfoJsonPath");

		try {
			JSONParser parser = new JSONParser();
			Reader reader = new FileReader(appInfoJsonPath);

			Object jsonObj = parser.parse(reader);
			JSONArray appInfoList = (JSONArray) jsonObj;
			Iterator<JSONObject> it = appInfoList.iterator();
			while (it.hasNext()) {
				JSONObject appInfoObj = it.next();
				AppInfoPojo appInfoPojo = new AppInfoPojo(appInfoObj);
				String appName = (String) appInfoObj.get("name");
				appInfoMap.put(appName, appInfoPojo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public static void closeAppium() {

		if (serverStarted) {
			service.stop();
			serverStarted = false;
		}

	}

}
