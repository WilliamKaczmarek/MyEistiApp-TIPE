package com.example.myeisti.classe;

import androidx.annotation.NonNull;


public class Slot {
    private String slotId;
    private String debut;
    private String fin;
    private String labelMatiere;
    private String teacherId;
    private String labelGroup;
    private String groupId;
    private String roomId;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getDebut() {
        return debut;
    }

    public void setDebut(String debut) {
        this.debut = debut;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public String getLabelMatiere() {
        return labelMatiere;
    }

    public void setLabelMatiere(String labelMatiere) {
        this.labelMatiere = labelMatiere;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getLabelGroup() {
        return labelGroup;
    }

    public void setLabelGroup(String labelGroup) {
        this.labelGroup = labelGroup;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Slot(String slotId, String oldDebut, String oldFin, String labelMatiere, String teacherId, String labelGroup, String groupId,String roomId) {
        int int_heure;
        String str_heure;
        this.slotId = slotId;
        this.labelMatiere = labelMatiere;
        this.teacherId = teacherId;
        this.labelGroup = labelGroup;
        this.groupId = groupId;
        this.roomId = roomId;

        //Ajout des deux heure de décalage de la France pour l'heure de début et de fin
        int_heure = Integer.parseInt(oldDebut.substring(11,13)) + 2;
        str_heure = int_heure<10?"0"+int_heure:String.valueOf(int_heure);
        oldDebut = str_heure+"h"+oldDebut.substring(14,16);
            this.debut = oldDebut;
        int_heure = Integer.parseInt(oldFin.substring(11,13)) + 2;
        str_heure = int_heure<10?"0"+int_heure:String.valueOf(int_heure);
        oldFin = str_heure+"h"+oldFin.substring(14,16);
            this.fin = oldFin;
    }

    @NonNull
    @Override
    public String toString() {
        return debut+"-"+fin+" "+labelMatiere+" - "+labelGroup;
    }
}
