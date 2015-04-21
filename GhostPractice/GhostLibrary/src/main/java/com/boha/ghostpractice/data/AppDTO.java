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
public class AppDTO implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int appID, numberUsers;
    private String appName, version;
    private long dateRegistered;
    
  

    public int getAppID() {
        return appID;
    }

    public int getNumberUsers() {
        return numberUsers;
    }

    public void setNumberUsers(int numberUsers) {
        this.numberUsers = numberUsers;
    }

    public void setAppID(int appID) {
        this.appID = appID;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
    
    
}
