

//NUR FÜR GITHUB NICHT GITLAB
kkjkjkjkjkjkj
package hsd.crawler;

import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.util.*;

import static com.google.gson.JsonParser.*;
/*
public class GitHubAPI {

    public static List<Repository> holeTopRepos(String token) throws Exception {
        List<Repository> repos = new ArrayList<>();
        int maxPages = 5; // z.B 5 Seiten à 100 Repos

        for (int page = 1; page <= maxPages; page++) {
            String urlString = "https://api.github.com/search/repositories?q=stars:>10000&sort=stars&per_page=100&page=" + page;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // GitHub API Header
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestProperty("Authorization", "token " + token); // Authentifizierung hier

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(response.toString()).getAsJsonObject();
            JsonArray items = root.getAsJsonArray("items");

            for (JsonElement item : items) {
                JsonObject repoJson = item.getAsJsonObject();
                String name = repoJson.get("full_name").getAsString();
                String language = repoJson.get("language").isJsonNull() ? "Unbekannt" : repoJson.get("language").getAsString();
                int stars = repoJson.get("stargazers_count").getAsInt();
                int forks = repoJson.get("forks_count").getAsInt();

                repos.add(new Repository(name, language, stars, forks));
            }
        }

        return repos;
    }

}
*/