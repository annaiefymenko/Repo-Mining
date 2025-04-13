import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Crawler {

/**Methode die Eigenschaften aus eingegebenen Repos ermittelt
 * 
 * @param repoName
 * @return
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
        logProcess.waitFor();

        return lastModifiedDate;
    }


    public static void main(String[] args) {
    
     //https://projectbase.medien.hs-duesseldorf.de/bsalgert/repo-mining.git
    
    /*
    try {
        Scanner scanner = new Scanner(System.in); 
        String reponame = scanner.nextLine(); //namen des repos eingeben
        String repoUrl = "https://github.com/" + reponame + ".git"; 
            String gitDatum = ermittleAenderungsdatum(repoUrl);
            System.out.println("Letzte Änderung: " + gitDatum);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
    try {
            String date = ermittleAenderungsdatum("microsoft/markitdown"); // google/A2A
            System.out.println("Letzte Änderung: " + date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
