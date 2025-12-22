package com.infrrd.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResultsPage extends BasePage {

    @FindBy(css = "div.fli-intl-lhs")
    private List<WebElement> flightCards;

    @FindBy(xpath = "//span[text()='Non Stop']/preceding-sibling::span/input")
    private WebElement nonStopFilterCheckbox;

    @FindBy(css = "div.flightBody")
    private WebElement resultsContainer;

    public ResultsPage(WebDriver driver) {
        super(driver);
    }

    public void waitForResultsToLoad() {
        wait.until(ExpectedConditions.visibilityOf(resultsContainer));
    }

    private double extractPrice(WebElement flightCard) {
        WebElement priceElement = flightCard.findElement(By.cssSelector("p.fld-rtng-prc"));
        String priceText = priceElement.getText().replaceAll("[^0-9]", "");
        if (priceText.isEmpty()) {
            return Double.MAX_VALUE;
        }
        return Double.parseDouble(priceText);
    }

    public List<FlightInfo> getFlightsSortedByPrice() {
        waitForResultsToLoad();
        List<WebElement> cards = flightCards;
        if (cards == null || cards.isEmpty()) {
            cards = driver.findElements(By.cssSelector("div.fli-intl-lhs"));
        }
        return cards.stream()
                .map(card -> {
                    String airline = card.findElement(By.cssSelector("span.airlineName")).getText();
                    String departure = card.findElement(By.cssSelector("div.dep-time")).getText();
                    String arrival = card.findElement(By.cssSelector("div.arr-time")).getText();
                    double price = extractPrice(card);
                    return new FlightInfo(airline, departure, arrival, price);
                })
                .sorted(Comparator.comparingDouble(FlightInfo::getPrice))
                .collect(Collectors.toList());
    }

    public Optional<FlightInfo> getCheapestFlight() {
        List<FlightInfo> sorted = getFlightsSortedByPrice();
        if (sorted.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(sorted.get(0));
    }

    public Optional<FlightInfo> getSecondCheapestFlight() {
        List<FlightInfo> sorted = getFlightsSortedByPrice();
        if (sorted.size() < 2) {
            return Optional.empty();
        }
        return Optional.of(sorted.get(1));
    }

    public void applyNonStopFilter() {
        waitForResultsToLoad();
        if (!nonStopFilterCheckbox.isSelected()) {
            nonStopFilterCheckbox.click();
        }
        wait.until(ExpectedConditions.stalenessOf(resultsContainer));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.flightBody")));
    }

    public static class FlightInfo {
        private final String airline;
        private final String departureTime;
        private final String arrivalTime;
        private final double price;

        public FlightInfo(String airline, String departureTime, String arrivalTime, double price) {
            this.airline = airline;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.price = price;
        }

        public String getAirline() {
            return airline;
        }

        public String getDepartureTime() {
            return departureTime;
        }

        public String getArrivalTime() {
            return arrivalTime;
        }

        public double getPrice() {
            return price;
        }

        @Override
        public String toString() {
            return "FlightInfo{" +
                    "airline='" + airline + '\'' +
                    ", departureTime='" + departureTime + '\'' +
                    ", arrivalTime='" + arrivalTime + '\'' +
                    ", price=" + price +
                    '}';
        }
    }
}


