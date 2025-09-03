package com.brokersystems.brokerapp.aki.dto;

public class LoginAuthDTO {


    private String token;
    private String loginUserId;
    private String issueAt;
    private String expires;
    private Long code;
    private Long LoginHistoryId;
    private String firstName;
    private String lastName;
    private Long loggedinEntityId;
    private String ApimSubscriptionKey;
    private Long IndustryTypeId;
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getLoginUserId() {
        return loginUserId;
    }
    public void setLoginUserId(String loginUserId) {
        this.loginUserId = loginUserId;
    }
    public String getIssueAt() {
        return issueAt;
    }
    public void setIssueAt(String issueAt) {
        this.issueAt = issueAt;
    }
    public String getExpires() {
        return expires;
    }
    public void setExpires(String expires) {
        this.expires = expires;
    }
    public Long getCode() {
        return code;
    }
    public void setCode(Long code) {
        this.code = code;
    }
    public Long getLoginHistoryId() {
        return LoginHistoryId;
    }
    public void setLoginHistoryId(Long loginHistoryId) {
        LoginHistoryId = loginHistoryId;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public Long getLoggedinEntityId() {
        return loggedinEntityId;
    }
    public void setLoggedinEntityId(Long loggedinEntityId) {
        this.loggedinEntityId = loggedinEntityId;
    }
    public String getApimSubscriptionKey() {
        return ApimSubscriptionKey;
    }
    public void setApimSubscriptionKey(String apimSubscriptionKey) {
        ApimSubscriptionKey = apimSubscriptionKey;
    }
    public Long getIndustryTypeId() {
        return IndustryTypeId;
    }
    public void setIndustryTypeId(Long industryTypeId) {
        IndustryTypeId = industryTypeId;
    }

}
