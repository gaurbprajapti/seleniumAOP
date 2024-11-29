package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Page {
  @FindBy(className = "test")
  private WebElement loginBtn;

  @FindBy(id = "loginInput")
  private WebElement loginInput;

  public Page(WebDriver driver) {
    PageFactory.initElements(driver, this);
  }
  
  public String simpletestCaseMethod() {
	  String name = "gaurav";
	  return name;
  }
  
  public String testcheckMethod() {
	  String name = "gaurav";
	  return name;
  }
  
  public void clickOnloginBtn() {
    loginBtn.click();
  }

  public void clickOnloginInput() {
    loginInput.click();
  }
  
}