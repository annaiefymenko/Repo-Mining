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
    
    /**4. Eigenschaft: die Anzahl der Dateien ermittelt:
     * 
     */
    	public static int countFiles(String projectPath, String privateToken) {
    	int fileCount = 0;
    	try {
    	String encodedProject = URLEncoder.encode(projectPath, "UTF-8");
    	String apiUrl = "https://gitlab.com/api/v4/projects/" + encodedProject + "/repository/tree?recursive=true&per_page=100";
    	URL url = new URL(apiUrl); HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	conn.setRequestProperty("PRIVATE-TOKEN", privateToken);
    	conn.setRequestMethod("GET");
    	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    	String line;
    	while ((line = in.readLine()) != null) {
    	if (line.contains(""type":"blob"")) {
    	fileCount++;
    	}
    	}
    	in.close();
    	} catch (Exception e) {
    	System.out.println("Fehler: " + e.getMessage());
    	}
    	return fileCount;
    	}
    	
    	/**5. Eigenschaft Programmiersprache ermitteln
    	 * 
    	 */
    	public static String detectLanguage(String code) {
    		code = code.toLowerCase();
    		Map> languageKeywords = new HashMap<>();
    		languageKeywords.put("Python", Arrays.asList("def ", "import ", "print(", "self", "None", "elif", "except"));
    		languageKeywords.put("Java", Arrays.asList("public static void main", "System.out.println", "class", "import java"));
    		languageKeywords.put("JavaScript", Arrays.asList("function", "console.log", "var ", "let ", "const ", "=>"));
    		languageKeywords.put("C", Arrays.asList("#include", "printf(", "scanf(", "int main"));
    		languageKeywords.put("C++", Arrays.asList("#include", "std::cout", "std::cin", "using namespace std", "class"));
    		languageKeywords.put("C#", Arrays.asList("using System", "Console.WriteLine", "public class", "static void Main"));
    		languageKeywords.put("Ruby", Arrays.asList("def ", "puts", "end", "class", "module"));
    		languageKeywords.put("PHP", Arrays.asList("", "function"));
    		languageKeywords.put("Go", Arrays.asList("package main", "import "", "func main", "fmt."));
    		languageKeywords.put("Rust", Arrays.asList("fn main()", "let mut", "println!", "use ", "::"));
    		for (Map.Entry> entry : languageKeywords.entrySet()) {
    		for (String keyword : entry.getValue()) {
    		if (code.contains(keyword.toLowerCase())) { return entry.getKey(); }
    		}
    		}
    		return "Unbekannt";
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
        

        //4.eigenschaft testen
        String project = "deine-gruppe/dein-projekt";
        // z. B. "mygroup/myrepo"
        String token = "DEIN_PRIVATE_TOKEN";
        int count = countFiles(project, token);
System.out.println("Dateien im Repository: " + count); }
    
    
    //5.Eigenschaft testen
    String snippet = "public static void main(String[] args) {
    		System.out.("Hello, World!"); }";
    		System.out.println("Erkannte Sprache: " + detectLanguage(snippet));
    		}


}//ende klasse gitrepoeigenschaften
