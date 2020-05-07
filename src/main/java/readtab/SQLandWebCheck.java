package readtab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class SQLandWebCheck {

	public static ResultSet rs;
	public Statement stmnt;
	public Connection con;
	public static WebDriver driver;
	static excelread reader;
	public SoftAssert softAssert;
	public ArrayList<Object> dataar;
	public ArrayList<Object> Webar;
	// public static void main(String[] args) throws InterruptedException {

	@BeforeClass
	public void setup() throws InterruptedException, SQLException, ClassNotFoundException {
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

		// databaseCoonection
		Class.forName("com.mysql.jdbc.Driver"); // this is class call Class having method forname which takes driver of
												// databse
		System.out.println("Driver loaded");

		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/fullform", "root", "munmun86");
		// getConnection returns Connection it, shows database connected;

		stmnt = con.createStatement(); // createStatement returns statement
		// this statement will help in executing database query

		String Query1 = "select * from contacts";
		rs = stmnt.executeQuery(Query1); // IT returns Result Set

		// read the data of table

	}

	@Test(priority = 1)

	public void databaseAndWebcheck() throws SQLException {
		Webar = new ArrayList<Object>();
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

		if (!reader.isSheetExist("tabledatanew")) {
			reader.addSheet("tabledatanew");
			reader.addColumn("tabledatanew", "Name");
			reader.addColumn("tabledatanew", "Company");
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
			Webar.add(Name);

			reader.setCellData("tabledatanew", "Name", i - 2, Name);
			reader.setCellData("tabledatanew", "Company", i - 2, Company);

		}
		System.out.println("ouput from webpage in the array is " + Webar);

		// Assert.assertEquals(Name, Fname , "data not equal"); /its hard assertion so
		// subsequent lines will not be executed

		// softAssert.assertEquals(Name, Fname , "data not equal");
		// softAssert.assertAll();

	}

//point to remember: if any method has return then it will be stand alone wont be executed in that class irrespective of 
	// Test annotation, so either that method has to be called somewhere else or the
	// return type has to be removed.

	@Test(priority = 2)
	public void getDatafromDb() throws SQLException {
		dataar = new ArrayList<Object>();

		while (rs.next()) {
			String Fname = rs.getString("FirstName");
			// System.out.println(" first rec is " + Fname);
			// Object st[] = { Fname };
			// System.out.println(st);
			dataar.add(Fname);

		}
		// return dataar;
		System.out.println("ouput from database fullform of contacts table is " + dataar);
		softAssert = new SoftAssert();
		softAssert.assertEquals(Webar, dataar, "data not equal");
		softAssert.assertAll();
		softAssert.assertEquals(Webar.toArray(), dataar.toArray(), "data not equal");
		softAssert.assertAll();
	}

	@AfterClass
	public void closeconnection() throws SQLException {
		rs.close();
		con.close();
		stmnt.close();
		driver.quit();
	}
}
