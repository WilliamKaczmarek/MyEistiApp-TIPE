package com.example.myeisti.vue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.myeisti.RSA.EncryptDecryptRSA;
import com.example.myeisti.bdd.AccesDistant;
import com.example.myeisti.classe.Marks;
import com.example.myeisti.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Historique extends AppCompatActivity {

    private String token;
    private Button btSend;
    private EditText et_URL;
    private EditText res;
    private AccesDistant accesDistant;
    private String test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);
        et_URL = findViewById(R.id.et_envoiRequete);
        et_URL.setText("https://arel.eisti.fr/api/me");
        res = findViewById(R.id.edit_text_result);

        /* //D-Test de l'accès à la base de donnée
        String id="72562";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyHHmm");
        String date=simpleDateFormat.format(new Date());
        String historique=date+";";
        accesDistant = new AccesDistant();
        accesDistant.envoi("id",new JSONArray(),"id="+id);
        accesDistant.envoi("addHistorique",new JSONArray(),"id="+id+"&add="+historique);
        //F-Test de l'accès à la base de donnée */

       /* //D-Test calcul RSA
        String str = EncryptDecryptRSA.Encrypt("5.00");
        Log.d("Message Encrypt ",str);
        str = EncryptDecryptRSA.Decrypt(str);
        Log.d("Message Decrypt ", str);
        str = EncryptDecryptRSA.Encrypt("60.00");
        Log.d("Message Encrypt ",str);
        str = EncryptDecryptRSA.Decrypt(str);
        Log.d("Message Decrypt ", str);
        str = EncryptDecryptRSA.Encrypt("7.25");
        Log.d("Message Encrypt ",str);
        str = EncryptDecryptRSA.Decrypt(str);
        Log.d("Message Decrypt ", str);
        //F-Test calcul RSA*/


        final Intent HistoriqueActivity = getIntent();
        if(HistoriqueActivity != null){
            if (HistoriqueActivity.hasExtra("Token")){
                token = HistoriqueActivity.getStringExtra("Token");
            }
        }
        btSend = findViewById(R.id.bt_send);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient();

                String url = et_URL.getText().toString();

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
                    public void onResponse(Call call, final Response response) throws IOException {
                        if(response.isSuccessful()){
                            final String myResponse = response.body().string();
                            Historique.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        JSONObject jsonObject = new JSONObject(myResponse);
                                        res.setText(jsonObject.toString(3));
                                    }catch (JSONException err){
                                        Log.d("Error ! ",err.toString());
                                        res.setText(myResponse);
                                    }



                                }
                            });

                        }else{
                            final String myResponse = response.body().string();
                            Historique.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "La requête n'a pas aboutie, vérifier l'url !",Toast.LENGTH_SHORT).show();
                                    res.setText("Erreur - " + myResponse);
                                }
                            });
                        }
                    }
                });
            }
        });



    }
}
