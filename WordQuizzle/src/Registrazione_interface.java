import java.rmi.Remote;
import java.rmi.RemoteException;

//Creazione di interfaccia in RMI per la gestione della registrazione

public interface Registrazione_interface extends Remote {
	
		public int registra_utente(String username, String password) throws RemoteException;		
}
