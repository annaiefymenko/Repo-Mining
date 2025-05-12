package hsd.crawler;

import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.*;

public class GitRepoEigenschaften {

    private static final String GITLAB_API_BASE = "https://gitlab.com/api/v4/projects/";

    // Zentrale Methode für API-GET-Aufrufe
    private static HttpURLConnection erzeugeVerbindung(String endpoint, String token) throws IOException {
        URL url = new URL(GITLAB_API_BASE + URLEncoder.encode(endpoint, "UTF-8"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("PRIVATE-TOKEN", token);
        conn.setRequestMethod("GET");
        return conn;
    }

    public static List<Repository> ladeAlleProjekte(String privateToken, String namespace) {
        List<Repository> repos = new ArrayList<>();
        int page = 1;
        boolean hasMore = true;

        try {
            while (hasMore) {
                String encodedNamespace = URLEncoder.encode(namespace, "UTF-8");
                String apiUrl = "https://gitlab.com/api/v4/users/" + encodedNamespace + "/projects?per_page=100&page=" + page;
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("PRIVATE-TOKEN", privateToken);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JsonArray projects = new JsonParser().parse(response.toString()).getAsJsonArray();
                if (projects.size() == 0) {
                    hasMore = false;
                } else {
                    for (JsonElement elem : projects) {
                        JsonObject obj = elem.getAsJsonObject();
                        String name = obj.get("path_with_namespace").getAsString();
                        String lang = obj.has("language") && !obj.get("language").isJsonNull() ? obj.get("language").getAsString() : "Unbekannt";
                        int stars = obj.get("star_count").getAsInt();
                        int forks = obj.get("forks_count").getAsInt();
                        repos.add(new Repository(name, lang, stars, forks));
                    }
                    page++;
                }
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Projekte: " + e.getMessage());
        }

        return repos;
    }

    public static int ermittleAnzahlCommits(String projectPath, String privateToken) {
        try {
            HttpURLConnection conn = erzeugeVerbindung(projectPath + "/repository/commits?per_page=1", privateToken);
            conn.setRequestMethod("HEAD");
            String totalCommits = conn.getHeaderField("X-Total");
            return totalCommits != null ? Integer.parseInt(totalCommits) : 0;
        } catch (Exception e) {
            System.out.println("Fehler beim Abrufen der Commit-Anzahl: " + e.getMessage());
            return 0;
        }
    }

    public static int ermittleAnzahlBranches(String projectPath, String privateToken) {
        try {
            HttpURLConnection conn = erzeugeVerbindung(projectPath + "/repository/branches", privateToken);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);
            reader.close();

            JsonElement json = new JsonParser().parse(response.toString());
            return json.getAsJsonArray().size();
        } catch (Exception e) {
            System.out.println("Fehler beim Abrufen der Branches: " + e.getMessage());
            return 0;
        }
    }

    public static int countFiles(String projectPath, String privateToken) {
        int fileCount = 0;
        try {
            HttpURLConnection conn = erzeugeVerbindung(projectPath + "/repository/tree?recursive=true&per_page=100", privateToken);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("\"type\":\"blob\"")) fileCount++;
            }
            in.close();
        } catch (Exception e) {
            System.out.println("Fehler beim Zählen der Dateien: " + e.getMessage());
        }
        return fileCount;
    }

    public static String ermittleLetztesAenderungsdatum(String projectPath, String privateToken) {
        try {
            HttpURLConnection conn = erzeugeVerbindung(projectPath + "/repository/commits?per_page=1", privateToken);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);
            reader.close();

            JsonArray commits = new JsonParser().parse(response.toString()).getAsJsonArray();
            if (commits.size() > 0) {
                return commits.get(0).getAsJsonObject().get("committed_date").getAsString();
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Abrufen des Änderungsdatums: " + e.getMessage());
        }
        return "Unbekannt";
    }

    public static String ermittleAutoren(String projectPath, String privateToken) {
        try {
            HttpURLConnection conn = erzeugeVerbindung(projectPath + "/repository/commits?per_page=1", privateToken);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);
            reader.close();

            JsonArray commits = new JsonParser().parse(response.toString()).getAsJsonArray();
            if (commits.size() > 0) {
                return commits.get(0).getAsJsonObject().get("author_name").getAsString();
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Abrufen des Autors: " + e.getMessage());
        }
        return "Unbekannt";
    }

    public static String ermittleSprache(String code) {
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
                if (code.contains(keyword)) return entry.getKey();
            }
        }
        return "Unbekannt";
    }
    
    public static void listSubmodules(String projectPath, String privateToken) {

        try {
            HttpURLConnection conn = erzeugeVerbindung(projectPath + "/repository/submodules", privateToken);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String zeile;

            while ((zeile = reader.readLine()) != null) {
                response.append(zeile);
            }
            reader.close();

            // Anzahl der Submodule ausgeben
            System.out.println("Anzahl der Submodule im Repository: " + submodulesArray.length());

            } catch (Exception e) {
            System.err.println("Fehler beim Abrufen der Submodule:"  + e.getMessage());
            
        }

    }

    public static void findGitignore(String projectPath, String privateToken) {
        //Wir überprüfen damit, ob .gitatignore Datei vorhanden ist 
        try {
            // HTTP-Verbindung zur .gitignore-API aufbauen
            HttpURLConnection conn = erzeugeVerbindung(projectPath, privateToken);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String zeile;

            // Lese den InputStream und baue den Response-String auf
            while ((zeile = reader.readLine()) != null) {
                response.append(zeile);
            }
            reader.close();

            // Überprüfe die Antwort und gib sie aus
            System.out.println(".gitignore Inhalt:");
            System.out.println(response.toString());

        } catch (Exception e) {
            System.err.println("Fehler beim Abrufen der .gitignore-Datei:" + e.getMessage());
        }
    }

    public static void findGitAttributes(String projectPath, String privateToken) {
        //Wir überprüfen damit, ob .gitattributes Datei vorhanden ist 
        try {
            // HTTP-Verbindung zur .gitattributes - API aufbauen
            HttpURLConnection conn = erzeugeVerbindung(projectPath, privateToken);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String zeile;

            // Lese den InputStream und baue den Response-String auf
            while ((zeile = reader.readLine()) != null) {
                response.append(zeile);
            }
            reader.close();

            // Überprüfe die Antwort und gib sie aus
            System.out.println(".gitattributes Inhalt:");
            System.out.println(response.toString());

        } catch (Exception e) {
            System.err.println("Fehler beim Abrufen der .gitattributes - Datei:" + e.getMessage());
        }
    }


    

}
