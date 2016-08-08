package com.nagornyi.uc.util;

import com.google.common.base.Predicate;
import com.nagornyi.uc.pages.LoginDialog;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AQAUserUtils {

    public static void loginUser(WebElement loginLink, final WebDriver driver) {
        loginLink.click();

        final LoginDialog loginDialog = PageFactory.initElements(driver, LoginDialog.class);

        loginDialog.getEmail().sendKeys("test@test.com");
        loginDialog.getPass().sendKeys("Qwer1234");

        loginDialog.getLoginButton().click();

        new WebDriverWait(driver, 2).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver webDriver) {
                return !isElementPresent(driver, By.id("btn-login"));
            }
        });
    }

    public static void logoutUser(WebElement logoutLink, WebDriver driver, final WebElement whiteBackground) {
        logoutLink.click();

        new WebDriverWait(driver, 2).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver webDriver) {
                return !whiteBackground.isDisplayed();
            }
        });
    }

    public static boolean isElementPresent(WebDriver driver, By locatorKey) {
        try {
            driver.findElement(locatorKey);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
