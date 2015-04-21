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
public class FinancialStatusReport implements Serializable {
    
  
	private static final long serialVersionUID = 1L;
    
    List<Branch> branches;

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }
    
}
