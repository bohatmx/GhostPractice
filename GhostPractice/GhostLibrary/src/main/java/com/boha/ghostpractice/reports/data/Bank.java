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
public class Bank implements Serializable {
    
  
	private static final long serialVersionUID = 1L;
    
    String name;
    double balance;
    String dateReconciled;
    double reconciledAmount;
    double receiptsForPeriod;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getDateReconciled() {
        return dateReconciled;
    }

    public void setDateReconciled(String dateReconciled) {
        this.dateReconciled = dateReconciled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getReceiptsForPeriod() {
        return receiptsForPeriod;
    }

    public void setReceiptsForPeriod(double receiptsForPeriod) {
        this.receiptsForPeriod = receiptsForPeriod;
    }

    public double getReconciledAmount() {
        return reconciledAmount;
    }

    public void setReconciledAmount(double reconciledAmount) {
        this.reconciledAmount = reconciledAmount;
    }
    
    
    
    
}
