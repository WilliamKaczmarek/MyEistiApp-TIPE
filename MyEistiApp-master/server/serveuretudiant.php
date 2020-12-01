<?php
include "fonctions.php";

if(isset($_REQUEST["operation"])){

    if($_REQUEST["operation"]=="id"){
        /*
            * Si id alors on récupère juste le contenu de la ligne
            * Doit contenir l'id dans $_GET["id"]
        */
         try {
            print("id%");
            $cnx = connexionPDO();
            $req = $cnx->prepare('select * from etudiant where idEtudiant= ?');
            $req->execute(array($_GET["id"]));
            if($ligne = $req->fetch(PDO::FETCH_ASSOC)){
                print(json_encode($ligne));
            }
            $req->closeCursor();
        } catch (PDOException $e) {
            print "Erreur !%".$e->getMessage();
            die();        
        }
    }else if($_REQUEST["operation"]=="addHistorique"){
        /*
            * Si addHistorique alors on ajouter à historique une ligne
            * Doit contenir la ligne à ajouter dans $_GET["add"] finissant par ; (separateur)
            * Doit contenir l'id où ajouter la ligne dans $_GET["id"]
            * Première requête qui récupère l'historique
            * Seconde requête qui met à jour l'historique
        */
        try {
            print("addHistorique%");
            $cnx = connexionPDO();
            $req = $cnx->prepare('select idEtudiant, historique from etudiant where idEtudiant = ?');
            $req->execute(array($_GET["id"]));
            if($ligne = $req->fetch()){
                $historique= $ligne['historique'];
            }
            $req->closeCursor();
            if($ligne['idEtudiant']==NULL){
                $req= $cnx->prepare('INSERT INTO etudiant (`idEtudiant`) VALUES (\''.$_GET["id"].'\')');
                $req->execute();
                $req->closeCursor();
            }
            if(substr_count($historique, ';')==50){ //Lorsqu'il y a 50 actions d'enregistrées, on enlève la moins récente
                $historique = substr($historique,0,strrpos($historique,";",-2)+1);
            }
            $historique = $_GET["add"].$historique;
            $req = $cnx->prepare('UPDATE etudiant SET historique = ? WHERE idEtudiant = ?');
            $req->execute(array($historique,$_GET["id"]));
            print("Historique Ajouté !");
            $req->closeCursor();
        } catch (PDOException $e) {
            print "Erreur !".$e->getMessage();
            die();
        }
    }else if($_REQUEST["operation"]=="seeHistorique"){
        /*
            * Si seeHistorique on doit renvoyer l'historique
            * Doit contenir l'id correspondant à l'historique que
            * l'on souhaite voir
        */
         try {
            print("seeHistorique%");
            $cnx = connexionPDO();
            $req = $cnx->prepare('select historique from etudiant where idEtudiant= ?');
            $req->execute(array($_GET["id"]));
            if($ligne = $req->fetch()){
                print($ligne['historique']);
            }
            $req->closeCursor();
        } catch (PDOException $e) {
            print "Erreur !%".$e->getMessage();
            die();        
        }
    }else if($_REQUEST["operation"]=="seeSolde"){
        /*
            * Si seeSolde on doit renvoyer le solde crypté
            * Doit contenir l'id correspondant au solde que
            * l'on souhaite voir
        */
         try {
            print("seeSolde%");
            $cnx = connexionPDO();
            $req = $cnx->prepare('select solde from etudiant where idEtudiant= ?');
            $req->execute(array($_GET["id"]));
            if($ligne = $req->fetch()){
                print($ligne['solde']);
            }
            $req->closeCursor();
        } catch (PDOException $e) {
            print "Erreur !%".$e->getMessage();
            die();        
        }
    }
}

?>