package com.example.myeisti.bdd;

import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.myeisti.controleur.Controle;
import com.example.myeisti.vue.HistoriqueDetail;

import org.json.JSONArray;

public class AccesDistant implements AsyncResponse{

    //Constante
    private static final String SERVERADDR = "https://myeistitipe.000webhostapp.com/serveuretudiant.php";

    private Controle controle;

    public AccesDistant(){
        super();
    }

    public AccesDistant(String id){//Ici 0 c'est pour historique normalement
        controle = Controle.getInstance(null,id,0);
    }

    /**
     * Retour du serveur distant
     * @param output
     */
    @Override
    public void processFinish(String output) {
        Log.d("serveur","---------"+output);
        // découpage du message reçu avec %
        String[] message = output.split("%");
        //dans message[0] : "id", "Erreur !"
        //dans message[1] : reste du message

        // s'il y a 2 cases
        if(message.length>1){
            if(message[0].equals("id")){
                Log.d("id","------"+message[1]);
            }else if(message[0].equals("Erreur !")){
                Log.d("Erreur !","------"+message[1]);
            }else if(message[0].equals("seeHistorique")){
                Log.d("Historique !","------"+message[1]);
                controle.setHistorique(message[1]);
                controle.instance = null; //Permet de réafficher le contenu si on quitte et revient sur la page
            }else if(message[0].equals("seeSolde")){
                Log.d("Solde !","------"+message[1]);
                controle.setSolde(message[1]);
                controle.instance = null; //Permet de réafficher le contenu si on quitte et revient sur la page
            }
        }
    }

    public void envoi(String operation, JSONArray lesDonneesJSON,String Get){
        String URL ="";
        AccesHTTP accesDonnees = new AccesHTTP();
        // lien de délégation
        accesDonnees.delegate = this;
        // ajout paramètre
        accesDonnees.addParam("operation",operation);
        accesDonnees.addParam("lesdonnees", lesDonneesJSON.toString());
        // appel au serveur
        if(!Get.equals("")){
            URL = SERVERADDR+"?"+Get;
        }else{
            URL = SERVERADDR;
        }
        accesDonnees.execute(URL);
    }
}
