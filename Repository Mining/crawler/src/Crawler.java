import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Crawler {

/**Methode die Eigenschaften aus eingegebenen Repos ermittelt
 * 
 * @param repoName
 * @return lastModifiedDate
 * @throws IOException
 * @throws InterruptedException
 */
    public static String ermittleAenderungsdatum(String repoName) throws IOException, InterruptedException {
    
    //repo name zusammensetzen
        String repoUrl = "https://github.com/" + repoName + ".git"; //parameter repräsentiert dateipfad der url
        
        //Extrahiert nur den Repository-Namen (brauchen wir für dateipfad)
        String folderName = repoName.substring(repoName.indexOf("/") + 1);

        // 1. Repository klonen
        ProcessBuilder cloneBuilder = new ProcessBuilder("git", "clone", repoUrl); //neues ProcessBuilder Objekt erstellen-> führt den shell-befehl git clone <URL> aus
        Process cloneProcess = cloneBuilder.start(); //start den prozess
        cloneProcess.waitFor(); //wartet bis das klonen abgeschlossen wird

        // 2. Git log ausführen
        ProcessBuilder logBuilder = new ProcessBuilder("git", "log", "-1", "--format=%cd", "--date=iso"); //gibt uns das Datum des letzten Commits in einem standardisierten ISO-Datum zurück
        logBuilder.directory(new File(folderName)); // sorgt dafür, dass der Befehl im richtigen Ordner ausgeführt wird


        Process logProcess = logBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(logProcess.getInputStream())); //Liest die Ausgabe von git log (also das Änderungsdatum) aus dem Prozess
        String lastModifiedDate = reader.readLine();//readLine() holt die erste Zeile – genau das Datum, das wir wollen.
        reader.close();
        logProcess.waitFor();

        return lastModifiedDate;
    }

        public static void loescheOrdnerMitShell(String ordnerName) throws IOException, InterruptedException {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder builder;
        
            if (os.contains("win")) {
                builder = new ProcessBuilder("cmd", "/c", "rmdir", "/s", "/q", ordnerName);
            } else {
                builder = new ProcessBuilder("rm", "-rf", ordnerName);
            }
        
            Process deleteProcess = builder.start();
            deleteProcess.waitFor();
        }
        
    public static void main(String[] args) {
    try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Repository Ort Eingeben:"); //Google oder Microsoft
            String repoOrt = scanner.nextLine();
            System.out.println("Repository Namen Eingeben:"); //A2A oder markitdown
            String repoName = scanner.nextLine();
            scanner.close();
            String date = ermittleAenderungsdatum(repoOrt + "/" + repoName); // Ort und Name werden automatisch mit einem "/" verbunden
            System.out.println("Letzte Änderung: " + date);
            loescheOrdnerMitShell(repoName.substring(repoName.indexOf("/") + 1));

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


