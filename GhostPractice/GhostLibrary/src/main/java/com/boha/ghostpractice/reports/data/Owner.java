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
public class Owner implements Serializable {
    
  
	private static final long serialVersionUID = 1L;
    
    String name;
    String UserCode;
    RecordedMTD recordedMTD;
    double invoicedMTDTotal;
    RecordedYTD recordedYTD;
    MatterActivity matterActivity;
    MatterBalances matterBalances;
    
    public double getInvoicedMTD() {
        return invoicedMTDTotal;
    }

    public void setInvoicedMTD(double invoicedMTDTotal) {
        this.invoicedMTDTotal = invoicedMTDTotal;
    }

    public RecordedMTD getRecordedMTD() {
        return recordedMTD;
    }

    public void setRecordedMTD(RecordedMTD recordedMTD) {
        this.recordedMTD = recordedMTD;
    }

    
    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String UserCode) {
        this.UserCode = UserCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MatterActivity getMatterActivity() {
        return matterActivity;
    }

    public void setMatterActivity(MatterActivity matterActivity) {
        this.matterActivity = matterActivity;
    }

    public MatterBalances getMatterBalances() {
        return matterBalances;
    }

    public void setMatterBalances(MatterBalances matterBalances) {
        this.matterBalances = matterBalances;
    }

    public RecordedYTD getRecordedYTD() {
        return recordedYTD;
    }

    public void setRecordedYTD(RecordedYTD recordedYTD) {
        this.recordedYTD = recordedYTD;
    }
    
    
    
}
