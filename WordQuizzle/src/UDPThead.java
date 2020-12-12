	import java.io.IOException;
	import static java.lang.Thread.sleep;
	import java.net.DatagramPacket;
	import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
	
	
	  
	 // Classe che implementa un task listener che si mette in ascolto di datagrammi  
	 // UDP contenenti richieste di sfida inviati al client
public class UDPThead extends Thread implements Runnable {

	    //Connessione su cui il task è in ascolto
	    DatagramSocket connection;
	    //Client a cui è collegato il listener
	 
	    Integer ServerPort;
	    
	    public boolean SfidaInCorso=false;
	    
	    //Flag per terminare il task
	    public boolean Termination;
	    
	    //Flag per consentire o negare l'arrivo di pacchetti
	    public boolean OtherPackets;
	    
	    public String sfidante;


	    //Array di byte utile per contenere il messaggio
	    byte[] Message;
	    
	    static Scanner sc = new Scanner(System.in);

	    public UDPThead(DatagramSocket UDPConn) {
	        connection = UDPConn;
	        Termination = false;
	        Message = new byte[1024];
	    }
	    
	    //setta il flag OtherPackets a true impedendo che arrivino ulteriori UDP packet 
	    public void SonoOccupato() {
	        OtherPackets = true;
	    }
	
 
	      // Metodo che setta il flag OtherPackets a false consentendo l'arrivo di altri UDP packet	    
	    public void SonoLibero() {
	        OtherPackets = false;
	    }
	    
	    public void setInSfida() {
	    	SfidaInCorso=true;
	    }
	    
	    public boolean getInSfida() {
	    	return SfidaInCorso;
	    }


	    public void setServerPort(DatagramPacket packet ) {
	    		
	    		ServerPort=packet.getPort();
	    }
	    
	    public int getServerPort() {
	    	
			return ServerPort;
	    	
	    }
	    
	    /*
	        Metodo di esecuzione del UDPThread che mette in ascolto il
	        thread a cui è stato assegnato sulla connessione UDP  finché
	        non arriva un pacchetto contenente una richiesta di sfida
	    */
	    @Override
	    public void run() {
	        OtherPackets = false;
	      //  String data;
	        String input;
	        while(!Termination) {                                            
	            try {  
	            	  	String data= "";
	            	 	DatagramPacket Packet = new DatagramPacket(Message,1000);
	            	 	connection.receive(Packet);
	                    setServerPort(Packet);
	                    data = new String(Packet.getData(), Packet.getOffset(), Packet.getLength(), StandardCharsets.UTF_8);

	                    
	                /*
	                    Il listener riceve i pacchetti soltanto se l'utente non sta già
	                    sfidando un altro giocatore
	                */
	                if(!OtherPackets) { 
	                	
		               
		                    	System.out.println("Vuoi accettare la sfida proposta da " + data + "? "+ " Si o No " );
		                    	System.out.println("Hai 10sec per accettare la richiesta, al termine dei quali puoi continuare con le tue operazioni se decidi di non accettare" );
		                    	
	                    
	                }
	                //attendo parole
	                else {
	                	
	                	if( data.contains("FINE") ) {
	                		
	                		System.out.println("Fine della partita. Attendi il risultato");
	                		
	                		connection.setSoTimeout(0);
	                		String risultati= "";
		            	 	DatagramPacket PacketRisultati = new DatagramPacket(Message,1000);
		            	 	connection.receive(PacketRisultati);
		                    setServerPort(PacketRisultati);
		                    risultati = new String(PacketRisultati.getData(), PacketRisultati.getOffset(), PacketRisultati.getLength(), StandardCharsets.UTF_8);
		                    String[] input_splitted = risultati.split("\\s+");
	                		if(risultati.contains("RISULTATI")) {
	                			System.out.println("\n");
	                			System.out.println("RISULTATO: ");
	                			System.out.println("Vincitore: " + input_splitted[1]);
	                			System.out.println("Hai indovinato: " + input_splitted[2] + " parole");
	                			System.out.println("Hai totalizzato: " + input_splitted[3] + " punti");
	                			System.out.println("Il tuo avversario ha totalizzato: " + input_splitted[4] + " punti");
	                			System.out.println("\n");
	                			
	                			
	                			System.out.println("Usage: WordQuizzle COMMAND [ ARGS ...]\r\n" +
	                					"\tregistra_utente <nickUtente > <password > registra l' utente\r\n" + 
	                					"\tlogin <nickUtente > <password > effettua il login\r\n" + 
	                					"\tlogout effettua il logout\r\n" + 
	                					"\taggiungi_amico <nickAmico > crea relazione di amicizia con nickAmico\r\n" + 
	                					"\tlista_amici mostra la lista dei propri amici\r\n" +
	                					"\tsfida <nickAmico > richiesta di una sfida a nickAmico\r\n" + 
	                					"\tmostra_punteggio mostra il punteggio dell'utente\r\n" + 
	                					"\tmostra_classifica mostra una classifica degli amici dell'utente\r\n");
	                			SonoLibero();	
	                			SfidaInCorso=false;
	                		}
	                		
	                	}
	                	else {
	                		System.out.println("\tParola da tradurre: ");
	                		System.out.println(data);
		                	System.out.println("Inserisci la traduzione ");
	                	}
	                		
	                }
	                
	                
	            }
	            catch (IOException e) {
	            	 e.printStackTrace();
	            } 
	        
	        }
	    }


}

