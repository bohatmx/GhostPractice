/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.ghostpractice.data;

import com.boha.ghostpractice.reports.data.FeeTargetProgressReport;
import com.boha.ghostpractice.reports.data.FinancialStatusReport;
import com.boha.ghostpractice.reports.data.MatterAnalysisByOwnerReport;

import java.util.List;

/**
 *
 * @author Aubrey Malabie esq.
 */
public class WebServiceResponse {

    private int responseCode, activityID;
    private long elapsedSeconds;
    private String responseMessage;
    private String deviceID;
    private UserDTO user;
    private AppDTO app;
    private PlatformDTO platform;
    private double fee;
    private  FeeTargetProgressReport feeTargetProgressReport;
    private MatterAnalysisByOwnerReport matterAnalysisByOwnerReport;
    private FinancialStatusReport financialStatusReport;
    
    private MatterDTO matter;
    private List<MobileTariffCodeDTO> mobileTariffCodeList;
    private List<MatterSearchResultDTO> matterSearchList;
    private List<MobileUser> mobileUsers;

    public List<MobileUser> getMobileUsers() {
        return mobileUsers;
    }

    public void setMobileUsers(List<MobileUser> mobileUsers) {
        this.mobileUsers = mobileUsers;
    }

    public int getActivityID() {
		return activityID;
	}

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public void setActivityID(int activityID) {
		this.activityID = activityID;
	}

	public String getDeviceID() {
        return deviceID;
    }

    public FeeTargetProgressReport getFeeTargetProgressReport() {
        return feeTargetProgressReport;
    }

    public void setFeeTargetProgressReport(FeeTargetProgressReport feeTargetProgressReport) {
        this.feeTargetProgressReport = feeTargetProgressReport;
    }

    public FinancialStatusReport getFinancialStatusReport() {
        return financialStatusReport;
    }

    public void setFinancialStatusReport(FinancialStatusReport financialStatusReport) {
        this.financialStatusReport = financialStatusReport;
    }

    public MatterAnalysisByOwnerReport getMatterAnalysisByOwnerReport() {
        return matterAnalysisByOwnerReport;
    }

    public void setMatterAnalysisByOwnerReport(MatterAnalysisByOwnerReport matterAnalysisByOwnerReport) {
        this.matterAnalysisByOwnerReport = matterAnalysisByOwnerReport;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public List<MatterSearchResultDTO> getMatterSearchList() {
        return matterSearchList;
    }

    public void setMatterSearchList(List<MatterSearchResultDTO> matterSearchList) {
        this.matterSearchList = matterSearchList;
    }

    public AppDTO getApp() {
        return app;
    }

    public void setApp(AppDTO app) {
        this.app = app;
    }

    public PlatformDTO getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformDTO platform) {
        this.platform = platform;
    }

   
    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public MatterDTO getMatter() {
        return matter;
    }

    public void setMatter(MatterDTO matter) {
        this.matter = matter;
    }

    public long getElapsedSeconds() {
        return elapsedSeconds;
    }

    public void setElapsedSeconds(long elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
    }



    public List<MobileTariffCodeDTO> getMobileTariffCodeList() {
        return mobileTariffCodeList;
    }

    public void setMobileTariffCodeList(List<MobileTariffCodeDTO> mobileTariffCodeList) {
        this.mobileTariffCodeList = mobileTariffCodeList;
    }

}
