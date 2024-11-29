package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class BaseTest {

	   private WebDriver driver;
	    private Page page;

	    @BeforeTest
	    public void initDriver() {
	        driver = new ChromeDriver();
	        driver.manage().window().maximize();
	        page = new Page(driver);
	    }

	    @Test
	    public void simpleTest() {
	        driver.get("https://www.google.com");
	        String name = page.simpletestCaseMethod();
	        System.out.println("DONE");
	        page.clickOnloginBtn();
	    }
}
