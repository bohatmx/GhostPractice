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
public class RecordedYTD implements Serializable {
    
  
	private static final long serialVersionUID = 1L;
    
    double unbilled;
    double invoiced;
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
        return invoiced;
    }

    public void setInvoicedDebits(double invoiced) {
        this.invoiced = invoiced;
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
