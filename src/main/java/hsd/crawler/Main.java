package hsd.crawler;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            // Teil 1: Lokales Repo analysieren
            String lokalerRepoPfad = "markitdown";

            String erstesCommit = GitRepoEigenschaften.ermittleErstesCommitDatum(lokalerRepoPfad);
            System.out.println("Erstes Commit am: " + erstesCommit);

            int anzahlCommits = GitRepoEigenschaften.ermittleAnzahlCommits(lokalerRepoPfad);
            System.out.println("Anzahl der Commits: " + anzahlCommits);

            int anzahlBranches = GitRepoEigenschaften.ermittleAnzahlBranches(lokalerRepoPfad);
            System.out.println("Anzahl der Branches: " + anzahlBranches);

            String snippet = "public static void main(String[] args) { System.out.println(\"Hello, World!\"); }";
            System.out.println("Erkannte Sprache: " + GitRepoEigenschaften.detectLanguage(snippet));

            // Teil 2: GitHub populäre Repos abrufen
            List<String> repos = GitHubAPI.werteQuelleAus();
            System.out.println("Top Repositories mit >10.000 Stars:");
            for (String repo : repos) {
                System.out.println(repo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
