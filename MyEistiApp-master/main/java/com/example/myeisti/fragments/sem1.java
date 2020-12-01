package com.example.myeisti.fragments;


import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myeisti.R;
import com.example.myeisti.classe.Marks;


/**
 * A simple {@link Fragment} subclass.
 */
public class sem1 extends Fragment{

    private TextView tv;
    private LinearLayout uneNote;
    private String[] Semestre,Ue,Label,Mark,Coef,Name;
    private Marks preMark;
    private char numSem;
    private int compt;

    public sem1() {
        // Required empty public constructor
    }


    public static sem1 newInstance(Marks[] tab){
        sem1 frag = new sem1();
        Bundle args = new Bundle();
        args.putStringArray("Semestre",Marks.TabMarkToSemestre(tab));
        args.putStringArray("Ue",Marks.TabMarkToUe(tab));
        args.putStringArray("Label",Marks.TabMarkToLabel(tab));
        args.putStringArray("Mark",Marks.TabMarkToMark(tab));
        args.putStringArray("Coef",Marks.TabMarkToCoef(tab));
        args.putStringArray("Name",Marks.TabMarkToName(tab));
        frag.setArguments(args);
        return (frag);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sem1, container, false);

        //Paramètre de "position" pour le nom de la note
        LinearLayout.LayoutParams title = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        title.weight = 0.3f;

        //Paramètre de "position" pour la note
        LinearLayout.LayoutParams note = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        note.weight = 0.7f;

        //Paramètre de "position" pour le separateur
        LinearLayout.LayoutParams sepa = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 25);
        sepa.topMargin=20;

        //Paramètre de "position" pour le nom du module
        LinearLayout.LayoutParams module = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        module.topMargin=10;
        module.bottomMargin=10;

        // Recuperation des notes
        Semestre = getArguments().getStringArray("Semestre");
        Ue = getArguments().getStringArray("Ue");
        Label = getArguments().getStringArray("Label");
        Mark = getArguments().getStringArray("Mark");
        Coef = getArguments().getStringArray("Coef");
        Name = getArguments().getStringArray("Name");

        // Create a LinearLayout element
        LinearLayout cadreNote = (LinearLayout) v.findViewById(R.id.cadreNote);

        preMark = null; compt = 0;
        for (int i = 0; i < Semestre.length; i++) {
            numSem=Semestre[i].charAt(Semestre[i].length()-1);
            if(numSem=='1'||numSem=='3'||numSem=='5'||numSem=='7'||numSem=='9'){
                if(preMark==null){
                    tv = new TextView(container.getContext());
                    tv.setLayoutParams(sepa);
                    tv.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rectangle_separator, null));
                    cadreNote.addView(tv);
                    tv = new TextView(container.getContext());
                    tv.setLayoutParams(module);
                    tv.setText(Label[i]);
                    tv.setTextSize(25);
                    tv.setTextColor(Color.BLACK);
                    tv.setGravity(Gravity.CENTER);
                    cadreNote.addView(tv);

                }else if(!preMark.getUeId().equals(Ue[i])){
                    Log.d("'"+preMark.getUeId()+"'","'"+Ue[i]+"'");
                    tv = new TextView(container.getContext());
                    tv.setLayoutParams(sepa);
                    tv.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rectangle_separator, null));
                    cadreNote.addView(tv);
                    tv = new TextView(container.getContext());
                    tv.setLayoutParams(module);
                    tv.setText(Label[i]);
                    tv.setTextSize(25);
                    tv.setTextColor(Color.BLACK);
                    tv.setGravity(Gravity.CENTER);
                    cadreNote.addView(tv);
                }else if(!preMark.getLabel().equals(Label[i])){
                    tv = new TextView(container.getContext());
                    tv.setLayoutParams(module);
                    tv.setText(Label[i]);
                    tv.setTextSize(25);
                    tv.setTextColor(Color.BLACK);
                    tv.setGravity(Gravity.CENTER);
                    cadreNote.addView(tv);
                }

                uneNote= new LinearLayout(container.getContext());
                uneNote.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                uneNote.setOrientation(LinearLayout.HORIZONTAL);

                TextView titre = new TextView(container.getContext());
                titre.setLayoutParams(title);
                titre.setText(Name[i]);
                titre.setGravity(Gravity.LEFT);
                titre.setTextSize(20);
                uneNote.addView(titre);
                titre = new TextView(container.getContext());
                titre.setLayoutParams(note);
                titre.setText(Mark[i]+" ("+Coef[i]+")");
                titre.setGravity(Gravity.CENTER);
                titre.setTextSize(20);
                uneNote.addView(titre);
                cadreNote.addView(uneNote);
                compt++;
                preMark = new Marks(Semestre[i],Ue[i],Label[i],Mark[i],Coef[i],Name[i]);
            }
        }
        if(compt==0){
            tv = new TextView(container.getContext());
            tv.setLayoutParams(module);
            tv.setText("Aucune note disponible pour ce semestre.");
            tv.setTextSize(25);
            tv.setTextColor(Color.BLACK);
            tv.setGravity(Gravity.CENTER);
            cadreNote.addView(tv);
        }

        return v;
    }

}
