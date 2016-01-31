package uk.me.duncancorps;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.google.common.collect.Sets;

public class Pets4Homes {
	private static List<WebElement> finds(final SearchContext searchContext,
			final By by) {
		return searchContext.findElements(by);
	}

	private static WebElement find(final SearchContext searchContext,
			final By by) {
		return searchContext.findElement(by);
	}

	private static boolean currentUrlIs(WebDriver webDriver, String expectedUrl) {
		String actualUrl = webDriver.getCurrentUrl();

		return StringUtils.equals(expectedUrl, actualUrl);
	}

	public static void main(String[] args) throws InterruptedException {
		WebDriver webDriver = new HtmlUnitDriver(); // FirefoxDriver();
		webDriver.get("http://www.pets4homes.co.uk/login/");
		boolean loggedIn = false;
		List<WebElement> forms = finds(webDriver, By.tagName("form"));

		for (WebElement form : forms) {
			if (!loggedIn) {
				WebElement email = find(form, By.name("email"));
				WebElement pass = find(form, By.name("pass"));
				WebElement login = find(form, By.name("login"));

				if (email != null && pass != null && login != null) {
					email.sendKeys("liz.corps@ntlworld.com");
					pass.sendKeys("Canterbury1");
					login.click();

					loggedIn = currentUrlIs(webDriver,
							"http://www.pets4homes.co.uk/account/");
				}
			}
		}

		if (loggedIn) {
			Set<String> aHrefs = Sets.newHashSet();
			boolean clicked;

			do {
				clicked = false;
				webDriver
						.get("http://www.pets4homes.co.uk/account/manage-adverts/");
				List<WebElement> as = finds(webDriver,
						By.cssSelector("table.manageTable a"));

				for (WebElement a : as) {
					if (!clicked) {
						String aTitle = a.getAttribute("title");

						if (StringUtils.equals(aTitle,
								"Refreshes adverts last updated date")) {
							String aHref = a.getAttribute("href");

							if (!aHrefs.contains(aHref)) {
								a.click();
								aHrefs.add(aHref);
								clicked = true;
							}
						}
					}
				}
			} while (clicked);
		}

		Thread.sleep(2000);

		webDriver.close();
		webDriver.quit();
	}
}
