package com.example.myeisti.vue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myeisti.ProgressBar.LoadingDialog;
import com.example.myeisti.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Absences extends AppCompatActivity {

    private String token;
    private LinearLayout scroll;
    private TextView tv_nbAbsences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absences);

        scroll= findViewById(R.id.scrollAbsence);
        tv_nbAbsences = findViewById(R.id.nbAbsences);

        final LoadingDialog loadingDialog = new LoadingDialog(Absences.this);
        loadingDialog.startLoadginDialog();
        final Intent AbsenceActivity = getIntent();
        if(AbsenceActivity != null){
            if (AbsenceActivity.hasExtra("Token")){
                token = AbsenceActivity.getStringExtra("Token");
            }
        }

        OkHttpClient client = new OkHttpClient();
        String url = "https://arel.eisti.fr/api/me/absences?format=json";
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
                    Absences.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //res est un test car j'ai pas d'absences
                                //String res="{\"absences\":[{\"id\":168668,\"userId\":73139,\"slotId\":3464103},{\"id\":140445,\"userId\":73139,\"slotId\":2872906},{\"id\":141088,\"userId\":73139,\"slotId\":2932423},{\"id\":154319,\"userId\":73139,\"slotId\":3294836},{\"id\":165059,\"userId\":73139,\"slotId\":3435705},{\"id\":139826,\"userId\":73139,\"slotId\":2838845},{\"id\":165110,\"userId\":73139,\"slotId\":3440920},{\"id\":165238,\"userId\":73139,\"slotId\":3441692},{\"id\":165975,\"userId\":73139,\"slotId\":3446659},{\"id\":144994,\"userId\":73139,\"slotId\":3082188},{\"id\":145363,\"userId\":73139,\"slotId\":3083847},{\"id\":146479,\"userId\":73139,\"slotId\":3145848},{\"id\":165297,\"userId\":73139,\"slotId\":3441664}]}";
                                //JSONObject jsonResponse = new JSONObject(res);
                                //Transformation de la réponse en JSONObject
                                JSONObject jsonResponse = new JSONObject(myResponse);
                                JSONArray jsonTabAbsence = jsonResponse.getJSONArray("absences");
                                //Pour chaque absences :
                                JSONObject jsonSlot;
                                int compt=0;
                                for (int i = 0; i < jsonTabAbsence.length(); i++) {
                                    jsonSlot = jsonTabAbsence.getJSONObject(i);
                                    String SlotId = String.valueOf(jsonSlot.getInt("slotId"));
                                    afficherAbsences(SlotId);
                                    compt++;
                                }
                                tv_nbAbsences.setText("Vous avez "+compt+" absence(s) !");
                                tv_nbAbsences.setVisibility(View.VISIBLE);
                                loadingDialog.dismissDialog();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),
                                        "La conversion JSON a échouée 1 ",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    final String myResponse = response.body().string();
                    Absences.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Une erreur est survenue",Toast.LENGTH_SHORT).show();
                        }
                    }
                    );
                }
            }
        });

    }

    private void afficherAbsences(String slotId){
        OkHttpClient client = new OkHttpClient();
        String url = "https://arel.eisti.fr/api/planning/slots/"+slotId+"?format=json";
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
                    Absences.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String date, debut, fin, cours;
                                JSONObject jsonResponse = new JSONObject(myResponse);
                                date = jsonResponse.getString("beginDate");
                                date = date.substring(8,10)+"/"+date.substring(5,7)+"/"+date.substring(0,4);
                                debut = jsonResponse.getString("beginDate");
                                debut = debut.substring(11,16);
                                fin = jsonResponse.getString("endDate");
                                fin = fin.substring(11,16);
                                cours = jsonResponse.getString("label");
                                addAbsenceVue(date,debut,fin,cours);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),
                                        "La conversion JSON a échouée 2",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    final String myResponse = response.body().string();
                    Absences.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Une erreur est survenue",Toast.LENGTH_SHORT).show();
                        }
                    }
                    );
                }
            }
        });
    }

    private void addAbsenceVue(String date,String debut, String fin, String cours){
        //Création des paramètes du layout pour une ligne
        LinearLayout.LayoutParams paramLayout = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramLayout.gravity = Gravity.CENTER;
        LinearLayout.LayoutParams photo = new LinearLayout.LayoutParams(
                330, 220);

        LinearLayout newLine = new LinearLayout(this);
        newLine.setLayoutParams(paramLayout);
        newLine.setPadding(0,0,0,15);
        newLine.setOrientation(LinearLayout.HORIZONTAL);


        ImageView image = new ImageView(this);
        image.setLayoutParams(photo);
        image.setImageResource(R.drawable.logo_absence);
        newLine.addView(image);

        TextView information = new TextView(this);
        information.setLayoutParams(paramLayout);
        information.setText(date+'\n'+"De "+debut+" à "+fin+"\n"+cours);
        information.setTextColor(Color.BLACK);
        information.setTextSize(16);
        information.setGravity(Gravity.CENTER);
        newLine.addView(information);

        scroll.addView(newLine);
    }
}
