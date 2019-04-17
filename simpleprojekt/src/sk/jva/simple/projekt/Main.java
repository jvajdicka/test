package sk.jva.simple.projekt;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Main {

    public static void main(String[] args) {

        String gcdriver = System.getProperty("webdriver.gecko.driver");

        if(gcdriver == null || gcdriver.length() == 0)
            System.setProperty("webdriver.gecko.driver","resources\\geckodriver.exe");
        else
            System.out.println("found webdriver.gecko.driver=" + gcdriver);

        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface,
        // not the implementation.
        WebDriver driver = new FirefoxDriver();

        // And now use this to visit Google
        driver.get("https://www.otpbanka.sk/otp-hypo-uver");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        try {
            List<WebElement> someElements = driver.findElements(By.cssSelector(".dynamicForm.formWrapper input[type='text']"));

            for (WebElement element : someElements) {
                setInputTextValue(driver, element, "form-field-name", "name");
                setInputTextValue(driver, element, "form-field-surname", "surname");
                setInputTextValue(driver, element, "form-field-phone", "123123123");
                setInputTextValue(driver, element, "form-field-email", "name@surname.sk");
            }

            someElements = driver.findElements(By.cssSelector(".dynamicForm.formWrapper select"));

            for (WebElement element : someElements) {
                String id = element.getAttribute("id");
                if (id != null && id.endsWith("_branch")) {
                    selectFromCombobox(driver, element);
                }
            }

            someElements = driver.findElements(By.cssSelector(".dynamicForm.formWrapper input[type='checkbox']"));

            for (WebElement element : someElements) {
                element.click();
            }

            someElements = driver.findElements(By.cssSelector(".dynamicForm.formWrapper .QapTcha .bgSlider div"));

            for (WebElement element : someElements) {
                scrollToElement(driver, element);

                int moveToX = driver.manage().window().getSize().getWidth() - element.getLocation().getX() - element.getSize().getWidth() - 1 - 1;

                Actions actions = new Actions(driver);
                actions.clickAndHold(element).moveByOffset(moveToX,0).release();
                actions.perform();
            }

            scrollToElement(driver, driver.findElement(By.cssSelector(".dynamicForm.formWrapper")));

            System.out.println("submit? (y/n)");
            Scanner in = new Scanner(System.in);
            if("y".equals(in.nextLine())) {
                scrollToElement(driver, driver.findElement(By.cssSelector(".dynamicForm.formWrapper")));
                driver.findElement(By.cssSelector(".dynamicForm.formWrapper input[type='submit']")).click();
                in.nextLine();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            //Close the browser
            driver.quit();
        }
    }

    private static void selectFromCombobox(WebDriver driver, WebElement element) {
        WebElement parent = (WebElement) ((JavascriptExecutor) driver)
                .executeScript(
                        "return arguments[0].parentNode;", element);

        WebElement clickElement = parent.findElement(By.cssSelector(".selectize-input"));

        scrollToElement(driver, parent);
        clickElement.click();

        WebElement optionElement = parent.findElement(By.cssSelector(".selectize-dropdown-content"));

        List<WebElement> options = optionElement.findElements(By.cssSelector(".option"));

        int optionToSelect = new Random().nextInt(options.size() -1) + 1;
        scrollToElement(driver, options.get(optionToSelect));
        options.get(optionToSelect).click();
    }

    private static void scrollToElement(WebDriver driver, WebElement element) {

        /*
        Actions actions = new Actions(driver);
        actions.moveToElement(element);
        actions.perform();
        */

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        try {
            Thread.sleep(500);
        } catch (Exception e) {

        }
    }

    private static boolean setInputTextValue(WebDriver driver, WebElement element, String key, String value) {
        String dataType = element.getAttribute("data-type");
        if(dataType != null && key.equals(dataType)){
            scrollToElement(driver, element);
            element.sendKeys(value);
            return true;
        }
        return false;
    }

    private void codeDump1() {
        System.setProperty("webdriver.gecko.driver","resources\\geckodriver.exe");

        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface,
        // not the implementation.
        WebDriver driver = new FirefoxDriver();

        // And now use this to visit Google
        driver.get("http://wsw74616:6060/simple.web.projekt.empty");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        List<WebElement> someElements = driver.findElements(By.cssSelector("input"));

        for(WebElement element : someElements) {
            if(element.getAttribute("name") != null &&
                    element.getAttribute("name").contains("field")) {
                element.sendKeys("ahoj!");
            }
        }

        //Close the browser
        driver.quit();
    }

    private void codeDump() {
        System.setProperty("webdriver.gecko.driver","resources\\geckodriver.exe");

        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface,
        // not the implementation.
        WebDriver driver = new FirefoxDriver();

        // And now use this to visit Google
        driver.get("http://www.google.com");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());

        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("cheese!");
            }
        });

        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
    }
}
