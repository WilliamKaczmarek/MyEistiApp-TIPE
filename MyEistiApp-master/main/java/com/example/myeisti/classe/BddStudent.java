package com.example.myeisti.classe;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BddStudent {

    private String studentId;
    private String historique;
    private String token;
    private String solde;

    public BddStudent(String studentId, String historique, String token, String solde) {
        this.studentId = studentId;
        this.historique = historique;
        this.token = token;
        this.solde = solde;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getHistorique() {
        return historique;
    }

    public String getToken() {
        return token;
    }

    public String getSolde() {
        return solde;
    }

    public JSONArray convertToJSONArray(){
        List laListe = new ArrayList();
        laListe.add(studentId);
        laListe.add(historique);
        laListe.add(token);
        laListe.add(solde);
        return (new JSONArray(laListe));
    }

    /**
     * La France a un décalage horaire de 2h, ici est traité
       le décalage en modifiant, le jour, le mois ou l'année si nécessaire
     * @return La date avec 2h de plus, au format de la bdd (sans 'C' et ';')
     */
    public static String dateActuelle(){
        String jour,mois,annee,today,tomorrow;
        int day,month,year;
        boolean bissextile;
        today = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        day = Integer.parseInt((new SimpleDateFormat("dd")).format(new Date()));
        month = Integer.parseInt((new SimpleDateFormat("MM")).format(new Date()));
        year = Integer.parseInt((new SimpleDateFormat("yyyy")).format(new Date()));
        //Calculons si l'année est bissextile
        if(((year) % 4 ==0 && (year) % 100 != 0)||((year+2000)%400 == 0)){
            bissextile = true;
        }else {
            bissextile = false;
        }
        if(month==1||month==3||month==5||month==7||month==8||month==10){//mois de 31 jours
            if(day<31){
                day += 1;
            }else{
                day = 1;
                month += 1;
            }
        }else if(month==4||month==6||month==9||month==11){
            if(day<30){
                day += 1;
            }else{
                day = 1;
                month += 1;
            }
        }else if(month==2){
            if((bissextile) && (day==29)){
                day = 1;
                month += 1;
            }else if((!bissextile) && (day==28)){
                day = 1;
                month += 1;
            }else{
                day += 1;
            }
        }else if(month==12){
            if(day==31){
                year += 1;
                day = 1;
                month = 1;
            }else{
                day += 1;
            }
        }
        //Ajout du 0 devant si les nombre sont inférieur à 10
        jour = day<10?"0"+day:String.valueOf(day);
        mois = month<10?"0"+month:String.valueOf(month);
        annee = String.valueOf(year);

        return (today+"%"+annee+"-"+mois+"-"+jour);
    }
}
