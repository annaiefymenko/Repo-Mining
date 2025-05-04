package hsd.crawler;

import java.io.*;
import java.net.*;
import java.util.*;

public class GitRepoEigenschaften {

    public static String ermittleErstesCommitDatum(String repoPfad) throws IOException, InterruptedException {
        ProcessBuilder logBuilder = new ProcessBuilder("git", "log", "--reverse", "-1", "--format=%cd", "--date=iso");
        logBuilder.directory(new File(repoPfad));
        Process logProcess = logBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(logProcess.getInputStream()));
        String firstCommitDate = reader.readLine();
        logProcess.waitFor();

        return firstCommitDate;
    }

    public static int ermittleAnzahlCommits(String repoPfad) throws IOException, InterruptedException {
        ProcessBuilder countBuilder = new ProcessBuilder("git", "rev-list", "--count", "HEAD");
        countBuilder.directory(new File(repoPfad));
        Process countProcess = countBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(countProcess.getInputStream()));
        String countStr = reader.readLine();
        countProcess.waitFor();

        return Integer.parseInt(countStr.trim());
    }

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

    public static int countFiles(String projectPath, String privateToken) {
        int fileCount = 0;
        try {
            String encodedProject = URLEncoder.encode(projectPath, "UTF-8");
            String apiUrl = "https://gitlab.com/api/v4/projects/" + encodedProject + "/repository/tree?recursive=true&per_page=100";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("PRIVATE-TOKEN", privateToken);
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("\"type\":\"blob\"")) {
                    fileCount++;
                }
            }
            in.close();
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
        return fileCount;
    }

    public static String detectLanguage(String code) {
        code = code.toLowerCase();
        Map<String, List<String>> languageKeywords = new HashMap<>();
        languageKeywords.put("Python", Arrays.asList("def ", "import ", "print(", "self", "none", "elif", "except"));
        languageKeywords.put("Java", Arrays.asList("public static void main", "system.out.println", "class", "import java"));
        languageKeywords.put("JavaScript", Arrays.asList("function", "console.log", "var ", "let ", "const ", "=>"));
        languageKeywords.put("C", Arrays.asList("#include", "printf(", "scanf(", "int main"));
        languageKeywords.put("C++", Arrays.asList("#include", "std::cout", "std::cin", "using namespace std", "class"));
        languageKeywords.put("C#", Arrays.asList("using system", "console.writeline", "public class", "static void main"));
        languageKeywords.put("Ruby", Arrays.asList("def ", "puts", "end", "class", "module"));
        languageKeywords.put("PHP", Arrays.asList("<?php", "function"));
        languageKeywords.put("Go", Arrays.asList("package main", "import \"", "func main", "fmt."));
        languageKeywords.put("Rust", Arrays.asList("fn main()", "let mut", "println!", "use ", "::"));

        for (Map.Entry<String, List<String>> entry : languageKeywords.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (code.contains(keyword)) {
                    return entry.getKey();
                }
            }
        }
        return "Unbekannt";
    }
}
