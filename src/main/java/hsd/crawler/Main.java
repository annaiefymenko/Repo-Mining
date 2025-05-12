package hsd.crawler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        /*
        NUR FÜR GITHUB
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
        try (FileWriter writer = new FileWriter("github_repos_eigenschaften.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(repos, writer);
            System.out.println("JSON-Datei erstellt: github_repos_eigenschaften.json");
        } catch (Exception e) {
            System.err.println("Fehler beim Schreiben der JSON-Datei: " + e.getMessage());
        }*/

        String token = "JRgeDSqhNDEnaiGz6gww"; // <-- Token hier eintragen
        String namespace = "gitlab-org"; // z.B Benutzername oder Gruppenname
        List<Repository> repos = GitRepoEigenschaften.ladeAlleProjekte(token, namespace);

        for (Repository repo : repos) {
            System.out.println("Analysiere Repository: " + repo.name);

            repo.commits = GitRepoEigenschaften.ermittleAnzahlCommits(repo.name, token);
            repo.branches = GitRepoEigenschaften.ermittleAnzahlBranches(repo.name, token);
            repo.files = GitRepoEigenschaften.countFiles(repo.name, token);
            repo.lastModified = GitRepoEigenschaften.ermittleLetztesAenderungsdatum(repo.name, token);
            repo.submodules = GitRepoEigenschaften.listSubmodules(repo.name, token);
            repo.
            //repo.authors = GitRepoEigenschaften.ermittleAutoren(repo.name, token);

            System.out.println("→ Commits: " + repo.commits);
            System.out.println("→ Branches: " + repo.branches);
            System.out.println("→ Dateien: " + repo.files);
            System.out.println("→ Letzte Änderung: " + repo.lastModified);
            //System.out.println("→ Autoren: " + repo.authors);
            System.out.println("---------------------------------------");
        }

        // Schreibe Ergebnisse als JSON
        try (FileWriter writer = new FileWriter("gitlab_repos_eigenschaften.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(repos, writer);
            System.out.println("Analyseergebnisse gespeichert in analyse_ergebnisse.json");
        } catch (Exception e) {
            System.out.println("Fehler beim Schreiben der JSON-Datei: " + e.getMessage());
        }
    }
}