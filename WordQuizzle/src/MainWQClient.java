import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MainWQClient {
	
	private static int DEFAULT_PORT = 9436; // numero di default della porta su cui attenderà il server
	
	private static int DEFAULT_RMI_PORT = 9122; // numero di porta per servizio RMI
	private static Integer port;
	private static Integer porta_rmi;
	private static DataInputStream in;
	private static BufferedReader d;
	private static DataOutputStream outp;
	private static String current_username;

	private static Integer portUDP=0;
	private static Boolean exit = false;
	static Scanner sc = new Scanner(System.in);
	private static Socket sock;
	private static UDPThead TheadUDP; 
	private static ExecutorService StartListener;//Executor per un thread UDPThead 
	
	DatagramPacket UDPRichiestaSfida= null;//Datagramma ricevuto dal ListenerUDP
    byte[] UDPMessage = new byte[1024];//Messaggio incapsulato nel datagramma UDP
	
//interfaccia a linea di comando 
	public static void usage2() {
		System.out.println("\n");
		System.out.println("Usage: WordQuizzle COMMAND [ ARGS ...]\r\n" +
		
				"\tlogout effettua il logout\r\n" + 
				"\taggiungi_amico <nickAmico > crea relazione di amicizia con nickAmico\r\n" + 
				"\tlista_amici mostra la lista dei propri amici\r\n" +
				"\tsfida <nickAmico > richiesta di una sfida a nickAmico\r\n" + 
				"\tmostra_punteggio mostra il punteggio dell'utente\r\n" + 
				"\tmostra_classifica mostra una classifica degli amici dell'utente\r\n");
		
		return ;
	}

	public static void usage1() {
		System.out.println("\n");
		System.out.println("Usage: WordQuizzle COMMAND [ ARGS ...]\r\n" +
				"\tregister <nickUtente > <password > registra l' utente\r\n" + 
				"\tlogin <nickUtente > <password > effettua il login\r\n"  );
		
		return ;
	}
	static Integer getPortaUDP() {
		
		int selected;
		int min = 1024;
		int max = 65535;
		int intervallo = ((max-min) + 1);

			
			Random Rand = new Random();
			return selected = Rand.nextInt(intervallo) + min;

	
	}

    
	@SuppressWarnings("unchecked")
	public static void logged() throws IOException, NotBoundException {
		Boolean started = false;
		String input;
		
		while(!started) {
			
			System.out.println("STATO: logged");
			input = sc.nextLine();
			String[] input_splitted = input.split("\\s+");
			
			switch (input_splitted[0]) {
				
				case "si":
				case "SI":
				case "Si":
				case "no":
				case "No":
				case "NO": {
					if( !TheadUDP.getInSfida() ) {
						
						  DatagramSocket datagramChannel = new DatagramSocket(); 
					      //MANDA PACCHETTO UDP AL SERVER CONTENETE LA RISPOSTA 
					      String message = input_splitted[0];
					      byte[] toSend = new byte[100];
					      toSend = message.getBytes();
					      DatagramPacket packet = new DatagramPacket(toSend,toSend.length,InetAddress.getLocalHost(),TheadUDP.getServerPort());
					      datagramChannel.send(packet);	
					      
					      if(input_splitted[0].trim().equals("si") ||input_splitted[0].trim().equals("SI") ||input_splitted[0].trim().equals("Si")) {
					    	  System.out.println("Hai accettato la richiesta! Loading...");
					    	  TheadUDP.SonoOccupato();
					    	  TheadUDP.setInSfida();
					      }  
					      if(input_splitted[0].trim().equals("no") ||input_splitted[0].trim().equals("NO") ||input_splitted[0].trim().equals("No")) {
					    	  System.out.println("Non hai accettato la richiesta! ");
					    	  //se non ho accettato la richiesta, mi metto in attesa di un altro pacchetto UDP contenente richieste di sfida
					    	  TheadUDP.SonoLibero();
					      }
	
					      datagramChannel.close();
					}
					else 
						System.out.println("Operazione non ammessa mentre sei in sfida");
					    
				      usage2();
				      break;
					
				}
			
				case "aggiungi_amico": {
					if( !TheadUDP.getInSfida() ) {
						
						if(input_splitted.length == 2) {
							
							outp.writeBytes("aggiungi_amico" + "\n");
							outp.writeBytes(current_username + "\n"); // nickUtente1
							outp.writeBytes(input_splitted[1] + "\n"); // nickUtente2
							int r = d.read();
							
							if(r == 0) {
								System.out.println("amicizia " + current_username+ "-" + input_splitted[1] + " creata!");
							}else if(r == 1)
								System.out.println("Utente non registrato");
							else if (r== 2)
								System.out.println("amicizia " + current_username + "-" + input_splitted[1] + " già presente");
							else if (r== 3)
								System.out.println("non puoi aggiungere te stesso agli amici");
						}	
						else
							System.out.println("Dati non validi");
					}
					else 
						System.out.println("Operazione non ammessa mentre sei in sfida");
					usage2();
					break;
				}
				
				case "mostra_punteggio": {
					if( !TheadUDP.getInSfida() ) {
						if(input_splitted.length == 1) {
							
							outp.writeBytes("mostra_punteggio" + "\n");
							outp.writeBytes(current_username + "\n"); // nickUtente1
								
							int r = d.read();
							System.out.println("Punteggio: " + r);
							
						}	
						else
							System.out.println("Dati non validi");
					}
					else 
						System.out.println("Operazione non ammessa mentre sei in sfida");
					usage2();
					break;
				}
				
				case "lista_amici" : {
					if( !TheadUDP.getInSfida() ) {
					
						if(input_splitted.length == 1) {
							JSONObject c = null;
							outp.writeBytes("lista_amici" + "\n");
							outp.writeBytes(current_username + "\n"); // nickUtente
							
					         try {            
					               BufferedInputStream socket=new BufferedInputStream(sock.getInputStream());
					               ObjectInputStream reader = new ObjectInputStream(socket);      
					               c= (JSONObject) reader.readObject(); //Read an object from the ObjectInputStream.
					               
					         } catch (IOException | ClassNotFoundException e) {
					        	 e.printStackTrace();
					         }
					        List<String>  Friends = (List<String>) c.get("Amici");
					        if(!Friends.isEmpty()) {
	             
				                for(String s : Friends) {
				                	System.out.println(s);
				                }
	
					        }
					        else
					        	 System.out.println("Nessun amico da visualizzare");
			
						}
						else
							System.out.println("Dati non validi \n lista_amici ");
					}
					else 
						System.out.println("Operazione non ammessa mentre sei in sfida");
						
					usage2();
					break;
					
				}
				
				case "mostra_classifica" : {
					if( !TheadUDP.getInSfida() ) {
					
						if(input_splitted.length == 1) {
							String c = new String();
							outp.writeBytes("mostra_classifica" + "\n");
							outp.writeBytes(current_username + "\n"); // nickUtente
							
					         try {            
					               BufferedInputStream socket=new BufferedInputStream(sock.getInputStream());
					               ObjectInputStream reader = new ObjectInputStream(socket);      
					               c= (String) reader.readObject(); //Read an object from the ObjectInputStream.
					               
					         } catch (IOException | ClassNotFoundException e) {
					        	 e.printStackTrace();
					         }
					         JSONArray jsonArray = null;
	                         JSONParser parser = new JSONParser();
	
	                         try {
								jsonArray = (JSONArray) parser.parse(c);
							} catch (org.json.simple.parser.ParseException e) {
								
								e.printStackTrace();
							}
	
							 Iterator iterator = jsonArray.iterator();
							 StringBuilder result = new StringBuilder("Classifica: ");
							 while (iterator.hasNext()) {
							     JSONObject obj = (JSONObject) iterator.next();
							     String utente = (String) obj.get("username");
							     Long punteggio = (Long) obj.get("points");
							     result.append(utente).append(" ").append(punteggio);
							     if (iterator.hasNext()) {
							         result.append(", ");
							     }
							 }
							 System.out.println(result);
									
						}
						else
							System.out.println("Dati non validi");
					}
					else 
						System.out.println("Operazione non ammessa mentre sei in sfida");
					
					usage2();
					break;
					
				}
				
				case "sfida": {
					if( !TheadUDP.getInSfida() ) {	
					
						if(input_splitted.length == 2) {
						
							outp.writeBytes("richiesta" + "\n");
							outp.writeBytes(current_username + "\n"); 
							outp.writeBytes(input_splitted[1] + "\n"); // nickAmico
							
							String r = d.readLine(); 
							
							if(r.equals("0"))
								System.out.println(input_splitted[1] + " non ha accettato la tua richiesta");
							if(r.equals("3"))
								System.out.println(input_splitted[1] + " non è online oppure non appartiene ai tuoi amici");
							
							else if (!r.equals("0") &&!r.equals("2")) {
								System.out.println(input_splitted[1] + " ha accettato la tua richiesta");
								
								TheadUDP.setInSfida();
								TheadUDP.SonoOccupato();
	
							}
						
							else if (r.equals("2") )
							System.out.println(input_splitted[1] + " è impegnato in un altra sfida");
							
						}
						else 
							System.out.println("Dati non validi \n SFIDA ");
					}
					else 
						System.out.println("Operazione non ammessa mentre sei in sfida");
					usage2();
					break;
					
				}
			
				case "logout":{
					if( !TheadUDP.getInSfida() ) {
						outp.writeBytes("logout" + "\n");
						outp.writeBytes(current_username + "\n"); 
						started = true;
					}
					else 
						System.out.println("Operazione non ammessa mentre sei in sfida");
					break;
				}
				
				default: {
					if(TheadUDP.getInSfida()) {
						//INVIA LA TRADUZIONE

							outp.writeBytes("sfida" + "\n");
							outp.writeBytes(current_username + "\n"); 
							outp.writeBytes(input_splitted[0] + "\n"); // traduzione
							
					}
					else
						System.out.println("Operazione non ammessa");
					break;	
				}
			} //fine switch
			
		} //fine while
		
		if(started) return; //torna in started
	}
	

	public static void started() throws NotBoundException, IOException {
		String input;
		
		/*struttura per la connessione UDP. Ogni utente ha un UDPThread in ascolto di nuove richieste di sfida*/
		
		
		/*all interno di DatagramSocket devo mettere la porta del client. qua devo fare che la porta la faccio generare al client ed è relativa al client e poi
		 * nel login devo madare al server la porta generata in caso di login con successo*/
		
		
		while(!exit) {
			System.out.println("STATO: started");
			usage1();
			input = sc.nextLine();
			String[] input_splitted = input.split("\\s+");
			
			switch (input_splitted[0]) {
			
				case "register" :{
					
					Registry registry = LocateRegistry.getRegistry(porta_rmi);
					Registrazione_interface stub = (Registrazione_interface) registry.lookup("Registrazione_Utente");
					if(input_splitted.length==3) {
						int ris = stub.registra_utente(input_splitted[1], input_splitted[2]);
						if(ris == 1)
							System.out.println("Registrazione eseguita con successo.");
						else {
							System.out.println("Nome utente non disponibile");
						}
					}else {
						System.out.println("Dati non validi \n register <username> <password>");
					}
					break;
					
				}
				
				case "login" :{
					
					portUDP=getPortaUDP();
					DatagramSocket ClientUDP = new DatagramSocket(portUDP);
					
					TheadUDP = new UDPThead(ClientUDP); 
					(TheadUDP).setPriority(Thread.MAX_PRIORITY);
			        StartListener = Executors.newSingleThreadExecutor();
			        
			        StartListener.execute(TheadUDP);
			       
					if(input_splitted.length==3) {
	
						outp.writeBytes("login" + "\n"); 
						outp.writeBytes(input_splitted[1] + "\n"); //username
						outp.writeBytes(input_splitted[2] + "\n"); //password
						outp.writeBytes(portUDP.toString()  + "\n"); //portaUDP
						
						int r = d.read();
						if(r == 0) {
							System.out.println("Login eseguito con successo.");
							current_username = input_splitted[1];
				
							usage2();
						//	----- stato : LOGGED ---- //
							logged();
							System.out.println("Logout eseguito con successo.");
						}else if(r == 1)
							System.out.println("Utente non registrato");
						else if (r== 2)
							System.out.println("Password errata");
						else if (r== 3)
							System.out.println("login già effettuato");
					}else {
						System.out.println("Dati non validi \n login <username> <password>");
					}
					
					break;
					
				}
				
				case "exit" :{
					exit = true;
					outp.writeBytes("exit" + "\n"); 
					in.close();
					d.close();
					outp.flush();
					outp.close();
				}
				default: {
					
					
					
				break;
				}
			}	
			
		}//fine while
		sc.close();
	}
	
	
	public static void main (String[] args) throws IOException, NotBoundException {

		Properties prop = new Properties();
		String fileName = "C:\\Users\\marti\\eclipse-workspace\\WQ-reti\\src\\app.config";
		InputStream is = null;
		
		try {
		    is = new FileInputStream(fileName);
		} catch (FileNotFoundException ex) {
		    System.out.println("File non trovato");
		    port = DEFAULT_PORT;
		    porta_rmi = DEFAULT_RMI_PORT;
		}
		
		prop.load(is);
		port = Integer.parseInt(prop.getProperty("app.port"));
		porta_rmi = Integer.parseInt(prop.getProperty("app.porta_rmi"));
	
		sock = new Socket("localhost", port);
		in = new DataInputStream(sock.getInputStream());
		d= new BufferedReader(new InputStreamReader(sock.getInputStream()));
		outp = new DataOutputStream(sock.getOutputStream());
        
		started();
		System.out.println("Fine Esecuzione");
		sock.close();
		
	}	

	}
	
	

