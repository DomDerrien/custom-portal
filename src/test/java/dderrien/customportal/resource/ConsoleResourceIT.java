package dderrien.customportal.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class ConsoleResourceIT {

	private static WebDriver driver;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = new FirefoxDriver();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
	}

	@Before
	public void setUp() throws Exception {
		logIn(driver);
	}

	@After
	public void tearDown() throws Exception {
		logOut(driver);
	}

	private static void logIn(WebDriver driver) {
		driver.get("http://localhost:9090/console");

		WebElement emailField = driver.findElement(By.name("email"));
		assertNotNull(emailField);
		emailField.clear();
		emailField.sendKeys("tester-1@test.org");

		WebElement isAdminField = driver.findElement(By.name("isAdmin"));
		assertNotNull(isAdminField);

		List<WebElement> actionFields = driver.findElements(By.name("action"));
		assertNotNull(actionFields);
		assertEquals(2, actionFields.size());
		assertEquals("submit", actionFields.get(0).getAttribute("type"));
		assertEquals("submit", actionFields.get(1).getAttribute("type"));

		for (WebElement el : actionFields) {
			if ("Log In".equals(el.getAttribute("value"))) {
				el.click();
				break;
			}
		}

		long end = System.currentTimeMillis() + 5000;
		while (System.currentTimeMillis() < end) {
			WebElement bodyTag = driver.findElement(By.tagName("body"));

			if ("visible".equals(bodyTag.getCssValue("visibility"))) {
				break;
			}
		}
	}

	private static void logOut(WebDriver driver) {
		WebElement adminMenu = driver.findElement(By.id("admin"));
		assertNotNull(adminMenu);
		adminMenu.click();

		WebElement logoutBtn = driver.findElement(By.id("logoutBtn"));
		assertNotNull(logoutBtn);
		logoutBtn.click();
	}

	@Test
	public void testLoginLogout() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
