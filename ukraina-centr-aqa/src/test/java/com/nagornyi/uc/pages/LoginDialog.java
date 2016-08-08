package com.nagornyi.uc.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class LoginDialog extends Page {

    @FindBy(how = How.CLASS_NAME, using = "popup-close")
    private WebElement closeButton;

    @FindBy(how = How.CSS, using = "[name='email']")
    private WebElement email;

    @FindBy(how = How.CSS, using = "[name='password']")
    private WebElement pass;

    @FindBy(how = How.ID, using = "btn-login")
    private WebElement loginButton;


    public LoginDialog(WebDriver driver) {
        super(driver);
    }

    public WebElement getCloseButton() {
        return closeButton;
    }

    public WebElement getEmail() {
        return email;
    }

    public WebElement getPass() {
        return pass;
    }

    public WebElement getLoginButton() {
        return loginButton;
    }
}
