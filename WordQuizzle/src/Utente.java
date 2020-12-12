
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Utente  {
	
	private final String nickUtente;
    private final String password;
    private int score;
    private List<String> friends;
	
    public Utente(String nick,String pw){
    	
    	password=pw;
    	nickUtente=nick;
        score = 0;
        friends = new ArrayList<String>();
    }
	
	
    public String getNick(){
    	
    	return this.nickUtente;
    }
    
	
    public String getPassword(){
    	
    	return this.password;
    }
    
    public void setFriends(List<String> list) {
    	
    	friends=list;
   
    }
    
    public void setScore(int num){
    	
        score += num;
    }
	
    public int getScore(int num){
    	
    	return this.score;
    }
    

    
    @SuppressWarnings("unchecked")
	public JSONObject PlayerToJSON() {
    	
        JSONObject PlayerData = new JSONObject();
        JSONArray PlayerFriends = new JSONArray();
        
        PlayerFriends.addAll(friends);
        
        PlayerData.put("Nome", nickUtente);
        PlayerData.put("Password", password);
        
        //inizializzazione 
        PlayerData.put("Punteggio", score);
        PlayerData.put("Amici", PlayerFriends);
     
     // PlayerData è un oggetto contenente info del giocatore
     return PlayerData; 
     
    }
    
    
}
