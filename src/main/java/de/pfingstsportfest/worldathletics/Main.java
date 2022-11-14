package de.pfingstsportfest.worldathletics;

import java.time.Duration;
import java.util.Iterator;
import java.util.logging.Level;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Main {

    public Main(String... args) {
        String url = "https://www.worldathletics.org/athletes/-/14193865";
        WebDriverManager.chromedriver().setup();
        ChromeDriver driver = null;
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");

            DesiredCapabilities cap = new DesiredCapabilities();
            cap.setCapability(ChromeOptions.CAPABILITY, options);

            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
            options.setCapability("goog:loggingPrefs", logPrefs);
            driver = new ChromeDriver(options);

            driver.get(url);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

            LogEntries logs = driver.manage().logs().get("performance");
            int status = -1;
            System.out.println("\\nList of log entries:\\n");
            for (Iterator<LogEntry> it = logs.iterator(); it.hasNext();) {
                LogEntry entry = it.next();
                try {
                    JSONObject json = new JSONObject(entry.getMessage());
                    if (json.toString().contains("\"x-api-key\":")) {
                        JSONObject message = json.getJSONObject("message");
                        JSONObject params = message.getJSONObject("params");
                        JSONObject request = params.getJSONObject("request");

                        if (request.has("headers")) {
                            System.out.println("API-Endpoint: " + request.getString("url"));
                            System.out
                                    .println("API-KEY: " + request.getJSONObject("headers").getString("x-api-key"));
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("\nstatus code: " + status);
        } finally

        {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    public static void main(String... args) {
        try {
            new Main(args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
