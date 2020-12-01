package com.example.myeisti.controleur;

import android.content.Context;

import com.example.myeisti.bdd.AccesDistant;
import com.example.myeisti.vue.HistoriqueDetail;
import com.example.myeisti.vue.Solde;

import org.json.JSONArray;

public final class Controle {

    public static Controle instance = null;
    private static Context contexte;
    public static AccesDistant accesDistant;


    private Controle(){ super();}

    /**
     *
     * @param context
     * @param id
     * @param page 0 pour HistoriqueDetail et 1 pour Solde
     * //On peut rajouter un param pour separer de quand on veut seeHistorique et quand
        on voudra afficher le solde
     * @return
     */
    public static final Controle getInstance(Context context,String id,int page){
        if(context != null){
            Controle.contexte = context;
        }
        if(Controle.instance == null){
            Controle.instance = new Controle();
            if(page==0) {
                accesDistant = new AccesDistant(id);
                accesDistant.envoi("seeHistorique", new JSONArray(), "id=" + id);
            }else if(page ==1){
                accesDistant = new AccesDistant(id);
                accesDistant.envoi("seeSolde", new JSONArray(), "id=" + id);
            }
        }
        return Controle.instance;
    }

    public void setHistorique(String str){
        ((HistoriqueDetail)contexte).afficherHistorique(str);
    }

    public void setSolde(String str){
        ((Solde)contexte).afficherSolde(str);
    }
}
