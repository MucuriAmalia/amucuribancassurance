package com.brokersystems.brokerapp.certs.model;

import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.RiskTrans;

import javax.persistence.*;

/**
 * Created by peter on 2/18/2017.
 */
@Entity
@Table(name="sys_brk_certificate_print")
public class CertPrint {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="cerp_id")
    private Long cerpId;

    @ManyToOne
    @JoinColumn(name="cerp_user_code",nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="cerp_cq_id",nullable=false,unique = true)
    private PrintCertificateQueue certQueue;

    public Long getCerpId() {
        return cerpId;
    }

    public void setCerpId(Long cerpId) {
        this.cerpId = cerpId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PrintCertificateQueue getCertQueue() {
        return certQueue;
    }

    public void setCertQueue(PrintCertificateQueue certQueue) {
        this.certQueue = certQueue;
    }
}
