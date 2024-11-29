package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class SeleniumAspectSupportTest {

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

    @Test
    public void testcheck() {
//    	test.info(hudfkjvn)
        driver.get("https://recruitcrm.io/jobs/rcrm");
        String name = page.testcheckMethod();
        
        System.out.println("DONE");
    }
    
    @Test(dataProvider = "testData")
    public void dataProviderTestCase(String testData) {
       String data = testDataMethod(testData);
       if (data == "testDataA") {
    	   Assert.fail("Data is invalid. ");
       }
    }

    @DataProvider(name = "testData")
    public Object[][] testData() {
        return new Object[][] {
            {"testDataA"}, 
            {"testDataB"}
        };
    }
    
    
    public String testDataMethod(String data) {
    	return data;
    }
    @AfterTest
    public void dropDriver() {
        driver.close();
    }
}
