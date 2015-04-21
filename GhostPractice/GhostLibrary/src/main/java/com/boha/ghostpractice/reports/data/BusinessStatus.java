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
public class BusinessStatus implements Serializable {
    
  
	private static final long serialVersionUID = 1L;
    
    double businessDebtors;
    double businessCreditors;
    double banksTotal;
    List<Bank> banks;
    double vat;
    double unbilled;
    double pendingDisbursements;
    double availableForTransfer;

    public double getAvailableForTransfer() {
        return availableForTransfer;
    }

    public void setAvailableForTransfer(double availableForTransfer) {
        this.availableForTransfer = availableForTransfer;
    }

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

    public double getBusinessCreditors() {
        return businessCreditors;
    }

    public void setBusinessCreditors(double businessCreditors) {
        this.businessCreditors = businessCreditors;
    }

    public double getBusinessDebtors() {
        return businessDebtors;
    }

    public void setBusinessDebtors(double businessDebtors) {
        this.businessDebtors = businessDebtors;
    }

    public double getPendingDisbursements() {
        return pendingDisbursements;
    }

    public void setPendingDisbursements(double pendingDisbursements) {
        this.pendingDisbursements = pendingDisbursements;
    }

    public double getUnbilled() {
        return unbilled;
    }

    public void setUnbilled(double unbilled) {
        this.unbilled = unbilled;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }
    
    
    
}
