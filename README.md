Cose da fare

1) Layout sintomi(checkbox) --> ELIA

2) Settings --> MATTIA
    --> Download automatici
    --> Lingua
    --> Qualità foto scaricate
    --> Tasto aggiorna posizione
    --> {Se la memoria è poca ==> salvataggio in SD [DikLRUCache]} --> Tasto rimuovi cache dalla memoria

3) {Struttura file in memoria ==> com.detons97gmail.progetto_embedded.Database} --> Più avanti

4) Navigazione tra le activity --> ELIA
    --> Intent con i giusti extra
    --> Navigazione all' indietro

5) Activity di prima apertura --> MATTIA
    --> con un if per capire se è la prima attivazione
    --> permessi [Lettura/scrittura]

6) Gestione RAM [Adapter Species_list_adapter][Android.util.LRUCache] --> ROCCO --> Provo a caricare immagini a manetta e riduco RAM emulatore
        --> Primo livello ==> Riduzione cache (resize, trim)
        --> Secondo livello ==> Elimino il riferimento alla cache

7) Utilizzo di risorse vettoriali nella MainActivity[icone] --> MATTIA

8) Download fasulli --> Finisce ELIA
    --> Sistemare un pelo ( cartella di destinazione)
    --> AddressBar [Notifica]
    --> Da fare in un service e usare JobIntentService ( al posto di IntentService)
    --> Prima del download fare controllo dei permessi, se mancanti chiederli

9) Report --> ROCCO

10) com.detons97gmail.progetto_embedded.Database --> ELIA
    --> Databse differente per ogni nazione e per ogni lingua
    --> Attributi
            - Path Immagine
            - Nome latino
            - Nome Specie Tradotto
            - Lista di sintomi SEPARATI DA VIRGOLE
            - Tipologia di contatto (Unica)
    --> Room

11) Controllo impostazioni di download --> DOPO
    --> Esempio download solo wifi

12) Icona Sintomi vettoriale --> MATTIA

13) Icona dell'applicazione --> Guardiamo un po' tutti

Proposta schema funzionamento app:

1) MainActivity controlla cartella applicazione rilevando le cartelle relative ad ogni nazione, ex: vengono rilevate cartelle per Italia, Cina, India, Africa.
Nello spinner verranno quindi mostrate solo quelle 4 nazioni. Sarebbe opportuno controllare che in ogni cartella vi sia il database relativo presente, se così non fosse
potremmo evitare di mostrare quella nazione.

MainActivity chiama poi SpeciesListActivity passando come extra nell'Intent: NAZIONE, CATEGORIA (Animali, Insetti, Piante).

2) SpeciesListActivity si occupa solo di passare le informazioni a SpeciesListFragment. C'è secondo me una scelta da fare ossia:
    A) I percorsi delle foto vengono presi direttamente dal database, possibilmente più veloce che scansionare tutta la cartella delle foto, e porterebbe ad evitare errori.
    Ad esempio se nella cartella ci fossero foto che non sono state inserite da noi, l'app potrebbe avere dei comportamenti scorretti. Controllare questo tipo di errori sarebbe complicato.
    Tuttavia può anche essere che percorsi di foto nel database non corrispondano a foto presenti in memoria, perchè sono state ad esempio cancellate. In questo caso si potrebbe semplicemente
    mostrare una foto placeholder

    B) I percorsi delle foto vengono presi scansionando la cartella delle foto, andando a prendere le informazioni nel database delle sole foto presenti. I problemi di questo approccio sono quelli descritti sopra.
    Sarei dell'opinione di usare l'alternativa A, ma dobbiamo discuterne.

In ogni caso, dovrebbe essere il fragment ad aprire la connessione col database. Questo permetterebbe di mantenerla aperta fino a che l'Activity ospitante non viene distrutta del tutto, ma cambi di orientamento
manterrebbero aperta la connessione, che è quello che vogliamo. Allo SpeciesListAdapter passiamo solo le informazioni dei percorsi delle foto e dei nomi degli animali, presi dal database. Solo al click di un elemento
andiamo a prendere le informazioni rimanenti dal database (Specie, dieta, sintomi, descrizione, aree geografiche).

3) DetailsActivity è il punto finale di questo percorso e non deve fare niente di complicato, solo mostrare le informazioni che ha ricevuto.

4) Un percorso alternativo è quello dei sintomi, dove va interrogato il database per conoscere tutti i sintomi registrati. Una volta selezionati, vanno passati tramite Intent alla SpeciesListActivity.
Quindi SpeciesListActivity mostrerebbe Animali, insetti, piante con quei sintomi dopo aver interrogato il database.
Con questa soluzione andrebbero aperte 2 connessioni diverse al database. Una possibile soluzione potrebbe essere quella di usare sintomi predefiniti e di inserirli staticamente in una classe Java, in questo modo
dovremmo interrogare il database solo nella SpeciesListActivity.



OPZIONI:
Qualità foto: Alta o bassa
Lingua: Italiano inglese
Rimuovi cache memoria
Se scheda SD, download in SD, altrimenti no

Show progress bar: https://stuff.mit.edu/afs/sipb/project/android/docs/training/notify-user/display-progress.html

DA CHIEDERE: Non ricordo perchè chiediamo il permesso di accedere alla memoria esterna.



SINTOMI

Abdominals Cramps
Muscolar Paralysis
Ptosis
Vomiting
Diarrhoea
Dizziness
Allergic Reactions
Pain at the site of the bite
Swelling
Local burning sensation
Urticaria
Inflammation
Redness
Vesication
Tetanic convulsions
Headache
Convulsions
Endema
Discoloration
Vomiting
Acute Pain
Soreness
Ulcer
Pain
Muscular paralysis
Choking sensation
Diarrhea
Necrosis
Swelling
Sleepiness
Itchiness
Abdominal cramps
Irritation
Hallucinations



Path esempio: /COUNTRY/Images/[Plants-Insects-Animals]/nome.webp


Cambio globale qualità immagini


MOTIVI PER USARE SINTOMI IN INGLESE:
    1.Si vuole evitare una query al database ogni volta che viene aperta la ricerca dei sintomi
    2.Anche se AppDatabase mantiene una rererence ai database aperti, ci sono problemi di inconsistenza ossia:
        Ogni database di una certa nazione può non contenere tutti i sintomi possibili, faccio una query
        su tutti i database disponibili solo per prendere tutti i sintomi?

        Altra soluzione sarebbe quella di prendere i sintomi solo della nazione selezionata, cambio dati nell'adapter ogni volta che l'utente
        modifica lo spinner?

        È anche difficile mettere in cache queste informazioni. Il ViewModel infatti viene distrutto quando l'activity che lo chiama viene distrutta.

        Si è preferito usare una lista di sintomi standard, salvarli nei database in lingua inglese e fornire le traduzioni nelle risorse
        dell'app. In questo modo i sintomi da caricare sono costanti e non ci sono database da interrogare, l'interrogazione avviene solo
        quando l'utente esegue una ricerca.