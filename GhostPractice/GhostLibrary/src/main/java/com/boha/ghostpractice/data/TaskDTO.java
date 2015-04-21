package com.boha.ghostpractice.data;

/**
 * Created by aubreyM on 2014/07/25.
 */
public class TaskDTO {
    private String taskDescription, matterID;
    private String userID;
    private boolean notifyWhenComplete;
    private long dueDate;

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getMatterID() {
        return matterID;
    }

    public void setMatterID(String matterID) {
        this.matterID = matterID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isNotifyWhenComplete() {
        return notifyWhenComplete;
    }

    public void setNotifyWhenComplete(boolean notifyWhenComplete) {
        this.notifyWhenComplete = notifyWhenComplete;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }
}
