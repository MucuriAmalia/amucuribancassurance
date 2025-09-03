package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * Created by peter on 5/28/2017.
 */
@Entity
@Table(name = "sys_brk_med_cards")
public class MedicalCards {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="mc_id")
    private Long cardId;

    @Column(name="mc_card_no",nullable = false)
    private String cardNo;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="mc_member_id",nullable = false)
    private CategoryMembers member;

    @Column(name="mc_wef_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @Column(name="mc_wet_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;

    @Column(name="mc_date_processed",nullable = false)
    private Date dateProcessed;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="mc_user_processed")
    private User processedBy;

    @Column(name="mc_status",nullable = false)
    private String status;

    @Column(name="mc_date_received")
    private Date dateReceived;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="mc_user_received")
    private User userReceived;

    @Column(name="mc_date_requested")
    private Date dateRequested;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name="mc_user_requested")
    private User userRequested;

    @Column(name="mc_date_dispatched")
    private Date dateDispatched;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="mc_user_dispatched")
    private User userDispatched;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="mc_mc_id")
    private MedicalCards prevCard;


    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public CategoryMembers getMember() {
        return member;
    }

    public void setMember(CategoryMembers member) {
        this.member = member;
    }

    public Date getDateProcessed() {
        return dateProcessed;
    }

    public void setDateProcessed(Date dateProcessed) {
        this.dateProcessed = dateProcessed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public Date getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(Date dateRequested) {
        this.dateRequested = dateRequested;
    }

    public Date getDateDispatched() {
        return dateDispatched;
    }

    public void setDateDispatched(Date dateDispatched) {
        this.dateDispatched = dateDispatched;
    }

    public Date getWefDate() {
        return wefDate;
    }

    public void setWefDate(Date wefDate) {
        this.wefDate = wefDate;
    }

    public Date getWetDate() {
        return wetDate;
    }

    public void setWetDate(Date wetDate) {
        this.wetDate = wetDate;
    }

    public User getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(User processedBy) {
        this.processedBy = processedBy;
    }

    public User getUserReceived() {
        return userReceived;
    }

    public void setUserReceived(User userReceived) {
        this.userReceived = userReceived;
    }

    public User getUserRequested() {
        return userRequested;
    }

    public void setUserRequested(User userRequested) {
        this.userRequested = userRequested;
    }

    public User getUserDispatched() {
        return userDispatched;
    }

    public void setUserDispatched(User userDispatched) {
        this.userDispatched = userDispatched;
    }

    public MedicalCards getPrevCard() {
        return prevCard;
    }

    public void setPrevCard(MedicalCards prevCard) {
        this.prevCard = prevCard;
    }
}
