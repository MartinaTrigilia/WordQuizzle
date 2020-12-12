import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class Challenge {
	
	int K;
	private String sfidante;
	private int portaSfidante;
	private String sfidato;
	private int portaSfidato;
	ArrayList<String> parole;
	Integer paroleCorretteSfidante;
	Integer paroleCorretteSfidato;
	Integer punteggioSfidato;
	boolean[] endgame;
	Integer punteggioSfidante;
	ArrayList<String> traduzioni;
	private Global_Data_Structures gds;
	//indice dello sfidante
	int i = 0;
	//indice dello sfidato
	int j = 0;

	public Challenge(String sfidante, String sfidato, int portaSfidante, int portaSfidato, ArrayList<String> parole,
			ArrayList<String> traduzioni,Global_Data_Structures gds, int K) {	
		this.sfidante = sfidante;
		this.portaSfidante = portaSfidante;
		this.sfidato = sfidato;
		this.portaSfidato = portaSfidato;
		this.parole = parole;
		this.traduzioni = traduzioni;
		this.gds= gds;
		this.punteggioSfidato = 0;
		this.punteggioSfidante = 0;
		this.paroleCorretteSfidante = 0; 
		this.paroleCorretteSfidato = 0; 
		this.K=K;
		this.endgame = new boolean [2]; //pos 0 c'è end_game sfidante e in pos 1 quello dello sfidato
	}


	public void begin() {
		
		try {	
			endgame[0]=false;
			endgame[1]=false;
			//timeout schedula il task CalcolatorePunteggio alla fine dei 50 sec dall'inizio della partita
			Timer timeout = new Timer ();
        	TimerTask CalcolatorePunteggio = new TimerTask() {
        		
        		public void end(int portaUtente) {
        			try {	

        				String message = "FINE";
        				byte toSend[] = message.getBytes("UTF8");
        			
        				InetAddress.getLocalHost(); 
        				DatagramSocket dc = new DatagramSocket();
        				DatagramPacket Packet = new DatagramPacket(toSend, toSend.length,InetAddress.getLocalHost(),portaUtente); 

        				dc.send(Packet); 
        			
        				dc.close();
        			}
        			catch(Exception e) {
        				e.printStackTrace();
        			}
        			
        		}
        		
        		public void calcola() {
        			try {
        				
        				String vincitore = null;
        				
        				if(punteggioSfidante > punteggioSfidato) {
        					vincitore=sfidante;
        					//AGGIORNAMENTO CLASSIFICA DEL VINCITORE
        					int c=gds.Classifica.get(sfidante);
        					c = c + punteggioSfidante + 3;
        					gds.Classifica.replace(sfidante,c);
        					gds.addScore(sfidante, c);
        					
        					//AGGIORNAMENTO CLASSIFICA DELL ALTRO GIOCATORE
        					int r=gds.Classifica.get(sfidato);
        					r = r + punteggioSfidato;
        					gds.Classifica.replace(sfidato,r);
        					gds.addScore(sfidato, r);
        				
        				}
        				else if(punteggioSfidato > punteggioSfidante) {
        					vincitore=sfidato;
        					
        					//AGGIORNAMENTO CLASSIFICA DEL VINCITORE
        					int r=gds.Classifica.get(sfidato);
        					r = r + punteggioSfidato +3;
        					gds.Classifica.replace(sfidato,r);
        					gds.addScore(sfidato, r);
        					
	
        					//AGGIORNAMENTO CLASSIFICA DELL ALTRO GIOCATORE
        					int c=gds.Classifica.get(sfidante);
        					c = c + punteggioSfidante;
        					gds.Classifica.replace(sfidante,c);
        					gds.addScore(sfidante, c);
        				
        					
        				}
        				
        				else if(punteggioSfidato==punteggioSfidante) {
        					vincitore="entrambi";
        					//AGGIORNAMENTO CLASSIFICA DI ENTRAMBI I GIOCATORI
        					int r=gds.Classifica.get(sfidato);
        					r = r + punteggioSfidato;
        					gds.Classifica.replace(sfidato,r);
        					gds.addScore(sfidato, r);
	
        					int c=gds.Classifica.get(sfidante);
        					c = c + punteggioSfidante;
        					gds.Classifica.replace(sfidante,c);
        					gds.addScore(sfidante, c);
        				
        				}
        				gds.Games.remove(sfidante);
        				gds.Games.remove(sfidato);
	        			DatagramSocket datagramChannel = new DatagramSocket();
	        			
	        			String messageSfidante = "RISULTATI" + " " +vincitore + " " + paroleCorretteSfidante + " " + punteggioSfidante + " " + punteggioSfidato + "\n";
	       			 	byte[] toSendSfidante = new byte[1000];
	       			 	toSendSfidante = messageSfidante.getBytes();
	       			 
	        			String messageSfidato = "RISULTATI" + " " + vincitore + " " + paroleCorretteSfidato + " " + punteggioSfidato + " " + punteggioSfidante+ "\n";
	       			 	byte[] toSendSfidato = new byte[1000];
	       			 	toSendSfidato = messageSfidato.getBytes();
	       			 	
	       			 	DatagramPacket packetSfidante = new DatagramPacket(toSendSfidante,toSendSfidante.length,InetAddress.getLocalHost(),portaSfidante);
	       			 	DatagramPacket packetSfidato = new DatagramPacket(toSendSfidato,toSendSfidato.length,InetAddress.getLocalHost(),portaSfidato);
	       			 
	       			 	datagramChannel.send(packetSfidante);
	       			 	datagramChannel.send(packetSfidato);
	       			 
	       			 	datagramChannel.close();
	       			 	
        			}catch(Exception e) {
        				e.printStackTrace();
        			}
	        			
        			
        		}
        		
        		@Override  
        		public void run() {  
        			
        			if(endgame[0] && endgame[1])
        				calcola();
        			
        			//sfidante non ha ancora finito la parita e sfidato si
        			if(endgame[0]==false && endgame[1])  {
        				end(portaSfidante);
        				calcola();
        			}
        			//sfidato non ha ancora finito la partita e sfidante si
        			if(endgame[1]==false && endgame[0])  {
        				end(portaSfidato);
        				calcola();
        			}
        			//nessuno dei due ha finito la partita
        			if(endgame[0]==false && endgame[1]==false)  {
        				end(portaSfidante);
        				end(portaSfidato);
        				calcola();
        			}
        			
        				
        			
        		};
        	};	
        	
        	timeout.schedule(CalcolatorePunteggio, 50000) ;
			
			 DatagramSocket dc = new DatagramSocket();

			 byte[] toSend = new byte[1000];
			 toSend = parole.get(0).getBytes();
			 
			 DatagramPacket packetSfidante = new DatagramPacket(toSend,toSend.length,InetAddress.getLocalHost(),portaSfidante);
			 DatagramPacket packetSfidato = new DatagramPacket(toSend,toSend.length,InetAddress.getLocalHost(),portaSfidato);
			 
			 dc.send(packetSfidante);
			 dc.send(packetSfidato);
			 
			 dc.close();
			 i++;
			 j++;
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}



	public void check(String nick, String traduzione) {
		
		int index;
		//controllo se utente è lo sfidante o lo sfidato
		if(nick.equals(sfidato)) {
			index = j;
			
			//traduzione corretta
			if( traduzione.equals(traduzioni.get(index-1)) ) {
				
				punteggioSfidato = punteggioSfidato + 2;
				paroleCorretteSfidato = paroleCorretteSfidato + 1;
				
			}
			
			//traduzione errata
			else
				punteggioSfidato = punteggioSfidato - 1;
				
			
		}
		
		else {
			index = i;
			
			//traduzione corretta
			if( traduzione.equals(traduzioni.get(index-1)) ) {
				
				punteggioSfidante = punteggioSfidante + 2;
				paroleCorretteSfidante = paroleCorretteSfidante + 1;
				
			}
			
			//traduzione errata
			else 
				punteggioSfidante = punteggioSfidante - 1;
					
		}
			
		
	}



	public void send_next(String nick) {
		
		int index=0;
		String utente1 = null;
		String utente2;
		int portaUtente1=0;
		int portaUtente2=0;
		
		if(nick.equals(sfidato)) {
			
			utente1 = sfidato; //quello a cui devo mandare il pacchetto
			portaUtente1 = portaSfidato;
			portaUtente2 = portaSfidante;
			utente2 = sfidante;
			index = j;
		}
		else if(nick.equals(sfidante)) {
			
			utente1 = sfidante; //quello a cui devo mandare il pacchetto
			portaUtente1 = portaSfidante;
			portaUtente2 = portaSfidato;
			utente2 = sfidato;
			index = i;
		}
		
		//le parole da mandare sono finite, devo terminare la partita
		if( index == K) {
			
			end_game(portaUtente1,utente1);		
		}
		else {
			 	try {
			 		
			 		DatagramSocket dc = new DatagramSocket();
			 		byte[] toSend = parole.get(index).getBytes("UTF8");
			 		DatagramPacket packetUtente1= new DatagramPacket(toSend,toSend.length,InetAddress.getLocalHost(),portaUtente1);
			 		dc.send(packetUtente1);
			 		dc.close();
			 		
			 		if(nick.equals(sfidato)) 
			 		 	j++;	
			 		else if(nick.equals(sfidante)) 
			 		 	i++;	
			 	}
			 	catch(Exception e) {
			 		e.printStackTrace();
			 	}
		}
	}



	private void end_game(int portaUtente1, String utente1) {
		try {	
			if(utente1.equals(sfidante))
				endgame[0]=true;
			if(utente1.equals(sfidato))
				endgame[1]=true;
			String message = "FINE";
			byte toSend[] = message.getBytes("UTF8");
		
			InetAddress.getLocalHost(); 
			DatagramSocket dc = new DatagramSocket();
			DatagramPacket Packet = new DatagramPacket(toSend, toSend.length,InetAddress.getLocalHost() , portaUtente1); 

			dc.send(Packet); 
		
			dc.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}