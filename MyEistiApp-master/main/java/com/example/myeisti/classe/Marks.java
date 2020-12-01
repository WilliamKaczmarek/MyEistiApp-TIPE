package com.example.myeisti.classe;


import android.util.Log;

public class Marks {
    private String semestreId;
    private String ueId;
    private String label;
    private String mark;
    private String coef;
    private String testName;


    public String getSemestreId() {
        return semestreId;
    }

    public String getUeId() {
        return ueId;
    }

    public String getLabel() {
        return label;
    }

    public String getMark() {
        return mark;
    }

    public String getCoef() {
        return coef;
    }

    public String getTestName() {
        return testName;
    }

    //Constructeur de l'objet
    public Marks(String semestreId, String ueId, String label, String mark, String coef, String testName) {
        this.semestreId = semestreId;
        this.ueId = ueId;
        this.label = label;
        this.mark = mark;
        this.coef = coef;
        this.testName = testName;
    }

    //fabrication du tableau
    public static Marks[] NoteToTabMark(String xml){
        Marks[] tab;
        int compt=0,res=0;
        String decoupage=xml;
        //Comptage du nombre de note
        while (res!=-1){
            res = decoupage.indexOf("<marks>");
            if(res!=-1){
                compt++;
            }
            decoupage = decoupage.substring(decoupage.indexOf("<marks>")+7);
        }
        if(compt==0){return (null);}
        tab = new Marks[compt];
        //Là on decoupe et on fabrique l'objet à chaque case du tableau
        decoupage=xml;
        for (int i = 0; i < compt ; i++) {
            tab[i] = new Marks(
                    xml.substring(xml.indexOf("<semestreId>")+12,xml.indexOf("</semestreId>")),
                    xml.substring(xml.indexOf("<ueId>")+6,xml.indexOf("</ueId>")),
                    xml.substring(xml.indexOf("<label>")+7,xml.indexOf("</label>")),
                    xml.substring(xml.indexOf("<mark>")+6,xml.indexOf("</mark>")),
                    xml.substring(xml.indexOf("<coefficient>")+13,xml.indexOf("</coefficient>")),
                    xml.substring(xml.indexOf("<testName>")+10,xml.indexOf("</testName>"))
                    );
            Log.d("myTag",tab[i].getMark()+":"+i);
            //Donc là on tronque xml avec +1 sinon on tombe à chaque fois sur la meme chose sans le +1
            xml = xml.substring(xml.indexOf("</marks>")+1);
        }
        return tab;
    }

    public static String[] TabMarkToSemestre(Marks[] tab){
        if (tab==null){Log.d("TabMarkToSemestre","Echec");return (null);}
        String[] tab2= new String[tab.length];
        for (int i = 0; i < tab.length; i++) {
            tab2[i]=tab[i].getSemestreId();
        }
        return (tab2);
    }

    public static String[] TabMarkToUe(Marks[] tab){
        if (tab==null){return (null);}
        String[] tab2= new String[tab.length];
        for (int i = 0; i < tab.length; i++) {
            tab2[i]=tab[i].getUeId();
        }
        return (tab2);
    }

    public static String[] TabMarkToLabel(Marks[] tab){
        if (tab==null){return (null);}
        String[] tab2= new String[tab.length];
        for (int i = 0; i < tab.length; i++) {
            tab2[i]=tab[i].getLabel();
        }
        return (tab2);
    }

    public static String[] TabMarkToMark(Marks[] tab){
        if (tab==null){return (null);}
        String[] tab2= new String[tab.length];
        for (int i = 0; i < tab.length; i++) {
            tab2[i]=tab[i].getMark();
        }
        return (tab2);
    }

    public static String[] TabMarkToCoef(Marks[] tab){
        if (tab==null){return (null);}
        String[] tab2= new String[tab.length];
        for (int i = 0; i < tab.length; i++) {
            tab2[i]=tab[i].getCoef();
        }
        return (tab2);
    }

    public static String[] TabMarkToName(Marks[] tab){
        if (tab==null){return (null);}
        String[] tab2= new String[tab.length];
        for (int i = 0; i < tab.length; i++) {
            tab2[i]=tab[i].getTestName();
        }
        return (tab2);
    }
}
