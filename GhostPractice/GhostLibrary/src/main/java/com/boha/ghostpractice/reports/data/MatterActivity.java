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
public class MatterActivity implements Serializable {
    
  
	private static final long serialVersionUID = 1L;
    
    int active;
    int workedOn;
    int newWork;
    int deactivated;
    int noActivity;
    String noActivityDuration;

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getDeactivated() {
        return deactivated;
    }

    public void setDeactivated(int deactivated) {
        this.deactivated = deactivated;
    }

    public int getNewWork() {
        return newWork;
    }

    public void setNewWork(int newWork) {
        this.newWork = newWork;
    }

    public int getNoActivity() {
        return noActivity;
    }

    public void setNoActivity(int noActivity) {
        this.noActivity = noActivity;
    }

    public String getNoActivityDuration() {
        return noActivityDuration;
    }

    public void setNoActivityDuration(String noActivityDuration) {
        this.noActivityDuration = noActivityDuration;
    }

    public int getWorkedOn() {
        return workedOn;
    }

    public void setWorkedOn(int workedOn) {
        this.workedOn = workedOn;
    }
    
    
      
    
}
