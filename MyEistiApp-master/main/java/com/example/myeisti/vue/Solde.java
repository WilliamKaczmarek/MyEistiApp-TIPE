package com.example.myeisti.vue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myeisti.R;
import com.example.myeisti.RSA.EncryptDecryptRSA;
import com.example.myeisti.controleur.Controle;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Solde extends AppCompatActivity {

    TextView txtTagContent,demandeScan,lienGLPI;
    private Controle controle;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solde);

        Intent MenuActivity = getIntent();
        if(MenuActivity != null){
            if (MenuActivity.hasExtra("Id")){
                id = MenuActivity.getStringExtra("Id");
            }
        }

        //Declaration des TextView avec leur id
        txtTagContent = findViewById(R.id.montant_Solde);
        demandeScan = findViewById(R.id.demandeScan);
        lienGLPI = findViewById(R.id.lien_GLPI);
        //this.controle = Controle.getInstance(this,id,1);

        OkHttpClient client = new OkHttpClient();
        String url = "https://myeistitipe.000webhostapp.com/serveuretudiant.php?id="+id;

        RequestBody formBody = new FormBody.Builder()
                .add("operation","seeSolde")
                .build();


        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error ","Failure used"+e.toString());
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = response.body().string();
                    Solde.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            afficherSolde(myResponse.substring(9));
                        }
                    });

                }else{
                    final String myResponse = response.body().string();
                    Solde.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Erreur",Toast.LENGTH_SHORT).show();
                            afficherSolde("error");
                        }
                    }
                    );
                }
            }
        });


        //Ajout du lien pour faire une demande de rechargement credit impression
        lienGLPI.setMovementMethod(LinkMovementMethod.getInstance());

    }

    public void afficherSolde(String solde){
        //La fonction de cryptage/decryptage bug pour une chaine.lenght != 4
        //String str = EncryptDecryptRSA.Decrypt(solde);
        txtTagContent.setText("Vous avez "+solde+"€");
        demandeScan.setVisibility(View.INVISIBLE); //On enlève la demande de scanner la carte
    }
}
