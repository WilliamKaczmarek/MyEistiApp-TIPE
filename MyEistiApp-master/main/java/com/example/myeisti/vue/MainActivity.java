package com.example.myeisti.vue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.myeisti.ProgressBar.LoadingDialog;
import com.example.myeisti.R;
import com.example.myeisti.RSA.EncryptDecryptRSA;

import java.io.IOException;
import java.lang.String;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    EditText etUsername,etPassword;
    Button btSubmit;
    private String MessageBienvenue;
    private String token;
    private Switch swProf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btSubmit = findViewById(R.id.bt_connexion);
        swProf = findViewById(R.id.statut_prof);


        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btSubmit.setEnabled(false);
                OkHttpClient client = new OkHttpClient();
                String url = "https://arel.eisti.fr/oauth/token";

                RequestBody formBody = new FormBody.Builder()
                        .add("grant_type","password")
                        .add("username",etUsername.getText().toString())
                        .add("password",etPassword.getText().toString())
                        .add("scope","read")
                        .add("format","xml")
                        .build();


                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", Credentials.basic("test-myeisti-2126", "Yawv12tpCRPv5CMCvExf"))
                        .post(formBody)
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
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(
                                            MainActivity.this
                                    );
                                    builder.setIcon(R.drawable.ic_check);
                                    builder.setTitle("Bienvenue sur MyEISTI");
                                    token = myResponse.substring(myResponse.indexOf("<access_token>")+14,myResponse.indexOf("</access_token>"));
                                    MessageBienvenue = etUsername.getText().toString() + " identifié(e) ! ";
                                    builder.setMessage(MessageBienvenue);
                                    builder.setNegativeButton("D'accord", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            if(swProf.isChecked()){
                                                Intent MenuActivityProf = new Intent(getApplicationContext(), MenuPrincipalProf.class);
                                                MenuActivityProf.putExtra("Token",token);
                                                startActivity(MenuActivityProf);

                                            }else{
                                                Intent MenuActivity = new Intent(getApplicationContext(), MenuPrincipal.class);
                                                MenuActivity.putExtra("Token",token);
                                                startActivity(MenuActivity);

                                            }

                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    btSubmit.setEnabled(true);
                                }
                            });

                        }else{
                            final String myResponse = response.body().string();
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Identifiant ou mot de passe incorrecte",Toast.LENGTH_SHORT).show();
                                    btSubmit.setEnabled(true);
                                }
                            }
                            );
                        }
                    }
                });
            }
        });
    }
}
