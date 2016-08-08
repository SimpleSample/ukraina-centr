package com.nagornyi.uc.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

/**
 * Sample page
 */
public class HomePage extends Page {

  @FindBy(how = How.ID, using = "EUR-UAH")
  @CacheLookup
  private WebElement currencyWidget;

  @FindBy(how = How.CLASS_NAME, using = "auth-links")
  @CacheLookup
  private WebElement authLinks;

  @FindBy(how = How.CLASS_NAME, using = "light-op")
  @CacheLookup
  private WebElement whiteBackground;

  public HomePage(WebDriver webDriver) {
    super(webDriver);
  }

  public WebElement getCurrencyWidget() {
    return currencyWidget;
  }

  public WebElement getAuthLinks() {
    return authLinks;
  }

  public WebElement getWhiteBackground() {
    return whiteBackground;
  }
}
