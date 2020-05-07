package readtab;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ReadTable {

	public static WebDriver driver;
	static excelread reader;
	// public static void main(String[] args) throws InterruptedException {

	@BeforeClass
	public void setup() throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "C:\\SELENIUM TUTORIAL\\driver\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://classic.crmpro.com");
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.findElement(By.xpath("//input[@placeholder='Username']")).sendKeys("batchautomation");
		driver.findElement(By.xpath("//input[@name='password']")).sendKeys("Test@12345");
		driver.findElement(By.xpath("//input[@value='Login']")).click();
		Thread.sleep(2000);
		driver.switchTo().frame("mainpanel");

	}

	@Test(priority = 1)

	public void sheetwriting() throws InterruptedException {

		driver.findElement(By.xpath(" //a[contains(text(),'Contacts')]")).click();

		// xpath for first column and all rows
		String before_xpath_name = "//*[@id=\"vContactsForm\"]/table/tbody/tr[";
		String after_xpath_name = "]/td[2]/a";

		String before_xpath_company = "//*[@id=\"vContactsForm\"]/table/tbody/tr[";
		String after_xpath_company = "]/td[3]/a";

		try {
			reader = new excelread("C:\\SELENIUM TUTORIAL\\CRM_Contact_Data.xlsx");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//	reader.removeSheet("tabledata");
		if (!reader.isSheetExist("tabledata")) {
			reader.addSheet("tabledata");
			reader.addColumn("tabledata", "Name");
			reader.addColumn("tabledata", "Company");
		}
		List<WebElement> rows = driver.findElements(By.xpath("//*[@id=\"vContactsForm\"]//tr"));
		int row_count = rows.size();
		System.out.println(row_count);

		for (int i = 4; i < row_count - 1; i++) {
			String Actual_xpath_name = before_xpath_name + i + after_xpath_name;
			String Actual_xpath_company = before_xpath_company + i + after_xpath_company;
			// System.out.println(Actual_xpath_name);
			String Name = driver.findElement(By.xpath(Actual_xpath_name)).getText();
			String Company = driver.findElement(By.xpath(Actual_xpath_company)).getText();
			// System.out.println(Name);

			reader.setCellData("tabledata", "Name", i - 2, Name);
			reader.setCellData("tabledata", "Company", i - 2, Company);

		}

//goto page 20

//driver.findElement(By.xpath("//a[contains (text(), '20')]//following::td[@align='right']")).click();

//driver.switchTo().frame("leftpanel");
//driver.findElement(By.xpath("//a[contains (text(), 20)]//following::td[@align='right']")).click();
//driver.findElement(By.xpath("//a[contains (text(), '20')]//parent::div//preceding-sibling::a[contains (text(),'19')]")).click();

		driver.findElement(By.xpath("//td[@align='right']//preceding::a[contains (text(),'20')]")).click();
		List<WebElement> rows20 = driver.findElements(By.xpath("//*[@id='vContactsForm']//tr"));
		int rowcnt = rows20.size();
		int j = row_count - 3;
		System.out.println(rowcnt);
		for (int i = 4; i < rowcnt - 1; i++) {
			String Actual_xpath_name = before_xpath_name + i + after_xpath_name;
			String Actual_xpath_company = before_xpath_company + i + after_xpath_company;
			// System.out.println(Actual_xpath_name);
			String Name = driver.findElement(By.xpath(Actual_xpath_name)).getText();
			String Company = driver.findElement(By.xpath(Actual_xpath_company)).getText();
			System.out.println(Name);

			reader.setCellData("tabledata", "Name", j, Name);
			reader.setCellData("tabledata", "Company", j, Company);

			j = j + 1;
		}

	}

	@AfterClass
	public void teardown() {
		driver.quit();
	}
}
