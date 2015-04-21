/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.ghostpractice.data;


/**
 *
 * @author Aubrey Malabie
 */
public class GhostRequestDTO {

    private int requestType;
    private String deviceID, appName, platformName, searchString, matterID;
    private int appID, activityID;
    private double latitude, longitude, deviceElapsedSeconds;
    private int platformID;
    private int userID;
    private double amount;
    private FeeDTO fee;
    private MatterNoteDTO note;
    private String activationCode;
    private int tarrifCodeType, tariffCodeID;
    private int duration, companyID;
    private int reportType;
    private TaskDTO task;

    public TaskDTO getTask() {
        return task;
    }

    public void setTask(TaskDTO task) {
        this.task = task;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    

    public int getTariffCodeID() {
		return tariffCodeID;
	}

	public void setTariffCodeID(int tariffCodeID) {
		this.tariffCodeID = tariffCodeID;
	}

	public int getActivityID() {
		return activityID;
	}

	public void setActivityID(int activityID) {
		this.activityID = activityID;
	}

	public double getDeviceElapsedSeconds() {
		return deviceElapsedSeconds;
	}

	public void setDeviceElapsedSeconds(double deviceElapsedSeconds) {
		this.deviceElapsedSeconds = deviceElapsedSeconds;
	}

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public int getAppID() {
        return appID;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public void setAppID(int appID) {
        this.appID = appID;
    }

    public FeeDTO getFee() {
        return fee;
    }

    public void setFee(FeeDTO fee) {
        this.fee = fee;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public MatterNoteDTO getNote() {
        return note;
    }

    public void setNote(MatterNoteDTO note) {
        this.note = note;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getMatterID() {
        return matterID;
    }

    public void setMatterID(String matterID) {
        this.matterID = matterID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public int getPlatformID() {
        return platformID;
    }

    public void setPlatformID(int platformID) {
        this.platformID = platformID;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getTarrifCodeType() {
        return tarrifCodeType;
    }

    public void setTarrifCodeType(int tarrifCodeType) {
        this.tarrifCodeType = tarrifCodeType;
    }
       
    public static final int PROVISION_NEW_USER = 110;
    public static final int PING = 111;
    public static final int POST_NOTE = 112;
    public static final int POST_FEE = 113;
    public static final int POST_UNBILLABLE_FEE = 114;
    public static final int FIND_MATTER = 115;
    public static final int GET_MATTER_DETAIL = 116;
    public static final int GET_TARIFF_CODES = 117;
    public static final int GET_APP_PLATFORM_IDS = 109;
    public static final int GET_REPORT = 120;
    public static final int POST_DEVICE_ELAPSED_TIME = 121;
    public static final int CALCULATE_FEE = 122;
    public static final int GET_FEE_EARNERS = 123;
    public static final int ASSIGN_TASK = 124;
    
    //
    public static final String IPHONE = "iPhone";
    public static final String IPAD = "iPad";
    public static final String ANDROID_PHONE = "Android Phone";
    public static final String ANDROID_TABLET = "Android Tablet";
    public static final String BLACKBERRY = "Blackberry";
    public static final String BLACKBERRY_TABLET = "Blackberry Tablet";
    public static final String WINDOWS_PHONE = "Windows Phone";
}
