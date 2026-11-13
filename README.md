Progetto APSD
- VETTORI, LISTE, DEQ E INSIEMI DI DATI GENERICI -
M. BENERECETTI & F. MOGAVERO
Implementare una libreria di strutture dati, sia statiche sia dinamiche, utilizzando il linguaggio
Java per la gestione di vettori, liste, pile, code e insiemi ordinati e non ordinati di dati
generici, ovvero interi, stringhe, ecc.
Il codice sorgente prodotto dovra attenersi rigorosamente (sia nei nomi delle funzioni di libreria, 
sia nella struttura, gerarchia di interfacce e classi e nell’organizzazione delle diverse directory e
file “.java”) alla forma riportata nell’archivio Template.zip associato al presente esercizio. In
particolare, le funzionalita da implementare sono specificate nel diagramma delle interfacce e 
classi disponibile nella directory principale del archivio. Il colore usato nel diagramma per i
prototipi dei metodi va interpretato come di seguito indicato:
nero: dichiarazione astratta;
verde: dichiarazione e implementazione di default;
blu: riimplementazione (overriding);
rosso: ridichiarazione astratta.
Al fine di poter testare adeguatamente il funzionamento delle librerie sviluppate, si consiglia
di estendere l’insieme di procedure di test unitario denominato “simpletest”, fornito dai
docenti e implementato nella directory zapsdtest/simpletest del template utilizzando
il framework JUnit 5. Attenzione a non modificare in alcun modo il codice presente nella
directory zapsdtest/testframework.
La valutazione del progetto avvera in base ai seguenti criteri: 
• aderenza alla struttura del diagramma delle interfacce e classi;
• implementazione (riimplementazione) delle varie funzionalita al corretto livello di astrazione 
(interfaccia e/o classe);
• correttezza dell’implementazione delle varie funzionalita;
• efficienza dell’implementazione delle varie funzionalita;
• presenza o meno di errori nell’esecuzione dei test approfonditi fulltest non disponibili
nel template.
Si noti che i progetti la cui compilazione, effettuata usando i comandi da shell make
runtest o winbuild.ps1 runtest, genera errori saranno immediatamente valutati
insufficienti. Analogamente saranno considerati insufficienti i progetti con errori nel
simpletest.
Per la realizzazione delle librerie, in aggiunta alle spiegazioni dei docenti e al diagramma delle
classi sopra menzionato, si faccia riferimento alle sezioni 10.1 e 10.2 di [1] e alle sezioni 4.1,
4.2, 4.3 e 4.4 di [2].
