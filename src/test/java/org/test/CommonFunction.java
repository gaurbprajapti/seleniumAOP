package org.test;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CommonFunction {
	
	  @FindBy(id = "loginInput")
	  private WebElement loginInput;
	  
	public void addCandidateFromAPI() {
		
	}
	
	public void addContactFormAPI() {
		loginInput.click();
	}

}
