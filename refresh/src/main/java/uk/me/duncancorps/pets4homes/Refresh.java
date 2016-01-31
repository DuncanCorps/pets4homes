package uk.me.duncancorps.pets4homes;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.google.common.collect.Sets;

public class Refresh {
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

					loggedIn = currentUrlIs(webDriver, "http://www.pets4homes.co.uk/account/");
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
			webDriver.get("http://www.pets4homes.co.uk/account/manage-adverts/");
			List<WebElement> aWebElements = finds(webDriver, By.cssSelector("table.manageTable a"));

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
		if (ArrayUtils.getLength(arguments) >= 2) {
			String emailValue = "liz.corps@ntlworld.com";
			String passValue = "Canterbury1";
			WebDriver webDriver = new HtmlUnitDriver(); // FirefoxDriver();
			webDriver.get("http://www.pets4homes.co.uk/login/");

			final boolean loggedIn = logIn(webDriver, emailValue, passValue);

			if (loggedIn) {
				refreshAdverts(webDriver);
			}

			Thread.sleep(2000);
			webDriver.close();
			webDriver.quit();
		} else {
		}
	}
}
