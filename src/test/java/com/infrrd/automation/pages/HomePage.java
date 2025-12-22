package com.infrrd.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HomePage extends BasePage {

    @FindBy(xpath = "//li[@data-cy='menu_Flights']")
    private WebElement flightsTab;

    @FindBy(id = "fromCity")
    private WebElement fromCityInput;

    @FindBy(id = "toCity")
    private WebElement toCityInput;

    @FindBy(xpath = "//label[@for='departure']")
    private WebElement departureField;

    @FindBy(xpath = "//a[text()='Search']")
    private WebElement searchButton;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage goToFlightsSection() {
        wait.until(ExpectedConditions.elementToBeClickable(flightsTab)).click();
        return this;
    }

    public HomePage enterSourceCity(String city) {
        wait.until(ExpectedConditions.elementToBeClickable(fromCityInput)).click();
        WebElement input = driver.findElement(By.xpath("//input[@placeholder='From']"));
        input.sendKeys(city);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'" + city.split(" ")[0] + "')]"))).click();
        return this;
    }

    public HomePage enterDestinationCity(String city) {
        wait.until(ExpectedConditions.elementToBeClickable(toCityInput)).click();
        WebElement input = driver.findElement(By.xpath("//input[@placeholder='To']"));
        input.sendKeys(city);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'" + city.split(" ")[0] + "')]"))).click();
        return this;
    }

    public HomePage selectNextMonthAnyDate(int dayOfMonth) {
        wait.until(ExpectedConditions.elementToBeClickable(departureField)).click();

        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusMonths(1).withDayOfMonth(dayOfMonth);
        String monthYear = targetDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        String day = String.valueOf(targetDate.getDayOfMonth());

        // Navigate to next month in the calendar control if needed
        while (true) {
            WebElement monthTitle = driver.findElement(By.xpath("//div[@class='DayPicker-Caption']/div"));
            if (monthTitle.getText().equalsIgnoreCase(monthYear)) {
                break;
            }
            WebElement nextButton = driver.findElement(By.xpath("//span[@aria-label='Next Month']"));
            nextButton.click();
        }

        WebElement dayElement = driver.findElement(By.xpath(
                "//div[@class='DayPicker-Caption']/div[contains(text(),'" + monthYear + "')]/" +
                        "../../following-sibling::div//p[text()='" + day + "']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dayElement);

        return this;
    }

    public ResultsPage clickSearch() {
        // Close potential overlays/popups by sending ESC
        driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        return new ResultsPage(driver);
    }
}


