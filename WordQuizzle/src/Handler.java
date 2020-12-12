import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Handler extends Thread {
	private static final int T1 = 10000;
	private static final int K = 5;
	private Socket s;
	private DataInputStream in;
	private BufferedReader d;
	private DataOutputStream outp;
	private Global_Data_Structures gds;
	private Vector<String> Dizionario;
	
	
	
	public Handler(Socket sock,Global_Data_Structures gds ) throws IOException {
		this.s = sock;
		this.in = new DataInputStream(s.getInputStream());
		this.d= new BufferedReader(new InputStreamReader(s.getInputStream()));
		this.outp = new DataOutputStream(s.getOutputStream())	;
		this.gds = gds;

	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public void run() {
		Boolean quit = false;
		while(!quit) {
			try {
				int r;

				Richiesta rq = Richiesta.valueOf(in.readLine());
				
				switch (rq) {
					
					case login:{
						
						String data[] = new String[3];
						data[0] = d.readLine(); //username
						data[1] = d.readLine(); //password
						data[2] =  d.readLine(); //portaUDP
						
						if(!gds.checkOnline(data[0])) {

							System.out.println("Login di " + data[0] + ", passw: " + data[1] + " " + data[2] );
							r = gds.login(data[0], data[1], data[2]) ;
							
							outp.writeByte(r);
							
							if(r == 0) {
								System.out.println("Login eseguito con successo.");

							}
							else if(r == 1)
								System.out.println("Utente non registrato");
							else if (r== 2)
								System.out.println("Password errata");
							
							System.out.println("Utenti Online: " + gds.online.toString()); 
						}
						else {
							
							r=3;
							outp.writeByte(r);
						}
						break;
						
					}
					
					case aggiungi_amico:{
						System.out.println("dovrei entrare qua");
						String data[] = new String[2];
						data[0] = d.readLine(); //nickUtente1
						data[1] = d.readLine(); //nickUtente2
						System.out.println(data[0] + " " + data[1]);
						r = gds.addFriendship(data[0], data[1]) ;
						System.out.println(r);
						if(r == 0)
							System.out.println("amicizia " + data[0] + "-" + data[1] + " creata!");
						else if(r == 1)
							System.out.println("Utente non registrato");
						else if (r== 2)
							System.out.println("amicizia " + data[0] + "-" + data[1] + " già presente");
						else if (r== 3)
							System.out.println("non puoi aggiungere te stesso agli amici");
						outp.writeByte(r);
						break;
						
					}
					
					
					case mostra_punteggio:{
						
						String data[] = new String[1];
						data[0] = d.readLine(); //username
						
						r = gds.Classifica.get(data[0]);
						
						outp.writeByte(r);
						
					}
					
					case mostra_classifica:{
						String x;
						String data[] = new String[1];
						data[0] = d.readLine(); //nickUtente1
						
						SortedSet<Map.Entry<String,Integer>> result = gds.creaClassifica(data[0]);
				
	                    //trasformo la classifica in oggetto JSON e invio risposta.
	                    Iterator<Entry<String, Integer>>iterator= result.iterator();
	                    JSONArray array = new JSONArray();

	                    while(iterator.hasNext()){
	                        JSONObject obj = new JSONObject();
	                        Entry<String, Integer> next = iterator.next();
	                        obj.put("username",next.getKey());
	                        obj.put("points",next.getValue());
	                        array.add(obj);
	                    }

	                    String rispo = array.toJSONString();
			                try {
			                    ObjectOutputStream writer = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream())); //getOutputStream Returns an output stream for this socket.
			                    writer.writeObject(rispo); //Write the specified object to the ObjectOutputStream
			                    writer.flush(); //libera lo stream 
			                   } catch (IOException e) {
			                    e.printStackTrace();
			                   }
			             
						break;
						
					}		


					case lista_amici:{
						String x;
						String data[] = new String[1];
						data[0] = d.readLine(); //nickUtente1
						System.out.println(data[0]);
						//oggetto JSON che il server deve restituire
						JSONObject Data = GestioneFile.GetPlayerJSON(data[0]);
						
						if(Data!= null) {
							System.out.println("Visualizzazione degli amici di " + data[0]);
						
			                
			                try {
			                    ObjectOutputStream writer = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream())); //getOutputStream Returns an output stream for this socket.
			                    writer.writeObject(Data); //Write the specified object to the ObjectOutputStream
			                    writer.flush(); //libera lo stream 
			                   } catch (IOException e) {
			                    e.printStackTrace();
			                   }
			             
						}
						else {
							System.out.println("Nessun amico da visualizzare");
							x="error";
							
						}
						
						break;
					}
					
					case sfida: {
						String player[] = new String[2];
						player[0] = d.readLine(); // nickname 
						player[1] = d.readLine(); // traduzione
						
						//recupero il riferimento alla partita che sta giocando nickname
						
						Challenge game = gds.Games.get(player[0]);
						game.check(player[0], player[1]);
						game.send_next(player[0]);
						
						break;	
					}
					
					case richiesta:{
						Integer c;
					
						String data[] = new String[2];
						data[0] = d.readLine(); // nickname dell'utente che ha richiesto la sfida
						data[1] = d.readLine(); // nickname dell'utente sfidato
						
						if( gds.checkOnline(data[1]) && gds.checkAmico(data[0], data[1]) ) {
							
							int portaUDP= gds.online.get(data[1]);
							int portaUDPSfidante= gds.online.get(data[0]);
							
							c=UDPRequest(data[0],data[1],portaUDP);
							outp.writeBytes (c.toString() + "\n");
							if(c==1) 
								setupSfida(data[0], data[1],portaUDP,portaUDPSfidante);
							
						}
						
						else {
							
							c=3;
							outp.writeBytes(c.toString() + "\n");
				
						}
						break;
					}
					
					case logout:{
						
						String data = d.readLine();
						System.out.println("Richiesto logout di " + data);
						gds.logout(data);
					//	System.out.println("Utenti Online: " + gds.online.toString());
						break;
						
					}
					case exit:
						quit = true;
						//in.close();
						d.close();
						outp.flush();
						outp.close();
						break;
						
					default:
						break;
					}
					
				
				}catch(Exception e) {
					e.printStackTrace();	
				quit = true;
			}
		} //fine while
		
		try {
			System.out.println("Ora viene chiuso il socket");
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private int UDPRequest(String sfidante, String sfidato, int portaUDP)  {
		
		try {
		      /**
		       * Viene mandato un pacchetto con le informazioni della sfida,
		       * il server aspetta massimo 15 secondi la risposta dall'amico, se non 
		       * arriva risposta entro questo tempo la sfida viene annullata. Se invece
		       * arriva una risposta, se la risposta è di tipo 1 allora la sfida è stata accettata,
		       * se la risposta è 0 allora la sfida è stata rifiutata, mentre se 
		       * la risposta è 2 allora lo sfidato è gia impegnato in una sfida
		       */
		      
		      //MANDA PACCHETTO UDP AL CLIENT SFIDATO
			  DatagramSocket datagramChannel = new DatagramSocket();
			//  String sfida = "SFIDA" + "\n";
		     
		      byte[] toSend = sfidante.getBytes("UTF8");
		      DatagramPacket packet = new DatagramPacket(toSend,toSend.length,InetAddress.getLocalHost(),portaUDP);
		      datagramChannel.setSoTimeout(T1);
		      datagramChannel.send(packet);
		      
		      //RICEVE UN PACCHETTO UDP RISPOSTA DAL CLIENT SFIDATO
		      byte[] message = new byte[1024];
		      DatagramPacket risposta= new DatagramPacket(message,1024);
		      datagramChannel.receive(risposta);
		      String data = new String(risposta.getData());
		      System.out.println(data);
		      
		      
		     if (data.trim().equals("si") || data.trim().equals("Si") || data.trim().equals("Sì") || data.trim().equals("SI") || data.trim().equals("sì")  ) {
		    	  
		    	System.out.println("Sfida accettata");	
		    	/*SFIDA ACCETTATA ----- SETUP DELLA PARTITA*/
			    datagramChannel.close();
			    return 1;
			    
		      }
		      
		      else if (data.trim().equals("no") || data.trim().equals("No")  || data.trim().equals("NO") ){
		    	  
		    	System.out.println("Sfida rifiutata");
		        datagramChannel.close();
		        return 0;
		      }
		      
		      else {
		    	System.out.println("Impegnato in un altra sfida");
		        datagramChannel.close();
		        return 2;
		      } 
		      
		    } catch (SocketTimeoutException e1) {
		    	System.out.println("Timeout");
		      return 0; 
		      
		    }
		    catch (IOException e) {
		      e.printStackTrace();
		      return 0;
		    } 
		
		    catch (Exception e) {
		      e.printStackTrace();
		      return 0;
		    }
		
	}
	 /*si occupa del setup della sfida:
	 * carica il file dizionario all'interno di un vettore , dopo seleziona K parole casuali all'interno di questo vettore e le inserisce in 
	 * una hashMap che mappa ad ogni parola la sua traduzione */
	
	private void setupSfida(String sfidante, String sfidato, int portaUDP, int portaUDPSfidante) {
		
			Dizionario = caricaDizionario();
			
	        Random r = new Random();
	       // Hashtable<String,String> KParole = new Hashtable<String,String>();
	        ArrayList<String> parole = new ArrayList<String>();
	        ArrayList<String> traduzioni = new ArrayList<String>();
	        
	        for(int j=0; j<K; j++) {
	        	
	            int i = r.nextInt(Dizionario.size());
	          
	            String parola = Dizionario.get(i);
	            String traduzione = getTraduzioneHttp(parola);
	            
	            //questa è una copia di memorizzazione che tiene il server per avere sempre accesso alla traduzione delle parole
	            parole.add(parola);
	            traduzioni.add(traduzione);
	        }
 
	        	Challenge game = new Challenge(sfidante, sfidato, portaUDPSfidante, portaUDP, parole, traduzioni,gds,K);	
	        	gds.Games.put(sfidante, game);
	        	gds.Games.put(sfidato, game);
	        	
	        	game.begin();
	}

	private String getTraduzioneHttp(String parola) {
		
		 	String request = ("https://api.mymemory.translated.net/get?q="+parola+"&langpair=it|en");
	        URL url = null;
	        try {
				url = new URL(request);
			} catch (MalformedURLException e) {
				
				e.printStackTrace();
			}
	        
	        HttpURLConnection connection = null;
	        try {
	        	
				connection = (HttpURLConnection) url.openConnection();
			} 
	        catch (IOException e) {
				
				e.printStackTrace();
			}
	        try {
	        	
				connection.setRequestMethod("GET");
			} 
	        catch (ProtocolException e) {
				
				e.printStackTrace();
			}
	     
	        BufferedReader in = null;
	        try {
	        	
				in = new BufferedReader (new InputStreamReader(connection.getInputStream()));
				
			} catch (IOException e2) {
				
				e2.printStackTrace();
			}

	        String inputLine;
	            
	        StringBuilder content = new StringBuilder();
	        
	        /*
	            Leggendo in input dalla connessione HTTP, il servizio ricava il
	            contenuto della risposta (in formato JSON) e lo trasforma in stringa
	            e successivamente analizza i campi "responseData" e "translatedText" 
	            per ricavare la traduzione e trasformarla in stringa
	        */

	            try {
	            	
					while ((inputLine = in.readLine()) != null) {
					    content.append(inputLine);
					}
					
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
	            
	            try {
	            	
					in.close();
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
	            connection.disconnect();
	            String risposta = content.toString();
	        
	            JSONParser Parser = new JSONParser();
	        
	            String traduzione = "";
	            //da vedere che fa
	            JSONObject Obj = null;
				try {
					
					Obj = (JSONObject) Parser.parse(risposta);
					
				} catch (ParseException e) {
					
					e.printStackTrace();
				}
				
	            JSONObject ResponseData = (JSONObject) Obj.get("responseData");
	            traduzione = (String) ResponseData.get("translatedText");
	            
	            return traduzione;
	}

	
	private Vector<String> caricaDizionario() {
		//CARICAMENTO DEL DIZIONARIO
        File dictionary = new File(System.getProperty("user.dir")+"\\dizionario.txt");
        
        FileReader fr = null;
        
        try {
            fr = new FileReader(dictionary);
        } 
        catch (FileNotFoundException ex) {
        	
            System.out.println("dizionario non caricato \n");
        }
        
        BufferedReader rd = new BufferedReader(fr);
        Vector<String> dizionario = new Vector<String>();
        String parola;
        
        try {
        	
        	 while((parola = rd.readLine()) != null) {
        
                dizionario.add(parola.trim());
            }
        	 
        } 
        catch (IOException ex) {
        	
           System.out.println("impossibile leggere dal file contenente il dizionario\n");
        }
        
        return dizionario;
		
	}
	
	
}

		
