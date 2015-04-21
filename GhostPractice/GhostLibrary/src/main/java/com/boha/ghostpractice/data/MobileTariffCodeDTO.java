/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.ghostpractice.data;


/**
 *
 * @author Aubrey Malabie
 */
public class MobileTariffCodeDTO {

    double amount;
    String id;
    String name;
    String narration;
    boolean surchargeApplies;
    int tariffType;
    boolean timeBasedCode;
    double units;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public boolean isSurchargeApplies() {
        return surchargeApplies;
    }

    public void setSurchargeApplies(boolean nurchargeApplies) {
        this.surchargeApplies = nurchargeApplies;
    }

    public int getTariffType() {
        return tariffType;
    }

    public void setTariffType(int tariffType) {
        this.tariffType = tariffType;
    }

    public boolean isTimeBasedCode() {
        return timeBasedCode;
    }

    public void setTimeBasedCode(boolean timeBasedCode) {
        this.timeBasedCode = timeBasedCode;
    }

    public double getUnits() {
        return units;
    }

    public void setUnits(double units) {
        this.units = units;
    }
    
}
