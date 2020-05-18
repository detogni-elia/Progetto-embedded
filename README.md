Cose da fare

1) Layout sintomi(checkbox) --> ELIA
2) Settings --> MATTIA
    --> Download automatici
    --> Lingua
    --> Qualità foto scaricate
    --> Tasto aggiorna posizione
    --> {Se la memoria è poca ==> salvataggio in SD [DikLRUCache]} --> Tasto rimuovi cache dalla memoria
3) {Struttura file in memoria ==> Database} --> Più avanti
4) Navigazione tra le activity --> ELIA
    --> Intent con i giusti extra
    --> Navigazione all' indietro
5) Activity di prima apertura --> MATTIA
    --> con un if per capire se è la prima attivazione
    --> permessi [Lettura/scrittura]
6) Gestione RAM [Adapter Species_list_adapter][Android.util.LRUCache] --> ROCCO
        --> Primo livello ==> Riduzione cache (resize, trim)
        --> Secondo livello ==> Elimino il riferimento alla cache
7) Utilizzo di risorse vettoriali nella MainActivity[icone] --> MATTIA
8) Download fasulli --> ROCCO


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