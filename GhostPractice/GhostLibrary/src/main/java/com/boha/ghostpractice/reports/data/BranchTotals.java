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
public class BranchTotals implements Serializable {
    
  
	private static final long serialVersionUID = 1L;
 
    RecordedMTD recordedMTD;
    double InvoicedMTDTotal;
    RecordedYTD recordedYTD;
    MatterActivity matterActivity;
    MatterBalances matterBalances;

    public double getInvoicedMTD() {
        return InvoicedMTDTotal;
    }

    public void setInvoicedMTD(double InvoicedMTDTotal) {
        this.InvoicedMTDTotal = InvoicedMTDTotal;
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

    public RecordedMTD getRecordedMTD() {
        return recordedMTD;
    }

    public void setRecordedMTD(RecordedMTD recordedMTD) {
        this.recordedMTD = recordedMTD;
    }

    public RecordedYTD getRecordedYTD() {
        return recordedYTD;
    }

    public void setRecordedYTD(RecordedYTD recordedYTD) {
        this.recordedYTD = recordedYTD;
    }
    
    
    
}
