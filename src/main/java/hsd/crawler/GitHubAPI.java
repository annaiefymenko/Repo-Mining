package hsd.crawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GitHubAPI {

    public static List<String> werteQuelleAus() throws Exception {
        List<String> repos = new ArrayList<>();

        URL url = new URL("https://api.github.com/search/repositories?q=stars:%3E10000&sort=stars&per_page=100");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JsonParser parser = new JsonParser(); // für ältere Gson-Versionen
        JsonObject jsonObject = (JsonObject) parser.parse(response.toString());
        JsonArray items = jsonObject.getAsJsonArray("items");

        for (JsonElement item : items) {
            JsonObject repo = item.getAsJsonObject();
            String fullName = repo.get("full_name").getAsString();
            repos.add(fullName);
        }

        return repos;
    }
}
