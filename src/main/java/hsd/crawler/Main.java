package hsd.crawler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Verbinde zur GitHub API...");

            String token = "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";
            List<Repository> repos = GitHubAPI.holeTopRepos(token);
            System.out.println("Anzahl gefundener Repos: " + repos.size());

            for (Repository repo : repos) {
                GitHubAnalyzer.analysiere(repo);
            }

            // JSON exportieren
            exportiereAlsJson(repos);

        } catch (Exception e) {
            System.err.println("Fehler beim Ausführen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void exportiereAlsJson(List<Repository> repos) {
        try (FileWriter writer = new FileWriter("repos.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(repos, writer);
            System.out.println("✅ JSON-Datei erstellt: repos.json");
        } catch (Exception e) {
            System.err.println("Fehler beim Schreiben der JSON-Datei: " + e.getMessage());
        }
    }
}
