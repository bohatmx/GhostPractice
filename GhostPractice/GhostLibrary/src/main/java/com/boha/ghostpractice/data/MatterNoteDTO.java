/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.ghostpractice.data;


/**
 *
 * @author Aubrey Malabie
 */
public class MatterNoteDTO {

    String matterID;
    String narration;
    long date;
    String tariffCodeID;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMatterID() {
        return matterID;
    }

    public void setMatterID(String matterID) {
        this.matterID = matterID;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getTariffCodeID() {
        return tariffCodeID;
    }

    public void setTariffCodeID(String tariffCodeID) {
        this.tariffCodeID = tariffCodeID;
    }
    
}
