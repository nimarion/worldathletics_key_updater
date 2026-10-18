package de.pfingstsportfest.worldathletics;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final String BASE_URL = "https://worldathletics.org";
    private static final String PAGE_URL = BASE_URL + "/athletes/-/14193865";
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36";

    private static final Pattern SCRIPT_SRC_PATTERN = Pattern.compile("src=\"(/_next/static/[^\"]+\\.js)\"");
    private static final Pattern GRAPHQL_BLOCK_PATTERN = Pattern.compile("(?:^|[,{])graphql:\\{([^}]+)\\}");
    private static final Pattern ENDPOINT_PATTERN = Pattern.compile("(?:^|[,{])endpoint:\"([^\"]+)\"");
    private static final Pattern API_KEY_PATTERN = Pattern.compile("(?:^|[,{])apiKey:\"([^\"]+)\"");

    public Main(String... args) {
        int status = -1;
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        try {
            String html = fetch(client, PAGE_URL);
            Matcher scriptMatcher = SCRIPT_SRC_PATTERN.matcher(html);

            while (scriptMatcher.find()) {
                String scriptPath = scriptMatcher.group(1);
                String js = fetch(client, BASE_URL + scriptPath);

                Matcher blockMatcher = GRAPHQL_BLOCK_PATTERN.matcher(js);
                while (blockMatcher.find()) {
                    String block = blockMatcher.group(1);
                    Matcher endpointMatcher = ENDPOINT_PATTERN.matcher(block);
                    Matcher apiKeyMatcher = API_KEY_PATTERN.matcher(block);

                    if (endpointMatcher.find() && apiKeyMatcher.find()) {
                        String apiEndpoint = endpointMatcher.group(1);
                        String apiKey = apiKeyMatcher.group(1);

                        Path path = Paths.get("stellate.ts");
                        Charset charset = StandardCharsets.UTF_8;

                        String content = Files.readString(path, charset);
                        content = content.replaceAll("wa-api-key", apiKey);
                        content = content.replaceAll("wa-api-endpoint", apiEndpoint);
                        Files.writeString(path, content, charset);

                        System.out.println(apiEndpoint + ":" + apiKey);
                        status = 0;
                        break;
                    }
                }
                if (status == 0) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(status);
    }

    private String fetch(HttpClient client, String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Unexpected HTTP status " + response.statusCode() + " for " + url);
        }
        return response.body();
    }

    public static void main(String... args) {
        try {
            new Main(args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
