# WordQuizzle
1.	WQ: Descrizione del Problema

Il progetto consiste nell’implementazione di un sistema di sfide di traduzione italiano-inglese tra utenti registrati al 		servizio. Gli utenti registrati possono sfidare i propri amici ad una gara il cui scopo è quello di tradurre in inglese il maggiore numero di parole italiane proposte dal servizio. Il sistema consente inoltre la gestione di una rete sociale tra gli utenti iscritti. L’applicazione è implementata secondo una architettura client server.

2.	WQ: Specifiche per l'implementazione

●	la fase di registrazione viene implementata mediante RMI.

●	La fase di login deve essere effettuata come prima operazione dopo aver instaurato una connessione TCP con il server. Su questa connessione TCP, dopo previa login effettuata con successo, avvengono le interazioni client- server (richieste/risposte).

●	Il server inoltra la richiesta di sfida originata da nickUtente all'utente nickAmico usando la comunicazione UDP.

●	Il server può essere realizzato multithreaded oppure può effettuare il multiplexing dei canali mediante NIO.

●	Il server gestisce un dizionario di N parole italiane, memorizzato in un file. Durante la fase di setup di una sfida fra due utenti il server seleziona K parole a caso su N parole presenti nel dizionario. Prima dell’inizio della partita, ma dopo che ha ricevuto l’accettazione della sfida da parte dell’amico, il server chiede, tramite una chiamata HTTP GET, la traduzione delle parole selezionate al servizio esterno accessibile alla URL https://mymemory.translated.net/doc/spec.php. Le traduzioni  vengono memorizzate per tutta la durata della partita per verificare la correttezza delle risposte inviate dal client.

●	L'utente interagisce con WQ mediante un client che può utilizzare una semplice interfaccia grafica, oppure una interfaccia a linea di comando, definendo un insieme di comandi, presentati in un menu.

●	Il server persiste le informazioni di registrazione, relazioni di amicizia e punteggio degli utenti su file json.
