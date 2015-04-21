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
public class Branch implements Serializable {
    
  
	private static final long serialVersionUID = 1L;
	String name;
    List<Owner> owners;
    BranchTotals branchTotals;
    BusinessStatus businessStatus;
    TrustStatus trustStatus;
    
    public BranchTotals getBranchTotals() {
        return branchTotals;
    }

    public void setBranchTotals(BranchTotals branchTotals) {
        this.branchTotals = branchTotals;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Owner> getOwners() {
        return owners;
    }

    public void setOwners(List<Owner> owners) {
        this.owners = owners;
    }

    public BusinessStatus getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(BusinessStatus businessStatus) {
        this.businessStatus = businessStatus;
    }

    public TrustStatus getTrustStatus() {
        return trustStatus;
    }

    public void setTrustStatus(TrustStatus trustStatus) {
        this.trustStatus = trustStatus;
    }
    
    
    
    
}
