
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


public class Global_Data_Structures {
	
	ConcurrentHashMap<String, String> users;
	ConcurrentHashMap<String, List<String> > amici;
	ConcurrentHashMap<String, Integer> online;
	ConcurrentHashMap<String , Challenge > Games;
	ConcurrentHashMap<String , Integer> Classifica;
	
	public Global_Data_Structures () {
		
		users = new ConcurrentHashMap<String, String>();
		online = new ConcurrentHashMap<String, Integer>();
		amici = new ConcurrentHashMap<String, List<String>>();
		Games = new ConcurrentHashMap<String , Challenge >();
		Classifica = new ConcurrentHashMap<String, Integer>();
	}


	
	public int login(String username, String password, String portaUDP) {
		
		/*utente non registrato*/
		if(users.containsKey(username) == false) 
			return 1;
		
		/*login eseguito correttamente*/
		
		/*ogni volta che viene eseguito il login viene associato a quell'utente una nuova portaUDP, in modo da permettere l invio di pacchetti
		 *UDP a quest'ultimo , contenenti richieste di sfide */
		
		else if((users.get(username)).compareTo(password)==0) { 
				Integer porta = Integer.valueOf(portaUDP);
				online.put(username, porta);
				return 0;
		}
		 /*password sbagliata*/
		else 
				return 2;
			
	}
	
	
	public boolean checkOnline(String username) {
		
		if( online.containsKey(username)  )
			
			return true;
		
		else 
			return false;
	}
	
	public boolean checkAmico(String username1, String username2) {
			
		if(amici.get(username1)!=null) {
	
			if( amici.get(username1).contains(username2)  )
				return true;
		}
			
		return false;
	}
	
	public SortedSet<Entry<String, Integer>> creaClassifica(String utente) {
		
		 Map<String,Integer> ranking = new TreeMap<>();
		
		ranking.put(utente, Classifica.get(utente));
		
		for(String keys: users.keySet()) {
			
			if(amici.get(utente)!=null) {
				if(amici.get(utente).contains(keys)) 
					ranking.put(keys, Classifica.get(keys));
			}
				
		}
		SortedSet<Entry<String, Integer>> result = entriesSortedByValues(ranking);
		return result;

	}
	
	
	private <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<>(
                (e1, e2) -> {
                    int res = e2.getValue().compareTo(e1.getValue());
                    return res != 0 ? res : 1;
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }




	public void addScore(String nickUtente, int score) {
		
		Utente utente = new Utente (nickUtente, users.get(nickUtente));
		
		//Scrittura dei dati in formato JSON
		GestioneFile.WritePlayerData_AggiungiPunteggio(utente,score,amici.get(nickUtente));

	} 
	
	public int addFriendship(String nickUtente1, String nickUtente2) {
		
		//nome utenti non registrati nella piattaforma
		if( (users.containsKey(nickUtente1) == false) || users.containsKey(nickUtente2) == false )
			return 1;
		
		if(nickUtente2.equals(nickUtente1)) return 3;
		List<String> list1;
		List<String> list2;
		
		if(!amici.containsKey(nickUtente1)) 
			list1 = new ArrayList<String>();
		else
			list1 = amici.get(nickUtente1);
		
		if(!amici.containsKey(nickUtente2)) 
			list2 = new ArrayList<String>();
		else
			list2 = amici.get(nickUtente2);
		
		if(!list1.contains(nickUtente2)) {
			list1.add(nickUtente2); //aggiungo nickUtente2 alla lista di amici di nickUtente1
			list2.add(nickUtente1);	//aggiungo nickUtente1 alla lista di amici di nickUtente2
			amici.put(nickUtente1, list1);
			amici.put(nickUtente2, list2);
			Utente utente1 = new Utente (nickUtente1, users.get(nickUtente1));
			Utente utente2 = new Utente (nickUtente2, users.get(nickUtente2));
			
			//Scrittura dei dati in formato JSON
			GestioneFile.WritePlayerData_AggiungiAmico(utente1,list1);
			GestioneFile.WritePlayerData_AggiungiAmico(utente2,list2);
		}
		else 
			return 2;
		
		return 0;
	}

	
	
	
	public void logout(String data) {
		
		if(online.contains(data))
			
			online.remove(data);
	}
	
	
}
