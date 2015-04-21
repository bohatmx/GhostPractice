/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.ghostpractice.reports.data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Aubrey Malabie
 */
public class TrustStatus implements Serializable {
    
  
	private static final long serialVersionUID = 1L;
    
    double trustCreditors;
    double banksTotal;
    List<Bank> banks;
    double investments;


    public List<Bank> getBanks() {
        return banks;
    }

    public void setBanks(List<Bank> banks) {
        this.banks = banks;
    }

    public double getBanksTotal() {
        return banksTotal;
    }

    public void setBanksTotal(double banksTotal) {
        this.banksTotal = banksTotal;
    }

    public double getInvestments() {
        return investments;
    }

    public void setInvestments(double investments) {
        this.investments = investments;
    }

    public double getTrustCreditors() {
        return trustCreditors;
    }

    public void setTrustCreditors(double trustCreditors) {
        this.trustCreditors = trustCreditors;
    }

    
    
    
}
