/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.ghostpractice.data;

import java.io.Serializable;

/**
 *
 * @author Aubrey Malabie
 */
public class MatterSearchResultDTO implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int matterID;
    String matterName;
    String matterLegacyAccount;
    String clientName;
    String currentOwner;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner(String currentOwner) {
        this.currentOwner = currentOwner;
    }

    public int getMatterID() {
        return matterID;
    }

    public void setMatterID(int matterID) {
        this.matterID = matterID;
    }

    public String getMatterLegacyAccount() {
        return matterLegacyAccount;
    }

    public void setMatterLegacyAccount(String matterLegacyAccount) {
        this.matterLegacyAccount = matterLegacyAccount;
    }

   

    public String getMatterName() {
        return matterName;
    }

    public void setMatterName(String matterName) {
        this.matterName = matterName;
    }
    
    
}
