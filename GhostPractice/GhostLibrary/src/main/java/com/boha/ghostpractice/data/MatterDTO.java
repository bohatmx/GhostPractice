/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.ghostpractice.data;

import java.io.Serializable;


/**
 *
 * @author Aubrey Malabie
 */
public class MatterDTO implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double businessBalance;
    int clientNumber;
    double currentBalance;
    String matterID;
    double investmentTrustBalance;
    String legacyAccount;
    String matterName;
    String ourReference;
    double pendingDisbursementBalance;
    double reserveTrust;
    double surchargeFactor;
    double trustBalance;
    double unbilledBalance;
    String yourReference;

    
    public double getBusinessBalance() {
        return businessBalance;
    }

    public void setBusinessBalance(double businessBalance) {
        this.businessBalance = businessBalance;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(int clientNumber) {
        this.clientNumber = clientNumber;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public double getInvestmentTrustBalance() {
        return investmentTrustBalance;
    }

    public void setInvestmentTrustBalance(double investmentTrustBalance) {
        this.investmentTrustBalance = investmentTrustBalance;
    }

    public String getLegacyAccount() {
        return legacyAccount;
    }

    public void setLegacyAccount(String legacyAccount) {
        this.legacyAccount = legacyAccount;
    }

    public String getMatterID() {
        return matterID;
    }

    public void setMatterID(String matterID) {
        this.matterID = matterID;
    }

    public String getMatterName() {
        return matterName;
    }

    public void setMatterName(String matterName) {
        this.matterName = matterName;
    }

    public String getOurReference() {
        return ourReference;
    }

    public void setOurReference(String ourReference) {
        this.ourReference = ourReference;
    }

    public double getPendingDisbursementBalance() {
        return pendingDisbursementBalance;
    }

    public void setPendingDisbursementBalance(double pendingDisbursementBalance) {
        this.pendingDisbursementBalance = pendingDisbursementBalance;
    }

    public double getReserveTrust() {
        return reserveTrust;
    }

    public void setReserveTrust(double reserveTrust) {
        this.reserveTrust = reserveTrust;
    }

  

    public double getSurchargeFactor() {
        return surchargeFactor;
    }

    public void setSurchargeFactor(double surchargeFactor) {
        this.surchargeFactor = surchargeFactor;
    }

    public double getTrustBalance() {
        return trustBalance;
    }

    public void setTrustBalance(double trustBalance) {
        this.trustBalance = trustBalance;
    }

    public double getUnbilledBalance() {
        return unbilledBalance;
    }

    public void setUnbilledBalance(double unbilledBalance) {
        this.unbilledBalance = unbilledBalance;
    }

    public String getYourReference() {
        return yourReference;
    }

    public void setYourReference(String yourReference) {
        this.yourReference = yourReference;
    }
}
