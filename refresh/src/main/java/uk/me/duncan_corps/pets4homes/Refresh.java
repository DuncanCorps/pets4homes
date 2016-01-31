package uk.me.duncan_corps.pets4homes;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.google.common.collect.Sets;

public class Refresh {
	private static final String PETS4HOMES_SCHEME_HOST = "https://www.pets4homes.co.uk/";
	private static final String PETS4HOMES_ACCOUNT_PATH = "account/";
	private static final String PETS4HOMES_LOG_IN_PATH = "login/";

	private static void usage(String... errors) {
		System.err.println(
				"Usage: java -jar pets4homes-refresh-1.0.0-jar-with-dependencies.jar <emailAddress> <password>");
		for (String error : errors) {
			System.err.println("Error: " + error);
		}
	}

	private static List<WebElement> finds(final SearchContext searchContext, final By by) {
		return searchContext.findElements(by);
	}

	private static WebElement find(final SearchContext searchContext, final By by) {
		return searchContext.findElement(by);
	}

	private static boolean currentUrlIs(WebDriver webDriver, String expectedUrl) {
		String actualUrl = webDriver.getCurrentUrl();

		return StringUtils.equals(expectedUrl, actualUrl);
	}

	private static boolean logIn(WebDriver webDriver, String emailValue, String passValue) {
		boolean loggedIn = false;
		List<WebElement> forms = finds(webDriver, By.tagName("form"));

		for (WebElement form : forms) {
			if (!loggedIn) {
				WebElement emailWebElement = find(form, By.name("email"));
				WebElement passWebElement = find(form, By.name("pass"));
				WebElement loginWebElement = find(form, By.name("login"));

				if (emailWebElement != null && passWebElement != null && loginWebElement != null) {
					emailWebElement.sendKeys(emailValue);
					passWebElement.sendKeys(passValue);
					loginWebElement.click();

					loggedIn = currentUrlIs(webDriver, PETS4HOMES_SCHEME_HOST + PETS4HOMES_ACCOUNT_PATH);
				}
			}
		}

		return loggedIn;
	}

	private static void refreshAdverts(WebDriver webDriver) {
		Set<String> aHrefs = Sets.newHashSet();
		boolean clicked;

		do {
			clicked = false;
			webDriver.get(PETS4HOMES_SCHEME_HOST + PETS4HOMES_ACCOUNT_PATH + "manage-adverts/");
			List<WebElement> aWebElements = finds(webDriver,
					By.cssSelector("div.manageadvert div.managebuttons a.btn"));

			for (WebElement aWebElement : aWebElements) {
				if (!clicked) {
					String aTitle = aWebElement.getAttribute("title");

					if (StringUtils.equals(aTitle, "Refreshes adverts last updated date")) {
						String aHref = aWebElement.getAttribute("href");

						if (!aHrefs.contains(aHref)) {
							aWebElement.click();
							aHrefs.add(aHref);
							clicked = true;
						}
					}
				}
			}
		} while (clicked);
	}

	public static void main(String[] arguments) throws InterruptedException {
		int exitStatus = 0;

		if (ArrayUtils.getLength(arguments) >= 2) {
			String emailValue = arguments[0];

			if (GenericValidator.isEmail(emailValue)) {
				String passValue = arguments[1];

				WebDriver webDriver = new HtmlUnitDriver(); // FirefoxDriver();
				webDriver.get(PETS4HOMES_SCHEME_HOST + PETS4HOMES_LOG_IN_PATH);

				final boolean loggedIn = logIn(webDriver, emailValue, passValue);

				if (loggedIn) {
					refreshAdverts(webDriver);
				}

				Thread.sleep(2000);
				webDriver.close();
				webDriver.quit();
			} else {
				usage("First argument is not an email address.");
				exitStatus = 2;
			}
		} else {
			usage();
			exitStatus = 1;
		}

		System.exit(exitStatus);
	}
}
