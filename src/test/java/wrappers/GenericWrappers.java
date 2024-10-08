package wrappers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import utils.Reporter;

public class GenericWrappers {

	public static AndroidDriver<AndroidElement> driver;
	public static WebDriverWait wait;
	static ExtentTest test;
	static ExtentReports report;
	public String sUrl, primaryWindowHandle, sHubUrl, sHubPort;

	public Properties loadProp() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File("./config.properties")));
			sHubUrl = prop.getProperty("HUB");
			sHubPort = prop.getProperty("PORT");
			sUrl = prop.getProperty("URL");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}

	public static boolean initAndriodDriver() throws FileNotFoundException, IOException {

		boolean bReturn = false;
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File("./config.properties")));
			DesiredCapabilities caps = new DesiredCapabilities();
			// caps.setCapability("app",
			// "C:/Users/Invcuser_106/Desktop/apk/Android_SZephyr_12431_stg.apk");
			caps.setCapability("platformName", prop.getProperty("PLATFORM_NAME"));
			caps.setCapability("appium:platformVersion", prop.getProperty("PLATFORM_VERSION"));
			caps.setCapability("appium:udid", prop.getProperty("UDID"));
			caps.setCapability("appium:deviceName", prop.getProperty("DEVICE_NAME"));
//			caps.setCapability("appium:appPackage", prop.getProperty("APP_PACKAGE"));
//			caps.setCapability("appium:appActivity", prop.getProperty("APP_ACTIVITY"));
			caps.setCapability("appium:automationName", "uiautomator2");
			caps.setCapability("newCommandTimeout", 999999);
//			caps.setCapability("autoGrantPermissions", true);

			driver = new AndroidDriver<AndroidElement>(new URL("http://127.0.0.1:4723/"), caps);
//			keepSessionAlive(driver);
			bReturn = true;

			String appPackage = prop.getProperty("APP_PACKAGE");
			if (driver.isAppInstalled(appPackage)) {
				System.out.println("App is already installed. Launching the app...");
				driver.activateApp(appPackage); // Open the app
			} else {
				System.out.println("App is not installed. Installing and launching...");
				driver.installApp(
						"C:\\Users\\Invcuser_45\\Desktop\\Ashif\\Automation_Ashif\\Android_SZephyr_12888_stg.apk");
				driver.activateApp(appPackage); // Launch the app after installation
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("URL is malformed: " + e.getMessage());
			e.printStackTrace();
		}
		return bReturn;
	}

//	public boolean invokeApp(String browser,String url) {
//		boolean bReturn = false;
//		try {
//
//			DesiredCapabilities dc = new DesiredCapabilities();
//			dc.setBrowserName(browser);
//			dc.setPlatform(Platform.WINDOWS);
//			if(browser.equalsIgnoreCase("chrome")){
//				WebDriverManager.chromedriver().setup();
//				webDriver = new ChromeDriver();
//				
//			} else if(browser.equalsIgnoreCase("Edge")){
//				WebDriverManager.edgedriver();
//				webDriver = new EdgeDriver();
//				
//			} else if(browser.equalsIgnoreCase("Firefox")) {
//				WebDriverManager.firefoxdriver();
//				webDriver = new FirefoxDriver();
//			}
//
//			webDriver.manage().window().maximize();
//			webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//			webDriver.get(url);
//
//			primaryWindowHandle = driver.getWindowHandle();
//			
//			Reporter.reportStep("The URL : "+ url + " launched successfully in"+ browser + " browser " , "PASS");
//			bReturn = true;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			Reporter.reportStep("The browser:" + browser + " could not be launched", "FAIL");
//		}
//		return bReturn;
//	}

	public static void keepSessionAlive(AndroidDriver<AndroidElement> driver) {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(() -> {
			try {
				driver.currentActivity();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 0, 5, TimeUnit.MINUTES);
	}

	public static boolean launchApplication(String url) {
		boolean bReturn = false;
		try {
			driver.get(url);
			Reporter.reportStep("The browser:" + url + " launched successfully", "PASS");
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The browser:" + url + " could not be launched", "FAIL");

		}
		return bReturn;
	}

	public static boolean clickbyXpath(WebElement xpath, String button) {
		boolean bReturn = false;
		try {
			expWait(xpath);
			xpath.click();
			Reporter.reportStep(button + " is clicked Successfully.", "PASS");
			bReturn = true;

		} catch (Exception e) {
			Reporter.reportStep("The Field " + button + " could not be clicked.", "FAIL");
		}
		return bReturn;

	}

	public static boolean clickbyXpathwithoutReport(WebElement xpath, String button) {
		boolean bReturn = false;
		try {
			expWait(xpath);
			xpath.click();
			Reporter.reportStep(button + " is clicked Successfully.", "PASS");
			bReturn = true;

		} catch (Exception e) {
			// Reporter.reportStep("The Field "+button+" could not be clicked.", "FAIL");
		}
		return bReturn;

	}

	public boolean verifyTitle(String title) {
		boolean bReturn = false;
		try {
			if (driver.getTitle().equalsIgnoreCase(title)) {
				Reporter.reportStep("The title of the page matches with the value :" + title, "PASS");
				bReturn = true;
			} else {
				Reporter.reportStep(
						"The title of the page:" + driver.getTitle() + " did not match with the value :" + title,
						"SUCCESS");
			}

		} catch (Exception e) {
			Reporter.reportStep("The title did not match", "FAIL");
		}

		return bReturn;
	}

	public boolean selectById(WebElement id, int value, String fieldName) {
		boolean bReturn = false;
		try {
			expWait(id);
			new Select(id).selectByIndex(value);
			Reporter.reportStep("The element with id: " + fieldName + " is selected with value :" + value, "PASS");

			bReturn = true;

		} catch (Exception e) {
			Reporter.reportStep("The value: " + value + " could not be selected.", "FAIL");
		}
		return bReturn;
	}

	public boolean entervaluebyXpath(WebElement xpath, String fieldname, String value) {
		boolean bReturn = false;
		try {
			expWait(xpath);
			xpath.sendKeys(value);
			Reporter.reportStep(fieldname + " field is entered with value : " + value, "PASS");

		} catch (Exception e) {
			Reporter.reportStep("The value: " + value + " could not be entered.", "FAIL");
		}
		return bReturn;
	}

	public boolean entertoiFrame(WebElement xpath, String fName) {
		boolean bReturn = false;
		try {
			expWait(xpath);
			WebElement frame = xpath;
			driver.switchTo().frame(frame);
			Reporter.reportStep("iframe " + fName + " entered successfully", "PASS");
			bReturn = true;

		} catch (Exception e) {
			Reporter.reportStep("iframe could not be entered :", "FAIL");
		}
		return bReturn;
	}

	public boolean selectByVisibleText(WebElement xpath, String fieldName) {
		boolean bReturn = false;
		try {
			expWait(xpath);
			List<WebElement> size = new Select(xpath).getOptions();
			for (WebElement s : size) {
				if (s.isEnabled()) {
					new Select(xpath).selectByVisibleText(s.getText());
					break;
				}
				Reporter.reportStep("The dropdown: " + fieldName + " is selected", "PASS");
				bReturn = true;
			}
		} catch (Exception e) {
			Reporter.reportStep("The dropdown: " + fieldName + " is not selected", "FAIL");
		}
		return bReturn;
	}

	public boolean verifyTextContainsByXpath(WebElement xpath, String text, String field) {
		boolean bReturn = false;
		try {
			expWait(xpath);
			String sText = xpath.getText();
			System.out.println(sText);
			if (sText.trim().contains(text)) {
				Reporter.reportStep(field + "contains " + text, "PASS");
				bReturn = true;
			} else {
				Reporter.reportStep(field + " did not contain :" + text, "FAIL");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bReturn;
	}

	public void quitBrowser() {
		try {
			if (driver != null) {
				driver.quit();
			}
		} catch (Exception e) {
			Reporter.reportStep("The browser could not be closed.", "FAIL");
		}

	}

	public static void expWait(WebElement xpath) {
		try {
//			WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(5));
			wait.until(ExpectedConditions.visibilityOf(xpath));
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public void expWaitforPairing(WebElement xpath) {
		try {
//		WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(100));
			wait.until(ExpectedConditions.visibilityOf(xpath));
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public void runPythonScript() {
		try {
			// Update the path to the Python interpreter and the Python script
			ProcessBuilder processBuilder = new ProcessBuilder("C:/Python312/python.exe",
					"C:/Users/Invcuser_106/Desktop/Python code/serialport.py");
			// Start the process
			Process process = processBuilder.start();

			// Capture the script output (stdout)
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			System.out.println("Output of the Python script:");
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			// Wait for the process to complete
			int exitCode = process.waitFor();
			System.out.println("Python script exited with code: " + exitCode);

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

//	========================================

	public int extractintvalue(String str) {
		// Use regular expression to remove all non-digit characters
		String numbersOnly = str.replaceAll("\\D+", "");

		// Convert the extracted string to an integer (optional)
		int extractedValue = Integer.parseInt(numbersOnly);

//          System.out.println("Extracted numbers: " + numbersOnly);
		System.out.println("Extracted integer value: " + extractedValue);
		return extractedValue;
	}

	public String randomnumbers(int num) {

		String numbers = "123456789";

		// Create a StringBuilder to store the random numbers
		StringBuilder sb = new StringBuilder();

		// Create an object of Random class
		Random random = new Random();

		// Specify the length of the random string
		int length = num;

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(numbers.length());
			char randomNum = numbers.charAt(index);
			sb.append(randomNum);
		}
		String randomString = sb.toString();
		return randomString;
	}

	public boolean turnOnBT() {
		boolean bReturn = false;

		try {
			Runtime.getRuntime().exec("adb shell svc bluetooth enable");
			Reporter.reportStep("Bluetooth turned on successfully", "PASS");
			bReturn = true;
		} catch (IOException e) {
			e.printStackTrace();
			Reporter.reportStep("iframe could not be entered :", "FAIL");
		}
		return bReturn;

	}

	public boolean turnOffBT() throws Exception {

		boolean bReturn = false;
		try {
			Runtime.getRuntime().exec("adb shell svc bluetooth disable");
			Reporter.reportStep("Bluetooth turned OFF successfully", "PASS");
			bReturn = true;
		} catch (IOException e) {
			e.printStackTrace();
			Reporter.reportStep("iframe could not be entered :", "FAIL");
		}

		return bReturn;
	}

	public boolean isElementDisplayed(WebElement element) {

		try {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return element.isDisplayed();
		} catch (NoSuchElementException e) {
			return false;
		}
	}

}

class FTPUploader {

	private FTPClient ftpClient;
	private String randomDirName;

	String server2="ftp.iinvsys.com";
	int port2=2121;
	// Constructor to connect and login to FTP server
	public FTPUploader(String server, int port, String user, String pass) throws IOException {
		
		 ftpClient = new FTPClient();
	        if (!pingServer(server)) {
	            System.out.println(server + " is not reachable. Trying " + server2);
	            connectToServer(server2, port2, user, pass);
	        } else {
	            connectToServer(server, port, user, pass);
	        }
	    

	      
	}

	  private void connectToServer(String server, int port, String user, String pass) throws IOException {
          ftpClient.connect(server, port);
          boolean login = ftpClient.login(user, pass);
          
          if (!login) {
              throw new IOException("FTP login failed for server: " + server);
          }

	ftpClient.enterLocalPassiveMode(); // Set passive mode for FTP
	ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // Use binary file type
}
	        private boolean pingServer(String server) {
	            try {
	                InetAddress address = InetAddress.getByName(server);
	                return address.isReachable(2000); // Timeout after 2000 ms
	            } catch (IOException e) {
	                return false; // If there's an exception, the server is not reachable
	            }
	        }
	// Method to create a subdirectory and change the working directory to it
	public void createAndNavigateToSubdirectory(String existingDirectory, String newSubDir) throws IOException {
		// Navigate to the existing directory
		if (ftpClient.changeWorkingDirectory(existingDirectory)) {
			System.out.println("Navigated to directory: " + existingDirectory);

			// Create a new subdirectory
			if (ftpClient.makeDirectory(newSubDir)) {
				System.out.println("Created new subdirectory: " + newSubDir);

				// Change the working directory to the new subdirectory
				if (ftpClient.changeWorkingDirectory(newSubDir)) {
					System.out.println("Changed to new subdirectory: " + newSubDir);
				} else {
					throw new IOException("Failed to change to the new subdirectory");
				}
			} else {
				throw new IOException("Failed to create new subdirectory: " + newSubDir);
			}
		} else {
			throw new IOException("Failed to change directory to: " + existingDirectory);
		}
	}

	// Method to upload a file to the current directory
	public void uploadFile(String localFilePath, String remoteFileName) throws IOException {
		try (FileInputStream fis = new FileInputStream(new File(localFilePath))) {
			boolean success = ftpClient.storeFile(remoteFileName, fis);
			if (success) {
				System.out.println("File uploaded successfully to FTP: " + remoteFileName);
			} else {
				System.out.println("File upload failed.");
			}
		}
	}

	// Close the FTP connection
	public void disconnect() throws IOException {
		if (ftpClient.isConnected()) {
			ftpClient.logout();
			ftpClient.disconnect();
		}

	 }
	

}