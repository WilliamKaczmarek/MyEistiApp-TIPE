package com.example.myeisti.vue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myeisti.R;
import com.example.myeisti.RSA.EncryptDecryptRSA;
import com.example.myeisti.bdd.AccesDistant;
import com.example.myeisti.classe.BddStudent;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MenuPrincipal extends AppCompatActivity {

    private String id;
    TextView tv_Bonjour;
    String token,Prenom;
    Button btHistorique,btNotes,btSolde,btAbsences;
    private AccesDistant accesDistant;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);


        Log.e("--------Cryptage 10.97", EncryptDecryptRSA.Encrypt("10.97"));
        Log.e("--------Decryptage 10.97", EncryptDecryptRSA.Decrypt(EncryptDecryptRSA.Encrypt("10.97")));
        Log.e("--------Cryptage 7.85", EncryptDecryptRSA.Encrypt("7.85"));
        Log.e("--------Decryptage 7.85", EncryptDecryptRSA.Decrypt(EncryptDecryptRSA.Encrypt("7.85")));

        Intent MenuActivity = getIntent();
        if(MenuActivity != null){
            if (MenuActivity.hasExtra("Token")){
                token = MenuActivity.getStringExtra("Token");
            }
        }

        tv_Bonjour = findViewById(R.id.text_Bonjour);

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
                    MenuPrincipal.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Prenom  = myResponse.substring(myResponse.indexOf("<firstName>")+11,myResponse.indexOf("</firstName>"));
                            tv_Bonjour.append(Prenom);

                            //Ajout de la connexion à l'historique de la bdd
                            String historique="C"+ (new SimpleDateFormat("ddMMyyHHmm")).format(new Date()) +";";
                            accesDistant = new AccesDistant();
                            id=myResponse.substring(myResponse.indexOf("id=\"")+4,myResponse.indexOf("\"><username>"));
                            accesDistant.envoi("addHistorique",new JSONArray(),"id="
                                    +id
                                    +"&add="+historique);
                        }
                    });

                }else{
                    final String myResponse = response.body().string();
                    MenuPrincipal.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_Bonjour.setText("fail" +myResponse);
                        }
                    });
                }
            }
        });
     btHistorique = findViewById(R.id.bt_Historique);
     btHistorique.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent HistoriqueActivity = new Intent(getApplicationContext(), Historique.class);
             HistoriqueActivity.putExtra("Token",token);
             startActivity(HistoriqueActivity);
             //finish();

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


     btNotes = findViewById(R.id.bt_Notes);
     btNotes.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent NotesActivity = new Intent(getApplicationContext(), Notes.class);
             NotesActivity.putExtra("Token",token);
             startActivity(NotesActivity);
         }
     });

        btAbsences = findViewById(R.id.bt_Absences);
        btAbsences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AbsencesActivity = new Intent(getApplicationContext(), Absences.class);
                AbsencesActivity.putExtra("Token",token);
                startActivity(AbsencesActivity);
            }});

    }
}
