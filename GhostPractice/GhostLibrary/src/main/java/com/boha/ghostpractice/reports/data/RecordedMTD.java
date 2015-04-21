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
public class RecordedMTD implements Serializable {
    
  
	private static final long serialVersionUID = 1L;
    
    double unbilled;
    double invoicedDebits;
    double total;
    double estimatedTarget;
    double achieved;

    public double getAchieved() {
        return achieved;
    }

    public void setAchieved(double achieved) {
        this.achieved = achieved;
    }

    public double getEstimatedTarget() {
        return estimatedTarget;
    }

    public void setEstimatedTarget(double estimatedTarget) {
        this.estimatedTarget = estimatedTarget;
    }

    public double getInvoicedDebits() {
        return invoicedDebits;
    }

    public void setInvoicedDebits(double invoicedDebits) {
        this.invoicedDebits = invoicedDebits;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getUnbilled() {
        return unbilled;
    }

    public void setUnbilled(double unbilled) {
        this.unbilled = unbilled;
    }
    
}
