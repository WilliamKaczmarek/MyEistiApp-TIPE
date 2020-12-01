package com.example.myeisti.vue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myeisti.R;
import com.example.myeisti.bdd.AccesDistant;
import com.example.myeisti.classe.BddStudent;
import com.example.myeisti.classe.Slot;
import com.example.myeisti.classe.Student;
import com.example.myeisti.controleur.Controle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Appel extends AppCompatActivity  implements View.OnClickListener {
    private Spinner spinner;
    private String token;
    private Button bt_startAppel,bt_terminerAppel,bt_confirmerAppel,bt_menu;
    private List<Slot> slotList;
    private TextView tv_error,tv_eleveScan,tv_titre;
    private NfcAdapter nfcAdapter=null;
    private Student[] tabEleve;
    private ImageView iv_NFC_logo;
    private Slot slotChoisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appel);

        //Déclaration des composants
        bt_startAppel = findViewById(R.id.bt_startAppel);
        spinner = findViewById(R.id.spinner);
        tv_error = findViewById(R.id.siError);
        tv_eleveScan = findViewById(R.id.eleveScan);
        iv_NFC_logo = findViewById(R.id.imageNFC);
        bt_terminerAppel = findViewById(R.id.bt_terminerAppel);
        tv_titre = findViewById(R.id.titre);
        bt_confirmerAppel = findViewById(R.id.bt_confirmerAppel);
        bt_menu = findViewById(R.id.bt_retourMenu);


        //Création de l'argument date pour la requête html
        String[] argumentDate = BddStudent.dateActuelle().split("%",2);

        //Récupération du token de l'activité précédente
        final Intent AppelActivity = getIntent();
        if(AppelActivity != null){
            if (AppelActivity.hasExtra("Token")){
                token = AppelActivity.getStringExtra("Token");
            }
        }

        //Création de la requête http pour récuperer le planning du jour
        OkHttpClient client = new OkHttpClient();
        //String url = "https://arel.eisti.fr/api/planning/slots?format=json&start=2020-06-08&end=2020-06-09";
        String url = "https://arel.eisti.fr/api/planning/slots?format=json&start="+argumentDate[0]+"&end="+argumentDate[1];
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization","bearer "+token)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override

            //Lorsque la requête echoue
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("Erreur",e.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){

                    //Si la réponse recu est bonne
                    final String myResponse = response.body().string();
                    Appel.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //Transformation de la réponse en JSONObject
                                JSONObject jsonResponse = new JSONObject(myResponse);
                                JSONArray jsonSlots = jsonResponse.getJSONArray("planningSlots");

                                //Création de la liste des crénaux
                                slotList = new ArrayList<>();
                                Slot slot;
                                JSONObject creneaux;
                                for (int i = 0; i < jsonSlots.length(); i++) {
                                    creneaux = jsonSlots.getJSONObject(i);
                                    slot = new Slot(
                                            creneaux.getString("id"),
                                            creneaux.getString("beginDate"),
                                            creneaux.getString("endDate"),
                                            creneaux.getString("label"),
                                            creneaux.getString("teacherId"),
                                            creneaux.getString("groupLabel"),
                                            creneaux.getString("groupId"),
                                            creneaux.getString("roomId")
                                    );
                                    //Condition car ce groupe ne contient aucun étudiant des P2C1
                                    if(slot.getGroupId().equals("72290")){slot.setGroupId("73091");}
                                    slotList.add(slot);
                                }
                                displaySpinner();
                                bt_startAppel.setEnabled(true);

                            } catch (JSONException e) {
                                Log.d("JSON api AREL","La conversion de la réponse en JSONObject est impossible.");
                            }
                        }
                    });

                }else{
                    //En cas d'erreur
                    final String myResponse = response.body().string();
                    Appel.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Une erreur s'est produite dans la réception du planning.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        bt_startAppel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //Récupération du créneau choisi
                slotChoisi = (Slot) spinner.getSelectedItem();

                //Gestion de la deuxième partie de l'affichage pour l'appel avec le NFC
                LinearLayout choixSlot = findViewById(R.id.visibleChoixSlot);
                final LinearLayout partieNFC = findViewById(R.id.invisibleNFC);
                TextView tv_infoCreneau = findViewById(R.id.infoCreneau);
                choixSlot.setVisibility(View.GONE);
                partieNFC.setVisibility(View.VISIBLE);
                tv_infoCreneau.setText(slotChoisi.getLabelMatiere()+" de "+slotChoisi.getDebut()+" à "+slotChoisi.getFin());

                //Création d'une nouvelle requête pour récupérer les étudiants du groupe
                OkHttpClient client2 = new OkHttpClient();
                String url = "https://arel.eisti.fr/api/groups/"+slotChoisi.getGroupId()+"?format=json";
                Request request2 = new Request.Builder()
                        .url(url)
                        .header("Authorization","bearer "+token)
                        .build();
                client2.newCall(request2).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();

                    }
                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if(response.isSuccessful()){
                            final String myResponse = response.body().string();
                            Appel.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(myResponse);
                                        tabEleve = Student.JSONToTabStudent(jsonResponse);
                                        defineAdapterNFC();
                                        bt_terminerAppel.setEnabled(true);


                                    } catch (JSONException e) {
                                        tv_error.setText("Erreur--Convertion de la réponse");
                                    }
                                }
                            });

                        }else{
                            final String myResponse = response.body().string();
                            Appel.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_error.setText("Erreur--" +myResponse);
                                }
                            });
                        }
                    }
                });
            }
        });
        bt_terminerAppel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Paramètre de "position" pour le switch de chaque élève
                LinearLayout.LayoutParams paramSwitch = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                //Mise à jour du titre
                tv_titre.setText("Confirmation");

                //Déclaration des variables et mise à jour du visuel
                LinearLayout layoutNFC = findViewById(R.id.invisibleNFC);
                LinearLayout layoutScroll = findViewById(R.id.layoutScrollAppel);
                LinearLayout scroll = findViewById(R.id.scrollAppel);
                layoutNFC.setVisibility(View.GONE);
                layoutScroll.setVisibility(View.VISIBLE);

                //Création des switch pour chaque élève
                Switch switch_button;
                for (int i = 0; i < tabEleve.length; i++) {
                    switch_button = new Switch(Appel.this);
                    switch_button.setOnClickListener(Appel.this);
                    switch_button.setTag(i);
                    switch_button.setChecked(!tabEleve[i].isAbsent());
                    switch_button.setLayoutParams(paramSwitch);
                    switch_button.setPadding(0,0,0,15);
                    switch_button.setTextSize(20);
                    switch_button.setText(tabEleve[i].getPrenom()+" "+tabEleve[i].getNom());
                    scroll.addView(switch_button);
                }
                bt_confirmerAppel.setEnabled(true);

            }
        });

        bt_confirmerAppel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layoutScroll = findViewById(R.id.layoutScrollAppel);
                LinearLayout recap = findViewById(R.id.recapAppel);
                TextView tv_recap = findViewById(R.id.putAbsencesSheet);
                layoutScroll.setVisibility(View.GONE);
                recap.setVisibility(View.VISIBLE);
                tv_titre.setText("Récapitulatif");
                String putRecap="{";
                putRecap += "\"slotId\": "+slotChoisi.getSlotId()+",";
                putRecap += "\"teacherId\": "+slotChoisi.getTeacherId()+",";
                putRecap += "\"roomId\": "+slotChoisi.getRoomId()+",";
                putRecap += "\"students\": [";
                if(tabEleve.length>0){
                    putRecap += "{\"studentId\": "+tabEleve[0].getNumero();
                    putRecap += ",\"absent\": "+tabEleve[0].isAbsent()+"}";
                }
                for (int i = 1; i < tabEleve.length-1; i++) {
                    putRecap += ",{\"studentId\": "+tabEleve[i].getNumero();
                    putRecap += ",\"absent\": "+tabEleve[i].isAbsent()+"}";
                }
                putRecap += "]}";
                try {
                    JSONObject absencesSheet = new JSONObject(putRecap);
                    tv_recap.setText(absencesSheet.toString(3)+'\n');
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Demander l'envoi de mail
                sendMailDialog();
                bt_menu.setEnabled(true);

            }
        });

        bt_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void displaySpinner(){

        //Affichage de la liste des crénaux d'aujourd'hui
        ArrayAdapter<Slot> adapter = new ArrayAdapter<Slot>(
                this,
                R.layout.custom_spinner,
                slotList
        );
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner.setAdapter(adapter);
    }

    private void defineAdapterNFC(){
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter != null){
            if(!nfcAdapter.isEnabled()){
                iv_NFC_logo.setImageResource(R.drawable.nfc_logo_red);
                showWirelessSettingsDialog();
            }else{
                iv_NFC_logo.setImageResource(R.drawable.nfc_logo_green);
            }
            enableForegroundDispatchSystem();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfcAdapter != null){
            if(!nfcAdapter.isEnabled()){
                iv_NFC_logo.setImageResource(R.drawable.nfc_logo_red);
                showWirelessSettingsDialog();
            }else{
                iv_NFC_logo.setImageResource(R.drawable.nfc_logo_green);
            }
            enableForegroundDispatchSystem();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nfcAdapter != null){
            disableForegroundDispatchSystem();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(this, "Carte NFC scannée !", Toast.LENGTH_SHORT).show();

            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(parcelables != null && parcelables.length > 0)
            {
                readTextFromMessage((NdefMessage) parcelables[0]);
            }else{
                Toast.makeText(this, "Carte étrangère !", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {

        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if(ndefRecords != null && ndefRecords.length>0){

            NdefRecord ndefRecord = ndefRecords[0];

            String tagContent = getTextFromNdefRecord(ndefRecord);

            int index = Student.indexId(tabEleve,tagContent);
            if(index != -1){
                tv_eleveScan.setText("Dernier élève scanné(e) : "+tabEleve[index].getPrenom()+" "+tabEleve[index].getNom());
                tabEleve[index].setAbsent(false);
            }else{
                tv_eleveScan.setText("Dernier élève scanné(e) : non reconnu");
            }

        }else
        {
            Toast.makeText(this, "Carte vierge!", Toast.LENGTH_SHORT).show();
        }

    }

    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, Appel.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }


    public String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

    private void showWirelessSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.nfc_disabled);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
        builder.create().show();
        return;
    }

    private void sendMailDialog(){
        //Création du de la demande d'envoi des mails
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.send_mail_dialog);
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                //Création du mail
                String to="";
                for (int j = 0; j < tabEleve.length; j++) {
                    if(tabEleve[j].isAbsent()){
                        to += tabEleve[j].getMail()+",";
                    }
                }
                Log.d("Adresses",to);
                to = to.substring(0,to.length()-1);
                Log.d("Adresses 2",to);
                String message = "Bonjour !\n " +
                        "Vous venez d'être noté(e) absent(e) au cours : "+
                        slotChoisi.getLabelMatiere()+"\n"+
                        "de "+slotChoisi.getDebut()+" à "+slotChoisi.getFin()+"\n\n"+
                        "A "+(new SimpleDateFormat("HH:mm")).format(new Date())+
                        ", le "+(new SimpleDateFormat("dd/MM/yyyy")).format(new Date())+".";
                String subject = "[Absence] - "+slotChoisi.getLabelMatiere();
                Intent email = new Intent(Intent.ACTION_VIEW,Uri.parse("mailto:"+to));
                email.putExtra(email.EXTRA_SUBJECT,subject);
                email.putExtra(email.EXTRA_TEXT,message);
                startActivity(email);


            }
        });
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                //Ne fait rien
            }
        });
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        Switch sw;
        ViewGroup parent = (ViewGroup) v.getParent();
        for (int itemPos = 0; itemPos < parent.getChildCount(); itemPos++) {
            View view = parent.getChildAt(itemPos);
            if ((view instanceof Switch) && (view.getTag().equals(v.getTag()))) {
                sw = (Switch) view; //Found it!
                tabEleve[Integer.valueOf(v.getTag().toString())].setAbsent(!sw.isChecked());
                String nom = tabEleve[Integer.valueOf(v.getTag().toString())].getPrenom();
                nom += " "+tabEleve[Integer.valueOf(v.getTag().toString())].getNom();
                if(sw.isChecked()){
                    Toast.makeText(this,nom+" est présent !",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,nom+" est absent !",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
