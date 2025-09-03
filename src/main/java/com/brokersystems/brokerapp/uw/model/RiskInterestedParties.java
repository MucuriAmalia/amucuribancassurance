package com.brokersystems.brokerapp.uw.model;

import com.brokersystems.brokerapp.setup.model.InterestedParties;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by HP on 9/13/2017.
 */
@Entity
@Table(name="sys_brk_rsk_ips")
public class RiskInterestedParties {

    @Id
    @SequenceGenerator(name = "riskPartiesSeq",sequenceName = "risk_parties_seq",allocationSize=1)
    @GeneratedValue(generator = "riskPartiesSeq")
    @Column(name="rid_id")
    private Long ridId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="rid_risk_id")
    private RiskTrans risk;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="rid_ip_id")
    private InterestedParties interestedParties;

    public Long getRidId() {
        return ridId;
    }

    public void setRidId(Long ridId) {
        this.ridId = ridId;
    }

    public RiskTrans getRisk() {
        return risk;
    }

    public void setRisk(RiskTrans risk) {
        this.risk = risk;
    }

    public InterestedParties getInterestedParties() {
        return interestedParties;
    }

    public void setInterestedParties(InterestedParties interestedParties) {
        this.interestedParties = interestedParties;
    }


    @Override
    public String toString() {
        return "RiskInterestedParties{" +
                "ridId=" + ridId +
                ", risk=" + risk +
                ", interestedParties=" + interestedParties +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RiskInterestedParties that = (RiskInterestedParties) o;

        if (!risk.equals(that.risk)) return false;
        return interestedParties.equals(that.interestedParties);

    }

    @Override
    public int hashCode() {
        int result = risk.hashCode();
        result = 31 * result + interestedParties.hashCode();
        return result;
    }
}
