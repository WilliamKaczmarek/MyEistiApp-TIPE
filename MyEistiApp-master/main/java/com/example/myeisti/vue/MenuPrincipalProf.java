package com.example.myeisti.vue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myeisti.R;
import com.example.myeisti.bdd.AccesDistant;
import com.example.myeisti.classe.BddStudent;

import org.json.JSONArray;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MenuPrincipalProf extends AppCompatActivity {

    private String token,Prenom,id;
    private TextView tv_Bonjour;
    private Button btAbsences,btSolde,btHistorique;
    private AccesDistant accesDistant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal_prof);
        Intent MenuActivity = getIntent();
        if(MenuActivity != null){
            if (MenuActivity.hasExtra("Token")){
                token = MenuActivity.getStringExtra("Token");
            }
        }

        tv_Bonjour = findViewById(R.id.text_Bonjour);
        btHistorique = findViewById(R.id.bt_Historique);

        OkHttpClient client = new OkHttpClient();
        String url = "https://arel.eisti.fr/api/me";
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization","bearer "+token)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = response.body().string();
                    MenuPrincipalProf.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Prenom  = myResponse.substring(myResponse.indexOf("<firstName>")+11,myResponse.indexOf("</firstName>"));
                            tv_Bonjour.append(Prenom+" (prof)");

                            //Ajout de la connexion à l'historique de la bdd
                            String historique="C"+ (new SimpleDateFormat("ddMMyyHHmm")).format(new Date()) +";";
                            accesDistant = new AccesDistant();
                            id= myResponse.substring(myResponse.indexOf("id=\"")+4,myResponse.indexOf("\"><username>"));
                            accesDistant.envoi("addHistorique",new JSONArray(),"id="
                                    +id
                                    +"&add="+historique);
                            btHistorique.setEnabled(true);


                        }
                    });

                }else{
                    final String myResponse = response.body().string();
                    MenuPrincipalProf.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_Bonjour.setText("Erreur--" +myResponse);
                        }
                    });
                }
            }
        });

        btAbsences = findViewById(R.id.bt_Absences);
        btAbsences.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Intent AppelActivity = new Intent(getApplicationContext(), Appel.class);
                                              AppelActivity.putExtra("Token",token);
                                              startActivity(AppelActivity);
                                          }
                                      }

        );

        btSolde = findViewById(R.id.bt_Solde);
        btSolde.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent SoldeActivity = new Intent(getApplicationContext(), Solde.class);
                SoldeActivity.putExtra("Id",id);
                startActivity(SoldeActivity);
            }
        });

        btHistorique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent HistoriqueActivity = new Intent(getApplicationContext(),HistoriqueDetail.class);
                HistoriqueActivity.putExtra("Id",id);
                startActivity(HistoriqueActivity);
            }
        });

    }
}
