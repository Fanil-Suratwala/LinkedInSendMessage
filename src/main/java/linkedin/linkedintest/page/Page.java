package linkedin.linkedintest.page;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class Page {
	Set<String> set_allNames = new LinkedHashSet<String>();
	WebDriver driver;
	WebDriverWait wait;
	String linkedInURL = "https://www.linkedin.com/login";
	int total_connections;

	@FindBy(css = "#username")
	public WebElement username;

	@FindBy(css = "#password")
	public WebElement password;

	@FindBy(xpath = "//button[text()='Sign in']")
	public WebElement signInCTA;

	@FindBy(css = "#mynetwork-tab-icon")
	public WebElement networkTab;

	@FindBy(xpath = "(//li-icon[@type='people-icon'])[1]")
	public WebElement peopleIcon;

	@FindBy(css = "h1.t-18.t-black")
	public WebElement totalConnectionsHeader;

	@FindBy(xpath = "//div[@data-control-name]/button[@aria-label]")
	public List<WebElement> listOfAllNames;

	@FindBy(xpath = "//div[@aria-label='Write a message…']/p")
	public WebElement messageBox;

	@FindBy(xpath = "//button[@type='submit']")
	public WebElement sendCTA;

	@FindBy(css = "span.artdeco-pill__text")
	public WebElement messageToSection;

	@FindBy(xpath = "//button[@data-control-name='overlay.close_conversation_window']")
	public WebElement closeCTA;

	public Page() {
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get(linkedInURL);
		wait = new WebDriverWait(driver, 20);
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, 20), this);
	}

	public void login(String id,String pass) {
		wait.until(ExpectedConditions.visibilityOf(username));
		username.sendKeys(id);
		password.sendKeys(pass);
		signInCTA.click();
	}

	public void navigateToConnections() {
		wait.until(ExpectedConditions.visibilityOf(networkTab)).click();
		wait.until(ExpectedConditions.visibilityOf(peopleIcon)).click();
		wait.until(ExpectedConditions.visibilityOf(totalConnectionsHeader));
		total_connections = Integer.parseInt(totalConnectionsHeader.getText().replaceAll("[^\\d]", ""));
	}

	public void sendMessageToConnections(int today_count,String message) throws InterruptedException {
		int count = 0;
		boolean flag = false;
		while (set_allNames.size() < total_connections) {
			for (WebElement e : listOfAllNames) {
				if (set_allNames.add(e.getAttribute("aria-label"))) {
					Thread.sleep(1000);
					if (!e.isDisplayed())
						((JavascriptExecutor) driver).executeScript("window.scrollBy(0,50)", e);
					System.out.println(e.getAttribute("aria-label"));
					sendMessage(e,message);
					writeCSV(e);
					count++;
					if (count == today_count) {
						flag = true;
						break;
					}
				}
			}
			if (flag)
				break;
		}
	}

	public void sendMessage(WebElement e,String message) throws InterruptedException {
		Thread.sleep(1000);
		wait.until(ExpectedConditions.visibilityOf(e));
		driver.findElement(
				By.xpath("//button[@aria-label='" + e.getAttribute("aria-label") + "']/span[text()='Message']"))
				.click();
		Thread.sleep(1000);
		wait.until(ExpectedConditions.visibilityOf(messageBox)).click();
		Thread.sleep(1000);
		messageBox.sendKeys(message);
		Thread.sleep(1000);
		wait.until(ExpectedConditions.visibilityOf(sendCTA)).click();
		Thread.sleep(1000);
		wait.until(ExpectedConditions.visibilityOf(messageToSection));
		Assert.assertTrue(e.getAttribute("aria-label").contains(messageToSection.getText()));
		wait.until(ExpectedConditions.visibilityOf(closeCTA)).click();
		Thread.sleep(1000);
	}

	public void readCSV() {
		try {
			System.out.println("In read csv");
			String strFile = System.getProperty("user.dir") + "\\File\\demofile.csv";
			CSVReader reader = new CSVReaderBuilder(new FileReader(strFile)).withSkipLines(1).build();
			List<String[]> allRows = reader.readAll();
			for (String[] row : allRows) {
				set_allNames.add(Arrays.toString(row).replace("[", "").replace("]", ""));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void writeCSV(WebElement e) {
		String csv = System.getProperty("user.dir") + "\\File\\demofile.csv";
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(csv, true));
			String[] data = e.getAttribute("aria-label").split("\n");
			writer.writeNext(data);
			writer.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	@AfterTest
	public void tearDown() {
		driver.close();
	}
}
