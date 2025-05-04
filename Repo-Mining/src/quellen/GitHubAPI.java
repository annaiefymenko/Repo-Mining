package quellen;

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

//URL, HttpURLConnection: Um Internetanfragen zu schicken
//BufferedReader: Um die Antwort vom Server zu lesen
//List, ArrayList: Um die Ergebnisse zu speichern
//Gson-Klassen: Zum Verarbeiten von JSON


public class GitHubAPI {


	/**Methode die JSON in Text umwandelt und repos zurückgibt
	 * 
	 * @return Liste von Strings
	 * @throws Exception falls etwas schiefgeht zB Internetproblem
	 */
    public static List<String> werteQuelleAus() throws Exception {
        List<String> repos = new ArrayList<>();

        // 1. URL vorbereiten die abgefragt wird
        URL url = new URL("https://api.github.com/search/repositories?q=stars:%3E10000&sort=stars&per_page=100");

        // 2. HTTP-Verbindung aufbauen
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET"); //Sagen dass man nur die Daten holen will
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json"); //sagen, dass man GitHUb API-Sprache spricht

        // 3. Antwort lesen
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder(); //sammle alles zu großen text zsm
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close(); //verbindung sauber schließen

        /* 4. JSON parsen neue gson version 2.13.1.jar
        JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
        JsonArray items = jsonObject.getAsJsonArray("items");
*/
     // 4. JSON mit älterer Methode parsen (für Gson 2.8.5)
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = (JsonObject) parser.parse(response.toString()); // ältere Methode (großer text wird als JSON Objekt interpretiert
        JsonArray items = jsonObject.getAsJsonArray("items"); //array items rausholen(Liste aller repositories)
        
        // 5. Repositories auslesen
        //für jedes repository full_name herausziehen wie zB "micorosoft/markitdown und in Liste einfügen
        for (JsonElement item : items) {
            JsonObject repo = item.getAsJsonObject();
            String fullName = repo.get("full_name").getAsString();
            repos.add(fullName);
        }

        return repos; //rückgabe der fertigen Liste von Repositories
    }

    //Testmethode
    public static void main(String[] args) {
    	
    	//werteQuelleAus() aufrufen
        try {
            List<String> repos = werteQuelleAus();
            
            //jedes Repository auf Konsole ausgeben
            for (String repo : repos) {
                System.out.println(repo);
            }
            //wenn fehler passiert,wird fehler ausgegeben
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
}
