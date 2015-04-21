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
public class MatterAnalysisByOwnerReport implements Serializable {
    
  
	private static final long serialVersionUID = 1L;
    
    List<Branch> branches;
    PracticeTotals practiceTotals;

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    public PracticeTotals getPracticeTotals() {
        return practiceTotals;
    }

    public void setPracticeTotals(PracticeTotals practiceTotals) {
        this.practiceTotals = practiceTotals;
    }
    
    
}
