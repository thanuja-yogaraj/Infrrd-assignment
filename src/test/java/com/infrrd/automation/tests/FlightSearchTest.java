package com.infrrd.automation.tests;

import com.infrrd.automation.core.BaseTest;
import com.infrrd.automation.pages.HomePage;
import com.infrrd.automation.pages.ResultsPage;
import com.infrrd.automation.pages.ResultsPage.FlightInfo;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WindowType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

public class FlightSearchTest extends BaseTest {

    @Test(description = "Search flights and print cheapest and second cheapest flight details, then open Google in new tab")
    public void searchFlightsAndPrintCheapest() {
        HomePage homePage = new HomePage(driver);

        ResultsPage resultsPage = homePage
                .goToFlightsSection()
                .enterSourceCity("Bengaluru, India")
                .enterDestinationCity("Delhi, India")
                .selectNextMonthAnyDate(10)
                .clickSearch();

        Optional<FlightInfo> cheapest = resultsPage.getCheapestFlight();
        Optional<FlightInfo> secondCheapest = resultsPage.getSecondCheapestFlight();

        Assert.assertTrue(cheapest.isPresent(), "No flights found for cheapest flight.");

        System.out.println("Cheapest flight: " + cheapest.get());
        secondCheapest.ifPresent(info -> System.out.println("Second cheapest flight: " + info));

        // Open a new tab within same session and navigate to Google
        if (driver instanceof JavascriptExecutor) {
            driver.switchTo().newWindow(WindowType.TAB);
            driver.get("https://www.google.com");
            Assert.assertTrue(driver.getTitle().toLowerCase().contains("google"), "Google did not open correctly.");
        }
    }

    @Test(description = "Additional scenario: Apply Non-Stop filter and verify at least one result is shown")
    public void searchFlightsAndApplyNonStopFilter() {
        HomePage homePage = new HomePage(driver);

        ResultsPage resultsPage = homePage
                .goToFlightsSection()
                .enterSourceCity("Bengaluru, India")
                .enterDestinationCity("Delhi, India")
                .selectNextMonthAnyDate(15)
                .clickSearch();

        resultsPage.applyNonStopFilter();
        List<FlightInfo> flights = resultsPage.getFlightsSortedByPrice();

        Assert.assertFalse(flights.isEmpty(), "No non-stop flights found after applying filter.");
        System.out.println("Found " + flights.size() + " non-stop flights after filtering.");
    }
}


