package com.brokersystems.brokerapp.mail.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by HP on 8/9/2017.
 */
public class MailMessage {

    private String fromAddress;
    private Set<String> toAddresses = new HashSet<>();
    private Set<String> ccAddresses = new HashSet<>();
    private Set<String> bccAddresses = new HashSet<>();
    private String subject = "";
    private String message = "";
    private String toEmail;

    public MailMessage() {
        super();
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    /**
     * @return Returns the bccAddresses.
     */
    public Set getBccAddresses() {
        return bccAddresses;
    }

    public void addBccAddress(String addr) {
        bccAddresses.add(addr);
    }

    public void removeBccAddress(String addr) {
        bccAddresses.remove(addr);
    }

    /**
     * @param bccAddresses The bccAddresses to set.
     */
    public void setBccAddresses(Set bccAddresses) {
        this.bccAddresses = bccAddresses;
    }

    /**
     * @return Returns the ccAddresses.
     */
    public Set getCcAddresses() {
        return ccAddresses;
    }

    public void addCcAddress(String addr) {
        ccAddresses.add(addr);
    }

    public void removeCcAddress(String addr) {
        ccAddresses.remove(addr);
    }

    /**
     * @param ccAddresses The ccAddresses to set.
     */
    public void setCcAddresses(Set ccAddresses) {
        this.ccAddresses = ccAddresses;
    }

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return Returns the subject.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject The subject to set.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return Returns the toAddresses.
     */
    public Set getToAddresses() {
        return toAddresses;
    }

    public void addToAddress(String addr) {
        toAddresses.add(addr);
    }

    public void removeToAddress(String addr) {
        toAddresses.remove(addr);
    }

    /**
     * @param toAddresses The toAddresses to set.
     */
    public void setToAddresses(Set toAddresses) {
        this.toAddresses = toAddresses;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }
}
