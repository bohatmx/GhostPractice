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
public class UserDTO implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int userID, gpID, numberApps, statusFlag;
    private String userName, email, cellphone, deviceID;
    private long dateRegistered;
    private CompanyDTO company;

  

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getEmail() {
        return email;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public int getGpID() {
        return gpID;
    }

    public void setGpID(int gpID) {
        this.gpID = gpID;
    }

    public int getNumberApps() {
        return numberApps;
    }

    public int getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(int statusFlag) {
        this.statusFlag = statusFlag;
    }

    public void setNumberApps(int numberApps) {
        this.numberApps = numberApps;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    
    
}
