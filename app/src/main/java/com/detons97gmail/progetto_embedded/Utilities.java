package com.detons97gmail.progetto_embedded;

import android.content.Context;
import android.widget.Toast;

import java.util.LinkedList;

public class Utilities {
    public static void showToast(Context context, String message, int duration){
        Toast.makeText(context, message, duration).show();
    }
    public static class AnimalDetails {
        private int imageRef;
        private LinkedList<String> attributeName;
        private LinkedList<String> attributeContent;

        public AnimalDetails() {
            //Riempito per un esempio, sarebbe da collegare ad un database
            attributeName=new LinkedList<>();
            attributeContent=new LinkedList<>();

            //Initialize imageRef
            imageRef=R.drawable.taz;

            //Initialize attributeNameList
            attributeName.addLast("Nome:");
            attributeName.addLast("Specie:");
            attributeName.addLast("Dieta:");
            attributeName.addLast("Descrizione:");

            //Initialize attributeContentList
            attributeContent.addLast("Taz");
            attributeContent.addLast("Diavolo della Tasmania");
            attributeContent.addLast("Carnivoro");
            attributeContent.addLast("Taz è generalmente raffigurato come un feroce (seppur ottuso) carnivoro, irascibile e poco paziente." +
                    " Anche se può essere molto subdolo, a volte è anche dolce. Il suo enorme appetito sembra non conoscere limiti, poiché mangia qualsiasi cosa sul suo cammino. " +
                    "È noto soprattutto per i suoi discorsi costituiti principalmente da grugniti e ringhi (nelle sue prime apparizioni, parla con una grammatica primitiva) e per la sua capacità di girare come un vortice e mordere quasi tutto." +
                    "Taz ha un punto debole: può essere calmato da quasi ogni musica." +
                    " Mentre si trova in questo stato calmo, può essere facilmente affrontato. " +
                    "L'unica musica nota per non pacificare Taz è la cornamusa, che trova insopportabile.");
        }

        public int getImageRef()
        {
            return imageRef;
        }
        public String getAttributeName(int position) {
            //position-1 perchè il primo elemento èp sempre un' immagine
            return attributeName.get(position-1);
        }

        public String getAttributeContent(int position) {
            //position-1 perchè il primo elemento è sempre un' immagine
            return attributeContent.get(position-1);
        }

        public int size() {
            //Size+1 perchè conto anche la foto
            return attributeName.size()+1;
        }
    }
}
