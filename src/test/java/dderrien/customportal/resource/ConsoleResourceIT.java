package dderrien.customportal.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class ConsoleResourceIT {

    private final static int DELAY = 1000;
    private final static int LIMIT = 5000;

    private static WebDriver driver;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        driver = new FirefoxDriver();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        driver.quit();
    }

    @Before
    public void setUp() throws Exception {
        driver.get("http://localhost:9090/console");

        logIn("tester-1@test.org", false);
    }

    @After
    public void tearDown() throws Exception {
        logOut();
    }

    // Wait optionally a bit to let the current action effects take place
    // Wait for the body tag to be visible, as a sign that the page load is finished
    private static void waitForPageReady(boolean withDelay) {
        if (withDelay) {
            try {
                Thread.sleep(DELAY);
            }
            catch (InterruptedException e) {}
        }
        long end = System.currentTimeMillis() + LIMIT;
        while (System.currentTimeMillis() < end) {
            WebElement bodyTag = driver.findElement(By.tagName("body"));

            if ("visible".equals(bodyTag.getCssValue("visibility"))) {
                break;
            }
        }
    }

    // Fill up the login form and submit it
    private static void logIn(String email, boolean isAdmin) {
        WebElement emailField = driver.findElement(By.name("email"));
        emailField.clear();
        emailField.sendKeys(email);

        if (isAdmin) {
            driver.findElement(By.name("isAdmin")).click();
        }

        List<WebElement> actionFields = driver.findElements(By.name("action"));
        assertEquals(2, actionFields.size());
        assertEquals("submit", actionFields.get(0).getAttribute("type"));
        assertEquals("submit", actionFields.get(1).getAttribute("type"));

        for (WebElement el : actionFields) {
            if ("Log In".equals(el.getAttribute("value"))) {
                el.click();
                break;
            }
        }

        waitForPageReady(false);
    }

    // Trigger the log out item from the admin menu
    private static void logOut() {
        driver.findElement(By.id("admin")).click();
        driver.findElement(By.id("logoutBtn")).click();
    }

    @Test
    public void testLoginLogout() {
        // Rely on setUp() and tearDown()
    }

    // Trigger the add category item from the admin menu
    // Fill up the category form and submit it
    public static void createCategory(String title) {
        driver.findElement(By.id("admin")).click();
        waitForPageReady(true);
        driver.findElement(By.id("addCat")).click();

        WebElement addCatForm = driver.findElement(By.id("addCatForm"));
        addCatForm.findElement(By.id("addCatTitle")).sendKeys(title);
        addCatForm.findElement(By.id("addCatOK")).click();

        waitForPageReady(true);
    }

    // Trigger the deletion of all category items from the category contextual menu
    public static void deleteAllCategories() {

        List<WebElement> categories = driver.findElements(By.id("catCtxtMenu-0"));
        while (categories.size() != 0) {
            categories.get(0).click();
            waitForPageReady(true);
            driver.findElement(By.id("catDelete-0")).click();

            waitForPageReady(true);

            categories = driver.findElements(By.id("catCtxtMenu-0"));
        }
    }

    @Test
    public void testCreateDeleteCategory() throws InterruptedException {

        createCategory("category"); // idx: 0

        try {
            // Verify the title is OK
            assertEquals("category", driver.findElement(By.id("catTitle-0")).getText());
        }
        finally {
            deleteAllCategories();

            // Verify the category box is not there anymore
            assertEquals(0, driver.findElements(By.id("catTitle-0")).size());
        }
    }

    // Trigger the add link item from the category contextual menu
    // Fill up the category form and submit it
    public static void createLink(int catIdx, String title) {
        driver.findElement(By.id("catCtxtMenu-" + catIdx)).click();
        waitForPageReady(true);
        driver.findElement(By.id("catAddLink-" + catIdx)).click();

        WebElement addLinkForm = driver.findElement(By.id("addLnkForm"));
        addLinkForm.findElement(By.id("addLnkTitle")).sendKeys(title);
        addLinkForm.findElement(By.id("addLnkHRef")).sendKeys("http://test.org/");
        addLinkForm.findElement(By.id("addLnkOK")).click();

        waitForPageReady(true);
    }

    // Trigger the delete link action
    public static void deleteLink(int catIdx, int linkIdx) {
        driver.findElement(By.id("linkDelete-" + catIdx + "-" + linkIdx)).click();

        waitForPageReady(true);
    }

    @Test
    public void testCreateDeleteCategoryAndLink() throws InterruptedException {

        createCategory("category"); // idx: 0

        try {
            createLink(0, "link"); // idx: 0

            // Verify the title is OK
            WebElement linkAnchor = driver.findElement(By.id("linkAnchor-0-0"));
            assertEquals("link", linkAnchor.getText());
            assertEquals("http://test.org/", linkAnchor.getAttribute("href"));

            deleteLink(0, 0);

            // Verify the category box is not there anymore
            assertEquals(0, driver.findElements(By.id("linkAnchor-0-0")).size());
        }
        finally {
            deleteAllCategories();
        }
    }

    @Test
    public void testCreateChangeWidthDeleteCategory() throws InterruptedException {

        createCategory("category"); // idx: 0

        try {
            // Verify the default width
            assertTrue(driver.findElement(By.id("catBox-0")).getAttribute("class").contains("span2"));

            // Update the width to the largest one
            driver.findElement(By.id("catCtxtMenu-0")).click();
            waitForPageReady(true);
            driver.findElement(By.id("setCatWidth-0")).click();

            WebElement setCatWidthForm = driver.findElement(By.id("setCatWidthForm"));
            WebElement selectBox = setCatWidthForm.findElement(By.id("setCatWidth"));
            selectBox.click();
            selectBox.sendKeys(Keys.END, Keys.ENTER);
            setCatWidthForm.findElement(By.id("setCatWidthOK")).click();

            waitForPageReady(true);

            // Verify the width is now max
            assertTrue(driver.findElement(By.id("catBox-0")).getAttribute("class").contains("span12"));
        }
        finally {
            deleteAllCategories();
        }
    }

    @Test
    public void testCreateSwapDeleteCategories() throws InterruptedException {

        createCategory("first"); // idx: 0
        createCategory("middle"); // idx: 1
        createCategory("last"); // idx: 2

        try {
            // Verify the orders
            assertEquals("first", driver.findElement(By.id("catTitle-0")).getText());
            assertEquals("middle", driver.findElement(By.id("catTitle-1")).getText());
            assertEquals("last", driver.findElement(By.id("catTitle-2")).getText());

            // Update the position of the first category to be the last one, others will shift
            driver.findElement(By.id("catCtxtMenu-0")).click();
            waitForPageReady(true);
            driver.findElement(By.id("setCatOrder-0")).click();

            WebElement setCatOrderForm = driver.findElement(By.id("setCatOrderForm"));
            WebElement selectBox = setCatOrderForm.findElement(By.id("setCatOrder"));
            selectBox.click();
            selectBox.sendKeys(Keys.END, Keys.ENTER);
            setCatOrderForm.findElement(By.id("setCatOrderOK")).click();

            waitForPageReady(true);

            // Verify the orders
            assertEquals("middle", driver.findElement(By.id("catTitle-0")).getText());
            assertEquals("last", driver.findElement(By.id("catTitle-1")).getText());
            assertEquals("first", driver.findElement(By.id("catTitle-2")).getText());
        }
        finally {
            deleteAllCategories();

            // Verify the category box is not there anymore
            assertEquals(0, driver.findElements(By.id("catTitle-0")).size());
        }
    }
}
