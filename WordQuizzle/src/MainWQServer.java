import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
//import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.locks.ReentrantLock; 


public class MainWQServer {
	
	private static int DEFAULT_PORT = 9436; // numero di default della porta su cui attenderà il server
	
	private static int DEFAULT_RMI_PORT = 9122; // numero di porta per servizio RMI che gestisce l'operazione di registrazione
	
    private static ThreadPoolExecutor ex;
    
    public static Global_Data_Structures gds = new Global_Data_Structures();
    

	public static void main (String[] args) throws IOException, java.rmi.AlreadyBoundException {
		
		/*  -------- porta per le connessioni TCP -------- */
		
		Properties prop = new Properties();
	    String fileName = "C:\\Users\\marti\\eclipse-workspace\\WQ-reti\\src\\app.config";
		InputStream is = null;
		Integer port;
		Integer porta_rmi;
		 port = DEFAULT_PORT;
		try {
			
		    is = new FileInputStream(fileName);
		    prop.load(is);
			port = Integer.parseInt(prop.getProperty("app.port"));
			porta_rmi = Integer.parseInt(prop.getProperty("app.porta_rmi"));
		
			
		} catch (FileNotFoundException ex) {
			
		    System.out.println("File di configurazione non trovato");
		    port = DEFAULT_PORT;
		    porta_rmi = DEFAULT_RMI_PORT;
		     
		}	
		ex = (ThreadPoolExecutor)Executors.newFixedThreadPool(25);
		
		
		 /*  --------------------- Registrazione con RMI ---------------------- */
		
		try {
			
			Registrazione obj = new Registrazione(gds);
			Registrazione_interface stub = null;
			Registry registry = null;
			registry = LocateRegistry.createRegistry(porta_rmi);
			stub = (Registrazione_interface) UnicastRemoteObject.exportObject(obj, 0);
			registry.bind("Registrazione_Utente", stub);

		}catch (RemoteException e){
			System.out.println("Communication error " + e.toString());
			
		} catch (AlreadyBoundException e1) {
			e1.printStackTrace();
		}
		
		/* ------------------------------------------------------------------- */
		
		ServerSocket welcomeSocket;
		try {
		
			
			welcomeSocket = new ServerSocket();
			welcomeSocket.bind(new InetSocketAddress(port));
			System.out.println("Attendo un nuovo client");
			while(true) {
				Socket sock = welcomeSocket.accept();
				System.out.println(sock);
					if(sock!=null) {
						Handler h = new Handler(sock,gds);
						ex.execute(h);	
				}
					
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
		
	}
	

