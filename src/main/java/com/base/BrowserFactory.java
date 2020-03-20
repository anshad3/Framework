package com.base;

import java.awt.Toolkit;
import java.io.File;

import org.apache.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.base.pojos.BrowserEnum;



public class BrowserFactory {

	public static String osEnv = null;
	protected static Logger logger = null;
	static {
		logger = Logger.getLogger("rootLogger");
		logger.info("logger is configured");
	}

	
	/**
	 * 
	 * @param browserParameters
	 * @return webdriver object for the browser, url passed
	 */
	
	
	
	public static WebDriver getBrowser(Long threadId) {
		
		String browser = null;
		
		BrowserEnum browserEnum = BaseSuite.threadBrowserMap.get(threadId);
		
		osEnv = BaseSuite.getCAPropertyValue("MachineOS");
		String chromeDriverName = null;
		String geckoDriverName = null;
		String ieDriverName = null;
		String msWebDriverName = null;
		if(osEnv!=null && osEnv.equalsIgnoreCase("Linux")){
			chromeDriverName="chromedriver";
			geckoDriverName="geckodriver";
			ieDriverName="IEDriverServer";
			msWebDriverName="MicrosoftWebDriver";
		}
		else{
			chromeDriverName="chromedriver.exe";
			geckoDriverName="geckodriver.exe";
			ieDriverName="IEDriverServer.exe";
			msWebDriverName="MicrosoftWebDriver.exe";
		}
		
		WebDriver driver = null;
		
		if (browserEnum.equals(BrowserEnum.Firefox)) {
			System.out.println("Creating the firefox browser");
			
			String geckoDriverPath = System.getProperty("user.dir")+File.separator+"drivers"+File.separator+geckoDriverName;
			System.setProperty("webdriver.firefox.marionette",geckoDriverPath);
			
			
			DesiredCapabilities capability = DesiredCapabilities.firefox();
			capability.setBrowserName("firefox");
			
			driver = new FirefoxDriver(capability);
			
		} else if (browserEnum.equals(BrowserEnum.Chrome)) {
			System.out.println("Creating the Chrome browser");
			
			String chromedriverPath = System.getProperty("user.dir")+File.separator+"drivers"+File.separator+chromeDriverName;
			System.out.println(chromedriverPath);
			System.setProperty("webdriver.chrome.driver",chromedriverPath);
			driver = new ChromeDriver();

		} else if (browserEnum.equals(BrowserEnum.IE)) {
			DesiredCapabilities capability = DesiredCapabilities.internetExplorer();
			capability.setCapability("ignoreZoomSetting", true);
			System.out.println("Creating the IE browser");
			String ieDriverPath = System.getProperty("user.dir")+File.separator+"drivers"+File.separator+ieDriverName;
			System.setProperty("webdriver.ie.driver", ieDriverPath);
			InternetExplorerOptions option = new InternetExplorerOptions(capability);
			option.takeFullPageScreenshot();
			driver = new InternetExplorerDriver(option);
			java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension maximizedScreenSize = new Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight());
			
			int width = 800;//gd.getDisplayMode().getWidth();
			int height = 600;//gd.getDisplayMode().getHeight();
			maximizedScreenSize = new Dimension(width, height);
			driver.manage().window().setSize(maximizedScreenSize);
			
			return driver;
			
			

		}else if (browserEnum.equals(BrowserEnum.Edge)) {
			System.out.println("Creating the Microsoft edge browser");
			String ieDriverPath = System.getProperty("user.dir")+File.separator+"drivers"+File.separator+msWebDriverName;
			System.setProperty("webdriver.edge.driver", ieDriverPath);
			driver = new EdgeDriver();

		}else if(browserEnum.equals(BrowserEnum.Headless)){
			
			if(osEnv!=null && osEnv.equalsIgnoreCase("Windows")){
			String phantomdriverPath = System.getProperty("user.dir")+File.separator+"drivers"+File.separator+"phantomjs.exe";
			System.setProperty("phantomjs.binary.path",phantomdriverPath);
			}else{
				
				String phantomdriverPath = System.getProperty("user.dir")+File.separator+"drivers"+File.separator+"phantomjs";
				System.setProperty("phantomjs.binary.path",phantomdriverPath);
			}
			
			driver = new PhantomJSDriver();
			
		}else if (browserEnum.equals(BrowserEnum.HeadlessWithChrome)) {
			
			ChromeOptions options = new ChromeOptions();
			options.setHeadless(true);
			String chromedriverPath = System.getProperty("user.dir")+File.separator+"drivers"+File.separator+chromeDriverName;
			System.out.println(chromedriverPath);
			System.setProperty("webdriver.chrome.driver",chromedriverPath);
			
			driver = new ChromeDriver(options);
			Dimension maximizedScreenSize = null;
			if(osEnv!=null && osEnv.equalsIgnoreCase("Linux")){
				//GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				int width = 1024;//gd.getDisplayMode().getWidth();
				int height = 800;//gd.getDisplayMode().getHeight();
				maximizedScreenSize = new Dimension(width, height);
			}
			else{
				java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				maximizedScreenSize = new Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight());
			}
			driver.manage().window().setSize(maximizedScreenSize);
			
			return driver;
		}
		else if (browserEnum.equals(BrowserEnum.HeadlessWithFirefox)) {
			
			FirefoxOptions options = new FirefoxOptions();
			options.setHeadless(true);
			String geckoDriverPath = System.getProperty("user.dir")+File.separator+"drivers"+File.separator+geckoDriverName;
			System.setProperty("webdriver.firefox.marionette",geckoDriverPath);
			
			driver = new FirefoxDriver(options);
			java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension maximizedScreenSize = new Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight());
			driver.manage().window().setSize(maximizedScreenSize);
			return driver;
		}
		
		driver.manage().window().maximize();
		return driver;

	}
	
	
}
