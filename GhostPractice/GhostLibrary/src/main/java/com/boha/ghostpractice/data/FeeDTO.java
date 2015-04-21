/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.ghostpractice.data;

/**
 *
 * @author Aubrey Malabie
 */
public class FeeDTO {

    String matterID;
    int duration;
    double amount;
    String narration;
    String tariffCodeID;
    long date;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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
