package com.example.myeisti.vue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myeisti.ProgressBar.LoadingDialog;
import com.example.myeisti.classe.Marks;
import com.example.myeisti.Adapter.PageAdapter;
import com.example.myeisti.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Notes extends AppCompatActivity {

    private TabLayout tablayout;
    private ViewPager viewPager;
    private TabItem sem1, sem2;
    public PagerAdapter pagerAdapter;
    private TextView textt;
    private static String token;
    private Marks[] tab;
    private String[] tab2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        final LoadingDialog loadingDialog = new LoadingDialog(Notes.this);
        loadingDialog.startLoadginDialog();

        Intent NotesActivity = getIntent();
        if(NotesActivity != null){
            if (NotesActivity.hasExtra("Token")){
                token = NotesActivity.getStringExtra("Token");
            }
        }

        //Requete pour notes
        OkHttpClient client = new OkHttpClient();
        String url = "https://arel.eisti.fr/api/me/marks";
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
                    Notes.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("xml  ",myResponse.substring(10,50));
                            tab = Marks.NoteToTabMark(myResponse);
                            tab2 = Marks.TabMarkToSemestre(tab);
                            //Creation des fragments
                            tablayout = (TabLayout) findViewById(R.id.tablayout);
                            sem1 = (TabItem) findViewById(R.id.sem1);
                            sem2 = (TabItem) findViewById(R.id.sem2);
                            viewPager = findViewById(R.id.viewpager);
                            loadingDialog.dismissDialog();
                            //pagerAdapter = new PageAdapter(getSupportFragmentManager(),tablayout.getTabCount());
                            pagerAdapter = new PageAdapter(getSupportFragmentManager(),tablayout.getTabCount(),tab);
                            viewPager.setAdapter(pagerAdapter);

                            tablayout.setOnTabSelectedListener(new OnTabSelectedListener() {
                                @Override
                                public void onTabSelected(TabLayout.Tab tab) {
                                    viewPager.setCurrentItem(tab.getPosition());
                                    if(tab.getPosition() == 0 ){
                                        pagerAdapter.notifyDataSetChanged();
                                    }else if(tab.getPosition() == 1 ) {
                                        pagerAdapter.notifyDataSetChanged();
                                    }


                                }

                                @Override
                                public void onTabUnselected(TabLayout.Tab tab) {

                                }

                                @Override
                                public void onTabReselected(TabLayout.Tab tab) {

                                }
                            });

                            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));

                        }
                    });

                }else{
                    final String myResponse = response.body().string();
                    Notes.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("xml :","failed");
                            Toast.makeText(getApplicationContext(),
                                    "Echec de la récupération des notes",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });








    }
}
