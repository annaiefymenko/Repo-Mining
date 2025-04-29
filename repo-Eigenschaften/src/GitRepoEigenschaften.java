import java.io.*;

public class GitRepoEigenschaften {

    /**
     * 1. Eigenschaft: Datum des ersten Commits
     * Beschreibung: Wann hat das Projekt begonnen
     */
    public static String ermittleErstesCommitDatum(String repoPfad) throws IOException, InterruptedException {
        ProcessBuilder logBuilder = new ProcessBuilder("git", "log", "--reverse", "-1", "--format=%cd", "--date=iso");
        logBuilder.directory(new File(repoPfad));
        Process logProcess = logBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(logProcess.getInputStream()));
        String firstCommitDate = reader.readLine();
        logProcess.waitFor();

        return firstCommitDate;
    }

    /**
     * 2. Eigenschaft: Anzahl der Commits
     * Beschreibung: Wie viele Aktivitäten/Commits im Projekt gemacht wurden
     */
    public static int ermittleAnzahlCommits(String repoPfad) throws IOException, InterruptedException {
        ProcessBuilder countBuilder = new ProcessBuilder("git", "rev-list", "--count", "HEAD");
        countBuilder.directory(new File(repoPfad));
        Process countProcess = countBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(countProcess.getInputStream()));
        String countStr = reader.readLine();
        countProcess.waitFor();

        return Integer.parseInt(countStr.trim());
    }

    /**
     * 3. Eigenschaft: Anzahl der Branches
     * Beschreibung: Entwicklungsmodell / Feature-Fokus.
     */
    public static int ermittleAnzahlBranches(String repoPfad) throws IOException, InterruptedException {
        ProcessBuilder branchBuilder = new ProcessBuilder("git", "branch", "-r");
        branchBuilder.directory(new File(repoPfad));
        Process branchProcess = branchBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(branchProcess.getInputStream()));
        int branchCount = 0;
        String line;

        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                branchCount++;
            }
        }

        branchProcess.waitFor();

        return branchCount;
    }

    /**
     * Hauptprogramm zum Testen der Methoden
     */
    public static void main(String[] args) {
        try {
            // Lokaler Pfad zu deinem geklonten Repository (nicht URL!)
            String lokalerRepoPfad = "markitdown"; // Beispiel: Ordnername nach dem Klonen

            // 1. Erstes Commit Datum ermitteln
            String erstesCommit = ermittleErstesCommitDatum(lokalerRepoPfad);
            System.out.println("Erstes Commit am: " + erstesCommit);

            // 2. Anzahl der Commits ermitteln
            int anzahlCommits = ermittleAnzahlCommits(lokalerRepoPfad);
            System.out.println("Anzahl der Commits: " + anzahlCommits);

            // 3. Anzahl der Branches ermitteln
            int anzahlBranches = ermittleAnzahlBranches(lokalerRepoPfad);
            System.out.println("Anzahl der Branches: " + anzahlBranches);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
