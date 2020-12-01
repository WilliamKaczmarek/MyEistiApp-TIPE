package com.example.myeisti.vue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myeisti.R;
import com.example.myeisti.controleur.Controle;

public class HistoriqueDetail extends AppCompatActivity {

    private Controle controle;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique_detail);

        Intent HistoriqueActivity = getIntent();
        if(HistoriqueActivity != null){
            if (HistoriqueActivity.hasExtra("Id")){
                id = HistoriqueActivity.getStringExtra("Id");
            }
        }
        this.controle = Controle.getInstance(this,id,0);

    }

    public void afficherHistorique(String outputBDD){
        //Déclaration des variables
        LinearLayout linearLayout = findViewById(R.id.scroll);
        LinearLayout newLine;
        String jour,annee,heure,minute,text;
        int mois;
        String[] moisString ={
                "Janvier",
                "Février",
                "Mars",
                "Avril",
                "Mai",
                "Juin",
                "Juillet",
                "Aout",
                "Septembre",
                "Octobre",
                "Novembre",
                "Décembre"
        };

        //Paramètre de "position" pour chaque détail
        LinearLayout.LayoutParams paramLayout = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramLayout.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams photo = new LinearLayout.LayoutParams(
                200, 200);

        LinearLayout.LayoutParams texte = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        String[] detail = outputBDD.split(";",0);
        for (int i = 0; i < detail.length; i++) {
            if(detail.length>0){
                switch (detail[i].charAt(0)){

                    case 'C': //S'il s'agit d'une connexion
                        //Initialisation des variables
                        jour = detail[i].substring(1,3);
                        mois = Integer.parseInt(detail[i].substring(3,5));
                        annee = detail[i].substring(5,7);
                        heure = detail[i].substring(7,9);
                        minute = detail[i].substring(9);
                        text = "Connexion le "+jour+" "+moisString[mois-1]+" 20"+annee+" à "+
                                heure+"h"+minute;

                        //Création du contenu xml
                        newLine = new LinearLayout(this);
                        newLine.setLayoutParams(paramLayout);

                        ImageView image = new ImageView(this);
                        image.setLayoutParams(photo);
                        image.setImageResource(R.drawable.internet);
                        newLine.addView(image);

                        TextView information = new TextView(this);
                        information.setLayoutParams(texte);
                        information.setText(text);
                        information.setTextColor(Color.BLACK);
                        information.setTextSize(20);
                        information.setGravity(Gravity.CENTER);
                        newLine.addView(information);

                        linearLayout.addView(newLine);
                        break;

                    default:
                        Log.d("Warning ","Chaine ne contenant ni C ni S en premier");
                }
            }
        }
    }

}
