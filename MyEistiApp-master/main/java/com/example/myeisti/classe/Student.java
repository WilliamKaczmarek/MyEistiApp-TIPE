package com.example.myeisti.classe;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;


public class Student {

    private String numero;
    private String nom;
    private String prenom;
    private boolean absent;
    private String mail;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String id) {
        this.numero = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public boolean isAbsent() {
        return absent;
    }

    public void setAbsent(boolean absent) {
        this.absent = absent;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Student(String numero, String nom, String prenom, boolean absent, String mail) {
        this.numero = numero;
        this.nom = nom;
        this.prenom = prenom;
        this.absent = absent;
        this.mail = mail;
    }

    public static int indexId(Student[] studentTab,String id){
        for (int i = 0; i < studentTab.length; i++) {
            if(studentTab[i].getNumero().equals(id)){return i;}
        }
        return (-1);
    }

    public static Student[] JSONToTabStudent(JSONObject reponse){
        Student[] studentTab;
        try {
            String anneeUser;
            JSONArray jsonUser = reponse.getJSONArray("users");
            JSONObject utilisateur;
            int nbEleve=0;
            Student eleveTest;
            List<Student> studentsList = new ArrayList<>();

            for (int i = 0; i < jsonUser.length(); i++) {
                anneeUser = jsonUser.getJSONObject(i).getString("creationDate").substring(0,4);

                Log.d("anneeUser --------",anneeUser);
                if (anneeUser.equals("2018")){
                    utilisateur = jsonUser.getJSONObject(i);
                    nbEleve++;
                    eleveTest = new Student(
                            utilisateur.getString("id"),
                            utilisateur.getString("lastname"),
                            utilisateur.getString("firstname"),
                            true,
                            utilisateur.getString("email")
                    );
                    studentsList.add(eleveTest);
                    Log.d("Student","Elève ajouté ! Eleve :"+eleveTest.getPrenom());
                }
            }

            studentTab = new Student[studentsList.size()];
            studentsList.toArray(studentTab);

        } catch (JSONException e) {
            Log.d("Erreur", "Récupération des élèves d'un JSONArray impossible");
            studentTab = null;
        }
        return (studentTab);
    }
}
