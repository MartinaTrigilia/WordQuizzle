import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class GestioneFile {
	
	public GestioneFile() {
		
	}
	
	//Metodo che dato in input il riferimento al file JSON relativo ad un utente
    //cne restituisce il JSONObjetc associato
	public static JSONObject GetPlayerJSON(String Nick)
    {
        try 
        {
            JSONParser parser = new JSONParser();
                  
            JSONObject PlayerData = (JSONObject) parser.parse(new FileReader(System.getProperty("user.dir")+"\\"+Nick.trim()+".JSON"));
            
            return PlayerData;
        } 
        
        catch (FileNotFoundException ex) 
        {
        	System.out.println("ERRORE: Impossibile scrivere file JSON relativo al giocatore "+ Nick +"\n");
            return null;
        } 
        catch (IOException | ParseException ex)
        {
            return null;
        }
    }


	
	@SuppressWarnings("unchecked")
	public static void WritePlayerData_AggiungiAmico(Utente player, List<String> list ) {

        //Trasformazione dei dati di registrazione di un giocatore in formato JSON
		
		player.setFriends(list);
        JSONObject Data = player.PlayerToJSON();
        
        try(FileWriter fw = new FileWriter(System.getProperty("user.dir")+"\\"+player.getNick().trim()+".JSON"))
        {
            fw.write(Data.toJSONString());
            fw.close();
        } 
        catch (IOException ex) 
        {
           System.out.println("ERRORE: Impossibile scrivere file JSON relativo al giocatore "+player.getNick()+"\n");
        }
    } 
	
	 public static void WritePlayerData_Registrazione(Utente player)
	    {
	        //caricamento delle info utente
	        File InfoPlayers = LoadPlayerInfo(player.getNick());

	        //Trasformazione dei dati di registrazione di un giocatore in formato JSON
	        JSONObject Data = player.PlayerToJSON();
	        
	        try(FileWriter fw = new FileWriter(InfoPlayers))
	        {
	            fw.write(Data.toJSONString());
	            fw.close();
	        } 
	        catch (IOException ex) 
	        {
	           System.out.println("ERRORE: Impossibile scrivere file JSON relativo al giocatore "+player.getNick()+"\n");
	        }
	    }
		public static void WritePlayerData_AggiungiPunteggio(Utente player, int score, List<String> list  ) {

	        //Trasformazione dei dati di registrazione di un giocatore in formato JSON
			
			player.setFriends(list);
			player.setScore(score);
	        JSONObject Data = player.PlayerToJSON();
	        
	        try(FileWriter fw = new FileWriter(System.getProperty("user.dir")+"\\"+player.getNick().trim()+".JSON"))
	        {
	            fw.write(Data.toJSONString());
	            fw.close();
	        } 
	        catch (IOException ex) 
	        {
	           System.out.println("ERRORE: Impossibile scrivere file JSON relativo al giocatore "+player.getNick()+"\n");
	        }
	    } 
	 	//restituisce il riferimento al file JSON dell'utente
	 
	    public static File LoadPlayerInfo(String player) {
	        //CARICAMENTO DELLE INFO UTENTI
	        File InfoPlayers = new File(System.getProperty("user.dir")+"\\"+player.trim()+".JSON");
	        
	        return InfoPlayers;
	    }

	
	    
}
