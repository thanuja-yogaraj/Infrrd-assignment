## Travel Automation POM Framework (Java, Maven, TestNG, Selenium)

This project is a sample **Page Object Model (POM)** based automation framework that automates a flight search flow on a travel website (MakeMyTrip) and demonstrates good test design practices.

### 1. Tech Stack

- **Language**: Java 11
- **Build Tool**: Maven
- **Test Framework**: TestNG
- **Web Automation**: Selenium WebDriver
- **Driver Management**: WebDriverManager (for automatic ChromeDriver setup)

All dependencies are declared in `pom.xml`.

### 2. Project Structure

- **`pom.xml`**  
  Maven configuration, dependencies (Selenium, TestNG, WebDriverManager), and Surefire plugin setup.

- **Core / Test Base**
  - `src/test/java/com/infrrd/automation/core/DriverManager.java`  
    Manages the WebDriver lifecycle using a `ThreadLocal<WebDriver>`:
    - Initializes ChromeDriver (via WebDriverManager).
    - Provides `getDriver()` to tests/pages.
    - Quits and cleans up the driver after each test.

  - `src/test/java/com/infrrd/automation/core/BaseTest.java`  
    Base class for all TestNG tests:
    - `@BeforeMethod` initializes the driver and navigates to the base URL (`https://www.makemytrip.com/`).
    - `@AfterMethod` quits the driver.

- **Page Objects**
  - `src/test/java/com/infrrd/automation/pages/BasePage.java`  
    Abstract base page:
    - Stores `WebDriver` and a reusable `WebDriverWait`.
    - Uses `PageFactory.initElements()` to initialize `@FindBy` elements.

  - `src/test/java/com/infrrd/automation/pages/HomePage.java`  
    Models the MakeMyTrip **Home / Flights** page:
    - `goToFlightsSection()` – opens the Flights tab.
    - `enterSourceCity(String city)` – selects the source city using the auto-suggest list.
    - `enterDestinationCity(String city)` – selects the destination city using the auto-suggest list.
    - `selectNextMonthAnyDate(int dayOfMonth)` – opens the date picker, navigates to **next month**, and selects the given day.
    - `clickSearch()` – closes any overlay (ESC) and clicks **Search**, returning a `ResultsPage`.

  - `src/test/java/com/infrrd/automation/pages/ResultsPage.java`  
    Models the flights **results** page:
    - `waitForResultsToLoad()` – waits for the main results container.
    - `getFlightsSortedByPrice()` – builds a list of `FlightInfo` objects and sorts them by price (ascending).
    - `getCheapestFlight()` – returns the cheapest flight as an `Optional<FlightInfo>`.
    - `getSecondCheapestFlight()` – returns the second cheapest flight (if any).
    - `applyNonStopFilter()` – applies the **Non Stop** filter and waits for the list to refresh.
    - `FlightInfo` inner class – holds airline, departure time, arrival time, and price.

- **Tests**
  - `src/test/java/com/infrrd/automation/tests/FlightSearchTest.java`  
    Contains two TestNG tests using the POM classes:
    - `searchFlightsAndPrintCheapest()`  
      Main scenario as per the problem statement:
      1. Navigate to MakeMyTrip (handled by `BaseTest`).
      2. Go to the Flights section.
      3. Enter source (`Bengaluru, India`) and destination (`Delhi, India`).
      4. Select a date for the **next month**.
      5. Click **Search**.
      6. Identify and **print** the **cheapest** and **second cheapest** flight details.
      7. Open a **new browser tab** within the same session and navigate to **Google**, asserting that Google opened successfully.

    - `searchFlightsAndApplyNonStopFilter()` (additional scenario)  
      1. Performs a similar flight search (different day of the next month).
      2. Applies the **Non Stop** filter on the results page.
      3. Asserts that at least one non-stop flight is present and prints the count.

### 3. How the POM Design Is Applied

- Each **page** (Home, Results) encapsulates:
  - Its own **locators** (`@FindBy`).
  - Its own **actions** and **business logic** (e.g., selecting cities, picking dates, filtering results).
- **Tests** are kept **high-level and readable**, chaining page methods instead of dealing with low-level Selenium calls:
  - Example: `homePage.goToFlightsSection().enterSourceCity("...").enterDestinationCity("...").selectNextMonthAnyDate(10).clickSearch();`
- Shared logic such as driver setup and waits live in `BaseTest` and `BasePage`, improving reusability and maintainability.

### 4. How to Build and Run Tests

Prerequisites:
- JDK 11+ installed and `JAVA_HOME` configured.
- Maven installed and available on your `PATH`.
- Chrome browser installed.

Steps:

1. Open a terminal in the project root:

   ```bash
   cd /Users/harish/Documents/infrrd
   ```

2. (Optional) Build/compile the project:

   ```bash
   mvn clean compile
   ```

3. Run the TestNG tests:

   ```bash
   mvn test
   ```

Maven will:
- Download all required dependencies.
- Launch Chrome via WebDriverManager.
- Execute the two TestNG tests in `FlightSearchTest`.

### 5. Notes and Possible Enhancements

- DOM structures on real travel websites change frequently. If elements move or are renamed, update the locators in `HomePage` and `ResultsPage`.
- For production frameworks, you can extend this project by:
  - Adding a `testng.xml` suite file.
  - Externalizing configuration (base URL, browser type, timeouts) into a properties or YAML file.
  - Adding logging (e.g., Log4j/SLF4J) and a reporting library (e.g., Allure or ExtentReports).


