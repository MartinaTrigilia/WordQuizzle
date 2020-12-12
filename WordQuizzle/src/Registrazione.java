import java.rmi.RemoteException;
import java.util.HashMap;

public class Registrazione implements Registrazione_interface{
	Global_Data_Structures gds;
	
	public Registrazione(Global_Data_Structures gds) throws RemoteException{
		this.gds = gds;
	}
	@Override
	
	public int registra_utente(String username, String pass) throws RemoteException {
		if(gds.users.containsKey(username) == false) {
			gds.users.put(username, pass);
			gds.Classifica.put(username, 0);
		
			//istanza dell'utente mediante username e password
			Utente player = new Utente(username,pass);
			
			//Scrittura dei dati di registrazione in formato JSON
			GestioneFile.WritePlayerData_Registrazione(player);
			
			return 1;
		}
		return 0;
	}		

}
