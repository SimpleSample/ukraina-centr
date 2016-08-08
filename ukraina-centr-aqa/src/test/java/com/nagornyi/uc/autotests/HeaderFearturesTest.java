package com.nagornyi.uc.autotests;

import com.nagornyi.uc.pages.HomePage;
import com.nagornyi.uc.util.AQAUserUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.logging.Logger;

public class HeaderFearturesTest extends NgTestBase {

    private static final Logger LOG = Logger.getLogger(HeaderFearturesTest.class.getName());

    private HomePage homepage;

    @BeforeMethod
    public void initPageObjects() {
        homepage = PageFactory.initElements(driver, HomePage.class);
    }

    @Test
    public void testHomePageHasCurrencyWidget() {
        WebElement currencyWidget = homepage.getCurrencyWidget();
        String currencyText = currencyWidget.getText();

        Assert.assertTrue(currencyText.endsWith("₴"));
        Assert.assertFalse("24.00₴".equals(currencyText));
    }

    @Test
    public void testCorrectLinksForGuestUser() {
        LOG.info("Checking auth links for guest user");

        WebElement authLinks = homepage.getAuthLinks();

        WebElement loginLink = authLinks.findElement(By.id("login"));
        WebElement cabinetLink = authLinks.findElement(By.id("cabinet"));
        WebElement registerLink = authLinks.findElement(By.id("register"));
        WebElement logoutLink = authLinks.findElement(By.id("logout"));

        Assert.assertTrue(loginLink.isDisplayed(), "Login link should be displayed");
        Assert.assertFalse(cabinetLink.isDisplayed(), "Cabinet link should be hidden");
        Assert.assertTrue(registerLink.isDisplayed(), "Register link should be displayed");
        Assert.assertFalse(logoutLink.isDisplayed(), "Logout link should be hidden");
    }

    @Test
    public void testCorrectLinksForAuthorizedUser() {
        LOG.info("Checking auth links for authorized user");

        WebElement authLinks = homepage.getAuthLinks();

        WebElement loginLink = authLinks.findElement(By.id("login"));
        WebElement cabinetLink = authLinks.findElement(By.id("cabinet"));
        WebElement registerLink = authLinks.findElement(By.id("register"));
        WebElement logoutLink = authLinks.findElement(By.id("logout"));

        AQAUserUtils.loginUser(loginLink, driver);

        Assert.assertFalse(loginLink.isDisplayed(), "Login link should be displayed");
        Assert.assertTrue(cabinetLink.isDisplayed(), "Cabinet link should be hidden");
        Assert.assertFalse(registerLink.isDisplayed(), "Register link should be displayed");
        Assert.assertTrue(logoutLink.isDisplayed(), "Logout link should be hidden");

        AQAUserUtils.logoutUser(logoutLink, driver, homepage.getWhiteBackground());
    }
}
