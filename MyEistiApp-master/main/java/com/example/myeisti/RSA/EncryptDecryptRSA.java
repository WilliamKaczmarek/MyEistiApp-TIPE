package com.example.myeisti.RSA;

import java.math.BigInteger;

public class EncryptDecryptRSA {

    private static final BigInteger
            //Valeurs clé pour le cryptage RSA
            p = BigInteger.valueOf(433),
            q = BigInteger.valueOf(787),
            n = BigInteger.valueOf(340771),
            phin = BigInteger.valueOf(339552),
            e = BigInteger.valueOf(797),
            d = BigInteger.valueOf(260309);

    /**
     * Fonction qui crypte les données avec le système RSA
     * @param msg Message à crypter
     * @return Message crypté
     */
    public static String Encrypt(String msg){
        String str="",code,temp,decoupe;
        String[] tab;
        BigInteger b1;
        char c;
        /*
         *  Ici on transforme les caractères en nombre ascii à 3 chiffres
         *  Si il y a moins que 3 chiffres on rajoute des 0 devant
         */
        for (int i = 0; i < msg.length(); i++) {
            c = msg.charAt(i);
            int ascii = (int)c;
            code = String.valueOf(ascii);
            while(code.length()<3){
                code = "0"+code;
            }
            str += code;
        }

        /*
         *  On rajoute des 0 afin que la taille soit divisible en bloc de 4 chiffres
         *  Cette étape permet de fusionner les nombres ascii en bloc de 4 pour
            ne pas que l'on puisse retrouver la même chaine cryptée pour un même
            caractère
         */
        if(str.length()%4==1){
            str = str.substring(0,str.length()-1)+"000"+str.substring(str.length()-1);
        }else if(str.length()%4==2){
            str = str.substring(0,str.length()-2)+"00"+str.substring(str.length()-2);
        }else if(str.length()%4==3){
            str = str.substring(0,str.length()-3)+"0"+str.substring(str.length()-3);
        }
        int nbBloc = str.length()/4;
        tab = new String[nbBloc];
        decoupe=str;
        /*
            Ici on crypte chaque bloc de 4 dans un tableau grâce à la
            clé public (e,n)
         */
        for (int i = 0; i < nbBloc; i++) {
            temp = decoupe.substring(0,4);
            b1 = new BigInteger(temp);
            b1 = b1.modPow(e,n);
            tab[i]=b1.toString();
            decoupe = decoupe.substring(4);
        }
        /*
         *  On sait que la longueur des caractères cryptés ne peuvent pas dépasser phin
            on va donc transformer notre tableau crypté en bloc de la taille du nombre
            de chiffre qu'il y a dans phin
         */
        str="";
        for (int i = 0; i < tab.length ; i++) {
            while (tab[i].length()<6){
                tab[i] = "0"+tab[i];
            }
            str += tab[i];//On construit une chaîne avec les bloc de 6chiffres
        }
        return str; //Renvoi du message crypté
    }


    /**
     * Fonction qui décrypte les données avec le système RSA
     * @param msg Message à décrypter
     * @return Message décrypté
     */
    public static String Decrypt(String msg){
        String str="",code,decoupe;
        String[] tab;
        BigInteger b1;

        decoupe=msg;
        /*
         * Ici on transforme le message crypté en bloc de 6
         */
        tab = new String[msg.length()/6];
        for (int i = 0; i < msg.length()/6; i++) {
            tab[i] = decoupe.substring(0,6);
            decoupe = decoupe.substring(6);
        }

        /*
         * Décryptage des blocs qui seront ensuite transformés en bloc de 4
           car c'est le processus qui a été fait pour le cryptage afin d'avoir
           une sécurité en plus pour que l'on ne puisse pas utiliser l'analyse
           de fréquence
         */
        for (int i = 0; i < msg.length()/6; i++) {
            b1 = new BigInteger(tab[i]);
            b1 = b1.modPow(d,n);
            tab[i]=b1.toString();
            while (tab[i].length()<4){
                tab[i] = "0"+tab[i];
            }
            str += tab[i];
        }
        code="";
        /*
         * Découpage en bloc de 3 puis transformation du code ascii en
           caractère
         */
        while (str.length()>3){
            if(Integer.parseInt(str.substring(0,3))!=0){
                char c = (char) Integer.parseInt(str.substring(0,3));
                code += c;
            }
            str = str.substring(3);
        }
        char c = (char) Integer.parseInt(str);
        code += c;
        return code; //Revoi du message décrypté
    }
}
