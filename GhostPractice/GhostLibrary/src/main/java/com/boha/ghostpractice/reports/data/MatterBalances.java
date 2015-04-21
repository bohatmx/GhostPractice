/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.ghostpractice.reports.data;

import java.io.Serializable;

/**
 *
 * @author Aubrey Malabie
 */
public class MatterBalances implements Serializable {
    
  
	private static final long serialVersionUID = 1L;
    
    
    double unbilled;
    double pendingDisbursements;
    double investment;
    double trust;
    double business;

    public double getBusiness() {
        return business;
    }

    public void setBusiness(double business) {
        this.business = business;
    }

    public double getInvestment() {
        return investment;
    }

    public void setInvestment(double investment) {
        this.investment = investment;
    }

    public double getPendingDisbursements() {
        return pendingDisbursements;
    }

    public void setPendingDisbursements(double pendingDisbursements) {
        this.pendingDisbursements = pendingDisbursements;
    }

    public double getTrust() {
        return trust;
    }

    public void setTrust(double trust) {
        this.trust = trust;
    }

    public double getUnbilled() {
        return unbilled;
    }

    public void setUnbilled(double unbilled) {
        this.unbilled = unbilled;
    }
    
    
    
}
