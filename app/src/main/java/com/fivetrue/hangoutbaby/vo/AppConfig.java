package com.fivetrue.hangoutbaby.vo;

/**
 * Created by kwonojin on 16. 3. 14..
 */
public class AppConfig {

    private int configId = 0;

    private int appVersionCode = 0;
    private String appId = null;
    private String appSercureKey = null;
    private String appVersionName = null;
    private String appMarketUrl = null;
    private String senderId = null;

    private int forceUpdate = 0;

    private String adminUrl = null;

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public int getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSercureKey() {
        return appSercureKey;
    }

    public void setAppSercureKey(String appSercureKey) {
        this.appSercureKey = appSercureKey;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getAppMarketUrl() {
        return appMarketUrl;
    }

    public void setAppMarketUrl(String appMarketUrl) {
        this.appMarketUrl = appMarketUrl;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public int getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getAdminUrl() {
        return adminUrl;
    }

    public void setAdminUrl(String adminUrl) {
        this.adminUrl = adminUrl;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "configId=" + configId +
                ", appVersionCode=" + appVersionCode +
                ", appId='" + appId + '\'' +
                ", appSercureKey='" + appSercureKey + '\'' +
                ", appVersionName='" + appVersionName + '\'' +
                ", appMarketUrl='" + appMarketUrl + '\'' +
                ", senderId='" + senderId + '\'' +
                ", forceUpdate=" + forceUpdate +
                ", adminUrl='" + adminUrl + '\'' +
                '}';
    }
}
